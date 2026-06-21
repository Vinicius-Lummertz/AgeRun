package com.example.myapplication.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AgeGoRepository
import com.example.myapplication.data.Announcement
import com.example.myapplication.data.AuthRequiredException
import com.example.myapplication.data.AuthSession
import com.example.myapplication.data.Challenge
import com.example.myapplication.data.DirectoryItem
import com.example.myapplication.data.InstructorSettings
import com.example.myapplication.data.RepositoryProvider
import com.example.myapplication.data.RunHistoryEntry
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
    val events: List<Event> = emptyList(),
    val routines: List<DirectoryItem> = emptyList(),
    val trainingNow: List<TrainingNowUser> = emptyList(),
    val runHistory: List<RunHistoryEntry> = emptyList(),
    val challenges: List<Challenge> = emptyList(),
    val instructorName: String = "",
    val instructorAvatarUrl: String = "",
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
        frequencyDays: List<Int>,
        token: String,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(authLoading = true, authMessage = null) }
            runCatching { repository.completeStudentFirstAccess(context.contentResolver, phone, email, nickname, photoUri, frequencyDays, token) }
                .onSuccess { session ->
                    _uiState.value = emptyDataState(session).copy(isLoading = true, authLoading = false, authMessage = null)
                    refresh()
                    onSuccess()
                }
                .onFailure { error -> _uiState.update { it.copy(authLoading = false, authMessage = error.message ?: "Primeiro acesso nao concluido") } }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
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
                    _uiState.value.instructorSettings.copy(pixKey = data.instructorPixKey)
                }
                _uiState.update { current -> current.copy(
                    isLoading = false,
                    students = data.students,
                    workouts = data.workouts,
                    announcements = data.announcements,
                    events = data.events,
                    routines = data.routines,
                    trainingNow = data.trainingNow,
                    runHistory = data.runHistory,
                    challenges = data.challenges,
                    instructorName = data.instructorName,
                    instructorAvatarUrl = data.instructorAvatarUrl,
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
                        events = emptyList(),
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
            runCatching { repository.deleteStudent(studentId) }
                .onSuccess { _uiState.update { state -> state.copy(students = state.students.filterNot { it.id == studentId }) } }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível excluir aluno") } }
        }
    }

    fun saveWorkout(workout: Workout, onSaved: (Workout) -> Unit = {}) {
        viewModelScope.launch {
            runCatching { repository.saveWorkout(workout) }
                .onSuccess { normalized ->
                    _uiState.update { state ->
                        val exists = state.workouts.any { it.id == normalized.id }
                        state.copy(workouts = if (exists) state.workouts.map { if (it.id == normalized.id) normalized else it } else listOf(normalized) + state.workouts)
                    }
                    onSaved(normalized)
                }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Nao foi possivel salvar treino") } }
        }
    }

    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            runCatching { repository.deleteWorkout(workoutId) }
                .onSuccess { _uiState.update { state -> state.copy(workouts = state.workouts.filterNot { it.id == workoutId }) } }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível excluir treino") } }
        }
    }

    fun saveEvent(event: Event) {
        viewModelScope.launch {
            runCatching { repository.saveEvent(event) }
                .onSuccess { normalized ->
                    _uiState.update { state ->
                        val exists = state.events.any { it.id == normalized.id }
                        state.copy(events = if (exists) state.events.map { if (it.id == normalized.id) normalized else it } else listOf(normalized) + state.events)
                    }
                }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível salvar evento") } }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            runCatching { repository.deleteEvent(eventId) }
                .onSuccess { _uiState.update { state -> state.copy(events = state.events.filterNot { it.id == eventId }) } }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível excluir evento") } }
        }
    }

    fun saveRoutine(routine: DirectoryItem, onSaved: (DirectoryItem) -> Unit = {}) {
        viewModelScope.launch {
            runCatching { repository.saveRoutine(routine) }
                .onSuccess { saved ->
                    _uiState.update { state ->
                        val exists = state.routines.any { it.id == saved.id }
                        state.copy(routines = if (exists) state.routines.map { if (it.id == saved.id) saved else it } else listOf(saved) + state.routines)
                    }
                    onSaved(saved)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(message = error.message ?: "Nao foi possivel salvar treino") }
                }
        }
    }

    fun saveAnnouncement(content: String, targetType: String) {
        viewModelScope.launch {
            runCatching { repository.saveAnnouncement(content, targetType) }
                .onSuccess { saved -> _uiState.update { state -> state.copy(announcements = listOf(saved) + state.announcements) } }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível publicar o aviso") } }
        }
    }

    fun checkInEvent(eventId: String) = viewModelScope.launch {
        runCatching { repository.checkInEvent(eventId) }
            .onSuccess { refresh() }
            .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível confirmar presença") } }
    }

    fun openEventCheckIn(eventId: String) = viewModelScope.launch {
        runCatching { repository.openEventCheckIn(eventId) }
            .onSuccess { refresh() }
            .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível iniciar o evento") } }
    }

    fun startGroupEvent(eventId: String) = viewModelScope.launch {
        runCatching { repository.startGroupEvent(eventId) }
            .onSuccess { refresh() }
            .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível iniciar a corrida") } }
    }

    fun finishGroupEvent(eventId: String) = viewModelScope.launch {
        runCatching { repository.finishGroupEvent(eventId) }
            .onSuccess { refresh() }
            .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível finalizar o evento") } }
    }

    fun saveEventRunResult(eventId: String, session: WorkoutSessionPayload, onSaved: () -> Unit = {}) = viewModelScope.launch {
        runCatching { repository.saveEventRunResult(eventId, session) }
            .onSuccess { onSaved(); refresh() }
            .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível enviar o resultado") } }
    }

    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            runCatching { repository.deleteRoutine(routineId) }
                .onSuccess { _uiState.update { state -> state.copy(routines = state.routines.filterNot { it.id == routineId }) } }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível excluir treino") } }
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

    fun submitPaymentProof(url: String) {
        viewModelScope.launch {
            runCatching { repository.submitPaymentProof(url) }
                .onSuccess { _uiState.update { it.copy(message = "Comprovante enviado para aprovação") }; refresh() }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível enviar o comprovante") } }
        }
    }

    fun approvePayment(studentId: String) {
        viewModelScope.launch {
            runCatching { repository.approvePayment(studentId) }
                .onSuccess { refresh() }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível aprovar o pagamento") } }
        }
    }

    fun rejectPayment(studentId: String, reason: String) {
        viewModelScope.launch {
            runCatching { repository.rejectPayment(studentId, reason) }
                .onSuccess { refresh() }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível rejeitar o comprovante") } }
        }
    }

    fun saveChallenge(challengeId: String, name: String, description: String, targetType: String, targetValue: Double) {
        viewModelScope.launch {
            runCatching { repository.saveChallenge(challengeId, name, description, targetType, targetValue) }
                .onSuccess { challenges -> _uiState.update { it.copy(challenges = challenges, message = if (challengeId.isBlank()) "Desafio criado" else "Desafio atualizado") } }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível salvar o desafio") } }
        }
    }

    fun deleteChallenge(challengeId: String) {
        viewModelScope.launch {
            runCatching { repository.deleteChallenge(challengeId) }
                .onSuccess { _uiState.update { state -> state.copy(challenges = state.challenges.filterNot { it.id == challengeId }) } }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Não foi possível excluir o desafio") } }
        }
    }
}

private fun emptyDataState(session: AuthSession) = AgeGoUiState(
    isLoading = false,
    authSession = session,
    students = emptyList(),
    workouts = emptyList(),
    announcements = emptyList(),
    events = emptyList(),
    routines = emptyList(),
    trainingNow = emptyList()
)
