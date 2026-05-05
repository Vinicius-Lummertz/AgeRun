package com.exemplo.agerun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.ui.theme.AgeRunTheme

private enum class AppTab(
    val label: String,
    val marker: String,
) {
    Inicio("Inicio", "In"),
    Escalas("Escalas", "Es"),
    Recados("Recados", "Re"),
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
                        topBar = {
                            AppSessionHeader(
                                authResponse = currentAuth,
                                onLogout = {
                                    authResponse = null
                                    selectedTab = AppTab.Inicio
                                },
                            )
                        },
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
                                        onOpenEscalas = { selectedTab = AppTab.Escalas },
                                        onOpenRecados = { selectedTab = AppTab.Recados },
                                    )
                                } else {
                                    AgeRunHomeScreen(
                                        authResponse = currentAuth,
                                        modifier = Modifier.padding(innerPadding),
                                        onOpenEscalas = { selectedTab = AppTab.Escalas },
                                        onOpenRecados = { selectedTab = AppTab.Recados },
                                    )
                                }
                            }

                            AppTab.Escalas -> {
                                EscalasScreen(
                                    authResponse = currentAuth,
                                    modifier = Modifier.padding(innerPadding),
                                )
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

@Composable
private fun AppSessionHeader(
    authResponse: AuthResponse,
    onLogout: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "AgeRun",
                color = Color(0xFF10221C),
                fontWeight = FontWeight.Black,
            )
            Text(
                text = authResponse.user.role,
                color = Color(0xFF587069),
            )
        }

        TextButton(onClick = onLogout) {
            Text(
                text = "Sair",
                color = Color(0xFF0B6B3A),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
