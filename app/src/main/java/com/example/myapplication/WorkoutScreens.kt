package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.GifBox
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.InsertEmoticon
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.Announcement
import com.example.myapplication.data.Student
import com.example.myapplication.data.Workout
import com.example.myapplication.data.Event
import com.example.myapplication.ui.AgeGoUiState
import com.example.myapplication.ui.AgeGoViewModel
import com.example.myapplication.ui.theme.AgeGoTheme
import com.example.myapplication.ui.theme.Lime
import com.example.myapplication.ui.theme.LimeMuted
import com.example.myapplication.ui.theme.NavigationPurple
import com.example.myapplication.ui.theme.PurpleBackground
import com.example.myapplication.ui.theme.PurpleDeep
import com.example.myapplication.ui.theme.PurpleSurface
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun WorkoutsScreen(
    workouts: List<Workout>,
    loading: Boolean,
    onBack: () -> Unit,
    onNewWorkout: () -> Unit,
    onWorkoutClick: (Workout) -> Unit
) {
    var filter by remember { mutableStateOf("Todos") }
    DirectoryScreen(
        title = "Hub Fit - Treinos",
        searchPlaceholder = "Pesquisar treino",
        filters = listOf("Todos", "Ativos", "Inativos"),
        selectedFilter = filter,
        onFilterSelected = { filter = it },
        actions = listOf(
            DirectoryAction("Novo treino", R.drawable.ic_option_treinos, onNewWorkout)
        ),
        items = workouts,
        loading = loading,
        itemId = { it.id },
        itemTitle = { it.name },
        itemStatus = { if (it.status == "active") "Ativos" else "Inativos" },
        itemMatchesQuery = { workout, query -> workout.name.contains(query, true) },
        onBack = onBack,
        onItemClick = onWorkoutClick
    )
}

