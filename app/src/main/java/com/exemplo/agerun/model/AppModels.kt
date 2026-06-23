package com.exemplo.agerun.model

import com.google.android.gms.maps.model.LatLng

enum class AppScreen { Login, App }

enum class Role { Professor, Aluno }

enum class CoachTab(val label: String) {
    Treinos("Treinos"),
    Alunos("Alunos"),
    Inicio("Início"),
    Pagamentos("Pagamentos"),
    Config("Config"),
}

enum class StudentTab(val label: String) {
    Inicio("Início"),
    Treinos("Treinos"),
    Mensalidade("Mensalidade"),
    Perfil("Perfil"),
}

enum class WorkoutType(val label: String) {
    Rodagem("Rodagem"),
    Intervalado("Intervalado"),
    Tempo("Tempo"),
    Longao("Longão"),
    Regenerativo("Regenerativo"),
    Fortalecimento("Fortalecimento"),
}

enum class Intensity(val label: String) {
    Leve("Leve"),
    Moderado("Moderado"),
    Forte("Forte"),
}

data class WorkoutBlock(
    val label: String,
    val detail: String,
    val distanceKm: Double? = null,
    val pace: String? = null,
)

data class PaymentEntry(
    val id: String,
    val label: String,
    val amount: String,
    val dueDate: String,
    val status: String,
)

data class WorkoutCompletion(
    val studentId: String,
    val status: String,        // "Pendente" | "Concluído"
    val feeling: String? = null,
    val actualKm: Double? = null,
    val cadence: Int? = null,
)

data class WorkoutEntry(
    val id: String,
    val title: String,
    val type: WorkoutType,
    val intensity: Intensity,
    val date: String,
    val focus: String,
    val targetPace: String,
    val notes: String,
    val blocks: List<WorkoutBlock>,
    val assignedStudentIds: List<String>,
    val completions: List<WorkoutCompletion> = emptyList(),
)

fun WorkoutEntry.totalDistanceKm(): Double =
    blocks.sumOf { it.distanceKm ?: 0.0 }

data class NoticeEntry(
    val id: String,
    val title: String,
    val body: String,
    val date: String,
    val pinned: Boolean,
)

data class StudentGroup(
    val id: String,
    val name: String,
    val studentIds: List<String>,
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
    val photoUrl: String? = null,
    val groupId: String? = null,
    val payments: List<PaymentEntry>,
)

data class CoachProfile(
    val name: String,
    val specialty: String,
    val bio: String,
    val email: String,
    val phone: String,
    val photoUrl: String? = null,
)

data class AssessoriaProfile(
    val name: String,
    val plans: List<String>,
)

data class AppPreferences(
    val darkTheme: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val language: String = "Português",
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

data class Run(
    val id: String,
    val studentId: String,
    val date: String,
    val distanceKm: Double,
    val durationSec: Int,
    val pace: String,
    val cadence: Int,
    val path: List<LocationPoint>,
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
