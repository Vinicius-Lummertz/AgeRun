package com.exemplo.agerun

import com.exemplo.agerun.data.InMemoryStudentRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositoriesTest {
    @Test
    fun students_inicia_com_dados_de_exemplo() {
        val repo = InMemoryStudentRepository()
        assertEquals(3, repo.students.size)
    }

    @Test
    fun togglePaymentStatus_alterna_status() {
        val repo = InMemoryStudentRepository()
        repo.togglePaymentStatus("student-1", "pay-2")
        val student = repo.students.first { it.id == "student-1" }
        assertEquals("Pago", student.payments.first { it.id == "pay-2" }.status)
    }

    @Test
    fun add_insere_no_topo() {
        val repo = InMemoryStudentRepository()
        repo.add(
            com.exemplo.agerun.model.Student(
                id = "x", name = "Novo", email = "", phone = "", plan = "P",
                startDate = "Hoje", status = "Novo", monthlyKm = 0,
                activeWorksheet = "-", coachNote = "", payments = emptyList(),
            ),
        )
        assertEquals("Novo", repo.students.first().name)
        assertEquals(4, repo.students.size)
        assertTrue(repo.students.any { it.id == "x" })
    }
}
