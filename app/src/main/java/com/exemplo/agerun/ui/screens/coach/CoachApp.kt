package com.exemplo.agerun.ui.screens.coach

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.model.CoachTab
import com.exemplo.agerun.state.AgeRunAppState
import com.exemplo.agerun.ui.components.AgeBottomBar
import com.exemplo.agerun.ui.components.BottomNavItem
import com.exemplo.agerun.ui.screens.StudentCreationSheet

@Composable
fun CoachApp(appState: AgeRunAppState) {
    val items = listOf(
        BottomNavItem(CoachTab.Treinos.name, CoachTab.Treinos.label, Icons.Filled.DirectionsRun),
        BottomNavItem(CoachTab.Alunos.name, CoachTab.Alunos.label, Icons.Filled.Group),
        BottomNavItem(CoachTab.Inicio.name, CoachTab.Inicio.label, Icons.Filled.Home),
        BottomNavItem(CoachTab.Pagamentos.name, CoachTab.Pagamentos.label, Icons.Filled.AttachMoney),
        BottomNavItem(CoachTab.Config.name, CoachTab.Config.label, Icons.Filled.Settings),
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
            when (appState.coachTab) {
                CoachTab.Inicio -> CoachHomeScreen(appState)
                CoachTab.Treinos -> CoachWorkoutsScreen(appState)
                CoachTab.Alunos ->
                    if (appState.selectedStudent != null) CoachStudentProfile(appState)
                    else CoachStudentsScreen(appState)
                CoachTab.Pagamentos -> CoachPaymentsScreen(appState)
                CoachTab.Config -> CoachSettingsScreen(appState)
            }
            Spacer(Modifier.height(28.dp))
        }
        AgeBottomBar(
            items = items,
            selectedKey = appState.coachTab.name,
            onSelect = { key -> appState.selectCoachTab(CoachTab.valueOf(key)) },
            elevatedKey = CoachTab.Inicio.name,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
    if (appState.showCreateStudentSheet) {
        StudentCreationSheet(
            onDismiss = appState::closeCreateStudentSheet,
            onCreateStudent = appState::createStudent,
        )
    }
}
