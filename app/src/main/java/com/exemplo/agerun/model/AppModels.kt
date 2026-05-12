package com.exemplo.agerun.model

import com.google.android.gms.maps.model.LatLng

enum class AppScreen {
    Login,
    Panel,
}

enum class BottomModule(val label: String) {
    Students("Alunos"),
    Home("Inicio"),
    Workouts("Treinos"),
}

data class PaymentEntry(
    val id: String,
    val label: String,
    val amount: String,
    val dueDate: String,
    val status: String,
)

data class WorkoutEntry(
    val id: String,
    val title: String,
    val date: String,
    val focus: String,
    val distanceKm: Int,
    val pace: String,
    val notes: String,
    val assignedStudentIds: List<String>,
)

data class NoticeEntry(
    val id: String,
    val title: String,
    val body: String,
    val date: String,
    val pinned: Boolean,
)

data class Student(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val plan: String,
    val startDate: String,
    val status: String,
    val monthlyKm: Int,
    val activeWorksheet: String,
    val coachNote: String,
    val payments: List<PaymentEntry>,
)

data class DashboardMetric(
    val label: String,
    val value: String,
)

data class WorkoutDay(
    val day: String,
    val value: String,
    val subtitle: String,
    val highlight: Boolean = false,
)

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}

data class StudentDraft(
    val name: String,
    val email: String,
    val phone: String,
    val plan: String,
)
