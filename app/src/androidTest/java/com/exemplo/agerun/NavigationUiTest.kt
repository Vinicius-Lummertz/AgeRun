package com.exemplo.agerun

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.exemplo.agerun.ui.theme.AgeRunTheme
import org.junit.Rule
import org.junit.Test

class NavigationUiTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun login_como_professor_mostra_home_e_abre_config_pelo_avatar() {
        rule.setContent { AgeRunTheme { AgeRunApp() } }

        rule.onNodeWithText("Sou professor").performClick()
        rule.onNodeWithText("Entrar na plataforma").performClick()

        // Home do professor mostra o nome do coach
        rule.onNodeWithText("Bem-vindo de volta").assertIsDisplayed()

        // Avatar (descrição "Perfil") abre Configurações
        rule.onNodeWithContentDescription("Perfil").performClick()
        rule.onNodeWithText("Dados da assessoria").assertIsDisplayed()
    }

    @Test
    fun login_como_aluno_mostra_treino_de_hoje() {
        rule.setContent { AgeRunTheme { AgeRunApp() } }
        rule.onNodeWithText("Sou aluno").performClick()
        rule.onNodeWithText("Entrar na plataforma").performClick()
        rule.onNodeWithText("Treino de hoje").assertIsDisplayed()
    }
}
