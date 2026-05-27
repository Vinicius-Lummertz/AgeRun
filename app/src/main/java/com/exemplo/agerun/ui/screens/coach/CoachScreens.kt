package com.exemplo.agerun.ui.screens.coach

import androidx.compose.foundation.clickable
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
import com.exemplo.agerun.model.totalDistanceKm
import com.exemplo.agerun.state.AgeRunAppState
import com.exemplo.agerun.ui.components.AgeCard
import com.exemplo.agerun.ui.components.SectionHeader
import com.exemplo.agerun.ui.components.StatusBadge
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary

@Composable
fun CoachWorkoutsScreen(appState: AgeRunAppState) {
    SectionHeader("Treinos")
    Spacer(Modifier.height(12.dp))
    Text(
        "Criação rica de treino e agenda chegam no próximo marco.",
        color = TextMuted, style = MaterialTheme.typography.bodyMedium,
    )
    Spacer(Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appState.workouts.forEach { w ->
            AgeCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(w.title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    StatusBadge(w.intensity.label)
                }
                Text(
                    "${w.type.label} • ${w.date} • ${"%.1f".format(w.totalDistanceKm())} km • pace ${w.targetPace}",
                    color = Lime, style = MaterialTheme.typography.labelLarge,
                )
                Spacer(Modifier.height(8.dp))
                w.blocks.forEach { b ->
                    Text("• ${b.label}: ${b.detail}", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Atribuído a ${w.assignedStudentIds.size} aluno(s)",
                    color = TextMuted, style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
fun CoachStudentsScreen(appState: AgeRunAppState) {
    SectionHeader("Alunos", action = "Novo", onAction = appState::openCreateStudentSheet)
    Spacer(Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appState.students.forEach { s ->
            AgeCard(modifier = Modifier.clickable { appState.openStudent(s.id) }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(s.name, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                        Text(s.plan, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                        Text(s.activeWorksheet, color = Lime, style = MaterialTheme.typography.labelLarge)
                    }
                    StatusBadge(s.status)
                }
            }
        }
    }
}

@Composable
fun CoachStudentProfile(appState: AgeRunAppState) {
    val s = appState.selectedStudent ?: return
    val mine = appState.workouts.filter { s.id in it.assignedStudentIds }
    Text("‹ Voltar", color = Lime, style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.clickable(onClick = appState::closeStudent))
    Spacer(Modifier.height(12.dp))
    SectionHeader(
        s.name,
        action = if (s.status == "Ativo") "Inativar" else "Ativar",
        onAction = { appState.toggleStudentStatus(s.id) },
    )
    Spacer(Modifier.height(16.dp))
    AgeCard {
        Text("Plano", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Text(s.plan, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        Text("Planilha ativa", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Text(s.activeWorksheet, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        Text("Observação", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Text(s.coachNote.ifBlank { "-" }, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
    }
    Spacer(Modifier.height(16.dp))
    SectionHeader("Treinos atribuídos")
    Spacer(Modifier.height(12.dp))
    if (mine.isEmpty()) {
        Text("Nenhum treino vinculado.", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            mine.forEach { w ->
                AgeCard {
                    Text(w.title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    Text("${w.type.label} • ${w.date}", color = Lime, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun CoachPaymentsScreen(appState: AgeRunAppState) {
    SectionHeader("Pagamentos")
    Spacer(Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appState.students.forEach { s ->
            s.payments.forEach { p ->
                AgeCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(s.name, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                            Text("${p.label} • ${p.amount} • ${p.dueDate}", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            p.status, color = Lime, style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.clickable { appState.togglePaymentStatus(s.id, p.id) },
                        )
                    }
                }
            }
        }
    }
}
