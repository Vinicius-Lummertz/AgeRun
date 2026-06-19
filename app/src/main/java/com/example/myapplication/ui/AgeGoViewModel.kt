package com.example.myapplication.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AgeGoRepository
import com.example.myapplication.data.Announcement
import com.example.myapplication.data.AuthRequiredException
import com.example.myapplication.data.AuthSession
import com.example.myapplication.data.CommunityPost
import com.example.myapplication.data.CommunityComment
import com.example.myapplication.data.DirectoryItem
import com.example.myapplication.data.InstructorSettings
import com.example.myapplication.data.RepositoryProvider
import com.example.myapplication.data.Student
import com.example.myapplication.data.TrainingNowUser
import com.example.myapplication.data.Workout
import com.example.myapplication.data.WorkoutSessionPayload
import com.example.myapplication.data.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AgeGoUiState(
    val isLoading: Boolean = true,
    val authSession: AuthSession? = null,
    val authLoading: Boolean = false,
    val authMessage: String? = null,
    val instructorSettings: InstructorSettings = InstructorSettings(),
    val students: List<Student> = emptyList(),
    val workouts: List<Workout> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val communityPosts: List<CommunityPost> = emptyList(),
    val events: List<Event> = emptyList(),
    val groups: List<DirectoryItem> = emptyList(),
    val routines: List<DirectoryItem> = emptyList(),
    val trainingNow: List<TrainingNowUser> = emptyList(),
    val message: String? = null
)

