package com.example.myapplication.data

import android.content.ContentResolver
import android.content.Context
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

interface AgeGoRepository {
    suspend fun restoreSession(): AuthSession?
    suspend fun clearSession()
    suspend fun startLogin(identifier: String): VerificationResult
    suspend fun verifyLogin(identifier: String, token: String): AuthSession
    suspend fun registerInstructor(name: String, email: String, phone: String): VerificationResult
    suspend fun verifyInstructor(contentResolver: ContentResolver, email: String, token: String, displayName: String, photoUri: Uri?): AuthSession
    suspend fun startStudentFirstAccess(phone: String): VerificationResult
    suspend fun completeStudentFirstAccess(contentResolver: ContentResolver, phone: String, email: String, nickname: String, photoUri: Uri?, frequencyDays: List<Int>, token: String): AuthSession
    suspend fun saveProfile(contentResolver: ContentResolver, name: String, photoUri: Uri?): AuthSession
    suspend fun loadSettings(): InstructorSettings
    suspend fun saveSettings(settings: InstructorSettings): InstructorSettings
    suspend fun loadDashboard(): DashboardData
    suspend fun saveAnnouncement(content: String, targetType: String): Announcement
    suspend fun sendPresence()
    suspend fun saveStudent(student: Student): Student
    suspend fun deleteStudent(studentId: String)
    suspend fun saveWorkout(workout: Workout): Workout
    suspend fun deleteWorkout(workoutId: String)
    suspend fun saveRoutine(routine: DirectoryItem): DirectoryItem
    suspend fun deleteRoutine(routineId: String)
    suspend fun saveEvent(event: Event): Event
    suspend fun deleteEvent(eventId: String)
    suspend fun checkInEvent(eventId: String)
    suspend fun openEventCheckIn(eventId: String)
    suspend fun startGroupEvent(eventId: String)
    suspend fun finishGroupEvent(eventId: String)
    suspend fun saveEventRunResult(eventId: String, session: WorkoutSessionPayload)
    suspend fun uploadMedia(contentResolver: ContentResolver, uri: Uri): String
    suspend fun saveWorkoutSession(session: WorkoutSessionPayload)
    suspend fun submitPaymentProof(url: String)
    suspend fun approvePayment(studentId: String)
    suspend fun rejectPayment(studentId: String, reason: String)
    suspend fun saveChallenge(challengeId: String, name: String, description: String, targetType: String, targetValue: Double): List<Challenge>
    suspend fun deleteChallenge(challengeId: String)
}

@Serializable
data class AuthSession(
    val accessToken: String = "",
    val refreshToken: String = "",
    val id: String = "",
    val role: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val needsProfile: Boolean = false
)

@Serializable
data class VerificationResult(
    val ok: Boolean = false,
    val message: String = "",
    val verificationToken: String = "",
    val nextStep: String = ""
)

class AuthRequiredException(message: String) : IllegalStateException(message)

@Serializable
private data class ApiErrorResponse(
    val error: String = "",
    val message: String = "",
    val details: String = "",
    val requestId: String = ""
)

@Serializable
data class InstructorSettings(
    val pixKey: String = "",
    val notificationEmail: Boolean = true,
    val notificationPush: Boolean = true
)

@Serializable
private data class SettingsResponse(val settings: InstructorSettings = InstructorSettings())

@Serializable
private data class LoginPayload(val identifier: String)

@Serializable
private data class LoginVerifyPayload(val identifier: String, val token: String)

@Serializable
private data class RefreshPayload(val refreshToken: String)

@Serializable
private data class InstructorRegisterPayload(val name: String, val email: String, val phone: String)

@Serializable
private data class VerifyInstructorPayload(
    val email: String,
    val token: String,
    val displayName: String,
    val photoBase64: String = "",
    val photoMimeType: String = ""
)

@Serializable
private data class StudentStartPayload(val phone: String)

@Serializable
private data class StudentCompletePayload(
    val phone: String,
    val email: String,
    val nickname: String,
    val photoBase64: String = "",
    val photoMimeType: String = "",
    val frequencyDays: List<Int> = emptyList(),
    val token: String
)

@Serializable
private data class ProfilePayload(
    val name: String,
    val photoBase64: String = "",
    val photoMimeType: String = ""
)

@Serializable
private data class ProfileResponse(val user: AuthSession = AuthSession())

@Serializable
private data class UploadMediaPayload(val base64: String, val mimeType: String)

