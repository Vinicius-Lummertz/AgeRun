package com.exemplo.agerun

import com.exemplo.agerun.model.AppScreen
import com.exemplo.agerun.model.CoachTab
import com.exemplo.agerun.model.Role
import com.exemplo.agerun.model.StudentTab
import com.exemplo.agerun.state.AgeRunAppState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AgeRunAppStateTest {
    private fun newState() = AgeRunAppState()

    @Test
    fun comeca_no_login() {
        assertEquals(AppScreen.Login, newState().currentScreen)
    }

    @Test
    fun login_professor_abre_app_professor_no_inicio() {
        val s = newState()
        s.login(Role.Professor)
        assertEquals(AppScreen.App, s.currentScreen)
        assertEquals(Role.Professor, s.role)
        assertEquals(CoachTab.Inicio, s.coachTab)
    }

    @Test
    fun login_aluno_abre_app_aluno() {
        val s = newState()
        s.login(Role.Aluno)
        assertEquals(Role.Aluno, s.role)
        assertEquals(StudentTab.Inicio, s.studentTab)
    }

    @Test
    fun avatar_abre_config() {
        val s = newState()
        s.login(Role.Professor)
        s.openSettingsFromAvatar()
        assertEquals(CoachTab.Config, s.coachTab)
    }

    @Test
    fun abrir_e_fechar_perfil_de_aluno() {
        val s = newState()
        s.login(Role.Professor)
        s.openStudent("student-1")
        assertEquals("student-1", s.selectedStudentId)
        s.closeStudent()
        assertNull(s.selectedStudentId)
    }

    @Test
    fun trocar_de_aba_limpa_aluno_selecionado() {
        val s = newState()
        s.login(Role.Professor)
        s.openStudent("student-1")
        s.selectCoachTab(CoachTab.Treinos)
        assertNull(s.selectedStudentId)
    }

    @Test
    fun logout_volta_ao_login() {
        val s = newState()
        s.login(Role.Professor)
        s.logout()
        assertEquals(AppScreen.Login, s.currentScreen)
        assertNull(s.role)
    }
}
