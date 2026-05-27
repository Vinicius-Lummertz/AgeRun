package com.exemplo.agerun.ui.screens.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.state.AgeRunAppState
import com.exemplo.agerun.ui.components.AgeCard
import com.exemplo.agerun.ui.components.AvatarPhoto
import com.exemplo.agerun.ui.components.OutlineActionButton
import com.exemplo.agerun.ui.components.SectionHeader
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary

@Composable
fun CoachSettingsScreen(appState: AgeRunAppState) {
    val coach = appState.profileRepository.coach
    val assessoria = appState.profileRepository.assessoria
    SectionHeader("Configurações")
    Spacer(Modifier.height(16.dp))

    AgeCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            AvatarPhoto(size = 64.dp)
            Column {
                Text(coach.name, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text(coach.specialty, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(12.dp))
        SettingLine("E-mail", coach.email)
        SettingLine("Telefone", coach.phone)
        SettingLine("Bio", coach.bio)
    }
    Spacer(Modifier.height(16.dp))
    AgeCard {
        Text("Dados da assessoria", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        SettingLine("Nome", assessoria.name)
        SettingLine("Planos", assessoria.plans.joinToString(", "))
    }
    Spacer(Modifier.height(16.dp))
    AgeCard {
        Text("Preferências", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        SettingLine("Tema", "Escuro")
        SettingLine("Notificações", "Ativadas")
        SettingLine("Idioma", "Português")
    }
    Spacer(Modifier.height(16.dp))
    AgeCard {
        Text("Conta", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        SettingLine("Sobre", "AgeRun v1.0")
    }
    Spacer(Modifier.height(16.dp))
    OutlineActionButton(modifier = Modifier.fillMaxWidth(), text = "Sair da conta", onClick = appState::logout)
}

@Composable
private fun SettingLine(label: String, value: String) {
    Column {
        Text(label, color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(10.dp))
    }
}
