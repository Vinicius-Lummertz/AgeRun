package com.exemplo.agerun.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.model.WorkoutEntry
import com.exemplo.agerun.state.AgeRunAppState
import com.exemplo.agerun.ui.components.AuthField
import com.exemplo.agerun.ui.components.DetailCard
import com.exemplo.agerun.ui.components.HeroSection
import com.exemplo.agerun.ui.components.SectionTitle
import com.exemplo.agerun.ui.components.SelectionChip
import com.exemplo.agerun.ui.components.SmallActionButton
import com.exemplo.agerun.ui.components.MultilineField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Text
import com.exemplo.agerun.ui.theme.TextPrimary
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun WorkoutsModuleScreen(appState: AgeRunAppState) {
    var title by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var focus by rememberSaveable { mutableStateOf("") }
    var pace by rememberSaveable { mutableStateOf("") }
    var distanceText by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var selectedStudentIds by remember { mutableStateOf(emptySet<String>()) }

    HeroSection(
        title = "Treinos",
        subtitle = "Crie treinos e vincule alunos ou grupos de forma objetiva.",
    )
    Spacer(modifier = Modifier.height(20.dp))
    DetailCard(title = "Novo treino") {
        AuthField(title, { title = it }, "Titulo do treino", KeyboardType.Text)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AuthField(date, { date = it }, "Data", KeyboardType.Text, modifier = Modifier.weight(1f))
            AuthField(focus, { focus = it }, "Foco", KeyboardType.Text, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AuthField(distanceText, { distanceText = it }, "KM", KeyboardType.Number, modifier = Modifier.weight(1f))
            AuthField(pace, { pace = it }, "Pace", KeyboardType.Text, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
        MultilineField(notes, { notes = it }, "Observacoes")
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Atribuir para",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            appState.students.forEach { student ->
                SelectionChip(
                    text = student.name,
                    selected = student.id in selectedStudentIds,
                    onClick = {
                        selectedStudentIds =
                            if (student.id in selectedStudentIds) {
                                selectedStudentIds.minus(student.id)
                            } else {
                                selectedStudentIds.plus(student.id)
                            }
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        SmallActionButton(
            text = "Criar treino",
            onClick = {
                if (title.isNotBlank()) {
                    appState.createWorkout(
                        WorkoutEntry(
                            id = "workout-${appState.workouts.size + 10}",
                            title = title.trim(),
                            date = date.ifBlank { "Sem data" }.trim(),
                            focus = focus.ifBlank { "Livre" }.trim(),
                            distanceKm = distanceText.toIntOrNull() ?: 0,
                            pace = pace.ifBlank { "-" }.trim(),
                            notes = notes.trim(),
                            assignedStudentIds = selectedStudentIds.toList(),
                        ),
                    )
                    title = ""
                    date = ""
                    focus = ""
                    pace = ""
                    distanceText = ""
                    notes = ""
                    selectedStudentIds = emptySet()
                }
            },
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    SectionTitle("Treinos criados")
    Spacer(modifier = Modifier.height(14.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appState.workouts.forEach { workout ->
            WorkoutListItem(workout = workout)
        }
    }
}
