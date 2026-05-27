package com.exemplo.agerun.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.exemplo.agerun.data.InMemoryNoticeRepository
import com.exemplo.agerun.data.InMemoryProfileRepository
import com.exemplo.agerun.data.InMemoryStudentRepository
import com.exemplo.agerun.data.InMemoryWorkoutRepository
import com.exemplo.agerun.data.NoticeRepository
import com.exemplo.agerun.data.ProfileRepository
import com.exemplo.agerun.data.StudentRepository
import com.exemplo.agerun.data.WorkoutRepository
import com.exemplo.agerun.model.AppScreen
import com.exemplo.agerun.model.CoachTab
import com.exemplo.agerun.model.DashboardMetric
import com.exemplo.agerun.model.Role
import com.exemplo.agerun.model.Student
import com.exemplo.agerun.model.StudentDraft
import com.exemplo.agerun.model.StudentTab
import com.exemplo.agerun.model.WorkoutDay
import com.exemplo.agerun.model.WorkoutEntry
import com.exemplo.agerun.model.totalDistanceKm

@Stable
class AgeRunAppState(
    val studentRepository: StudentRepository = InMemoryStudentRepository(),
    val workoutRepository: WorkoutRepository = InMemoryWorkoutRepository(),
    val noticeRepository: NoticeRepository = InMemoryNoticeRepository(),
    val profileRepository: ProfileRepository = InMemoryProfileRepository(),
) {
    var currentScreen by mutableStateOf(AppScreen.Login)
        private set
    var role by mutableStateOf<Role?>(null)
        private set
    var coachTab by mutableStateOf(CoachTab.Inicio)
        private set
    var studentTab by mutableStateOf(StudentTab.Inicio)
        private set
    var selectedStudentId by mutableStateOf<String?>(null)
        private set
    var showCreateStudentSheet by mutableStateOf(false)
        private set

    // "Aluno logado" (demo): primeiro aluno da base.
    val currentStudentId: String get() = studentRepository.students.first().id

    val students get() = studentRepository.students
    val workouts get() = workoutRepository.workouts
    val notices get() = noticeRepository.notices

    val selectedStudent: Student?
        get() = students.firstOrNull { it.id == selectedStudentId }

    val dashboardMetrics: List<DashboardMetric>
        get() = listOf(
            DashboardMetric("Alunos ativos", students.count { it.status != "Inativo" }.toString()),
            DashboardMetric("Treinos", workouts.size.toString()),
            DashboardMetric(
                "Pendências",
                students.sumOf { s -> s.payments.count { it.status != "Pago" } }.toString(),
            ),
        )

    val workoutDays: List<WorkoutDay>
        get() = workouts.take(4).mapIndexed { index, w ->
            WorkoutDay(w.date, "%.0f".format(w.totalDistanceKm()), w.focus, highlight = index == 1)
        }

    fun login(role: Role) {
        this.role = role
        currentScreen = AppScreen.App
        coachTab = CoachTab.Inicio
        studentTab = StudentTab.Inicio
        selectedStudentId = null
    }

    fun logout() {
        role = null
        currentScreen = AppScreen.Login
        selectedStudentId = null
    }

    fun selectCoachTab(tab: CoachTab) {
        coachTab = tab
        if (tab != CoachTab.Alunos) selectedStudentId = null
    }

    fun selectStudentTab(tab: StudentTab) { studentTab = tab }

    fun openSettingsFromAvatar() { selectCoachTab(CoachTab.Config) }

    fun openStudent(id: String) { selectedStudentId = id }
    fun closeStudent() { selectedStudentId = null }

    fun openCreateStudentSheet() { showCreateStudentSheet = true }
    fun closeCreateStudentSheet() { showCreateStudentSheet = false }

    fun createStudent(draft: StudentDraft) {
        studentRepository.add(
            Student(
                id = "student-${students.size + 10}",
                name = draft.name, email = draft.email, phone = draft.phone,
                plan = draft.plan.ifBlank { "Plano inicial" }, startDate = "Hoje",
                status = "Novo", monthlyKm = 0, activeWorksheet = "Sem planilha definida",
                coachNote = "", payments = emptyList(),
            ),
        )
        showCreateStudentSheet = false
    }

    fun createWorkout(entry: WorkoutEntry) = workoutRepository.add(entry)
    fun createNotice(title: String, body: String, pinned: Boolean) =
        noticeRepository.add(title, body, pinned)
    fun toggleStudentStatus(id: String) = studentRepository.toggleStatus(id)
    fun updateStudentNote(id: String, note: String) = studentRepository.updateNote(id, note)
    fun updateStudentWorksheet(id: String, ws: String) = studentRepository.updateWorksheet(id, ws)
    fun addStudentPayment(id: String, label: String, amount: String, due: String) =
        studentRepository.addPayment(id, label, amount, due)
    fun togglePaymentStatus(studentId: String, paymentId: String) =
        studentRepository.togglePaymentStatus(studentId, paymentId)
}

@Composable
fun rememberAgeRunAppState(): AgeRunAppState = remember { AgeRunAppState() }
