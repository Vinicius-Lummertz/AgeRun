package com.example.myapplication.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.myapplication.data.local.AgeGoDatabase
import com.example.myapplication.data.local.CachedDashboardEntity
import com.example.myapplication.data.local.PendingMutationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.UUID

private const val SYNC_WORK_NAME = "agego-offline-sync"
private const val MUTATION_WORKOUT_SESSION = "workout_session"
private const val MUTATION_EVENT_CHECKIN = "event_checkin"
private const val MUTATION_EVENT_RUN_RESULT = "event_run_result"

/** Room-backed cache plus an outbox for operations that must survive process death. */
class OfflineFirstAgeGoRepository private constructor(
    private val context: Context,
    private val remote: ApiAgeGoRepository
) : AgeGoRepository by remote {
    constructor(context: Context, baseUrl: String) : this(
        context.applicationContext,
        ApiAgeGoRepository(baseUrl, context.applicationContext)
    )
    private val dao = AgeGoDatabase.get(context).offlineDao()
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    override suspend fun restoreSession(): AuthSession? = remote.restoreSession()

    override suspend fun clearSession() {
        val userId = remote.restoreSession()?.id
        remote.clearSession()
        if (!userId.isNullOrBlank()) withContext(Dispatchers.IO) {
            dao.deleteDashboard(userId)
            dao.deleteMutations(userId)
        }
    }

    override suspend fun loadDashboard(): DashboardData {
        val userId = requireUserId()
        if (isOnline()) runCatching { flushPending() }
        return try {
            val fresh = remote.loadDashboard().offlineSnapshot()
            saveCache(userId, fresh)
            fresh
        } catch (error: Throwable) {
            if (error is AuthRequiredException) throw error
            cachedDashboard(userId) ?: throw error
        }
    }

    override suspend fun saveWorkoutSession(session: WorkoutSessionPayload) {
        if (isOnline()) {
            try {
                remote.saveWorkoutSession(session)
                return
            } catch (error: IOException) {
                // Persist below and retry with WorkManager.
            }
        }
        val userId = requireUserId()
        withContext(Dispatchers.IO) {
            val id = UUID.randomUUID().toString()
            dao.enqueue(PendingMutationEntity(id, userId, MUTATION_WORKOUT_SESSION, json.encodeToString(session), System.currentTimeMillis(), 0))
        }
        scheduleSync(context)
    }

    override suspend fun checkInEvent(eventId: String) {
        if (isOnline()) {
            try {
                remote.checkInEvent(eventId)
                return
            } catch (error: IOException) {
                // Persist below and retry with WorkManager.
            }
        }
        val userId = requireUserId()
        withContext(Dispatchers.IO) {
            val id = UUID.randomUUID().toString()
            dao.enqueue(PendingMutationEntity(id, userId, MUTATION_EVENT_CHECKIN, eventId, System.currentTimeMillis(), 0))
        }
        scheduleSync(context)
    }

    override suspend fun saveEventRunResult(eventId: String, session: WorkoutSessionPayload) {
        if (isOnline()) {
            try {
                remote.saveEventRunResult(eventId, session)
                return
            } catch (error: IOException) {
                // Persist below and retry with WorkManager.
            }
        }
        val userId = requireUserId()
        withContext(Dispatchers.IO) {
            val id = UUID.randomUUID().toString()
            dao.enqueue(
                PendingMutationEntity(
                    id,
                    userId,
                    MUTATION_EVENT_RUN_RESULT,
                    json.encodeToString(EventRunResultPayload(eventId, session)),
                    System.currentTimeMillis(),
                    0
                )
            )
        }
        scheduleSync(context)
    }

    /** Returns false only when retrying later is useful. Invalid server data remains queued for inspection. */
    suspend fun flushPending(): Boolean {
        val userId = remote.restoreSession()?.id ?: return true
        if (!isOnline()) return false
        val mutations = withContext(Dispatchers.IO) { dao.pending(userId) }
        for (mutation in mutations) {
            try {
                when (mutation.type) {
                    MUTATION_WORKOUT_SESSION -> remote.saveWorkoutSession(json.decodeFromString(mutation.payload))
                    MUTATION_EVENT_CHECKIN -> remote.checkInEvent(mutation.payload)
                    MUTATION_EVENT_RUN_RESULT -> {
                        val data = json.decodeFromString<EventRunResultPayload>(mutation.payload)
                        remote.saveEventRunResult(data.eventId, data.session)
                    }
                }
                withContext(Dispatchers.IO) { dao.deleteMutation(mutation.id) }
            } catch (error: AuthRequiredException) {
                throw error
            } catch (error: IOException) {
                withContext(Dispatchers.IO) { dao.incrementAttempts(mutation.id) }
                return false
            } catch (_: Throwable) {
                withContext(Dispatchers.IO) { dao.incrementAttempts(mutation.id) }
                return true
            }
        }
        return true
    }

    private suspend fun requireUserId(): String = remote.restoreSession()?.id
        ?.takeIf { it.isNotBlank() }
        ?: throw AuthRequiredException("Sessao expirada. Entre novamente.")

    private fun isOnline(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private suspend fun cachedDashboard(userId: String): DashboardData? = withContext(Dispatchers.IO) {
        dao.dashboard(userId)?.payload?.let { runCatching { json.decodeFromString<DashboardData>(it) }.getOrNull() }
    }

    private suspend fun saveCache(userId: String, dashboard: DashboardData) = withContext(Dispatchers.IO) {
        dao.saveDashboard(CachedDashboardEntity(userId, json.encodeToString(dashboard.offlineSnapshot()), System.currentTimeMillis()))
    }
}

@kotlinx.serialization.Serializable
private data class EventRunResultPayload(val eventId: String, val session: WorkoutSessionPayload)

private fun DashboardData.offlineSnapshot() = copy(
    // Presence is momentary; showing an old "training now" state would be misleading.
    trainingNow = emptyList()
)

private fun scheduleSync(context: Context) {
    val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    val request = OneTimeWorkRequestBuilder<AgeGoSyncWorker>().setConstraints(constraints).build()
    // If a worker is already sending, append another pass so an item queued during that run is not missed.
    WorkManager.getInstance(context).enqueueUniqueWork(SYNC_WORK_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
}

class AgeGoSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = try {
        val repository = OfflineFirstAgeGoRepository(applicationContext, com.example.myapplication.BuildConfig.AGEGO_API_URL)
        if (repository.flushPending()) Result.success() else Result.retry()
    } catch (_: AuthRequiredException) {
        Result.failure()
    } catch (_: Throwable) {
        Result.retry()
    }
}
