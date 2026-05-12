package com.exemplo.agerun

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.exemplo.agerun.model.BottomModule
import com.exemplo.agerun.state.rememberAgeRunAppState
import com.exemplo.agerun.ui.components.AppBackground
import com.exemplo.agerun.ui.components.BottomModuleBar
import com.exemplo.agerun.ui.components.FloatingActionIcon
import com.exemplo.agerun.ui.screens.CoachPanelScreen
import com.exemplo.agerun.ui.screens.LoginScreen
import com.exemplo.agerun.ui.screens.StudentCreationSheet

@Composable
fun AgeRunApp() {
    val appState = rememberAgeRunAppState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (appState.currentScreen) {
            com.exemplo.agerun.model.AppScreen.Login -> LoginScreen(
                onLogin = appState::login,
            )

            com.exemplo.agerun.model.AppScreen.Panel -> {
                AppBackground {
                    CoachPanelScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(WindowInsets.statusBars.asPaddingValues())
                            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
                        appState = appState,
                    )
                }
            }
        }
    }
}
