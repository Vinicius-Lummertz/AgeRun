package com.exemplo.agerun.data

import com.exemplo.agerun.model.NoticeEntry
import com.exemplo.agerun.model.PaymentEntry
import com.exemplo.agerun.model.Student
import com.exemplo.agerun.model.WorkoutEntry

fun sampleStudents(): List<Student> = listOf(
    Student(
        id = "student-1",
        name = "Joao Pedro",
        email = "joao@agerun.com",
        phone = "(11) 99999-1001",
        plan = "Assessoria Premium",
        startDate = "02 Mai",
        status = "Ativo",
        monthlyKm = 46,
        activeWorksheet = "Planilha 10K Maio",
        coachNote = "Responde bem aos treinos de ritmo. Ajustar longao na proxima semana.",
        payments = listOf(
            PaymentEntry("pay-1", "Mensalidade Maio", "R$ 149", "10/05", "Pago"),
            PaymentEntry("pay-2", "Mensalidade Junho", "R$ 149", "10/06", "Pendente"),
        ),
    ),
    Student(
        id = "student-2",
        name = "Marina Costa",
        email = "marina@agerun.com",
        phone = "(11) 98888-2402",
        plan = "Planilha 5K",
        startDate = "14 Abr",
        status = "Em dia",
        monthlyKm = 32,
        activeWorksheet = "Planilha Base Junho",
        coachNote = "Foco em consistencia e fortalecimento.",
        payments = listOf(
            PaymentEntry("pay-3", "Mensalidade Maio", "R$ 99", "05/05", "Pago"),
        ),
    ),
    Student(
        id = "student-3",
        name = "Lucas Melo",
        email = "lucas@agerun.com",
        phone = "(11) 97777-3003",
        plan = "Turma Iniciantes",
        startDate = "07 Mai",
        status = "Novo",
        monthlyKm = 18,
        activeWorksheet = "Primeira semana",
        coachNote = "Atleta novo. Priorizar adaptacao e frequencia.",
        payments = listOf(
            PaymentEntry("pay-4", "Matricula", "R$ 89", "08/05", "Pago"),
        ),
    ),
)

fun sampleWorkouts(): List<WorkoutEntry> = listOf(
    WorkoutEntry(
        id = "workout-1",
        title = "Rodagem controlada",
        date = "Seg 12",
        focus = "Base",
        distanceKm = 8,
        pace = "5:30/km",
        notes = "Fechar leve e solto.",
        assignedStudentIds = listOf("student-1", "student-2"),
    ),
    WorkoutEntry(
        id = "workout-2",
        title = "Intervalado 6x400",
        date = "Ter 13",
        focus = "Velocidade",
        distanceKm = 6,
        pace = "4:35/km",
        notes = "Recuperacao completa entre tiros.",
        assignedStudentIds = listOf("student-1"),
    ),
    WorkoutEntry(
        id = "workout-3",
        title = "Longao progressivo",
        date = "Sab 17",
        focus = "Resistencia",
        distanceKm = 14,
        pace = "5:45/km",
        notes = "Ultimos 3km em progressao.",
        assignedStudentIds = listOf("student-1", "student-3"),
    ),
)

fun sampleNotices(): List<NoticeEntry> = listOf(
    NoticeEntry(
        id = "notice-1",
        title = "Treino coletivo domingo",
        body = "Encontro as 7h no parque com aquecimento conjunto para toda a turma.",
        date = "Hoje",
        pinned = true,
    ),
    NoticeEntry(
        id = "notice-2",
        title = "Atualizacao das planilhas",
        body = "As planilhas de junho foram publicadas. Revisem seus perfis individuais.",
        date = "Ontem",
        pinned = false,
    ),
)
