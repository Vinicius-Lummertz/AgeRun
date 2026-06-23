package com.exemplo.agerun.ui.screens.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.model.StudentTab
import com.exemplo.agerun.state.AgeRunAppState
import com.exemplo.agerun.ui.components.AgeBottomBar
import com.exemplo.agerun.ui.components.AgeCard
import com.exemplo.agerun.ui.components.AgeTopBar
import com.exemplo.agerun.ui.components.BottomNavItem
import com.exemplo.agerun.ui.components.SectionHeader
import com.exemplo.agerun.ui.components.StatusBadge
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary
import com.exemplo.agerun.model.totalDistanceKm

@Composable
fun StudentApp(appState: AgeRunAppState) {
    val items = listOf(
        BottomNavItem(StudentTab.Inicio.name, StudentTab.Inicio.label, Icons.Filled.Home),
        BottomNavItem(StudentTab.Treinos.name, StudentTab.Treinos.label, Icons.Filled.DirectionsRun),
        BottomNavItem(StudentTab.Mensalidade.name, StudentTab.Mensalidade.label, Icons.Filled.AttachMoney),
        BottomNavItem(StudentTab.Perfil.name, StudentTab.Perfil.label, Icons.Filled.Person),
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(18.dp))
            when (appState.studentTab) {
                StudentTab.Inicio -> StudentHome(appState)
                StudentTab.Treinos -> StudentWorkouts(appState)
                StudentTab.Mensalidade -> StudentPayments(appState)
                StudentTab.Perfil -> StudentProfile(appState)
            }
            Spacer(Modifier.height(28.dp))
        }
        AgeBottomBar(
            items = items,
            selectedKey = appState.studentTab.name,
            onSelect = { key -> appState.selectStudentTab(StudentTab.valueOf(key)) },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}

private fun currentStudent(appState: AgeRunAppState) =
    appState.students.first { it.id == appState.currentStudentId }

@Composable
private fun StudentHome(appState: AgeRunAppState) {
    val me = currentStudent(appState)
    val today = appState.workouts.firstOrNull { me.id in it.assignedStudentIds }
    AgeTopBar(title = me.name, subtitle = "Bom treino!", onAvatarClick = { appState.selectStudentTab(StudentTab.Perfil) })
    Spacer(Modifier.height(20.dp))
    SectionHeader("Treino de hoje")
    Spacer(Modifier.height(12.dp))
    AgeCard {
        if (today == null) {
            Text("Sem treino atribuído hoje.", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        } else {
            Text(today.title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
            Text(
                "${today.type.label} • ${"%.1f".format(today.totalDistanceKm())} km • pace ${today.targetPace}",
                color = Lime, style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun StudentWorkouts(appState: AgeRunAppState) {
    val me = currentStudent(appState)
    val mine = appState.workouts.filter { me.id in it.assignedStudentIds }
    SectionHeader("Meus treinos")
    Spacer(Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        mine.forEach { w ->
            AgeCard {
                Text(w.title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${w.type.label} • ${"%.1f".format(w.totalDistanceKm())} km • pace ${w.targetPace}",
                    color = Lime, style = MaterialTheme.typography.labelLarge,
                )
                Text(w.notes, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun StudentPayments(appState: AgeRunAppState) {
    val me = currentStudent(appState)
    SectionHeader("Minha mensalidade")
    Spacer(Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        me.payments.forEach { p ->
            AgeCard {
                Text(p.label, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text("${p.amount} • vence ${p.dueDate}", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                StatusBadge(p.status)
            }
        }
    }
}

@Composable
private fun StudentProfile(appState: AgeRunAppState) {
    val me = currentStudent(appState)
    AgeTopBar(title = me.name, subtitle = me.plan, onAvatarClick = {})
    Spacer(Modifier.height(20.dp))
    AgeCard {
        Text("E-mail", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Text(me.email, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(10.dp))
        Text("Telefone", color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Text(me.phone, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
    }
    Spacer(Modifier.height(16.dp))
    com.exemplo.agerun.ui.components.OutlineActionButton(
        modifier = Modifier.fillMaxWidth(), text = "Sair", onClick = appState::logout,
    )
}
