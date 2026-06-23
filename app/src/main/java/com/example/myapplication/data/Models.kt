package com.example.myapplication.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: String,
    val name: String,
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val routine: String = "",
    @SerialName("plan_name") val planName: String = "Sem plano",
    val status: String = "active",
    @SerialName("billingDay") val billingDay: Int = 5,
    @SerialName("monthlyFee") val monthlyFee: String = "",
    val accessCode: String = "",
    val accessCodeExpiresAt: String = "",
    val workoutsCompleted: Int = 0,
    val workoutsTotal: Int = 0,
    val performanceDeltaPercent: Int? = null,
    val paymentStatus: String = "paid",
    val daysOverdue: Int = 0,
    val paymentProofUrl: String? = null,
    val paymentProofRejectionReason: String? = null,
    val asaasSubscriptionActive: Boolean = false
)

@Serializable
data class Workout(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("icon_name") val iconName: String? = null,
    val status: String = "draft"
)

@Serializable
data class Announcement(
    val id: String,
    val content: String,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("target_type") val targetType: String = "all"
)

@Serializable
data class Event(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("event_date") val eventDate: String,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val groupStatus: String = "waiting",
    val checkedIn: Boolean = false,
    val attendees: List<EventAttendee> = emptyList(),
    val results: List<EventRunResult> = emptyList(),
    val coverPhotoUrl: String? = null
)

@Serializable
data class EventAttendee(
    val studentId: String,
    val name: String,
    val avatarUrl: String = ""
)

@Serializable
data class EventRunResult(
    val studentId: String,
    val name: String,
    val elapsedMs: Long,
    val distanceMeters: Double,
    val paceSecondsPerKm: Double,
    val routePoints: List<WorkoutRoutePoint> = emptyList()
)

@Serializable
enum class SyncStatus {
    SYNCED,
    PENDING,
    FAILED
}

@Serializable
data class TrainingNowUser(
    val id: String,
    val name: String,
    val avatarUrl: String = "",
    val lastSeenAt: String = ""
)

@Serializable
data class DirectoryItem(
    val id: String,
    val name: String,
    val status: String = "active",
    val description: String = "",
    val studentIds: List<String> = emptyList()
)

@Serializable
data class RunHistoryEntry(
    val id: String,
    val routineName: String = "",
    val elapsedMs: Long = 0,
    val distanceMeters: Double = 0.0,
    val paceSecondsPerKm: Double = 0.0,
    val routePoints: List<WorkoutRoutePoint> = emptyList(),
    val completedAt: String? = null
)

@Serializable
data class Challenge(
    val id: String,
    val name: String,
    val description: String = "",
    val targetType: String = "distance",
    val targetValue: Double = 0.0,
    val completions: Int = 0,
    val totalDistanceMeters: Double = 0.0,
    val totalElapsedMs: Long = 0,
    val myCompleted: Boolean = false
)

@Serializable
data class DashboardData(
    val students: List<Student>,
    val workouts: List<Workout>,
    val announcements: List<Announcement>,
    val events: List<Event>,
    val routines: List<DirectoryItem> = emptyList(),
    val trainingNow: List<TrainingNowUser> = emptyList(),
    val instructorPixKey: String = "",
    val instructorName: String = "",
    val instructorAvatarUrl: String = "",
    val runHistory: List<RunHistoryEntry> = emptyList(),
    val challenges: List<Challenge> = emptyList(),
    val message: String? = null
)
