package com.exemplo.agerun.data

import com.exemplo.agerun.model.AssessoriaProfile
import com.exemplo.agerun.model.CoachProfile
import com.exemplo.agerun.model.Intensity
import com.exemplo.agerun.model.NoticeEntry
import com.exemplo.agerun.model.PaymentEntry
import com.exemplo.agerun.model.Student
import com.exemplo.agerun.model.WorkoutBlock
import com.exemplo.agerun.model.WorkoutCompletion
import com.exemplo.agerun.model.WorkoutEntry
import com.exemplo.agerun.model.WorkoutType

fun sampleCoach(): CoachProfile = CoachProfile(
    name = "Prof. Rafael",
    specialty = "Corrida de rua • 5K a maratona",
    bio = "Treinador certificado, foco em consistência e progressão.",
    email = "rafael@agerun.com",
    phone = "(11) 99999-0000",
)

fun sampleAssessoria(): AssessoriaProfile = AssessoriaProfile(
    name = "AgeRun Assessoria",
    plans = listOf("Planilha 5K", "Assessoria Premium", "Turma Iniciantes"),
)

fun sampleStudents(): List<Student> = listOf(
    Student(
        id = "student-1", name = "Joao Pedro", email = "joao@agerun.com",
        phone = "(11) 99999-1001", plan = "Assessoria Premium", startDate = "02 Mai",
        status = "Ativo", monthlyKm = 46, activeWorksheet = "Planilha 10K Maio",
        coachNote = "Responde bem aos treinos de ritmo.",
        payments = listOf(
            PaymentEntry("pay-1", "Mensalidade Maio", "R$ 149", "10/05", "Pago"),
            PaymentEntry("pay-2", "Mensalidade Junho", "R$ 149", "10/06", "Pendente"),
        ),
    ),
    Student(
        id = "student-2", name = "Marina Costa", email = "marina@agerun.com",
        phone = "(11) 98888-2402", plan = "Planilha 5K", startDate = "14 Abr",
        status = "Ativo", monthlyKm = 32, activeWorksheet = "Planilha Base Junho",
        coachNote = "Foco em consistência e fortalecimento.",
        payments = listOf(
            PaymentEntry("pay-3", "Mensalidade Maio", "R$ 99", "05/05", "Pago"),
        ),
    ),
    Student(
        id = "student-3", name = "Lucas Melo", email = "lucas@agerun.com",
        phone = "(11) 97777-3003", plan = "Turma Iniciantes", startDate = "07 Mai",
        status = "Novo", monthlyKm = 18, activeWorksheet = "Primeira semana",
        coachNote = "Atleta novo. Priorizar adaptação.",
        payments = listOf(
            PaymentEntry("pay-4", "Matricula", "R$ 89", "08/05", "Pago"),
        ),
    ),
)

fun sampleWorkouts(): List<WorkoutEntry> = listOf(
    WorkoutEntry(
        id = "workout-1", title = "Rodagem controlada", type = WorkoutType.Rodagem,
        intensity = Intensity.Leve, date = "Seg 12", focus = "Base",
        targetPace = "5:30/km", notes = "Fechar leve e solto.",
        blocks = listOf(
            WorkoutBlock("Aquecimento", "10 min trote", 1.5),
            WorkoutBlock("Principal", "Rodagem contínua", 5.0),
            WorkoutBlock("Volta à calma", "5 min", 1.5),
        ),
        assignedStudentIds = listOf("student-1", "student-2"),
        completions = listOf(WorkoutCompletion("student-2", "Concluído", "Tranquilo", 8.0, 168)),
    ),
    WorkoutEntry(
        id = "workout-2", title = "Intervalado 6x400", type = WorkoutType.Intervalado,
        intensity = Intensity.Forte, date = "Ter 13", focus = "Velocidade",
        targetPace = "4:35/km", notes = "Recuperação completa entre tiros.",
        blocks = listOf(
            WorkoutBlock("Aquecimento", "15 min", 2.5),
            WorkoutBlock("Principal", "6x400m forte / 200m trote", 3.6),
            WorkoutBlock("Volta à calma", "10 min", 1.5),
        ),
        assignedStudentIds = listOf("student-1"),
    ),
    WorkoutEntry(
        id = "workout-3", title = "Longão progressivo", type = WorkoutType.Longao,
        intensity = Intensity.Moderado, date = "Sab 17", focus = "Resistência",
        targetPace = "5:45/km", notes = "Últimos 3km em progressão.",
        blocks = listOf(
            WorkoutBlock("Base", "11 km constante", 11.0),
            WorkoutBlock("Progressão", "3 km acelerando", 3.0),
        ),
        assignedStudentIds = listOf("student-1", "student-3"),
    ),
)

fun sampleNotices(): List<NoticeEntry> = listOf(
    NoticeEntry("notice-1", "Treino coletivo domingo",
        "Encontro às 7h no parque com aquecimento conjunto.", "Hoje", true),
    NoticeEntry("notice-2", "Atualização das planilhas",
        "As planilhas de junho foram publicadas.", "Ontem", false),
)
