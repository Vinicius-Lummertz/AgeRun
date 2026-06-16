package com.example.myapplication.data

import com.example.myapplication.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

interface AgeGoRepository {
    suspend fun loadDashboard(): DashboardData
}

class SupabaseAgeGoRepository(
    private val client: SupabaseClient
) : AgeGoRepository {
    override suspend fun loadDashboard(): DashboardData {


        val students = client.postgrest.rpc("get_instructor_students").decodeList<Student>()
        val workouts = client.from("workouts").select().decodeList<Workout>()
        val announcements = client.from("announcements").select().decodeList<Announcement>()
        val events = client.from("events").select().decodeList<Event>()
        return DashboardData(students, workouts, announcements, events, isDemo = false)
    }
}

class DemoAgeGoRepository(
    private val reason: String? = null
) : AgeGoRepository {
    override suspend fun loadDashboard() = DashboardData(
        students = listOf(
            Student("1", "Marina Alves", "marina@agego.com", "Performance", "active"),
            Student("2", "Rafael Souza", "rafael@agego.com", "Essencial", "pending_payment"),
            Student("3", "Camila Lima", "camila@agego.com", "Performance", "active"),
            Student("4", "Bruno Martins", "bruno@agego.com", "Base", "inactive")
        ),
        workouts = listOf(
            Workout("1", "Intervalado 5 km", "Séries de velocidade e recuperação", "directions_run", "active"),
            Workout("2", "Longão progressivo", "Aumento gradual de ritmo", "route", "active"),
            Workout("3", "Força para corrida", "Mobilidade e estabilidade", "fitness_center", "draft")
        ),
        announcements = listOf(
            Announcement("1", "Treino de sábado confirmado às 6h30 no parque.", "Hoje, 09:10"),
            Announcement("2", "Lembrem de atualizar o resultado do treino semanal.", "Ontem, 18:40", "group")
        ),
        events = listOf(
            Event("1", "Treino coletivo", "Rodagem leve em grupo", demoDate(0, 6, 30), "Parque Central"),
            Event("2", "Avaliação de pace", "Teste de 5 km", demoDate(0, 18, 0), "Pista Municipal"),
            Event("3", "Longão da assessoria", "Percurso de 12 km", demoDate(1, 6, 0), "Praça das Águas"),
            Event("4", "Mobilidade", "Sessão orientada", demoDate(3, 19, 0), "Studio AgeGo")
        ),
        isDemo = true,
        message = reason ?: "Modo demonstração. Configure o Supabase e faça login para carregar dados reais."
    )
}

private fun demoDate(daysFromToday: Int, hour: Int, minute: Int): String {
    val calendar = java.util.Calendar.getInstance().apply {
        add(java.util.Calendar.DAY_OF_YEAR, daysFromToday)
        set(java.util.Calendar.HOUR_OF_DAY, hour)
        set(java.util.Calendar.MINUTE, minute)
    }
    return java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.US).format(calendar.time)
}

object RepositoryProvider {
    fun create(): AgeGoRepository {
        if (BuildConfig.SUPABASE_URL.isBlank() || BuildConfig.SUPABASE_ANON_KEY.isBlank()) {
            return DemoAgeGoRepository()
        }

        val client = createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
            install(Auth)
            install(Postgrest)
        }
        return FallbackRepository(SupabaseAgeGoRepository(client))
    }
}

private class FallbackRepository(
    private val remote: AgeGoRepository
) : AgeGoRepository {
    override suspend fun loadDashboard(): DashboardData = runCatching {
        remote.loadDashboard()
    }.getOrElse { error ->
        DemoAgeGoRepository(error.message).loadDashboard()
    }
}
