package com.example.myapplication.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: String,
    val name: String,
    val email: String = "",
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

data class DashboardData(
    val students: List<Student>,
    val workouts: List<Workout>,
    val announcements: List<Announcement>,
    val events: List<Event>,
    val isDemo: Boolean,
    val message: String? = null
)
