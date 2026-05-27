package com.exemplo.agerun.data

import androidx.compose.runtime.mutableStateListOf
import com.exemplo.agerun.model.AssessoriaProfile
import com.exemplo.agerun.model.CoachProfile
import com.exemplo.agerun.model.NoticeEntry
import com.exemplo.agerun.model.PaymentEntry
import com.exemplo.agerun.model.Student
import com.exemplo.agerun.model.WorkoutEntry

class InMemoryStudentRepository : StudentRepository {
    private val _students = mutableStateListOf<Student>().apply { addAll(sampleStudents()) }
    override val students: List<Student> get() = _students

    override fun add(student: Student) { _students.add(0, student) }

    override fun update(id: String, transform: (Student) -> Student) {
        val index = _students.indexOfFirst { it.id == id }
        if (index >= 0) _students[index] = transform(_students[index])
    }

    override fun toggleStatus(id: String) = update(id) {
        it.copy(status = if (it.status == "Ativo") "Inativo" else "Ativo")
    }

    override fun updateNote(id: String, note: String) = update(id) { it.copy(coachNote = note) }

    override fun updateWorksheet(id: String, worksheet: String) =
        update(id) { it.copy(activeWorksheet = worksheet) }

    override fun addPayment(id: String, label: String, amount: String, dueDate: String) =
        update(id) { student ->
            student.copy(
                payments = listOf(
                    PaymentEntry("pay-${student.payments.size + 100}", label, amount, dueDate, "Pendente"),
                ) + student.payments,
            )
        }

    override fun togglePaymentStatus(studentId: String, paymentId: String) =
        update(studentId) { student ->
            student.copy(
                payments = student.payments.map { payment ->
                    if (payment.id == paymentId) {
                        payment.copy(status = if (payment.status == "Pago") "Pendente" else "Pago")
                    } else payment
                },
            )
        }
}

class InMemoryWorkoutRepository : WorkoutRepository {
    private val _workouts = mutableStateListOf<WorkoutEntry>().apply { addAll(sampleWorkouts()) }
    override val workouts: List<WorkoutEntry> get() = _workouts
    override fun add(workout: WorkoutEntry) { _workouts.add(0, workout) }
}

class InMemoryNoticeRepository : NoticeRepository {
    private val _notices = mutableStateListOf<NoticeEntry>().apply { addAll(sampleNotices()) }
    override val notices: List<NoticeEntry> get() = _notices
    override fun add(title: String, body: String, pinned: Boolean) {
        _notices.add(0, NoticeEntry("notice-${_notices.size + 10}", title, body, "Agora", pinned))
    }
}

class InMemoryProfileRepository : ProfileRepository {
    override val coach: CoachProfile = sampleCoach()
    override val assessoria: AssessoriaProfile = sampleAssessoria()
}
