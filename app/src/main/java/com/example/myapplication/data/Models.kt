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
    val accessCodeExpiresAt: String = ""
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

@Serializable
enum class CommunityPostType {
    POST,
    POLL,
    CHALLENGE
}

@Serializable
data class CommunityPost(
    val id: String,
    val type: CommunityPostType,
    val title: String = "",
    val content: String,
    val target: String = "groups",
    @SerialName("authorName") val authorName: String = "Usuario",
    @SerialName("authorAvatarUrl") val authorAvatarUrl: String = "",
    @SerialName("linkedWorkoutId") val linkedWorkoutId: String? = null,
    @SerialName("pollOptions") val pollOptions: List<String> = emptyList(),
    @SerialName("commentThreads") val commentThreads: List<CommunityComment> = emptyList(),
    @SerialName("mediaLabel") val mediaLabel: String? = null,
    @SerialName("gifLabel") val gifLabel: String? = null,
    @SerialName("generatedImagePrompt") val generatedImagePrompt: String? = null,
    @SerialName("scheduledAt") val scheduledAt: String? = null,
    val location: String? = null,
    @SerialName("contentWarning") val contentWarning: String? = null,
    val liked: Boolean = false,
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0
)

@Serializable
data class CommunityComment(
    val id: String,
    @SerialName("authorName") val authorName: String,
    @SerialName("authorAvatarUrl") val authorAvatarUrl: String = "",
    val content: String,
    val liked: Boolean = false,
    val likes: Int = 0,
    val replies: List<CommunityComment> = emptyList()
)

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
data class DashboardData(
    val students: List<Student>,
    val workouts: List<Workout>,
    val announcements: List<Announcement>,
    val events: List<Event>,
    val groups: List<DirectoryItem> = emptyList(),
    val routines: List<DirectoryItem> = emptyList(),
    val communityPosts: List<CommunityPost> = emptyList(),
    val trainingNow: List<TrainingNowUser> = emptyList(),
    val message: String? = null
)
