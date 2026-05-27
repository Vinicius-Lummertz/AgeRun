package com.exemplo.agerun.data

import com.exemplo.agerun.model.AssessoriaProfile
import com.exemplo.agerun.model.CoachProfile
import com.exemplo.agerun.model.NoticeEntry
import com.exemplo.agerun.model.Student
import com.exemplo.agerun.model.WorkoutEntry

interface StudentRepository {
    val students: List<Student>
    fun add(student: Student)
    fun update(id: String, transform: (Student) -> Student)
    fun toggleStatus(id: String)
    fun updateNote(id: String, note: String)
    fun updateWorksheet(id: String, worksheet: String)
    fun addPayment(id: String, label: String, amount: String, dueDate: String)
    fun togglePaymentStatus(studentId: String, paymentId: String)
}

interface WorkoutRepository {
    val workouts: List<WorkoutEntry>
    fun add(workout: WorkoutEntry)
}

interface NoticeRepository {
    val notices: List<NoticeEntry>
    fun add(title: String, body: String, pinned: Boolean)
}

interface ProfileRepository {
    val coach: CoachProfile
    val assessoria: AssessoriaProfile
}
