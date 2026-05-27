package com.exemplo.agerun

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.exemplo.agerun.model.AppScreen
import com.exemplo.agerun.model.Role
import com.exemplo.agerun.state.rememberAgeRunAppState
import com.exemplo.agerun.ui.components.AppBackground
import com.exemplo.agerun.ui.screens.LoginScreen
import com.exemplo.agerun.ui.screens.coach.CoachApp
import com.exemplo.agerun.ui.screens.student.StudentApp

@Composable
fun AgeRunApp() {
    val appState = rememberAgeRunAppState()
    Surface(modifier = Modifier.fillMaxSize()) {
        when (appState.currentScreen) {
            AppScreen.Login -> LoginScreen(onLogin = appState::login)
            AppScreen.App -> AppBackground {
                when (appState.role) {
                    Role.Professor -> CoachApp(appState = appState)
                    Role.Aluno -> StudentApp(appState = appState)
                    null -> LoginScreen(onLogin = appState::login)
                }
            }
        }
    }
}
