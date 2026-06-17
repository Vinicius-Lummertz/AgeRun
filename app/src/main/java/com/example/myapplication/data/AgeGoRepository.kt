package com.example.myapplication.data

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.myapplication.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

interface AgeGoRepository {
    suspend fun loadDashboard(): DashboardData
    suspend fun saveStudent(student: Student): Student
    suspend fun deleteStudent(studentId: String)
    suspend fun saveWorkout(workout: Workout): Workout
    suspend fun deleteWorkout(workoutId: String)
    suspend fun saveGroup(group: DirectoryItem): DirectoryItem
    suspend fun deleteGroup(groupId: String)
    suspend fun saveRoutine(routine: DirectoryItem): DirectoryItem
    suspend fun deleteRoutine(routineId: String)
    suspend fun saveEvent(event: Event): Event
    suspend fun deleteEvent(eventId: String)
    suspend fun saveCommunityPost(post: CommunityPost): CommunityPost
    suspend fun toggleCommunityLike(postId: String)
    suspend fun addCommunityComment(postId: String, content: String, parentCommentId: String? = null)
    suspend fun toggleCommunityCommentLike(commentId: String)
    suspend fun shareCommunityPost(postId: String)
    suspend fun uploadMedia(contentResolver: ContentResolver, uri: Uri): String
}

@Serializable
private data class UploadMediaPayload(val base64: String, val mimeType: String)

@Serializable
private data class UploadMediaResponse(val url: String)

