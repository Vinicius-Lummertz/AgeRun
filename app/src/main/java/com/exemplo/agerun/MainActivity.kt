package com.exemplo.agerun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

private val AppDark = Color(0xFF08001F)
private val AppPanel = Color(0xFF14004A)
private val AppLime = Color(0xFFA7E22E)
private val AppLimeDark = Color(0xFF4D850B)
private val AppText = Color(0xFFF5F1FF)
private val AppMuted = Color(0xFFA8A0C2)

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
                        containerColor = AppDark,
                        bottomBar = {
                            PrototypeBottomNav(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it },
                            )
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
                                        onLogout = {
                                            authResponse = null
                                            selectedTab = AppTab.Inicio
                                        },
                                    )
                                } else {
                                    AgeRunHomeScreen(
                                        authResponse = currentAuth,
                                        modifier = Modifier.padding(innerPadding),
                                        onOpenEscalas = { selectedTab = AppTab.Escalas },
                                        onOpenRecados = { selectedTab = AppTab.Recados },
                                        onLogout = {
                                            authResponse = null
                                            selectedTab = AppTab.Inicio
                                        },
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
private fun PrototypeBottomNav(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppDark)
            .padding(horizontal = 34.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = AppPanel,
            shape = RoundedCornerShape(26.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppTab.entries.forEachIndexed { index, tab ->
                    if (index == 1) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(Color.Transparent, CircleShape)
                                .clickable { onTabSelected(tab) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Surface(
                                modifier = Modifier.size(22.dp),
                                color = if (selectedTab == tab) AppLime else Color.Transparent,
                                shape = CircleShape,
                                border = androidx.compose.foundation.BorderStroke(1.dp, AppText),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "+",
                                        color = if (selectedTab == tab) AppDark else AppText,
                                        fontWeight = FontWeight.Black,
                                    )
                                }
                            }
                        }
                    } else {
                        val selected = selectedTab == tab
                        Surface(
                            modifier = Modifier.clickable { onTabSelected(tab) },
                            color = if (selected) AppLime else Color.Transparent,
                            shape = RoundedCornerShape(20.dp),
                            border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, AppText.copy(alpha = 0.65f)),
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                text = tab.label,
                                color = if (selected) AppDark else AppText,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}