class AgeGoViewModel(
    private val repository: AgeGoRepository = RepositoryProvider.create()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AgeGoUiState())
    val uiState: StateFlow<AgeGoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val session = repository.restoreSession()
            _uiState.update { if (session == null) it.copy(isLoading = false, authSession = null) else emptyDataState(session).copy(isLoading = false) }
            if (session != null) refresh()
        }
    }

    fun startLogin(identifier: String, onToken: (String) -> Unit = {}) {
        startLogin(identifier) { token, _ -> onToken(token) }
    }

    fun startLogin(identifier: String, onResult: (String, String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(authLoading = true, authMessage = null) }
            runCatching { repository.startLogin(identifier) }
                .onSuccess { result ->
                    _uiState.update { it.copy(authLoading = false, authMessage = result.message.ifBlank { "Token enviado" }) }
                    onResult(result.verificationToken, result.nextStep)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(authLoading = false, authMessage = error.message ?: "Nao foi possivel gerar o token") }
                }
        }
    }

    fun verifyLogin(identifier: String, token: String, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(authLoading = true, authMessage = null) }
            runCatching { repository.verifyLogin(identifier, token) }
                .onSuccess { session ->
                    _uiState.value = emptyDataState(session).copy(isLoading = true, authLoading = false, authMessage = null)
                    refresh()
                    onSuccess(session.role)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(authLoading = false, authMessage = error.message ?: "Nao foi possivel entrar") }
                }
        }
    }

    fun registerInstructor(name: String, email: String, phone: String, onToken: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(authLoading = true, authMessage = null) }
            runCatching { repository.registerInstructor(name, email, phone) }
                .onSuccess { result ->
                    _uiState.update { it.copy(authLoading = false, authMessage = result.message.ifBlank { "Token enviado" }) }
                    onToken(result.verificationToken)
                }
                .onFailure { error -> _uiState.update { it.copy(authLoading = false, authMessage = error.message ?: "Cadastro nao concluido") } }
        }
    }

    fun verifyInstructor(context: Context, email: String, token: String, displayName: String, photoUri: Uri?, onSuccess: () -> Unit, onFailure: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(authLoading = true, authMessage = null) }
            runCatching { repository.verifyInstructor(context.contentResolver, email, token, displayName, photoUri) }
                .onSuccess { session ->
                    _uiState.value = emptyDataState(session).copy(isLoading = true, authLoading = false, authMessage = null)
                    refresh()
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(authLoading = false, authMessage = "${error.message ?: "Token invalido"}. Entre novamente para gerar um novo codigo.") }
                    onFailure()
                }
        }
    }

    fun startStudentFirstAccess(phone: String, onToken: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(authLoading = true, authMessage = null) }
            runCatching { repository.startStudentFirstAccess(phone) }
                .onSuccess { result ->
                    _uiState.update { it.copy(authLoading = false, authMessage = result.message.ifBlank { "Token enviado" }) }
                    onToken(result.verificationToken)
                }
                .onFailure { error -> _uiState.update { it.copy(authLoading = false, authMessage = error.message ?: "Telefone nao encontrado") } }
        }
    }

    fun completeStudentFirstAccess(
        phone: String,
        email: String,
        nickname: String,
        photoUri: Uri?,
        token: String,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(authLoading = true, authMessage = null) }
            runCatching { repository.completeStudentFirstAccess(context.contentResolver, phone, email, nickname, photoUri, token) }
                .onSuccess { session ->
                    _uiState.value = emptyDataState(session).copy(isLoading = true, authLoading = false, authMessage = null)
                    refresh()
                    onSuccess()
                }
                .onFailure { error -> _uiState.update { it.copy(authLoading = false, authMessage = error.message ?: "Primeiro acesso nao concluido") } }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearSession()
            _uiState.value = AgeGoUiState(isLoading = false)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                if (_uiState.value.authSession?.role == "student") {
                    runCatching { repository.sendPresence() }
                }
                val data = repository.loadDashboard()
                val settings = if (_uiState.value.authSession?.role == "instructor") {
                    runCatching { repository.loadSettings() }.getOrElse { _uiState.value.instructorSettings }
                } else {
                    _uiState.value.instructorSettings
                }
                _uiState.update { current -> current.copy(
                    isLoading = false,
                    students = data.students,
                    workouts = data.workouts,
                    announcements = data.announcements,
                    communityPosts = data.communityPosts,
                    events = data.events,
                    groups = data.groups,
                    routines = data.routines,
                    trainingNow = data.trainingNow,
                    message = data.message,
                    instructorSettings = settings
                ) }
            }.onFailure { error ->
                if (error is AuthRequiredException) {
                    repository.clearSession()
                    _uiState.value = AgeGoUiState(
                        isLoading = false,
                        authSession = null,
                        authMessage = error.message ?: "Sessao expirada. Entre novamente."
                    )
                    return@launch
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        students = emptyList(),
                        workouts = emptyList(),
                        announcements = emptyList(),
                        communityPosts = emptyList(),
                        events = emptyList(),
                        groups = emptyList(),
                        routines = emptyList(),
                        trainingNow = emptyList(),
                        message = error.message ?: "Nao foi possivel carregar dados"
                    )
                }
            }
        }
    }

    fun saveProfile(context: Context, name: String, photoUri: Uri?) {
        viewModelScope.launch {
            runCatching { repository.saveProfile(context.contentResolver, name, photoUri) }
                .onSuccess { session ->
                    _uiState.update { it.copy(authSession = session, message = "Perfil atualizado") }
                    refresh()
                }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Nao foi possivel salvar perfil") } }
        }
    }

    fun saveInstructorSettings(settings: InstructorSettings) {
        viewModelScope.launch {
            runCatching { repository.saveSettings(settings) }
                .onSuccess { saved -> _uiState.update { it.copy(instructorSettings = saved) } }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Nao foi possivel salvar configuracoes") } }
        }
    }

    fun saveStudent(student: Student, onSaved: (Student) -> Unit = {}) {
        viewModelScope.launch {
            runCatching { repository.saveStudent(student) }
                .onSuccess { saved ->
                    _uiState.update { state ->
                        val normalized = saved.copy(status = saved.status.ifBlank { "active" })
                        val exists = state.students.any { it.id == normalized.id }
                        state.copy(
                            students = if (exists) state.students.map { if (it.id == normalized.id) normalized else it } else listOf(normalized) + state.students,
                            message = if (normalized.accessCode.isNotBlank()) {
                                "Codigo do aluno: ${normalized.accessCode}. Valido por 24h."
                            } else {
                                "Aluno salvo"
                            }
                        )
                    }
                    onSaved(saved)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(message = error.message ?: "Nao foi possivel salvar aluno") }
                }
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            repository.deleteStudent(studentId)
            _uiState.update { state -> state.copy(students = state.students.filterNot { it.id == studentId }) }
        }
    }

    fun saveWorkout(workout: Workout) {
        viewModelScope.launch {
            val normalized = repository.saveWorkout(workout)
            _uiState.update { state ->
                val exists = state.workouts.any { it.id == normalized.id }
                state.copy(workouts = if (exists) state.workouts.map { if (it.id == normalized.id) normalized else it } else listOf(normalized) + state.workouts)
            }
        }
    }

    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            repository.deleteWorkout(workoutId)
            _uiState.update { state -> state.copy(workouts = state.workouts.filterNot { it.id == workoutId }) }
        }
    }

    fun saveEvent(event: Event) {
        viewModelScope.launch {
            val normalized = repository.saveEvent(event)
            _uiState.update { state ->
                val exists = state.events.any { it.id == normalized.id }
                state.copy(events = if (exists) state.events.map { if (it.id == normalized.id) normalized else it } else listOf(normalized) + state.events)
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
            _uiState.update { state -> state.copy(events = state.events.filterNot { it.id == eventId }) }
        }
    }

    fun saveGroup(group: DirectoryItem) {
        viewModelScope.launch {
            val saved = repository.saveGroup(group)
            _uiState.update { state ->
                val exists = state.groups.any { it.id == saved.id }
                state.copy(groups = if (exists) state.groups.map { if (it.id == saved.id) saved else it } else listOf(saved) + state.groups)
            }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            repository.deleteGroup(groupId)
            _uiState.update { state -> state.copy(groups = state.groups.filterNot { it.id == groupId }) }
        }
    }

    fun saveRoutine(routine: DirectoryItem) {
        viewModelScope.launch {
            val saved = repository.saveRoutine(routine)
            _uiState.update { state ->
                val exists = state.routines.any { it.id == saved.id }
                state.copy(routines = if (exists) state.routines.map { if (it.id == saved.id) saved else it } else listOf(saved) + state.routines)
            }
        }
    }

    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
            _uiState.update { state -> state.copy(routines = state.routines.filterNot { it.id == routineId }) }
        }
    }

    fun saveCommunityPost(post: CommunityPost) {
        val tempId = "local-${java.util.UUID.randomUUID()}"
        val session = _uiState.value.authSession
        val localPost = post.copy(
            id = tempId,
            authorName = session?.name?.ifBlank { "Voce" } ?: "Voce",
            authorAvatarUrl = session?.avatarUrl.orEmpty()
        )
        _uiState.update { state -> state.copy(communityPosts = listOf(localPost) + state.communityPosts) }
        viewModelScope.launch {
            val normalized = repository.saveCommunityPost(post)
            _uiState.update { state ->
                state.copy(
                    communityPosts = state.communityPosts.map { if (it.id == tempId) normalized else it }
                )
            }
        }
    }

    fun toggleCommunityLike(postId: String) {
        viewModelScope.launch { repository.toggleCommunityLike(postId) }
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map { post ->
                    if (post.id != postId) {
                        post
                    } else {
                        post.copy(
                            liked = !post.liked,
                            likes = (post.likes + if (post.liked) -1 else 1).coerceAtLeast(0)
                        )
                    }
                }
            )
        }
    }

    fun addCommunityComment(postId: String, content: String = "Novo comentario") {
        viewModelScope.launch { repository.addCommunityComment(postId, content) }
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            comments = post.comments + 1,
                            commentThreads = post.commentThreads + CommunityComment(
                                id = java.util.UUID.randomUUID().toString(),
                                authorName = state.authSession?.name?.ifBlank { "Voce" } ?: "Voce",
                                authorAvatarUrl = state.authSession?.avatarUrl.orEmpty(),
                                content = content
                            )
                        )
                    } else {
                        post
                    }
                }
            )
        }
    }

    fun replyCommunityComment(postId: String, commentId: String, content: String) {
        viewModelScope.launch { repository.addCommunityComment(postId, content, commentId) }
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            comments = post.comments + 1,
                            commentThreads = post.commentThreads.addReplyToComment(
                                commentId,
                                CommunityComment(
                                    id = java.util.UUID.randomUUID().toString(),
                                    authorName = state.authSession?.name?.ifBlank { "Voce" } ?: "Voce",
                                    authorAvatarUrl = state.authSession?.avatarUrl.orEmpty(),
                                    content = content
                                )
                            )
                        )
                    } else {
                        post
                    }
                }
            )
        }
    }

    fun toggleCommunityCommentLike(postId: String, commentId: String) {
        viewModelScope.launch { repository.toggleCommunityCommentLike(commentId) }
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map { post ->
                    if (post.id == postId) {
                        post.copy(commentThreads = post.commentThreads.toggleCommentLike(commentId))
                    } else {
                        post
                    }
                }
            )
        }
    }

    fun shareCommunityPost(postId: String) {
        viewModelScope.launch { repository.shareCommunityPost(postId) }
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map {
                    if (it.id == postId) it.copy(shares = it.shares + 1) else it
                }
            )
        }
    }

    suspend fun uploadMedia(context: Context, uri: Uri): String =
        repository.uploadMedia(context.contentResolver, uri)

    fun saveWorkoutSession(session: WorkoutSessionPayload, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching { repository.saveWorkoutSession(session) }
                .onSuccess {
                    _uiState.update { state -> state.copy(message = "Treino salvo") }
                    onSaved()
                    refresh()
                }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Nao foi possivel salvar treino") } }
        }
    }
}

private fun emptyDataState(session: AuthSession) = AgeGoUiState(
    isLoading = false,
    authSession = session,
    students = emptyList(),
    workouts = emptyList(),
    announcements = emptyList(),
    communityPosts = emptyList(),
    events = emptyList(),
    groups = emptyList(),
    routines = emptyList(),
    trainingNow = emptyList()
)

private fun List<CommunityComment>.addReplyToComment(
    commentId: String,
    reply: CommunityComment
): List<CommunityComment> = map { comment ->
    when {
        comment.id == commentId -> comment.copy(replies = comment.replies + reply)
        comment.replies.isNotEmpty() -> comment.copy(replies = comment.replies.addReplyToComment(commentId, reply))
        else -> comment
    }
}

private fun List<CommunityComment>.toggleCommentLike(commentId: String): List<CommunityComment> = map { comment ->
    when {
        comment.id == commentId -> comment.copy(
            liked = !comment.liked,
            likes = (comment.likes + if (comment.liked) -1 else 1).coerceAtLeast(0)
        )
        comment.replies.isNotEmpty() -> comment.copy(replies = comment.replies.toggleCommentLike(commentId))
        else -> comment
    }
}
