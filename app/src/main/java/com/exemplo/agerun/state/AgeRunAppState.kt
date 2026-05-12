package com.exemplo.agerun.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.exemplo.agerun.data.sampleNotices
import com.exemplo.agerun.data.sampleStudents
import com.exemplo.agerun.data.sampleWorkouts
import com.exemplo.agerun.model.AppScreen
import com.exemplo.agerun.model.BottomModule
import com.exemplo.agerun.model.DashboardMetric
import com.exemplo.agerun.model.NoticeEntry
import com.exemplo.agerun.model.PaymentEntry
import com.exemplo.agerun.model.Student
import com.exemplo.agerun.model.StudentDraft
import com.exemplo.agerun.model.WorkoutDay
import com.exemplo.agerun.model.WorkoutEntry

@Stable
class AgeRunAppState(
    initialScreen: AppScreen,
    initialModule: BottomModule,
) {
    var currentScreen by mutableStateOf(initialScreen)
        private set

    var selectedModule by mutableStateOf(initialModule)
        private set

    var selectedStudentId by mutableStateOf<String?>(null)
        private set

    var showCreateStudentSheet by mutableStateOf(false)
        private set

    val students = mutableStateListOf<Student>().apply { addAll(sampleStudents()) }
    val workouts = mutableStateListOf<WorkoutEntry>().apply { addAll(sampleWorkouts()) }
    val notices = mutableStateListOf<NoticeEntry>().apply { addAll(sampleNotices()) }

    val selectedStudent: Student?
        get() = students.firstOrNull { it.id == selectedStudentId }

    val dashboardMetrics: List<DashboardMetric>
        get() = listOf(
            DashboardMetric("Ativos", students.count { it.status != "Inativo" }.toString()),
            DashboardMetric("Treinos", workouts.size.toString()),
            DashboardMetric(
                "Pendencias",
                students.sumOf { student ->
                    student.payments.count { payment -> payment.status != "Pago" }
                }.toString(),
            ),
        )

    val workoutDays: List<WorkoutDay>
        get() = workouts.take(4).mapIndexed { index, workout ->
            WorkoutDay(
                day = workout.date,
                value = workout.distanceKm.toString(),
                subtitle = workout.focus,
                highlight = index == 1,
            )
        }

    fun login() {
        currentScreen = AppScreen.Panel
        selectedModule = BottomModule.Home
    }

    fun selectModule(module: BottomModule) {
        selectedModule = module
        if (module != BottomModule.Students) selectedStudentId = null
    }

    fun selectStudent(studentId: String) {
        selectedStudentId = studentId
    }

    fun clearSelectedStudent() {
        selectedStudentId = null
    }

    fun openCreateStudentSheet() {
        showCreateStudentSheet = true
    }

    fun closeCreateStudentSheet() {
        showCreateStudentSheet = false
    }

    fun createStudent(draft: StudentDraft) {
        students.add(
            0,
            Student(
                id = "student-${students.size + 10}",
                name = draft.name,
                email = draft.email,
                phone = draft.phone,
                plan = draft.plan.ifBlank { "Plano inicial" },
                startDate = "Hoje",
                status = "Novo",
                monthlyKm = 0,
                activeWorksheet = "Sem planilha definida",
                coachNote = "",
                payments = listOf(
                    PaymentEntry(
                        id = "pay-${students.size + 50}",
                        label = "Primeira mensalidade",
                        amount = "R$ 0",
                        dueDate = "--/--",
                        status = "Pendente",
                    ),
                ),
            ),
        )
        showCreateStudentSheet = false
    }

    fun createNotice(title: String, body: String, pinned: Boolean) {
        notices.add(
            0,
            NoticeEntry(
                id = "notice-${notices.size + 10}",
                title = title,
                body = body,
                date = "Agora",
                pinned = pinned,
            ),
        )
    }

    fun createWorkout(entry: WorkoutEntry) {
        workouts.add(0, entry)
    }

    fun toggleStudentStatus(studentId: String) {
        updateStudent(studentId) { student ->
            student.copy(status = if (student.status == "Ativo") "Inativo" else "Ativo")
        }
    }

    fun updateStudentNote(studentId: String, note: String) {
        updateStudent(studentId) { it.copy(coachNote = note) }
    }

    fun updateStudentWorksheet(studentId: String, worksheet: String) {
        updateStudent(studentId) { it.copy(activeWorksheet = worksheet) }
    }

    fun addStudentPayment(studentId: String, label: String, amount: String, dueDate: String) {
        updateStudent(studentId) { student ->
            student.copy(
                payments = listOf(
                    PaymentEntry(
                        id = "pay-${student.payments.size + 100}",
                        label = label,
                        amount = amount,
                        dueDate = dueDate,
                        status = "Pendente",
                    ),
                ) + student.payments,
            )
        }
    }

    fun togglePaymentStatus(studentId: String, paymentId: String) {
        updateStudent(studentId) { student ->
            student.copy(
                payments = student.payments.map { payment ->
                    if (payment.id == paymentId) {
                        payment.copy(status = if (payment.status == "Pago") "Pendente" else "Pago")
                    } else {
                        payment
                    }
                },
            )
        }
    }

    private fun updateStudent(studentId: String, transform: (Student) -> Student) {
        val index = students.indexOfFirst { it.id == studentId }
        if (index >= 0) {
            students[index] = transform(students[index])
        }
    }
}

@Composable
fun rememberAgeRunAppState(): AgeRunAppState {
    val initialScreen = rememberSaveable { AppScreen.Login }
    val initialModule = rememberSaveable { BottomModule.Home }
    return remember { AgeRunAppState(initialScreen, initialModule) }
}