@Composable
fun WorkoutDetailScreen(
    workout: Workout?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit
) {
    var selectedTab by remember(workout?.id) { mutableStateOf("Dados") }
    val structureLines = remember(workout?.description) {
        workoutStructureLines(workout?.description)
    }
    val structureSections = remember(workout?.description) {
        parseWorkoutStructure(workout?.description)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        WorkoutFlowHeader(
            breadcrumb = "Treinos",
            title = workout?.name ?: "Treino",
            onBack = onBack,
            readyLabel = if (workout != null) "Editar" else null,
            onReady = if (workout != null) onEdit else null
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (workout == null) {
                item { EmptyRoutineBuilderState("Treino nao encontrado.") }
                return@LazyColumn
            }

            item {
                WorkoutDetailTabs(
                    selected = selectedTab,
                    onSelected = { selectedTab = it }
                )
            }

            when (selectedTab) {
                "Dados" -> {
                    item {
                        WorkoutDetailSection(title = "Dados gerais") {
                            WorkoutDetailRow("Nome", workout.name)
                            WorkoutDetailRow("Valor", formatMoneyLabel(extractRoutinePrice(workout.description.orEmpty())))
                            WorkoutDetailRow("Status", workoutStatusLabel(workout.status))
                            WorkoutDetailRow("Tipo", workout.iconName?.replace('_', ' ')?.ifBlank { "Treino" } ?: "Treino")
                        }
                    }
                    item {
                        TextButton(
                            onClick = { onDelete(workout.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Excluir treino", color = Color(0xFFFFB3BE), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                "Estrutura" -> {
                    item {
                        WorkoutDetailSection(title = "Estrutura do treino") {
                            if (structureSections.isEmpty() && structureLines.isEmpty()) {
                                WorkoutDetailMutedText("Nenhuma estrutura personalizada salva para este treino.")
                            } else if (structureSections.isNotEmpty()) {
                                WorkoutStructureTimeline(structureSections)
                            } else {
                                structureLines.forEach { line ->
                                    WorkoutStructureLine(line)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun workoutStructureLines(description: String?): List<String> {
    val normalized = description?.trim().orEmpty()
    if (normalized.isBlank()) return emptyList()
    if (normalized.equals("Treino personalizado", ignoreCase = true)) return emptyList()
    if (normalized.equals("Treino sem estrutura definida", ignoreCase = true)) return emptyList()
    return normalized
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toList()
}

data class WorkoutStructureSection(
    val label: String,
    val steps: List<WorkoutStructureStep>
)

data class WorkoutStructureStep(
    val name: String,
    val value: String,
    val reps: String
)

fun parseWorkoutStructure(description: String?): List<WorkoutStructureSection> {
    return workoutStructureLines(description).mapNotNull { line ->
        val parts = line.split(":", limit = 2)
        if (parts.size != 2 || (!parts[0].trim().startsWith("Dia") && !parts[0].trim().startsWith("Secao"))) return@mapNotNull null
        val steps = parts[1]
            .split(";")
            .mapNotNull { rawStep ->
                val stepParts = rawStep.trim().split(":", limit = 2)
                if (stepParts.size != 2) return@mapNotNull null
                val valueParts = stepParts[1].trim().split(" x", limit = 2)
                WorkoutStructureStep(
                    name = stepParts[0].trim(),
                    value = valueParts.getOrNull(0)?.trim().orEmpty(),
                    reps = valueParts.getOrNull(1)?.trim()?.let { "${it}x" }.orEmpty()
                )
            }
        if (steps.isEmpty()) null else WorkoutStructureSection(parts[0].trim(), steps)
    }
}

@Composable
fun WorkoutDetailTabs(selected: String, onSelected: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Dados", "Estrutura").forEach { tab ->
            SlimFilterBadge(
                label = tab,
                selected = selected == tab,
                onClick = { onSelected(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun WorkoutDetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PurpleSurface,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            content()
        }
    }
}

@Composable
fun WorkoutDetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(label, color = Color.White.copy(alpha = .58f), fontSize = 12.sp, modifier = Modifier.width(92.dp))
        Text(value.ifBlank { "Nao informado" }, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
fun WorkoutDetailMutedText(text: String) {
    Text(text, color = Color.White.copy(alpha = .60f), fontSize = 13.sp, lineHeight = 19.sp)
}

@Composable
fun WorkoutStructureTimeline(sections: List<WorkoutStructureSection>) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        sections.forEach { section ->
            Surface(color = PurpleBackground, shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(section.label, color = Lime, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Column(Modifier.fillMaxWidth()) {
                        section.steps.forEachIndexed { index, step ->
                            WorkoutStructureStepRow(
                                step = step,
                                isLast = index == section.steps.lastIndex
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutStructureStepRow(step: WorkoutStructureStep, isLast: Boolean) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(workoutStructureStepColor(step.value)),
                contentAlignment = Alignment.Center
            ) {
                WorkoutStructureStepMark(step.value)
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(42.dp)
                        .background(workoutStructureStepColor(step.value).copy(alpha = .42f))
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(start = 12.dp, bottom = if (isLast) 2.dp else 14.dp)
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(PurpleSurface)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                step.name.ifBlank { "Objetivo" },
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(step.value, color = workoutStructureStepColor(step.value), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                if (step.reps.isNotBlank()) {
                    Text(step.reps, color = workoutStructureStepColor(step.value), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WorkoutStructureStepMark(value: String) {
    when {
        value.contains("km", ignoreCase = true) -> Text("KM", color = PurpleDeep, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        value.contains("desc", ignoreCase = true) -> Box(Modifier.size(18.dp)) {
            Box(Modifier.align(Alignment.Center).width(16.dp).height(4.dp).clip(RoundedCornerShape(4.dp)).background(PurpleDeep))
        }
        else -> Icon(Icons.Outlined.Schedule, contentDescription = null, tint = PurpleDeep, modifier = Modifier.size(18.dp))
    }
}

fun workoutStructureStepColor(value: String): Color = when {
    value.contains("km", ignoreCase = true) -> Color(0xFF8EDBFF)
    value.contains("desc", ignoreCase = true) -> Color(0xFFFFD166)
    else -> Lime
}

@Composable
fun WorkoutStructureLine(text: String) {
    WorkoutStructureStepRow(
        step = WorkoutStructureStep(name = text, value = "", reps = ""),
        isLast = true
    )
}

@Composable
fun WorkoutRoutineLinkRow(routine: DirectoryEntry) {
    Surface(color = PurpleBackground, shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.ic_option_modalidades), contentDescription = null, modifier = Modifier.size(22.dp))
            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                Text(routine.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(directoryStatusLabel(routine.status), color = Lime, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RoutinesScreen(
    routines: List<DirectoryEntry>,
    onBack: () -> Unit,
    onGroupsClick: () -> Unit,
    onNewRoutine: () -> Unit,
    onRoutineClick: (DirectoryEntry) -> Unit
) {
    var filter by remember { mutableStateOf("Todos") }
    DirectoryScreen(
        title = "Hub Fit - Treinos",
        searchPlaceholder = "Pesquisar treino",
        filters = listOf("Todos", "Ativas", "Inativas"),
        selectedFilter = filter,
        onFilterSelected = { filter = it },
        actions = listOf(
            DirectoryAction("Novo treino", R.drawable.ic_option_treinos, onNewRoutine),
            DirectoryAction("Grupos", R.drawable.ic_option_grupos, onGroupsClick)
        ),
        items = routines,
        loading = false,
        itemId = { it.id },
        itemTitle = { it.name },
        itemStatus = { directoryStatusLabel(it.status) },
        itemMatchesQuery = { modality, query -> modality.name.contains(query, true) },
        onBack = onBack,
        onItemClick = onRoutineClick
    )
}

data class RoutineDayDraft(
    val number: Int,
    val workoutIds: List<String> = emptyList(),
    val restDaysAfter: Int = 1
)

@Composable
fun RoutineBuilderScreen(
    routine: DirectoryEntry?,
    workouts: List<Workout>,
    onBack: () -> Unit,
    onSave: (DirectoryEntry) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var step by remember(routine?.id) { mutableStateOf(if (routine == null) 0 else 1) }
    var name by remember(routine?.id) { mutableStateOf(routine?.name.orEmpty()) }
    var price by remember(routine?.id) { mutableStateOf(extractRoutinePrice(routine?.description.orEmpty())) }
    var days by remember(routine?.id) {
        mutableStateOf(parseRoutineDayDrafts(routine?.description.orEmpty(), workouts).ifEmpty { listOf(RoutineDayDraft(1)) })
    }

    BackHandler { if (step > 0) step-- else onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        WorkoutFlowHeader(
            breadcrumb = when (step) {
                0 -> "Treinos"
                1 -> "Dias de treino"
                else -> "Resumo do treino"
            },
            title = when (step) {
                0 -> if (routine == null) "Adicionar novo treino" else "Editar treino"
                1 -> "Dias do treino"
                else -> "Treino construido"
            },
            onBack = { if (step > 0) step-- else onBack() },
            readyLabel = if (step == 1) "Resumo" else null,
            onReady = if (step == 1) ({ step = 2 }) else null,
            readyEnabled = days.any { it.workoutIds.isNotEmpty() }
        )

        if (step != 1) Spacer(Modifier.weight(1f))

        WorkoutBottomPanel(expanded = step == 1) {
            when (step) {
                0 -> {
                    WorkoutNameStep(name = name, onNameChange = { name = it }, placeholder = "Nome do treino")
                    WorkoutNextButton(enabled = name.isNotBlank()) { step = 1 }
                }
                1 -> {
                    RoutineDaysStep(
                        days = days,
                        workouts = workouts,
                        onDaysChange = { days = it }
                    )
                }
                else -> {
                    RoutineSummaryStep(
                        name = name,
                        price = price,
                        onPriceChange = { price = it },
                        days = days,
                        workouts = workouts,
                        onSave = {
                            onSave(
                                DirectoryEntry(
                                    id = routine?.id.orEmpty(),
                                    name = name.trim(),
                                    status = "active",
                                    description = routineDescriptionSummary(price, days, workouts)
                                )
                            )
                            onBack()
                        },
                        onDelete = if (routine != null && onDelete != null) ({ onDelete(routine.id) }) else null
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineDaysStep(
    days: List<RoutineDayDraft>,
    workouts: List<Workout>,
    onDaysChange: (List<RoutineDayDraft>) -> Unit
) {
    var selectedDay by remember(days.size) { mutableStateOf(days.firstOrNull()?.number ?: 1) }
    var workoutQuery by remember { mutableStateOf("") }
    val activeDay = days.firstOrNull { it.number == selectedDay } ?: days.first()
    val filteredWorkouts = workouts.filter { workout ->
        workout.name.contains(workoutQuery, true) ||
            workout.description.orEmpty().contains(workoutQuery, true) ||
            workout.status.contains(workoutQuery, true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            SegmentedFilterBar(
                options = days.map { "Dia ${it.number}" },
                selected = "Dia $selectedDay",
                onSelected = { label -> selectedDay = label.substringAfter("Dia ").toIntOrNull() ?: selectedDay }
            )
        }
        item {
            RoutineRestPicker(
                restDays = activeDay.restDaysAfter,
                onChange = { next ->
                    onDaysChange(days.map { if (it.number == activeDay.number) it.copy(restDaysAfter = next) else it })
                }
            )
        }
        item {
            Text("Treinos do dia", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        if (workouts.isNotEmpty()) {
            item {
                StudentSearchBar(
                    value = workoutQuery,
                    onValueChange = { workoutQuery = it },
                    placeholder = "Pesquisar treino",
                    modifier = Modifier.shadow(14.dp, RoundedCornerShape(50)).imePadding()
                )
            }
        }
        if (workouts.isEmpty()) {
            item { EmptyRoutineBuilderState("Nenhuma atividade disponível para compor os dias.") }
        } else {
            items(filteredWorkouts, key = { it.id }) { workout ->
                val selected = workout.id in activeDay.workoutIds
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable {
                        val nextIds = if (selected) {
                            activeDay.workoutIds - workout.id
                        } else {
                            activeDay.workoutIds + workout.id
                        }
                        onDaysChange(days.map { if (it.number == activeDay.number) it.copy(workoutIds = nextIds) else it })
                    },
                    color = if (selected) Lime.copy(alpha = .18f) else PurpleBackground,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(24.dp).clip(CircleShape).background(if (selected) Lime else PurpleSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selected) Icon(Icons.Outlined.Check, null, tint = PurpleDeep, modifier = Modifier.size(15.dp))
                        }
                        Column(Modifier.padding(start = 10.dp).weight(1f)) {
                            Text(workout.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(workout.description.orEmpty().ifBlank { workoutStatusLabel(workout.status) }, color = Color.White.copy(alpha = .58f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
        item {
            Button(
                onClick = {
                    val nextNumber = (days.maxOfOrNull { it.number } ?: 0) + 1
                    onDaysChange(days + RoutineDayDraft(nextNumber))
                    selectedDay = nextNumber
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep)
            ) {
                Icon(Icons.Outlined.Add, null)
                Text("Adicionar dia", Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RoutineRestPicker(restDays: Int, onChange: (Int) -> Unit) {
    Surface(color = PurpleBackground, shape = RoundedCornerShape(14.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Descanso apos este dia", color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Surface(
                modifier = Modifier.size(48.dp).clickable { onChange((restDays + 1) % 5) },
                color = Lime,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(restDays.toString(), color = PurpleDeep, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            Text("dias", color = Color.White.copy(alpha = .72f), fontSize = 13.sp, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun RoutineSummaryStep(
    name: String,
    price: String,
    onPriceChange: (String) -> Unit,
    days: List<RoutineDayDraft>,
    workouts: List<Workout>,
    onSave: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val includedWorkoutCount = days.sumOf { it.workoutIds.size }
    val minimumMinutes = days.flatMap { it.workoutIds }.sumOf { id -> minimumWorkoutMinutes(workouts.firstOrNull { it.id == id }) }
    val minimumDistanceKm = days.flatMap { it.workoutIds }.sumOf { id -> minimumWorkoutDistanceKm(workouts.firstOrNull { it.id == id }) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(price, { next -> onPriceChange(next.filter { it.isDigit() || it == ',' || it == '.' }) }, "Valor do treino")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RoutineMetricCard("Treinos inclusos", includedWorkoutCount.toString(), Modifier.weight(1f))
            RoutineMetricCard("Tempo minimo", "${minimumMinutes.cleanRoutineNumber()} min", Modifier.weight(1f))
            RoutineMetricCard("Distancia min.", "${minimumDistanceKm.cleanRoutineNumber()} km", Modifier.weight(1f))
        }
        Surface(color = PurpleBackground, shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(name.ifBlank { "Treino" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${days.size} dias de treino", color = Lime, fontWeight = FontWeight.SemiBold)
                days.forEach { day ->
                    val names = day.workoutIds.mapNotNull { id -> workouts.firstOrNull { it.id == id }?.name }
                    Text(
                        "Dia ${day.number}: ${names.joinToString().ifBlank { "sem treinos" }} | descanso ${day.restDaysAfter}d",
                        color = Color.White.copy(alpha = .78f),
                        fontSize = 13.sp
                    )
                }
            }
        }
        SaveButton(enabled = name.isNotBlank() && includedWorkoutCount > 0) { onSave() }
        if (onDelete != null) {
            TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                Text("Excluir treino", color = Color.White.copy(alpha = .62f))
            }
        }
    }
}

@Composable
fun RoutineMetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(color = PurpleBackground, shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = Color.White.copy(alpha = .58f), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun EmptyRoutineBuilderState(message: String) {
    Surface(color = PurpleBackground, shape = RoundedCornerShape(14.dp)) {
        Text(message, Modifier.fillMaxWidth().padding(16.dp), color = Color.White.copy(alpha = .72f))
    }
}

fun routineDescriptionSummary(price: String, days: List<RoutineDayDraft>, workouts: List<Workout>): String {
    val lines = mutableListOf<String>()
    if (price.isNotBlank()) lines += "Valor: R$ $price"
    lines += "${days.size} dias de treino"
    days.forEach { day ->
        val names = day.workoutIds.mapNotNull { id -> workouts.firstOrNull { it.id == id }?.name }
        lines += "Dia ${day.number}: ${names.joinToString().ifBlank { "sem treinos" }} | descanso ${day.restDaysAfter}d"
    }
    return lines.joinToString("\n")
}

fun parseRoutineDayDrafts(description: String, workouts: List<Workout>): List<RoutineDayDraft> {
    if (description.isBlank()) return emptyList()
    return description.lineSequence()
        .map { it.trim() }
        .filter { it.startsWith("Dia ") }
        .mapNotNull { line ->
            val number = line.substringAfter("Dia ").substringBefore(":").toIntOrNull() ?: return@mapNotNull null
            val workoutText = line.substringAfter(":", "").substringBefore("|").trim()
            val rest = line.substringAfter("descanso", "0").filter { it.isDigit() }.toIntOrNull() ?: 0
            val ids = workoutText
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() && !it.equals("sem treinos", ignoreCase = true) }
                .mapNotNull { name -> workouts.firstOrNull { it.name.equals(name, ignoreCase = true) }?.id }
            RoutineDayDraft(number = number, workoutIds = ids, restDaysAfter = rest.coerceIn(0, 4))
        }
        .toList()
}

fun minimumWorkoutMinutes(workout: Workout?): Double =
    parseWorkoutStructure(workout?.description).flatMap { it.steps }.sumOf { step ->
        if (step.value.contains("min", ignoreCase = true) && !step.value.contains("desc", ignoreCase = true)) {
            Regex("""\d+([.,]\d+)?""").find(step.value)?.value?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }
    }

fun minimumWorkoutDistanceKm(workout: Workout?): Double =
    parseWorkoutStructure(workout?.description).flatMap { it.steps }.sumOf { step ->
        if (step.value.contains("km", ignoreCase = true)) {
            Regex("""\d+([.,]\d+)?""").find(step.value)?.value?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }
    }

fun Double.cleanRoutineNumber(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)

fun extractRoutinePrice(description: String): String =
    description.lineSequence()
        .firstOrNull { it.startsWith("Valor: R$") }
        ?.substringAfter("Valor: R$")
        ?.trim()
        .orEmpty()

@Composable
fun StudentFormScreen(
    student: Student?,
    onBack: () -> Unit,
    onSave: (Student) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var name by remember(student?.id) { mutableStateOf(student?.name.orEmpty()) }
    var phone by remember(student?.id) { mutableStateOf(student?.phone.orEmpty()) }
    var routine by remember(student?.id) { mutableStateOf(student?.routine.orEmpty().ifBlank { student?.planName.orEmpty() }) }

    SimpleFormScaffold(
        title = if (student == null) "Novo aluno" else "Editar aluno",
        onBack = onBack,
        onDelete = if (student != null && onDelete != null) ({ onDelete(student.id) }) else null
    ) {
        item { FormTextField(name, { name = it }, "Nome") }
        item { FormTextField(phone, { phone = it }, "Telefone") }
        item { FormTextField(routine, { routine = it }, "Rotina") }
        item {
            SaveButton(enabled = name.isNotBlank()) {
                onSave(
                    Student(
                        id = student?.id.orEmpty(),
                        name = name.trim(),
                        email = student?.email.orEmpty(),
                        phone = phone.trim(),
                        routine = routine.trim(),
                        planName = routine.trim().ifBlank { student?.planName ?: "Sem rotina" },
                        status = student?.status ?: "active"
                    )
                )
            }
        }
    }
}

@Composable
fun NewStudentFlowScreen(
    workouts: List<Workout>,
    onBack: () -> Unit,
    onSave: (Student, Workout?, onComplete: () -> Unit, onError: () -> Unit) -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var createFromScratch by remember { mutableStateOf<Boolean?>(null) }
    var selectedWorkoutId by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var billingDay by remember { mutableStateOf(5) }
    var sections by remember { mutableStateOf(listOf(WorkoutBuilderSection(1, selected = true))) }
    var saving by remember { mutableStateOf(false) }

    val publicWorkouts = workouts.filterNot { it.description.orEmpty().lineSequence().any { line -> line.trim() == "Visibilidade: privado" } }
    val selectedWorkout = publicWorkouts.firstOrNull { it.id == selectedWorkoutId }
    val resultPrice = if (createFromScratch == true) price else extractRoutinePrice(selectedWorkout?.description.orEmpty())
    val resultName = if (createFromScratch == true) "Treino de ${name.trim()}" else selectedWorkout?.name.orEmpty()
    val resultSections = if (createFromScratch == true) sections else builderSectionsFromDescription(selectedWorkout?.description)

    fun saveStudentAndWorkout() {
        if (saving) return
        saving = true
        val customWorkout = if (createFromScratch == true) Workout(
            id = "",
            name = resultName,
            status = "active",
            description = workoutPlanDescription(price, sections, private = true)
        ) else null
        onSave(
            Student(
                id = "",
                name = name.trim(),
                phone = phone.trim(),
                routine = selectedWorkout?.id ?: resultName,
                planName = resultName,
                monthlyFee = resultPrice,
                billingDay = billingDay,
                status = "active"
            ),
            customWorkout,
            {
                saving = false
                step = 5
            },
            { saving = false }
        )
    }

    fun goBack() {
        when {
            step == 0 -> onBack()
            step == 5 -> onBack()
            else -> step--
        }
    }

    BackHandler { goBack() }
    Column(Modifier.fillMaxSize().background(PurpleBackground)) {
        WorkoutFlowHeader(
            breadcrumb = when (step) {
                0 -> "Alunos"
                1 -> "Novo aluno"
                2 -> if (createFromScratch == true) "Treino personalizado" else "Treinos"
                3 -> "Construtor de treino"
                4 -> "Valor do treino"
                else -> "Aluno criado"
            },
            title = when (step) {
                0 -> "Adicionar novo aluno"
                1 -> "Como deseja montar o treino?"
                2 -> "Escolha um treino"
                3 -> "Construa os dias"
                4 -> "Revise e defina o valor"
                else -> "Tudo pronto"
            },
            onBack = ::goBack,
            iconRes = R.drawable.ic_option_alunos,
            readyLabel = if (step == 3) "Valor" else null,
            onReady = if (step == 3) ({ step = 4 }) else null,
            readyEnabled = sections.any { it.goals.isNotEmpty() }
        )
        if (step != 3) Spacer(Modifier.weight(1f))
        WorkoutBottomPanel(expanded = step == 3) {
            when (step) {
                0 -> {
                    WorkoutNameStep(name, { name = it }, "Nome do aluno")
                    WorkoutNameStep(phone, { phone = it.filter { char -> char.isDigit() || char in "()+- " } }, "Telefone")
                    Text(
                        "O proprio aluno escolhe o dia de pagamento no primeiro acesso ou no financeiro do app.",
                        color = Color.White.copy(alpha = .62f),
                        fontSize = 12.sp
                    )
                    WorkoutNextButton(name.isNotBlank() && phone.isNotBlank()) { step = 1 }
                }
                1 -> {
                    Text("Escolha o ponto de partida", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    NewStudentChoiceCard(
                        title = "Usar treino existente",
                        subtitle = "Mantém o valor e os dias definidos no treino.",
                        selected = createFromScratch == false
                    ) { createFromScratch = false }
                    NewStudentChoiceCard(
                        title = "Criar do zero",
                        subtitle = "Personalize preço, objetivo, dias e treinos.",
                        selected = createFromScratch == true
                    ) { createFromScratch = true }
                    WorkoutNextButton(createFromScratch != null) { step = if (createFromScratch == true) 3 else 2 }
                }
                2 -> if (createFromScratch == false) {
                    if (publicWorkouts.isEmpty()) {
                        EmptyRoutineBuilderState("Nenhum treino disponível. Volte e escolha criar do zero.")
                    } else {
                        Column(Modifier.heightIn(max = 390.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            publicWorkouts.forEach { workout ->
                                NewStudentChoiceCard(
                                    title = workout.name,
                                    subtitle = listOfNotNull(
                                        extractRoutinePrice(workout.description.orEmpty()).takeIf { it.isNotBlank() }?.let { "R$ $it" },
                                        parseWorkoutStructure(workout.description).size.takeIf { it > 0 }?.let { "$it dias" }
                                    ).joinToString(" • ").ifBlank { "Treino estruturado" },
                                    selected = selectedWorkoutId == workout.id
                                ) { selectedWorkoutId = workout.id }
                            }
                        }
                        SaveButton(enabled = selectedWorkoutId.isNotBlank() && !saving) { saveStudentAndWorkout() }
                    }
                }
                3 -> WorkoutBuilderStep(sections) { sections = it }
                4 -> {
                    WorkoutNameStep(price, { next -> price = next.filter { it.isDigit() || it == ',' || it == '.' } }, "Valor mensal")
                    NewStudentResults(
                        studentName = name,
                        planName = resultName,
                        price = resultPrice,
                        objective = "Treino exclusivo construído para o aluno.",
                        sections = resultSections
                    )
                    SaveButton(enabled = price.isNotBlank() && sections.any { it.goals.isNotEmpty() } && !saving) { saveStudentAndWorkout() }
                }
                else -> {
                    Text("Aluno criado com sucesso", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("$name já está vinculado ao treino $resultName.", color = Color.White.copy(alpha = .72f))
                    SaveButton(enabled = true) { onBack() }
                }
            }
        }
    }
}

@Composable
private fun NewStudentChoiceCard(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        color = if (selected) Lime.copy(alpha = .18f) else PurpleBackground,
        shape = RoundedCornerShape(16.dp),
        border = if (selected) BorderStroke(1.dp, Lime) else null
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (selected) Icons.Outlined.Check else Icons.Outlined.FitnessCenter, null, tint = Lime)
            Column(Modifier.padding(start = 12.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(alpha = .64f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun NewStudentResults(
    studentName: String,
    planName: String,
    price: String,
    objective: String,
    sections: List<WorkoutBuilderSection>
) {
    val goals = sections.flatMap { it.goals }
    val minutes = goals.filter { it.type == WorkoutGoalType.Tempo }
        .sumOf { (rawWorkoutValue(it.value).replace(',', '.').toDoubleOrNull() ?: 0.0) * it.reps }
    val distance = goals.filter { it.type == WorkoutGoalType.Distancia }
        .sumOf { (rawWorkoutValue(it.value).replace(',', '.').toDoubleOrNull() ?: 0.0) * it.reps }
    Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(studentName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(planName, color = Lime, fontWeight = FontWeight.SemiBold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RoutineMetricCard("Dias", sections.count { it.goals.isNotEmpty() }.toString(), Modifier.weight(1f))
            RoutineMetricCard("Tempo mínimo", "${minutes.cleanRoutineNumber()} min", Modifier.weight(1f))
            RoutineMetricCard("Distância mín.", "${distance.cleanRoutineNumber()} km", Modifier.weight(1f))
        }
        Surface(color = PurpleBackground, shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Resultados esperados", color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    objective.ifBlank { "Evoluir com consistência seguindo os dias do treino selecionado." },
                    color = Color.White.copy(alpha = .76f), fontSize = 13.sp
                )
                Text("Frequência: ${sections.count { it.goals.isNotEmpty() }} dias por ciclo", color = Color.White.copy(alpha = .76f), fontSize = 13.sp)
                Text("Investimento: ${formatMoneyLabel(price)}", color = Lime, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutFormScreen(
    workout: Workout?,
    onBack: () -> Unit,
    onSave: (Workout) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var step by remember(workout?.id) { mutableStateOf(0) }
    var name by remember(workout?.id) { mutableStateOf(workout?.name.orEmpty()) }
    var price by remember(workout?.id) { mutableStateOf(extractRoutinePrice(workout?.description.orEmpty())) }
    var sections by remember(workout?.id) {
        mutableStateOf(builderSectionsFromDescription(workout?.description).ifEmpty { listOf(WorkoutBuilderSection(1, selected = true)) })
    }

    BackHandler { if (step > 0) step-- else onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        WorkoutFlowHeader(
            breadcrumb = when (step) {
                0 -> "Treinos"
                1 -> "Construtor de treino"
                else -> "Resumo do treino"
            },
            title = when (step) {
                0 -> "Adicionar novo treino"
                1 -> "Construtor de treino"
                else -> "Treino pronto"
            },
            onBack = { if (step > 0) step-- else onBack() },
            readyLabel = if (step == 1) "Resumo" else null,
            onReady = if (step == 1) ({ step = 2 }) else null,
            readyEnabled = sections.any { it.goals.isNotEmpty() }
        )

        if (step != 1) {
            Spacer(Modifier.weight(1f))
        }

        WorkoutBottomPanel(expanded = step == 1) {
            when (step) {
                0 -> {
                    WorkoutNameStep(name = name, onNameChange = { name = it })
                    WorkoutNextButton(enabled = name.isNotBlank()) { step = 1 }
                }
                1 -> {
                    WorkoutBuilderStep(
                        sections = sections,
                        onSectionsChange = { sections = it }
                    )
                }
                else -> WorkoutPlanSummaryStep(name, price, { price = it }, sections) {
                    onSave(
                        Workout(
                            id = workout?.id.orEmpty(),
                            name = name.trim(),
                            description = workoutPlanDescription(price, sections),
                            iconName = workout?.iconName,
                            status = "active"
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutPlanSummaryStep(
    name: String,
    price: String,
    onPriceChange: (String) -> Unit,
    sections: List<WorkoutBuilderSection>,
    onSave: () -> Unit
) {
    val goals = sections.flatMap { it.goals }
    val minutes = goals.filter { it.type == WorkoutGoalType.Tempo }
        .sumOf { (rawWorkoutValue(it.value).replace(',', '.').toDoubleOrNull() ?: 0.0) * it.reps }
    val distance = goals.filter { it.type == WorkoutGoalType.Distancia }
        .sumOf { (rawWorkoutValue(it.value).replace(',', '.').toDoubleOrNull() ?: 0.0) * it.reps }
    Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        WorkoutNameStep(price, { onPriceChange(it.filter { char -> char.isDigit() || char == ',' || char == '.' }) }, "Valor do treino")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RoutineMetricCard("Dias", sections.count { it.goals.isNotEmpty() }.toString(), Modifier.weight(1f))
            RoutineMetricCard("Tempo", "${formatSectionNumber(minutes)} min", Modifier.weight(1f))
            RoutineMetricCard("Distância", "${formatSectionNumber(distance)} km", Modifier.weight(1f))
        }
        Surface(color = PurpleBackground, shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                sections.filter { it.goals.isNotEmpty() }.forEach { section ->
                    Text("Dia ${section.number}: ${sectionTotalLabel(section.goals)}", color = Lime, fontSize = 13.sp)
                }
            }
        }
        SaveButton(enabled = name.isNotBlank() && price.isNotBlank() && goals.isNotEmpty(), onClick = onSave)
    }
}

data class WorkoutBuilderSection(
    val number: Int,
    val selected: Boolean = false,
    val goals: List<WorkoutGoal> = emptyList()
)
enum class WorkoutGoalType {
    Tempo,
    Distancia,
    Repouso
}

data class WorkoutGoal(
    val id: Int,
    val name: String = "",
    val value: String = "20",
    val reps: Int = 1,
    val type: WorkoutGoalType = WorkoutGoalType.Tempo,
    val pendingDelete: Boolean = false
)

@Composable
fun WorkoutFlowHeader(
    breadcrumb: String,
    title: String,
    onBack: () -> Unit,
    readyLabel: String? = null,
    onReady: (() -> Unit)? = null,
    readyEnabled: Boolean = true,
    iconRes: Int = R.drawable.ic_option_treinos
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpleBackground)
                .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Text(
                breadcrumb,
                modifier = Modifier.padding(start = 4.dp),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(Modifier.weight(1f))
            if (readyLabel != null && onReady != null) {
                Surface(
                    modifier = Modifier.clickable(enabled = readyEnabled, onClick = onReady),
                    color = if (readyEnabled) Lime else Lime.copy(alpha = .35f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        readyLabel,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        color = PurpleDeep,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        if (readyLabel != null) return
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = "Treino",
                modifier = Modifier.size(120.dp),
                tint = Color.White
            )
        }
        Text(
            title,
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun WorkoutBottomPanel(expanded: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(
        (if (expanded) Modifier.fillMaxSize() else Modifier.fillMaxWidth()).padding(14.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .then(
                    if (expanded) Modifier.fillMaxSize()
                    else Modifier.wrapContentHeight().heightIn(max = if (maxHeight.value.isFinite()) maxHeight * .78f else Dp.Unspecified)
                ),
            color = PurpleSurface,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 14.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content
            )
        }
    }
}

@Composable
fun WorkoutNameStep(name: String, onNameChange: (String) -> Unit, placeholder: String = "Nome do treino") {
    TextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth().height(58.dp),
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = .60f)) },
        singleLine = true,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF2C1252),
            unfocusedContainerColor = Color(0xFF2C1252),
            focusedIndicatorColor = Color(0xFF5529B0),
            unfocusedIndicatorColor = Color(0xFF5529B0),
            cursorColor = Lime
        )
    )
}

@Composable
fun WorkoutNextButton(enabled: Boolean, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep)
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Avancar")
        }
    }
}

@Composable
fun WorkoutBuilderStep(
    sections: List<WorkoutBuilderSection>,
    onSectionsChange: (List<WorkoutBuilderSection>) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val builderInteractionSource = remember { MutableInteractionSource() }
    var draftType by remember { mutableStateOf(WorkoutGoalType.Tempo) }
    var draftValue by remember { mutableStateOf("20") }
    var draftReps by remember { mutableStateOf(1) }
    var editingGoalId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = builderInteractionSource,
                indication = null
            ) { focusManager.clearFocus() },
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            sections.forEachIndexed { sectionIndex, section ->
                WorkoutSectionCard(
                    section = section,
                    editingGoalId = editingGoalId,
                    onEditingGoalChange = { editingGoalId = it },
                    onSelect = {
                        onSectionsChange(
                            sections.mapIndexed { index, item -> item.copy(selected = index == sectionIndex) }
                        )
                    },
                    onGoalsChange = { nextGoals ->
                        onSectionsChange(
                            sections.mapIndexed { index, item ->
                                if (index == sectionIndex) item.copy(goals = nextGoals) else item
                            }
                        )
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .height(34.dp)
                        .clickable {
                            val nextCount = if (sections.size >= 5) 1 else sections.size + 1
                            val nextSections = if (nextCount == 1) {
                                listOf(sections.firstOrNull()?.copy(number = 1, selected = true)
                                    ?: WorkoutBuilderSection(1, selected = true))
                            } else {
                                sections.map { it.copy(selected = false) } +
                                    WorkoutBuilderSection(nextCount, selected = true)
                            }
                            onSectionsChange(nextSections)
                        },
                    color = Color(0xFF2C1252),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Lime.copy(alpha = .65f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, tint = Lime, modifier = Modifier.size(16.dp))
                        Text("Dias ${sections.size}/5", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        WorkoutGoalBuilderMenu(
            type = draftType,
            value = draftValue,
            reps = draftReps,
            onTypeChange = {
                draftType = nextWorkoutGoalType(draftType)
                draftValue = defaultWorkoutDraftValue(draftType)
            },
            onValueChange = { draftValue = it },
            onRepsClick = { draftReps = if (draftReps >= 5) 1 else draftReps + 1 },
            onAdd = {
                val targetIndex = sections.indexOfFirst { it.selected }.takeIf { it >= 0 } ?: 0
                val targetSection = sections.getOrNull(targetIndex) ?: WorkoutBuilderSection(1, selected = true)
                val goal = WorkoutGoal(
                    id = ((sections.flatMap { it.goals }.maxOfOrNull { it.id } ?: 0) + 1),
                    name = "",
                    value = draftValue.ifBlank { defaultWorkoutDraftValue(draftType) },
                    reps = draftReps,
                    type = draftType
                )
                onSectionsChange(
                    if (sections.isEmpty()) {
                        listOf(targetSection.copy(goals = listOf(goal)))
                    } else {
                        sections.mapIndexed { index, item ->
                            if (index == targetIndex) item.copy(goals = item.goals + goal) else item
                        }
                    }
                )
                editingGoalId = goal.id
            }
        )
    }
}

@Composable
fun WorkoutSectionCard(
    section: WorkoutBuilderSection,
    editingGoalId: Int?,
    onEditingGoalChange: (Int?) -> Unit,
    onSelect: () -> Unit,
    onGoalsChange: (List<WorkoutGoal>) -> Unit
) {
    val goals = section.goals
    var goalNameHadFocus by remember(section.number) { mutableStateOf(false) }
    val goalNameFocusRequester = remember { FocusRequester() }
    val goalNameBringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val sectionTotal = sectionTotalLabel(goals)

    LaunchedEffect(editingGoalId) {
        if (editingGoalId != null) {
            kotlinx.coroutines.delay(40)
            goalNameFocusRequester.requestFocus()
            kotlinx.coroutines.delay(220)
            goalNameBringIntoViewRequester.bringIntoView()
        } else {
            goalNameHadFocus = false
        }
    }

    LaunchedEffect(imeBottom) {
        if (imeBottom == 0 && goalNameHadFocus) {
            onEditingGoalChange(null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 110.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (section.selected) 1.5.dp else 1.dp,
                color = if (section.selected) Lime else Color(0xFF5529B0).copy(alpha = .36f),
                shape = RoundedCornerShape(14.dp)
            )
            .background(PurpleBackground)
            .clickable(onClick = onSelect)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Dia ${section.number}",
                    color = if (section.selected) Lime else Color.White,
                    fontSize = 16.sp
                )
                Text(
                    " - $sectionTotal",
                    color = Color.White.copy(alpha = .60f),
                    fontSize = 16.sp
                )
            }
        }

        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp)) {
            if (goals.isEmpty()) {
                WorkoutEmptyStep()
            } else {
                goals.forEachIndexed { index, goal ->
                    WorkoutGoalStepRow(
                        goal = goal,
                        index = index,
                        isLast = index == goals.lastIndex,
                        editing = editingGoalId == goal.id,
                        nameFocusRequester = goalNameFocusRequester,
                        nameBringIntoViewRequester = goalNameBringIntoViewRequester,
                        onNameChange = { next ->
                            onGoalsChange(goals.map { if (it.id == goal.id) it.copy(name = next) else it })
                        },
                        onNameFocusChanged = {
                            if (it) {
                                goalNameHadFocus = true
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(220)
                                    goalNameBringIntoViewRequester.bringIntoView()
                                }
                            } else if (goalNameHadFocus) {
                                onEditingGoalChange(null)
                            }
                        },
                        onIconClick = {
                            onGoalsChange(
                                goals.map {
                                    if (it.id == goal.id) it.copy(pendingDelete = !it.pendingDelete) else it.copy(pendingDelete = false)
                                }
                            )
                        },
                        onDelete = {
                            onGoalsChange(goals.filterNot { it.id == goal.id })
                            if (editingGoalId == goal.id) onEditingGoalChange(null)
                        },
                        onDragMove = { direction ->
                            val targetIndex = (index + direction).coerceIn(0, goals.lastIndex)
                            if (targetIndex != index) {
                                onGoalsChange(goals.toMutableList().also { list ->
                                    val item = list.removeAt(index)
                                    list.add(targetIndex, item)
                                })
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutGoalBuilderMenu(
    type: WorkoutGoalType,
    value: String,
    reps: Int,
    onTypeChange: () -> Unit,
    onValueChange: (String) -> Unit,
    onRepsClick: () -> Unit,
    onAdd: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        color = NavigationPurple.copy(alpha = 0.60f),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(workoutGoalAccent(type))
                    .clickable(onClick = onTypeChange),
                contentAlignment = Alignment.Center
            ) {
                WorkoutGoalTypeMark(type = type, selected = true)
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(start = 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(PurpleSurface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = value,
                    onValueChange = { next -> onValueChange(next.filter { it.isDigit() || it == ',' || it == '.' }) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    singleLine = true,
                    placeholder = { Text(workoutValuePlaceholder(type), color = Color.White.copy(alpha = .42f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Lime
                    )
                )
                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp))
                        .background(Lime)
                        .clickable(onClick = onRepsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${reps}x", color = PurpleDeep, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Box(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Lime)
                    .clickable(onClick = onAdd),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Adicionar objetivo", tint = PurpleDeep, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun WorkoutEmptyStep() {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White.copy(alpha = .24f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White.copy(alpha = .42f), modifier = Modifier.size(16.dp))
            }
        }
        Text(
            "adicione o primeiro passo",
            modifier = Modifier.padding(start = 12.dp),
            color = Color.White.copy(alpha = .50f),
            fontSize = 13.sp
        )
    }
}

@Composable
fun WorkoutGoalStepRow(
    goal: WorkoutGoal,
    index: Int,
    isLast: Boolean,
    editing: Boolean,
    nameFocusRequester: FocusRequester,
    nameBringIntoViewRequester: BringIntoViewRequester,
    onNameChange: (String) -> Unit,
    onNameFocusChanged: (Boolean) -> Unit,
    onIconClick: () -> Unit,
    onDelete: () -> Unit,
    onDragMove: (Int) -> Unit
) {
    var dragAmount by remember(goal.id) { mutableStateOf(0f) }
    val markerColor = if (goal.pendingDelete) Color(0xFFFF5A6A) else workoutGoalAccent(goal.type)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(markerColor)
                    .clickable {
                        if (goal.pendingDelete) onDelete() else onIconClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                WorkoutGoalTypeMark(type = goal.type, selected = true)
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(54.dp)
                        .background(markerColor.copy(alpha = .46f))
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, bottom = if (isLast) 2.dp else 14.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PurpleSurface)
                .pointerInput(goal.id, index) {
                    detectDragGestures(
                        onDragEnd = { dragAmount = 0f },
                        onDragCancel = { dragAmount = 0f },
                        onDrag = { change, drag ->
                            change.consume()
                            dragAmount += drag.y
                            when {
                                dragAmount > 42f -> {
                                    onDragMove(1)
                                    dragAmount = 0f
                                }
                                dragAmount < -42f -> {
                                    onDragMove(-1)
                                    dragAmount = 0f
                                }
                            }
                        }
                    )
                }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (editing) {
                    TextField(
                        value = goal.name,
                        onValueChange = onNameChange,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .offset(y = (-4).dp)
                            .bringIntoViewRequester(nameBringIntoViewRequester)
                            .focusRequester(nameFocusRequester)
                            .onFocusChanged { onNameFocusChanged(it.isFocused) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Lime
                        )
                    )
                } else {
                    Text(
                        goal.name,
                        modifier = Modifier.weight(1f),
                        color = if (goal.pendingDelete) Color(0xFFFFB3BE) else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    WorkoutStepChip(text = formattedWorkoutGoalValue(goal), type = goal.type)
                    WorkoutStepChip(text = "${goal.reps}x", type = goal.type)
                }
            }
            if (goal.pendingDelete) {
                Text("toque no icone novamente para excluir", color = Color(0xFFFFB3BE), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun WorkoutStepChip(text: String, type: WorkoutGoalType) {
    Text(
        text,
        color = workoutGoalAccent(type),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun WorkoutGoalTypeMark(type: WorkoutGoalType, selected: Boolean) {
    val markColor = if (selected) PurpleDeep else Lime
    when (type) {
        WorkoutGoalType.Tempo -> Icon(
            Icons.Outlined.Schedule,
            contentDescription = "Tempo",
            tint = markColor,
            modifier = Modifier.size(19.dp)
        )
        WorkoutGoalType.Distancia -> Text(
            "KM",
            color = markColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        WorkoutGoalType.Repouso -> Box(Modifier.size(20.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 5.dp)
                    .width(17.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(markColor)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 4.dp, y = (-2).dp)
                    .width(3.dp)
                    .height(9.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(markColor)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-2).dp)
                    .width(3.dp)
                    .height(9.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(markColor)
            )
        }
    }
}

fun nextWorkoutGoalType(type: WorkoutGoalType): WorkoutGoalType =
    when (type) {
        WorkoutGoalType.Tempo -> WorkoutGoalType.Distancia
        WorkoutGoalType.Distancia -> WorkoutGoalType.Repouso
        WorkoutGoalType.Repouso -> WorkoutGoalType.Tempo
    }

fun defaultWorkoutDraftValue(type: WorkoutGoalType): String =
    when (type) {
        WorkoutGoalType.Tempo -> "20"
        WorkoutGoalType.Distancia -> "3"
        WorkoutGoalType.Repouso -> "2"
    }

fun workoutValuePlaceholder(type: WorkoutGoalType): String =
    when (type) {
        WorkoutGoalType.Tempo -> "min"
        WorkoutGoalType.Distancia -> "km"
        WorkoutGoalType.Repouso -> "desc"
    }

fun workoutGoalAccent(type: WorkoutGoalType): Color =
    when (type) {
        WorkoutGoalType.Tempo -> Lime
        WorkoutGoalType.Distancia -> Color(0xFF8EDBFF)
        WorkoutGoalType.Repouso -> Color(0xFFFFD166)
    }

fun formattedWorkoutGoalValue(goal: WorkoutGoal): String {
    val raw = rawWorkoutValue(goal.value).ifBlank { defaultWorkoutDraftValue(goal.type) }
    return when (goal.type) {
        WorkoutGoalType.Tempo -> "$raw min"
        WorkoutGoalType.Distancia -> "$raw km"
        WorkoutGoalType.Repouso -> "$raw min desc"
    }
}

fun workoutSectionsSummary(sections: List<WorkoutBuilderSection>): String {
    val activeSections = sections.filter { it.goals.isNotEmpty() }
    if (activeSections.isEmpty()) return ""
    return activeSections.joinToString("\n") { section ->
        val goals = section.goals.joinToString("; ") { goal ->
            "${goal.name}: ${formattedWorkoutGoalValue(goal)} x${goal.reps}"
        }
        "Dia ${section.number}: $goals"
    }
}

fun workoutPlanDescription(price: String, sections: List<WorkoutBuilderSection>, private: Boolean = false): String =
    listOfNotNull(
        "Visibilidade: privado".takeIf { private },
        "Valor: R$ ${price.trim()}".takeIf { price.isNotBlank() },
        workoutSectionsSummary(sections).takeIf { it.isNotBlank() }
    ).joinToString("\n")

fun builderSectionsFromDescription(description: String?): List<WorkoutBuilderSection> =
    parseWorkoutStructure(description).mapIndexed { sectionIndex, section ->
        WorkoutBuilderSection(
            number = Regex("""\d+""").find(section.label)?.value?.toIntOrNull() ?: sectionIndex + 1,
            selected = sectionIndex == 0,
            goals = section.steps.mapIndexed { goalIndex, step ->
                val type = when {
                    step.value.contains("km", true) -> WorkoutGoalType.Distancia
                    step.value.contains("desc", true) -> WorkoutGoalType.Repouso
                    else -> WorkoutGoalType.Tempo
                }
                WorkoutGoal(
                    id = sectionIndex * 1000 + goalIndex + 1,
                    name = step.name,
                    value = rawWorkoutValue(step.value),
                    reps = step.reps.filter { it.isDigit() }.toIntOrNull() ?: 1,
                    type = type
                )
            }
        )
    }

fun sectionTotalLabel(goals: List<WorkoutGoal>): String {
    val tempoTotal = goals
        .filter { it.type == WorkoutGoalType.Tempo }
        .sumOf { (rawWorkoutValue(it.value).replace(',', '.').toDoubleOrNull() ?: 0.0) * it.reps }
    val distanceTotal = goals
        .filter { it.type == WorkoutGoalType.Distancia }
        .sumOf { (rawWorkoutValue(it.value).replace(',', '.').toDoubleOrNull() ?: 0.0) * it.reps }
    return listOfNotNull(
        if (tempoTotal > 0.0) "${formatSectionNumber(tempoTotal)} min" else null,
        if (distanceTotal > 0.0) "${formatSectionNumber(distanceTotal)} km" else null
    ).joinToString(" | ").ifBlank { "0 min" }
}

fun formatSectionNumber(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else "%.1f".format(java.util.Locale.US, value)

fun rawWorkoutValue(value: String): String =
    value.filter { it.isDigit() || it == ',' || it == '.' }

fun formatWorkoutValue(label: String, rawValue: String): String {
    val number = rawWorkoutValue(rawValue).replace(',', '.')
    if (number.isBlank()) return ""
    val normalized = number.trimEnd('.')
    return when (label) {
        "Distancia" -> "$normalized km"
        "Repeticoes" -> "${normalized}x"
        else -> "$normalized min"
    }
}

@Composable
fun WorkoutCreatedStep(onBackToWorkouts: () -> Unit, onAddModality: () -> Unit, onDelete: (() -> Unit)?) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onBackToWorkouts,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C1252), contentColor = Color.White)
        ) {
            Image(painterResource(R.drawable.ic_option_treinos), contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Voltar", Modifier.padding(start = 8.dp))
        }
        Button(
            onClick = onAddModality,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep)
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Modalidade", Modifier.padding(start = 6.dp), fontSize = 13.sp)
        }
    }
    if (onDelete != null) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(onClick = onDelete) {
                Text("Excluir treino", color = Color.White.copy(alpha = .58f), fontSize = 12.sp)
            }
        }
    }
}
