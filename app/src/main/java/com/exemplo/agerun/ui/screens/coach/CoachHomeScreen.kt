package com.exemplo.agerun.ui.screens.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.exemplo.agerun.state.AgeRunAppState
import com.exemplo.agerun.ui.components.AgeCard
import com.exemplo.agerun.ui.components.AgeTopBar
import com.exemplo.agerun.ui.components.SectionHeader
import com.exemplo.agerun.ui.components.StatBadge
import com.exemplo.agerun.ui.components.StatusBadge
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary

@Composable
fun CoachHomeScreen(appState: AgeRunAppState) {
    val coach = appState.profileRepository.coach
    AgeTopBar(
        title = coach.name,
        subtitle = "Bem-vindo de volta",
        onAvatarClick = appState::openSettingsFromAvatar,
    )
    Spacer(Modifier.height(20.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        appState.dashboardMetrics.forEach { m ->
            StatBadge(modifier = Modifier.weight(1f), label = m.label, value = m.value)
        }
    }
    Spacer(Modifier.height(24.dp))
    SectionHeader("Treinos da semana")
    Spacer(Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appState.workouts.take(3).forEach { w ->
            AgeCard {
                Text(w.title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text("${w.type.label} • ${w.date}", color = Lime, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
    Spacer(Modifier.height(24.dp))
    SectionHeader("Comunicados")
    Spacer(Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appState.notices.forEach { n ->
            AgeCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(n.title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    if (n.pinned) StatusBadge("Fixado")
                }
                Spacer(Modifier.height(6.dp))
                Text(n.body, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
