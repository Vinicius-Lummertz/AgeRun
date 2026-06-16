package com.example.myapplication.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: String,
    val name: String,
    val email: String = "",
    val phone: String = "",
    val routine: String = "",
    @SerialName("plan_name") val planName: String = "Sem plano",
    val status: String = "active"
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
    val location: String? = null
)

enum class CommunityPostType {
    POST,
    POLL,
    CHALLENGE
}

data class CommunityPost(
    val id: String,
    val type: CommunityPostType,
    val title: String = "",
    val content: String,
    val target: String = "groups",
    val authorName: String = "AgeGo",
    val linkedWorkoutId: String? = null,
    val pollOptions: List<String> = emptyList(),
    val commentThreads: List<CommunityComment> = emptyList(),
    val mediaLabel: String? = null,
    val gifLabel: String? = null,
    val generatedImagePrompt: String? = null,
    val scheduledAt: String? = null,
    val location: String? = null,
    val contentWarning: String? = null,
    val liked: Boolean = false,
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0
)

data class CommunityComment(
    val id: String,
    val authorName: String,
    val content: String,
    val liked: Boolean = false,
    val likes: Int = 0,
    val replies: List<CommunityComment> = emptyList()
)

data class DashboardData(
    val students: List<Student>,
    val workouts: List<Workout>,
    val announcements: List<Announcement>,
    val events: List<Event>,
    val isDemo: Boolean,
    val message: String? = null
)