@Serializable
private data class UploadMediaResponse(val url: String)

@Serializable
data class WorkoutSessionPayload(
    val routineId: String = "",
    val routineName: String = "",
    val challengeId: String? = null,
    val dayNumber: Int = 1,
    val cycleStep: Int = 1,
    val elapsedMs: Long = 0,
    val distanceMeters: Double = 0.0,
    val paceSecondsPerKm: Double = 0.0,
    val status: String = "completed",
    val plannedSteps: List<WorkoutSessionStep> = emptyList(),
    val routePoints: List<WorkoutRoutePoint> = emptyList(),
    val splits: List<WorkoutSplit> = emptyList()
)

@Serializable
data class WorkoutSessionStep(
    val name: String = "",
    val targetType: String = "open",
    val targetValue: Double = 0.0,
    val unit: String = ""
)

@Serializable
data class WorkoutRoutePoint(
    val lat: Double,
    val lon: Double,
    val timestamp: Long
)

@Serializable
data class WorkoutSplit(
    val km: Int,
    val elapsedMs: Long,
    val paceSeconds: Double
)

class ApiAgeGoRepository(
    private val baseUrl: String,
    context: Context? = null
) : AgeGoRepository {
    private val appContext = context?.applicationContext
    private val prefs = context?.getSharedPreferences("agego_auth", Context.MODE_PRIVATE)
    private var accessToken: String = ""
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun restoreSession(): AuthSession? =
        prefs?.getString("session", null)?.let { runCatching { json.decodeFromString<AuthSession>(it) }.getOrNull() }?.also {
            accessToken = it.accessToken
        }

    override suspend fun clearSession() {
        accessToken = ""
        prefs?.edit()?.remove("session")?.apply()
    }

    override suspend fun startLogin(identifier: String): VerificationResult =
        request<VerificationResult, LoginPayload>("POST", "/auth/login/start", LoginPayload(identifier)).also {
            showAuthTokenNotification(it.verificationToken)
        }

    override suspend fun verifyLogin(identifier: String, token: String): AuthSession {
        val session: AuthSession = request("POST", "/auth/login/verify", LoginVerifyPayload(identifier, token))
        saveSession(session)
        return session
    }

    override suspend fun registerInstructor(name: String, email: String, phone: String): VerificationResult =
        request<VerificationResult, InstructorRegisterPayload>("POST", "/auth/instructor/register", InstructorRegisterPayload(name, email, phone)).also {
            showAuthTokenNotification(it.verificationToken)
        }

    override suspend fun verifyInstructor(contentResolver: ContentResolver, email: String, token: String, displayName: String, photoUri: Uri?): AuthSession {
        val photo = photoUri?.let { preparePhotoPayload(contentResolver, it) }
        val session: AuthSession = request(
            "POST",
            "/auth/instructor/verify",
            VerifyInstructorPayload(email, token, displayName, photo?.base64.orEmpty(), photo?.mimeType.orEmpty())
        )
        saveSession(session)
        return session
    }

    override suspend fun startStudentFirstAccess(phone: String): VerificationResult =
        request<VerificationResult, StudentStartPayload>("POST", "/auth/student/start", StudentStartPayload(phone)).also {
            showAuthTokenNotification(it.verificationToken)
        }

    override suspend fun completeStudentFirstAccess(
        contentResolver: ContentResolver,
        phone: String,
        email: String,
        nickname: String,
        photoUri: Uri?,
        frequencyDays: List<Int>,
        token: String
    ): AuthSession {
        val photo = photoUri?.let { preparePhotoPayload(contentResolver, it) }
        val session: AuthSession = request(
            "POST",
            "/auth/student/complete",
            StudentCompletePayload(phone, email, nickname, photo?.base64.orEmpty(), photo?.mimeType.orEmpty(), frequencyDays, token)
        )
        saveSession(session)
        return session
    }

    override suspend fun saveProfile(contentResolver: ContentResolver, name: String, photoUri: Uri?): AuthSession {
        val photo = photoUri?.let { preparePhotoPayload(contentResolver, it) }
        val response: ProfileResponse = request(
            "PUT",
            "/me",
            ProfilePayload(name = name, photoBase64 = photo?.base64.orEmpty(), photoMimeType = photo?.mimeType.orEmpty())
        )
        val current = restoreSession()
        val updated = response.user.copy(
            accessToken = current?.accessToken.orEmpty(),
            refreshToken = current?.refreshToken.orEmpty()
        )
        saveSession(updated)
        return updated
    }

    override suspend fun loadSettings(): InstructorSettings {
        val response: SettingsResponse = request("GET", "/settings")
        return response.settings
    }

    override suspend fun saveSettings(settings: InstructorSettings): InstructorSettings {
        val response: SettingsResponse = request("PUT", "/settings", settings)
        return response.settings
    }

    private fun saveSession(session: AuthSession) {
        accessToken = session.accessToken
        prefs?.edit()?.putString("session", json.encodeToString(session))?.apply()
    }

    private fun showAuthTokenNotification(token: String) {
        val context = appContext ?: return
        if (token.length != 6) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        val channelId = "agego_auth_tokens"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "Tokens de acesso", NotificationManager.IMPORTANCE_HIGH)
            )
        }
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.Notification.Builder(context, channelId)
        } else {
            android.app.Notification.Builder(context)
        }
            .setSmallIcon(R.drawable.ic_nav_hub_fit)
            .setContentTitle("Token AgeGo")
            .setContentText("Seu codigo de acesso: $token")
            .setStyle(android.app.Notification.BigTextStyle().bigText("Seu codigo de acesso AgeGo e $token. Ele expira em alguns minutos."))
            .setAutoCancel(true)
            .build()
        manager.notify(6100 + (token.takeLast(2).toIntOrNull() ?: 0), notification)
    }

    override suspend fun loadDashboard(): DashboardData =
        request("GET", "/dashboard")

    override suspend fun saveAnnouncement(content: String, targetType: String): Announcement {
        val response: AnnouncementResponse = request("POST", "/announcements", AnnouncementPayload(content, targetType))
        return response.announcement
    }

    override suspend fun sendPresence() {
        requestUnit("POST", "/presence/training-now")
    }

    override suspend fun saveStudent(student: Student): Student {
        val response: StudentResponse = if (student.id.isBlank()) {
            request("POST", "/students", student.toPayload())
        } else {
            request("PUT", "/students/${student.id}", student.toPayload())
        }
        return response.student?.let {
            it.copy(
                id = it.id.ifBlank { student.id },
                billingDay = student.billingDay,
                monthlyFee = student.monthlyFee,
                accessCode = response.accessCode,
                accessCodeExpiresAt = response.accessCodeExpiresAt
            )
        } ?: student
    }

    override suspend fun deleteStudent(studentId: String) {
        requestUnit("DELETE", "/students/$studentId")
    }

    override suspend fun saveWorkout(workout: Workout): Workout {
        val response: WorkoutResponse = if (workout.id.isBlank()) {
            request("POST", "/workouts", workout.toPayload())
        } else {
            request("PUT", "/workouts/${workout.id}", workout.toPayload())
        }
        return response.workout ?: workout
    }

    override suspend fun deleteWorkout(workoutId: String) {
        requestUnit("DELETE", "/workouts/$workoutId")
    }

    override suspend fun saveRoutine(routine: DirectoryItem): DirectoryItem {
        val response: DirectoryResponse = if (routine.id.isBlank()) {
            request("POST", "/routines", routine.toPayload())
        } else {
            request("PUT", "/routines/${routine.id}", routine.toPayload())
        }
        return response.routine ?: routine
    }

    override suspend fun deleteRoutine(routineId: String) {
        requestUnit("DELETE", "/routines/$routineId")
    }

    override suspend fun saveEvent(event: Event): Event {
        val response: EventResponse = if (event.id.isBlank()) {
            request("POST", "/events", event.toPayload())
        } else {
            request("PUT", "/events/${event.id}", event.toPayload())
        }
        return response.event ?: event
    }

    override suspend fun deleteEvent(eventId: String) {
        requestUnit("DELETE", "/events/$eventId")
    }

    override suspend fun checkInEvent(eventId: String) {
        requestUnit("POST", "/events/$eventId/check-in")
    }

    override suspend fun openEventCheckIn(eventId: String) {
        requestUnit("POST", "/events/$eventId/open")
    }

    override suspend fun startGroupEvent(eventId: String) {
        requestUnit("POST", "/events/$eventId/start")
    }

    override suspend fun finishGroupEvent(eventId: String) {
        requestUnit("POST", "/events/$eventId/finish")
    }

    override suspend fun saveEventRunResult(eventId: String, session: WorkoutSessionPayload) {
        requestUnit("POST", "/events/$eventId/results", session)
    }

    override suspend fun uploadMedia(contentResolver: ContentResolver, uri: Uri): String =
        withContext(Dispatchers.IO) {
            val originalBytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IllegalStateException("Não foi possível ler a imagem")
            val originalMimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val (bytes, mimeType) = prepareImageUpload(originalBytes, originalMimeType)
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val body = json.encodeToString(UploadMediaPayload(base64 = base64, mimeType = mimeType))
            val responseText = execute("POST", "/upload-media", body, readTimeoutMs = 60_000)
            json.decodeFromString<UploadMediaResponse>(responseText).url
        }

    override suspend fun saveWorkoutSession(session: WorkoutSessionPayload) {
        requestUnit("POST", "/workout-sessions", session)
    }

    override suspend fun submitPaymentProof(url: String) {
        requestUnit("POST", "/me/payment-proof", PaymentProofPayload(url))
    }

    override suspend fun approvePayment(studentId: String) {
        requestUnit("POST", "/students/$studentId/approve-payment")
    }

    override suspend fun rejectPayment(studentId: String, reason: String) {
        requestUnit("POST", "/students/$studentId/reject-payment", RejectPaymentPayload(reason))
    }

    override suspend fun saveChallenge(challengeId: String, name: String, description: String, targetType: String, targetValue: Double): List<Challenge> {
        val response: ChallengesResponse = if (challengeId.isBlank()) {
            request("POST", "/challenges", ChallengePayload(name, description, targetType, targetValue))
        } else {
            request("PUT", "/challenges/$challengeId", ChallengePayload(name, description, targetType, targetValue))
        }
        return response.challenges
    }

    override suspend fun deleteChallenge(challengeId: String) {
        requestUnit("DELETE", "/challenges/$challengeId")
    }

    private suspend fun preparePhotoPayload(contentResolver: ContentResolver, uri: Uri): UploadMediaPayload =
        withContext(Dispatchers.IO) {
            val originalBytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IllegalStateException("Nao foi possivel ler a imagem")
            val originalMimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val (bytes, mimeType) = prepareImageUpload(originalBytes, originalMimeType)
            UploadMediaPayload(
                base64 = Base64.encodeToString(bytes, Base64.NO_WRAP),
                mimeType = mimeType
            )
        }

    private suspend inline fun <reified T> request(method: String, path: String): T =
        json.decodeFromString(execute(method, path, body = null))

    private suspend inline fun <reified T, reified B> request(method: String, path: String, body: B): T =
        json.decodeFromString(execute(method, path, json.encodeToString(body)))

    private suspend fun requestUnit(method: String, path: String) {
        execute(method, path, body = null)
    }

    private suspend inline fun <reified B> requestUnit(method: String, path: String, body: B) {
        execute(method, path, json.encodeToString(body))
    }

    private suspend fun execute(method: String, path: String, body: String?, readTimeoutMs: Int = 20_000): String =
        withContext(Dispatchers.IO) {
            fun performRequest(includeAuth: Boolean = true): Pair<Int, String> {
            val connection = (URL("${baseUrl.trimEnd('/')}$path").openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = 12_000
                readTimeout = readTimeoutMs
                setRequestProperty("Accept", "application/json")
                if (BuildConfig.APP_GATE_KEY.isNotBlank()) {
                    setRequestProperty("X-AgeGo-App-Key", BuildConfig.APP_GATE_KEY)
                }
                if (includeAuth && accessToken.isNotBlank()) {
                    setRequestProperty("Authorization", "Bearer $accessToken")
                }
                if (body != null) {
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                }
            }

            if (body != null) {
                OutputStreamWriter(connection.outputStream).use { it.write(body) }
            }

            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            connection.disconnect()
                return code to text
            }

            var (code, text) = performRequest()
            if (code == HttpURLConnection.HTTP_UNAUTHORIZED && path != "/auth/refresh" && refreshSession()) {
                val retry = performRequest()
                code = retry.first
                text = retry.second
            }

            if (code !in 200..299) {
                val apiMessage = parseApiError(text).ifBlank { "HTTP $code" }
                Log.e("AgeGoApi", "$method $path failed HTTP $code: $text")
                if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    accessToken = ""
                    prefs?.edit()?.remove("session")?.apply()
                    throw AuthRequiredException(apiMessage.ifBlank { "Sessao expirada. Entre novamente." })
                }
                throw IllegalStateException(apiMessage)
            }
            text.ifBlank { "{}" }
        }

    private fun refreshSession(): Boolean {
        val current = prefs?.getString("session", null)
            ?.let { runCatching { json.decodeFromString<AuthSession>(it) }.getOrNull() }
            ?: return false
        if (current.refreshToken.isBlank()) return false

        return runCatching {
            val body = json.encodeToString(RefreshPayload(current.refreshToken))
            val connection = (URL("${baseUrl.trimEnd('/')}/auth/refresh").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 12_000
                readTimeout = 20_000
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Content-Type", "application/json")
                if (BuildConfig.APP_GATE_KEY.isNotBlank()) {
                    setRequestProperty("X-AgeGo-App-Key", BuildConfig.APP_GATE_KEY)
                }
                doOutput = true
            }
            OutputStreamWriter(connection.outputStream).use { it.write(body) }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            connection.disconnect()
            if (code !in 200..299) return false
            val refreshed = json.decodeFromString<AuthSession>(text)
            saveSession(refreshed)
            true
        }.getOrDefault(false)
    }

    private fun parseApiError(text: String): String {
        if (text.isBlank()) return ""
        return runCatching {
            val error = json.decodeFromString<ApiErrorResponse>(text)
            listOf(error.error, error.message, error.details, error.requestId.takeIf { it.isNotBlank() }?.let { "ID $it" }.orEmpty())
                .filter { it.isNotBlank() }
                .joinToString(" - ")
        }.getOrElse { text }
    }
}

