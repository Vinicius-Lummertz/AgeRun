package com.exemplo.agerun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.exemplo.agerun.ui.theme.AgeRunTheme

private enum class AppTab(
    val label: String,
    val marker: String,
) {
    Inicio("Inicio", "I"),
    Recados("Recados", "R"),
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgeRunTheme {
                var authResponse by remember { mutableStateOf<AuthResponse?>(null) }
                var selectedTab by remember { mutableStateOf(AppTab.Inicio) }

                if (authResponse == null) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AuthScreen(
                            modifier = Modifier.padding(innerPadding),
                            onAuthenticated = {
                                authResponse = it
                                selectedTab = AppTab.Inicio
                            },
                        )
                    }
                } else {
                    val currentAuth = requireNotNull(authResponse)

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar {
                                AppTab.entries.forEach { tab ->
                                    NavigationBarItem(
                                        selected = selectedTab == tab,
                                        onClick = { selectedTab = tab },
                                        icon = { Text(text = tab.marker) },
                                        label = { Text(text = tab.label) },
                                    )
                                }
                            }
                        },
                    ) { innerPadding ->
                        when (selectedTab) {
                            AppTab.Inicio -> {
                                if (currentAuth.user.role == "professor") {
                                    ProfessorHomeScreen(
                                        authResponse = currentAuth,
                                        modifier = Modifier.padding(innerPadding),
                                        onOpenRecados = { selectedTab = AppTab.Recados },
                                    )
                                } else {
                                    AgeRunHomeScreen(modifier = Modifier.padding(innerPadding))
                                }
                            }

                            AppTab.Recados -> {
                                RecadosScreen(
                                    authResponse = currentAuth,
                                    modifier = Modifier.padding(innerPadding),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
