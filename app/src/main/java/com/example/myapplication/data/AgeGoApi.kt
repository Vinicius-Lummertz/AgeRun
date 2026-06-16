package com.example.myapplication.data

import com.example.myapplication.BuildConfig

/**
 * Endpoints administrativos disponíveis no servidor Node.
 * Todas as chamadas devem enviar o access token do instrutor no header Bearer.
 */
object AgeGoApi {
    val baseUrl: String = BuildConfig.AGEGO_API_URL.trimEnd('/')

    fun studentsUrl() = "$baseUrl/students"
    fun workoutsUrl() = "$baseUrl/workouts"
    fun announcementsUrl() = "$baseUrl/announcements"
    fun groupsUrl() = "$baseUrl/groups"
}