class ApiAgeGoRepository(
    private val baseUrl: String
) : AgeGoRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun loadDashboard(): DashboardData =
        request("GET", "/dashboard")

    override suspend fun saveStudent(student: Student): Student {
        val response: StudentResponse = if (student.id.isBlank()) {
            request("POST", "/students", student.toPayload())
        } else {
            request("PUT", "/students/${student.id}", student.toPayload())
        }
        return response.student?.let {
            student.copy(id = it.id.ifBlank { student.id })
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

    override suspend fun saveGroup(group: DirectoryItem): DirectoryItem {
        val response: DirectoryResponse = if (group.id.isBlank()) {
            request("POST", "/groups", group.toPayload())
        } else {
            request("PUT", "/groups/${group.id}", group.toPayload())
        }
        return response.group ?: group
    }

    override suspend fun deleteGroup(groupId: String) {
        requestUnit("DELETE", "/groups/$groupId")
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

    override suspend fun saveCommunityPost(post: CommunityPost): CommunityPost {
        val response: PostResponse = request("POST", "/posts", post.toPayload())
        return response.post ?: post
    }

    override suspend fun toggleCommunityLike(postId: String) {
        requestUnit("POST", "/posts/$postId/like")
    }

    override suspend fun addCommunityComment(postId: String, content: String, parentCommentId: String?) {
        requestUnit("POST", "/posts/$postId/comments", CommentPayload(content, parentCommentId))
    }

    override suspend fun toggleCommunityCommentLike(commentId: String) {
        requestUnit("POST", "/comments/$commentId/like")
    }

    override suspend fun shareCommunityPost(postId: String) {
        requestUnit("POST", "/posts/$postId/share")
    }

    override suspend fun uploadMedia(contentResolver: ContentResolver, uri: Uri): String =
        withContext(Dispatchers.IO) {
            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IllegalStateException("Não foi possível ler a imagem")
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val body = json.encodeToString(UploadMediaPayload(base64 = base64, mimeType = mimeType))
            val responseText = execute("POST", "/upload-media", body, readTimeoutMs = 60_000)
            json.decodeFromString<UploadMediaResponse>(responseText).url
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
            val connection = (URL("${baseUrl.trimEnd('/')}$path").openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = 12_000
                readTimeout = readTimeoutMs
                setRequestProperty("Accept", "application/json")
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

        if (code !in 200..299) {
                Log.e("AgeGoApi", "$method $path failed HTTP $code: $text")
                throw IllegalStateException(text.ifBlank { "HTTP $code" })
            }
            text.ifBlank { "{}" }
        }
}

class DemoAgeGoRepository(
    private val reason: String? = null
) : AgeGoRepository {
    override suspend fun loadDashboard() = DashboardData(
        students = listOf(
            Student("1", "Marina Alves", "marina@agego.com", "+55 11 90000-0101", "Performance", "Performance", "active"),
            Student("2", "Rafael Souza", "rafael@agego.com", "+55 11 90000-0102", "Essencial", "Essencial", "pending_payment"),
            Student("3", "Camila Lima", "camila@agego.com", "+55 11 90000-0103", "Performance", "Performance", "active"),
            Student("4", "Bruno Martins", "bruno@agego.com", "+55 11 90000-0104", "Base", "Base", "inactive")
        ),
        workouts = listOf(
            Workout("1", "Intervalado 5 km", "Series de velocidade e recuperacao", "directions_run", "active"),
            Workout("2", "Longao progressivo", "Aumento gradual de ritmo", "route", "active"),
            Workout("3", "Forca para corrida", "Mobilidade e estabilidade", "fitness_center", "draft")
        ),
        announcements = listOf(
            Announcement("1", "Treino de sabado confirmado as 6h30 no parque.", "Hoje, 09:10"),
            Announcement("2", "Lembrem de atualizar o resultado do treino semanal.", "Ontem, 18:40", "group")
        ),
        events = listOf(
            Event("1", "Treino coletivo", "Rodagem leve em grupo", demoDate(0, 6, 30), "Parque Central"),
            Event("2", "Avaliacao de pace", "Teste de 5 km", demoDate(0, 18, 0), "Pista Municipal")
        ),
        groups = listOf(
            DirectoryItem("iniciante", "Iniciantes", "active", "Grupo para alunos em fase inicial."),
            DirectoryItem("performance", "Performance", "active", "Treinos focados em evolucao de pace.")
        ),
        routines = listOf(
            DirectoryItem("corrida", "Corrida", "active", "Treinos de rua, pista e provas."),
            DirectoryItem("fortalecimento", "Fortalecimento", "active", "Base de forca, mobilidade e estabilidade.")
        ),
        communityPosts = demoCommunityPosts(emptyList()),
        isDemo = true,
        message = reason ?: "Modo demonstracao. Inicie a API local para carregar dados reais."
    )

    override suspend fun saveStudent(student: Student) = student.withId()
    override suspend fun deleteStudent(studentId: String) = Unit
    override suspend fun saveWorkout(workout: Workout) = workout.copy(id = workout.id.ifBlank { java.util.UUID.randomUUID().toString() })
    override suspend fun deleteWorkout(workoutId: String) = Unit
    override suspend fun saveGroup(group: DirectoryItem) = group.withId()
    override suspend fun deleteGroup(groupId: String) = Unit
    override suspend fun saveRoutine(routine: DirectoryItem) = routine.withId()
    override suspend fun deleteRoutine(routineId: String) = Unit
    override suspend fun saveEvent(event: Event) = event.copy(id = event.id.ifBlank { java.util.UUID.randomUUID().toString() })
    override suspend fun deleteEvent(eventId: String) = Unit
    override suspend fun saveCommunityPost(post: CommunityPost) = post.copy(id = post.id.ifBlank { java.util.UUID.randomUUID().toString() })
    override suspend fun toggleCommunityLike(postId: String) = Unit
    override suspend fun addCommunityComment(postId: String, content: String, parentCommentId: String?) = Unit
    override suspend fun toggleCommunityCommentLike(commentId: String) = Unit
    override suspend fun shareCommunityPost(postId: String) = Unit
    override suspend fun uploadMedia(contentResolver: ContentResolver, uri: Uri): String = uri.toString()
}

private fun Student.withId() = copy(id = id.ifBlank { java.util.UUID.randomUUID().toString() })
private fun DirectoryItem.withId() = copy(id = id.ifBlank { java.util.UUID.randomUUID().toString() })

private fun demoDate(daysFromToday: Int, hour: Int, minute: Int): String {
    val calendar = java.util.Calendar.getInstance().apply {
        add(java.util.Calendar.DAY_OF_YEAR, daysFromToday)
        set(java.util.Calendar.HOUR_OF_DAY, hour)
        set(java.util.Calendar.MINUTE, minute)
    }
    return java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.US).format(calendar.time)
}

object RepositoryProvider {
    fun create(): AgeGoRepository =
        FallbackRepository(ApiAgeGoRepository(BuildConfig.AGEGO_API_URL))
}

private class FallbackRepository(
    private val remote: AgeGoRepository
) : AgeGoRepository {
    private var demo: DemoAgeGoRepository? = null

    override suspend fun loadDashboard(): DashboardData = runCatching {
        remote.loadDashboard()
    }.getOrElse { error ->
        Log.e("AgeGoApi", "dashboard fallback: ${error.message}", error)
        DemoAgeGoRepository(error.message).also { demo = it }.loadDashboard()
    }

    override suspend fun saveStudent(student: Student) = runRemoteOrDemo { saveStudent(student) }
    override suspend fun deleteStudent(studentId: String) = runRemoteOrDemoUnit { deleteStudent(studentId) }
    override suspend fun saveWorkout(workout: Workout) = runRemoteOrDemo { saveWorkout(workout) }
    override suspend fun deleteWorkout(workoutId: String) = runRemoteOrDemoUnit { deleteWorkout(workoutId) }
    override suspend fun saveGroup(group: DirectoryItem) = runRemoteOrDemo { saveGroup(group) }
    override suspend fun deleteGroup(groupId: String) = runRemoteOrDemoUnit { deleteGroup(groupId) }
    override suspend fun saveRoutine(routine: DirectoryItem) = runRemoteOrDemo { saveRoutine(routine) }
    override suspend fun deleteRoutine(routineId: String) = runRemoteOrDemoUnit { deleteRoutine(routineId) }
    override suspend fun saveEvent(event: Event) = runRemoteOrDemo { saveEvent(event) }
    override suspend fun deleteEvent(eventId: String) = runRemoteOrDemoUnit { deleteEvent(eventId) }
    override suspend fun saveCommunityPost(post: CommunityPost) = runRemoteOrDemo { saveCommunityPost(post) }
    override suspend fun toggleCommunityLike(postId: String) = runRemoteOrDemoUnit { toggleCommunityLike(postId) }
    override suspend fun addCommunityComment(postId: String, content: String, parentCommentId: String?) =
        runRemoteOrDemoUnit { addCommunityComment(postId, content, parentCommentId) }
    override suspend fun toggleCommunityCommentLike(commentId: String) = runRemoteOrDemoUnit { toggleCommunityCommentLike(commentId) }
    override suspend fun shareCommunityPost(postId: String) = runRemoteOrDemoUnit { shareCommunityPost(postId) }
    override suspend fun uploadMedia(contentResolver: ContentResolver, uri: Uri): String =
        remote.uploadMedia(contentResolver, uri)

    private suspend fun <T> runRemoteOrDemo(block: suspend AgeGoRepository.() -> T): T =
        runCatching { remote.block() }.getOrElse {
            (demo ?: DemoAgeGoRepository(it.message).also { repo -> demo = repo }).block()
        }

    private suspend fun runRemoteOrDemoUnit(block: suspend AgeGoRepository.() -> Unit) {
        runCatching { remote.block() }.getOrElse {
            (demo ?: DemoAgeGoRepository(it.message).also { repo -> demo = repo }).block()
        }
    }
}

@Serializable
private data class StudentPayload(
    val name: String,
    val email: String,
    val phone: String,
    val routine: String,
    val status: String
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
    val location: String
)

@Serializable
private data class PostPayload(
    val type: String,
    val title: String,
    val content: String,
    val target: String,
    val linkedWorkoutId: String?,
    val pollOptions: List<String>,
    val mediaLabel: String?,
    val gifLabel: String?,
    val generatedImagePrompt: String?,
    val scheduledAt: String?,
    val location: String?,
    val contentWarning: String?
)

@Serializable
private data class CommentPayload(
    val content: String,
    val parentCommentId: String? = null
)

@Serializable private data class StudentResponse(val student: Student? = null)
@Serializable private data class WorkoutResponse(val workout: Workout? = null)
@Serializable private data class DirectoryResponse(val group: DirectoryItem? = null, val routine: DirectoryItem? = null)
@Serializable private data class EventResponse(val event: Event? = null)
@Serializable private data class PostResponse(val post: CommunityPost? = null)

private fun Student.toPayload() = StudentPayload(name, email, phone, routine, status)
private fun Workout.toPayload() = WorkoutPayload(name, description.orEmpty(), iconName ?: "directions_run", status)
private fun DirectoryItem.toPayload() = DirectoryPayload(name, description, status, studentIds)
private fun Event.toPayload() = EventPayload(name, description.orEmpty(), eventDate, location.orEmpty())
private fun CommunityPost.toPayload() = PostPayload(
    type = type.name.lowercase(),
    title = title,
    content = content,
    target = target,
    linkedWorkoutId = linkedWorkoutId,
    pollOptions = pollOptions,
    mediaLabel = mediaLabel,
    gifLabel = gifLabel,
    generatedImagePrompt = generatedImagePrompt,
    scheduledAt = scheduledAt,
    location = location,
    contentWarning = contentWarning
)

private fun demoCommunityPosts(workouts: List<Workout>): List<CommunityPost> = listOf(
    CommunityPost(
        id = "post-1",
        type = CommunityPostType.POST,
        title = "Recado do treino",
        content = "Amanha teremos rodagem leve. Hidratem bem e cheguem 10 minutos antes.",
        target = "groups",
        authorName = "Marina Alves",
        commentThreads = listOf(
            CommunityComment("comment-1", "Rafael Souza", "Confirmado, prof!"),
            CommunityComment("comment-2", "Camila Lima", "Vou chegar mais cedo para aquecer.")
        ),
        likes = 12,
        comments = 3,
        shares = 1
    )
)