object RepositoryProvider {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun create(): AgeGoRepository =
        appContext?.let { OfflineFirstAgeGoRepository(it, BuildConfig.AGEGO_API_URL) }
            ?: ApiAgeGoRepository(BuildConfig.AGEGO_API_URL)
}

@Serializable
private data class StudentPayload(
    val name: String,
    val email: String,
    val phone: String,
    val routine: String,
    val status: String,
    val billingDay: Int,
    val monthlyFee: String
)

@Serializable
private data class WorkoutPayload(
    val name: String,
    val description: String,
    val iconName: String,
    val status: String
)

@Serializable
private data class DirectoryPayload(
    val name: String,
    val description: String,
    val status: String,
    val studentIds: List<String> = emptyList()
)

@Serializable
private data class EventPayload(
    val name: String,
    val description: String,
    val eventDate: String,
    val location: String,
    val latitude: Double?,
    val longitude: Double?,
    val coverPhotoUrl: String?
)

@Serializable
private data class StudentResponse(
    val student: Student? = null,
    val accessCode: String = "",
    val accessCodeExpiresAt: String = ""
)

@Serializable private data class WorkoutResponse(val workout: Workout? = null)
@Serializable private data class DirectoryResponse(val routine: DirectoryItem? = null)
@Serializable private data class EventResponse(val event: Event? = null)
@Serializable private data class AnnouncementPayload(val content: String, val targetType: String)
@Serializable private data class AnnouncementResponse(val announcement: Announcement)
@Serializable private data class PaymentProofPayload(val url: String)
@Serializable private data class RejectPaymentPayload(val reason: String)
@Serializable private data class ChallengePayload(val name: String, val description: String, val targetType: String, val targetValue: Double)
@Serializable private data class ChallengesResponse(val challenges: List<Challenge> = emptyList())

private fun Student.toPayload() = StudentPayload(name, email, phone, routine, status, billingDay, monthlyFee)
private fun Workout.toPayload() = WorkoutPayload(name, description.orEmpty(), iconName ?: "directions_run", status)
private fun DirectoryItem.toPayload() = DirectoryPayload(name, description, status, studentIds)
private fun Event.toPayload() = EventPayload(name, description.orEmpty(), eventDate, location.orEmpty(), latitude, longitude, coverPhotoUrl)

private fun prepareImageUpload(bytes: ByteArray, mimeType: String): Pair<ByteArray, String> {
    if (!mimeType.startsWith("image/") || mimeType == "image/gif") return bytes to mimeType

    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return bytes to mimeType

    val maxSide = 1600
    var sampleSize = 1
    while ((bounds.outWidth / sampleSize) > maxSide || (bounds.outHeight / sampleSize) > maxSide) {
        sampleSize *= 2
    }

    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options) ?: return bytes to mimeType
    return try {
        ByteArrayOutputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 86, output)
            output.toByteArray() to "image/jpeg"
        }
    } finally {
        bitmap.recycle()
    }
}
