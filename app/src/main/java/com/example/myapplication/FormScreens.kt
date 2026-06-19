package com.example.myapplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CalendarMonth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.myapplication.data.CommunityPost
import com.example.myapplication.data.CommunityPostType
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(
    event: Event?,
    onBack: () -> Unit,
    onSave: (Event) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var name by remember(event?.id) { mutableStateOf(event?.name.orEmpty()) }
    var eventDate by remember(event?.id) { mutableStateOf(event?.eventDate ?: defaultEventDate()) }
    var location by remember(event?.id) { mutableStateOf(event?.location.orEmpty()) }
    var description by remember(event?.id) { mutableStateOf(event?.description.orEmpty()) }

    SimpleFormScaffold(
        title = if (event == null) "Novo evento" else "Editar evento",
        onBack = onBack,
        onDelete = if (event != null && onDelete != null) ({ onDelete(event.id) }) else null
    ) {
        item { FormTextField(name, { name = it }, "Nome do evento") }
        item { EventDatePickerField(eventDate, { eventDate = it }) }
        item { FormTextField(location, { location = it }, "Local") }
        item { FormTextField(description, { description = it }, "Descricao") }
        item {
            SaveButton(enabled = name.isNotBlank() && eventDate.isNotBlank()) {
                onSave(Event(event?.id.orEmpty(), name.trim(), description.trim(), eventDate.trim(), location.trim()))
            }
        }
    }
}

@Composable
fun DirectoryFormScreen(
    title: String,
    entry: DirectoryEntry?,
    nameLabel: String,
    descriptionLabel: String,
    selectableStudents: List<Student> = emptyList(),
    onBack: () -> Unit,
    onSave: (DirectoryEntry) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var name by remember(entry?.id) { mutableStateOf(entry?.name.orEmpty()) }
    var description by remember(entry?.id) { mutableStateOf(entry?.description.orEmpty()) }
    var status by remember(entry?.id) { mutableStateOf(entry?.status ?: "active") }
    var selectedStudentIds by remember(entry?.id) { mutableStateOf(entry?.studentIds.orEmpty().toSet()) }
    var studentQuery by remember { mutableStateOf("") }
    val filteredStudents = selectableStudents.filter { student ->
        student.name.contains(studentQuery, true) ||
            student.email.contains(studentQuery, true) ||
            student.phone.contains(studentQuery, true)
    }

    SimpleFormScaffold(
        title = title,
        onBack = onBack,
        onDelete = if (entry != null && onDelete != null) ({ onDelete(entry.id) }) else null
    ) {
        item { FormTextField(name, { name = it }, nameLabel) }
        item { FormTextField(description, { description = it }, descriptionLabel) }
        if (selectableStudents.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Alunos do grupo", modifier = Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${selectedStudentIds.size} selecionados", color = Lime, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    StudentSearchBar(
                        value = studentQuery,
                        onValueChange = { studentQuery = it },
                        placeholder = "Pesquisar aluno",
                        modifier = Modifier.shadow(14.dp, RoundedCornerShape(50)).imePadding()
                    )
                }
            }
            items(filteredStudents) { student ->
                val selected = student.id in selectedStudentIds
                SelectableStudentRow(
                    student = student,
                    selected = selected,
                    onClick = {
                        selectedStudentIds = if (selected) selectedStudentIds - student.id else selectedStudentIds + student.id
                    }
                )
            }
        }
        item {
            SaveButton(enabled = name.isNotBlank()) {
                onSave(DirectoryEntry(entry?.id.orEmpty(), name.trim(), status, description.trim(), selectedStudentIds.toList()))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    student: Student?,
    routines: List<DirectoryEntry> = emptyList(),
    onBack: () -> Unit,
    onSave: (Student) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    val context = LocalContext.current
    var editing by remember(student?.id) { mutableStateOf(student == null) }
    var tab by remember { mutableStateOf("Dados") }
    var name by remember(student?.id) { mutableStateOf(student?.name.orEmpty()) }
    var phone by remember(student?.id) { mutableStateOf(student?.phone.orEmpty()) }
    var email by remember(student?.id) { mutableStateOf(student?.email.orEmpty()) }
    var routine by remember(student?.id) { mutableStateOf(student?.routine.orEmpty().ifBlank { student?.planName.orEmpty() }) }
    var routineQuery by remember(student?.id) { mutableStateOf(student?.routine.orEmpty().ifBlank { student?.planName.orEmpty() }) }
    var routinePickerOpen by remember(student?.id) { mutableStateOf(student == null) }
    var status by remember(student?.id) { mutableStateOf(student?.status ?: "active") }
    var billingDay by remember(student?.id) { mutableStateOf((student?.billingDay ?: 5).coerceIn(1, 28).toString()) }
    val title = if (student == null) "Novo aluno" else student.name
    val currentRoutine = routines.firstOrNull { it.name.equals(routine, ignoreCase = true) || it.name.equals(student?.planName, ignoreCase = true) }
    val routineMonthlyFee = currentRoutine?.description?.let { extractRoutinePrice(it) }.orEmpty()
    val filteredRoutines = routines.filter { entry ->
        entry.name.contains(routineQuery, ignoreCase = true) ||
            entry.description.contains(routineQuery, ignoreCase = true) ||
            entry.status.contains(routineQuery, ignoreCase = true)
    }
    val billingDayInt = billingDay.toIntOrNull()?.coerceIn(1, 28) ?: 5
    val isBillingDueToday = billingDayInt == Calendar.getInstance().get(Calendar.DAY_OF_MONTH).coerceAtMost(28)
    val tabs = if (student == null) listOf("Dados", "Rotinas") else listOf("Dados", "Rotinas", "Faturamento")
    val canSave = name.isNotBlank() && phone.isNotBlank() && routine.isNotBlank()

    EditableDetailScaffold(
        title = title.ifBlank { "Aluno" },
        editing = editing,
        onBack = onBack,
        onEdit = { editing = true },
        onSave = {
            onSave(
                Student(
                    id = student?.id.orEmpty(),
                    name = name.trim(),
                    email = email.trim(),
                    phone = phone.trim(),
                    routine = routine.trim(),
                    planName = routine.trim().ifBlank { "Sem rotina" },
                    status = status,
                    billingDay = billingDayInt,
                    monthlyFee = routineMonthlyFee
                )
            )
            editing = false
        },
        onDelete = if (student != null && onDelete != null) ({ onDelete(student.id) }) else null,
        saveEnabled = if (student == null) canSave else name.isNotBlank()
    ) {
        item { DetailTabs(tabs, tab) { tab = it } }
        when (tab) {
            "Dados" -> {
                if (editing) {
                    item { FormTextField(name, { name = it }, "Nome") }
                    item { FormTextField(phone, { phone = it }, "Telefone") }
                    if (student != null) {
                        item {
                            DetailInfoRow(
                                "Acesso do aluno",
                                "Email e foto sao definidos pelo aluno no primeiro acesso."
                            )
                        }
                    } else {
                        item {
                            DetailInfoRow(
                                "Acesso do aluno",
                                "Ao salvar, o app gera um codigo de 24h para voce enviar ao aluno."
                            )
                        }
                    }
                } else {
                    item { DetailInfoRow("Nome", student?.name.orEmpty()) }
                    item { DetailInfoRow("Telefone", student?.phone.orEmpty().ifBlank { "Sem telefone" }) }
                }
            }
            "Rotinas" -> {
                if (editing) {
                    item {
                        RoutineSearchSelectorHeader(
                            selectedRoutine = routine,
                            query = routineQuery,
                            onQueryChange = {
                                routineQuery = it
                                routinePickerOpen = true
                            },
                            open = routinePickerOpen,
                            onToggle = { routinePickerOpen = !routinePickerOpen }
                        )
                    }
                    if (routinePickerOpen) {
                        if (routines.isEmpty()) {
                            item { EmptyDetailState("Crie uma rotina antes de vincular ao cliente.") }
                        } else if (filteredRoutines.isEmpty()) {
                            item { EmptyDetailState("Nenhuma rotina encontrada.") }
                        } else {
                            items(filteredRoutines) { entry ->
                                SelectableRoutineListRow(
                                    routine = entry,
                                    selected = entry.name.equals(routine, ignoreCase = true),
                                    onClick = {
                                        routine = entry.name
                                        routineQuery = entry.name
                                        routinePickerOpen = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    item {
                        if (currentRoutine == null && routine.isBlank()) {
                            EmptyDetailState("Nenhuma rotina vinculada ainda.")
                        } else {
                            StudentRoutineCard(
                                routineName = currentRoutine?.name ?: routine.ifBlank { student?.planName ?: "Sem rotina" },
                                description = currentRoutine?.description.orEmpty(),
                                status = currentRoutine?.status ?: "active"
                            )
                        }
                    }
                }
            }
            else -> {
                if (editing) {
                    item {
                        DetailInfoRow("Valor mensal", formatMoneyLabel(routineMonthlyFee.ifBlank { student?.monthlyFee.orEmpty() }))
                    }
                    item { BillingDayPicker(billingDayInt) { billingDay = it.toString() } }
                } else {
                    item {
                        BillingSummaryCard(
                            billingDay = student?.billingDay ?: billingDayInt,
                            monthlyFee = routineMonthlyFee.ifBlank { student?.monthlyFee.orEmpty() },
                            dueToday = isBillingDueToday,
                            onGenerateCharge = {
                                Toast.makeText(
                                    context,
                                    "Cobrança gerada para ${student?.name ?: name} no dia $billingDayInt.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workout: Workout?,
    onBack: () -> Unit,
    onSave: (Workout) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var editing by remember(workout?.id) { mutableStateOf(workout == null) }
    var tab by remember { mutableStateOf("Dados") }
    var name by remember(workout?.id) { mutableStateOf(workout?.name.orEmpty()) }
    var description by remember(workout?.id) { mutableStateOf(workout?.description.orEmpty()) }
    var iconName by remember(workout?.id) { mutableStateOf(workout?.iconName ?: "directions_run") }
    var status by remember(workout?.id) { mutableStateOf(workout?.status ?: "active") }

    EditableDetailScaffold(
        title = workout?.name ?: "Novo treino",
        editing = editing,
        onBack = onBack,
        onEdit = { editing = true },
        onSave = {
            onSave(Workout(workout?.id.orEmpty(), name.trim(), description.trim(), iconName.trim(), status))
            editing = false
        },
        onDelete = if (workout != null && onDelete != null) ({ onDelete(workout.id) }) else null,
        saveEnabled = name.isNotBlank()
    ) {
        item { DetailTabs(listOf("Dados", "Etapas", "Alunos"), tab) { tab = it } }
        when (tab) {
            "Dados" -> {
                if (editing) {
                    item { FormTextField(name, { name = it }, "Nome do treino") }
                    item { FormTextField(description, { description = it }, "Descricao") }
                    item { FormTextField(iconName, { iconName = it }, "Icone") }
                    item { StatusPicker(status) { status = it } }
                } else {
                    item { DetailInfoRow("Nome", workout?.name.orEmpty()) }
                    item { DetailInfoRow("Descricao", workout?.description.orEmpty().ifBlank { "Sem descricao" }) }
                    item { DetailInfoRow("Status", workoutStatusLabel(workout?.status.orEmpty())) }
                }
            }
            "Etapas" -> item { EmptyDetailState("As etapas do treino serao exibidas aqui.") }
            else -> item { EmptyDetailState("Nenhum aluno vinculado a este treino.") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    group: DirectoryEntry?,
    students: List<Student>,
    onBack: () -> Unit,
    onSave: (DirectoryEntry) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var editing by remember(group?.id) { mutableStateOf(group == null) }
    var tab by remember { mutableStateOf("Dados") }
    var name by remember(group?.id) { mutableStateOf(group?.name.orEmpty()) }
    var description by remember(group?.id) { mutableStateOf(group?.description.orEmpty()) }
    var status by remember(group?.id) { mutableStateOf(group?.status ?: "active") }
    var selectedIds by remember(group?.id) { mutableStateOf(group?.studentIds.orEmpty().toSet()) }
    var query by remember { mutableStateOf("") }
    val filteredStudents = students.filter { it.name.contains(query, true) || it.email.contains(query, true) || it.phone.contains(query, true) }
    val members = students.filter { it.id in selectedIds }

    EditableDetailScaffold(
        title = group?.name ?: "Novo grupo",
        editing = editing,
        onBack = onBack,
        onEdit = { editing = true },
        onSave = {
            onSave(DirectoryEntry(group?.id.orEmpty(), name.trim(), status, description.trim(), selectedIds.toList()))
            editing = false
        },
        onDelete = if (group != null && onDelete != null) ({ onDelete(group.id) }) else null,
        saveEnabled = name.isNotBlank()
    ) {
        item { DetailTabs(listOf("Dados", "Alunos", "Eventos"), tab) { tab = it } }
        when (tab) {
            "Dados" -> {
                if (editing) {
                    item { FormTextField(name, { name = it }, "Nome do grupo") }
                    item { FormTextField(description, { description = it }, "Descricao") }
                } else {
                    item { DetailInfoRow("Nome", group?.name.orEmpty()) }
                    item { DetailInfoRow("Descricao", group?.description.orEmpty().ifBlank { "Sem descricao" }) }
                    item { DetailInfoRow("Alunos", "${members.size} selecionados") }
                }
            }
            "Alunos" -> {
                if (editing) {
                    item {
                        StudentSearchBar(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = "Pesquisar aluno",
                            modifier = Modifier.shadow(14.dp, RoundedCornerShape(50)).imePadding()
                        )
                    }
                    items(filteredStudents) { student ->
                        val selected = student.id in selectedIds
                        SelectableStudentRow(
                            student = student,
                            selected = selected,
                            onClick = { selectedIds = if (selected) selectedIds - student.id else selectedIds + student.id }
                        )
                    }
                } else {
                    if (members.isEmpty()) item { EmptyDetailState("Nenhum aluno selecionado.") }
                    items(members) { student -> DirectoryListRow(student.name, onClick = {}) }
                }
            }
            else -> item { EmptyDetailState("Nenhum evento vinculado a este grupo.") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event?,
    onBack: () -> Unit,
    onSave: (Event) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var editing by remember(event?.id) { mutableStateOf(event == null) }
    var tab by remember { mutableStateOf("Dados") }
    var name by remember(event?.id) { mutableStateOf(event?.name.orEmpty()) }
    var eventDate by remember(event?.id) { mutableStateOf(event?.eventDate ?: defaultEventDate()) }
    var location by remember(event?.id) { mutableStateOf(event?.location.orEmpty()) }
    var description by remember(event?.id) { mutableStateOf(event?.description.orEmpty()) }

    EditableDetailScaffold(
        title = event?.name ?: "Novo evento",
        editing = editing,
        onBack = onBack,
        onEdit = { editing = true },
        onSave = {
            onSave(Event(event?.id.orEmpty(), name.trim(), description.trim(), eventDate.trim(), location.trim()))
            editing = false
        },
        onDelete = if (event != null && onDelete != null) ({ onDelete(event.id) }) else null,
        saveEnabled = name.isNotBlank() && eventDate.isNotBlank()
    ) {
        item { DetailTabs(listOf("Dados", "Presenca", "Comunicacao"), tab) { tab = it } }
        when (tab) {
            "Dados" -> {
                if (editing) {
                    item { FormTextField(name, { name = it }, "Nome do evento") }
                    item { EventDatePickerField(eventDate, { eventDate = it }) }
                    item { FormTextField(location, { location = it }, "Local") }
                    item { FormTextField(description, { description = it }, "Descricao") }
                } else {
                    item { DetailInfoRow("Nome", event?.name.orEmpty()) }
                    item { DetailInfoRow("Data", event?.eventDate.orEmpty()) }
                    item { DetailInfoRow("Local", event?.location.orEmpty().ifBlank { "Sem local" }) }
                    item { DetailInfoRow("Descricao", event?.description.orEmpty().ifBlank { "Sem descricao" }) }
                }
            }
            "Presenca" -> item { EmptyDetailState("Nenhuma presenca registrada.") }
            else -> item { EmptyDetailState("Nenhuma comunicacao vinculada.") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableDetailScaffold(
    title: String,
    editing: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onDelete: (() -> Unit)?,
    saveEnabled: Boolean = true,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    Scaffold(
        containerColor = PurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text(title.ifBlank { "Detalhes" }, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar") } },
                actions = {
                    if (onDelete != null) IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, "Excluir") }
                    TextButton(onClick = if (editing) onSave else onEdit, enabled = if (editing) saveEnabled else true) {
                        Text(if (editing) "Salvar" else "Editar", color = Lime, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding(),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun EventDatePickerField(value: String, onValueChange: (String) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val calendar = remember(value) { calendarFromEventDate(value) }
    val displayValue = remember(value) {
        SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(calendar.time)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable {
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    val selected = calendarFromEventDate(value).apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                    }
                    onValueChange(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(selected.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        color = PurpleSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Lime, modifier = Modifier.size(20.dp))
            Column(Modifier.padding(start = 10.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Data", color = Color.White.copy(alpha = .58f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(displayValue, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

fun calendarFromEventDate(value: String): Calendar {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 7)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    runCatching {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).parse(value)
    }.getOrNull()?.let { calendar.time = it }
    return calendar
}

@Composable
fun DetailTabs(tabs: List<String>, selected: String, onSelected: (String) -> Unit) {
    SegmentedFilterBar(options = tabs, selected = selected, onSelected = onSelected)
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = Color.White.copy(alpha = .58f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(value.ifBlank { "-" }, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun StudentRoutineCard(routineName: String, description: String, status: String) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = Lime, modifier = Modifier.size(22.dp))
                Text(
                    routineName.ifBlank { "Sem rotina" },
                    modifier = Modifier.padding(start = 10.dp).weight(1f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
            val lines = description.lineSequence()
                .map { it.trim() }
                .filter { it.isNotBlank() && !it.startsWith("Valor:") }
                .toList()
            if (lines.isEmpty()) {
                Text("Estrutura ainda nao definida.", color = Color.White.copy(alpha = .68f), fontSize = 13.sp)
            } else {
                lines.forEach { line ->
                    Surface(color = PurpleBackground, shape = RoundedCornerShape(10.dp)) {
                        Text(
                            line,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 9.dp),
                            color = Color.White.copy(alpha = .78f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineSearchSelectorHeader(
    selectedRoutine: String,
    query: String,
    onQueryChange: (String) -> Unit,
    open: Boolean,
    onToggle: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Rotina do cliente", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = onToggle, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text(if (open) "Fechar" else "Trocar", color = Lime, fontWeight = FontWeight.Bold)
            }
        }
        if (!open && selectedRoutine.isNotBlank()) {
            StudentRoutineCard(
                routineName = selectedRoutine,
                description = "Toque em Trocar para selecionar outra rotina.",
                status = "active"
            )
        } else {
            StudentSearchBar(
                value = query,
                onValueChange = onQueryChange,
                placeholder = "Pesquisar rotina",
                modifier = Modifier.shadow(14.dp, RoundedCornerShape(50)).imePadding()
            )
        }
    }
}

@Composable
fun SelectableRoutineListRow(routine: DirectoryEntry, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (selected) Lime else PurpleSurface),
                contentAlignment = Alignment.Center
            ) {
                if (selected) Icon(Icons.Outlined.Check, contentDescription = null, tint = PurpleDeep, modifier = Modifier.size(15.dp))
            }
            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                Text(
                    routine.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (routine.description.isNotBlank()) {
                    Text(
                        routine.description,
                        color = Color.White.copy(alpha = .56f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Selecionar rotina",
                modifier = Modifier.size(18.dp),
                tint = Color.White.copy(alpha = 0.78f)
            )
        }
        HorizontalDivider(color = NavigationPurple, thickness = 0.8.dp)
    }
}

@Composable
fun BillingSummaryCard(
    billingDay: Int,
    monthlyFee: String,
    dueToday: Boolean,
    onGenerateCharge: () -> Unit
) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Payments, contentDescription = null, tint = Lime, modifier = Modifier.size(24.dp))
                Column(Modifier.padding(start = 10.dp).weight(1f)) {
                    Text("Cobrança mensal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Vence todo dia $billingDay", color = Color.White.copy(alpha = .62f), fontSize = 12.sp)
                }
                StatusBadge(if (dueToday) "Cobrar hoje" else "Agendada", if (dueToday) Color(0xFFFFC107) else Lime)
            }
            DetailInfoRow("Valor", formatMoneyLabel(monthlyFee))
            if (dueToday) {
                Surface(color = Color(0xFFFFC107).copy(alpha = .18f), shape = RoundedCornerShape(12.dp)) {
                    Text(
                        "Hoje é dia de cobrança deste cliente.",
                        Modifier.fillMaxWidth().padding(12.dp),
                        color = Color(0xFFFFD166),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
            Button(
                onClick = onGenerateCharge,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Outlined.Payments, contentDescription = null)
                Text("Gerar cobrança", Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BillingDayPicker(day: Int, onDayChange: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Dia da cobrança", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("S", "T", "Q", "Q", "S", "S", "D").forEach { label ->
                        Text(
                            label,
                            modifier = Modifier.weight(1f),
                            color = Color.White.copy(alpha = .52f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                (1..28).chunked(7).forEach { week ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        week.forEach { option ->
                            val selected = day == option
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clickable { onDayChange(option) },
                                color = if (selected) Lime else PurpleBackground,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        option.toString(),
                                        color = if (selected) PurpleDeep else Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatMoneyLabel(value: String): String =
    value.ifBlank { "R$ 0,00" }.let { raw ->
        if (raw.startsWith("R$")) raw else "R$ $raw"
    }

@Composable
fun EmptyDetailState(message: String) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Text(message, Modifier.fillMaxWidth().padding(18.dp), color = Color.White.copy(alpha = .72f))
    }
}

@Composable
fun SelectableStudentRow(student: Student, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        color = if (selected) Lime.copy(alpha = .18f) else PurpleSurface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(22.dp).clip(CircleShape).background(if (selected) Lime else PurpleBackground),
                contentAlignment = Alignment.Center
            ) {
                if (selected) Icon(Icons.Outlined.Check, null, tint = PurpleDeep, modifier = Modifier.size(15.dp))
            }
            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                Text(student.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                if (student.email.isNotBlank()) Text(student.email, color = Color.White.copy(alpha = .58f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StudentStatusPicker(status: String, onStatusChange: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("active" to "Em dia", "pending_payment" to "A pagar", "inactive" to "Inativo").forEach { (value, label) ->
            SlimFilterBadge(
                modifier = Modifier.weight(1f),
                label = label,
                selected = status == value,
                onClick = { onStatusChange(value) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleFormScaffold(
    title: String,
    onBack: () -> Unit,
    onDelete: (() -> Unit)? = null,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    Scaffold(
        containerColor = PurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar") } },
                actions = {
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, "Excluir") }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@Composable
fun SaveButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep)
    ) {
        Icon(Icons.Outlined.Check, null)
        Text("Salvar", Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
    }
}
@Composable
fun FormTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = PurpleSurface,
            unfocusedContainerColor = PurpleSurface,
            focusedIndicatorColor = Lime,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Lime
        )
    )
}

@Composable
fun StatusPicker(status: String, onStatusChange: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("active" to "Ativo", "draft" to "Rascunho", "inactive" to "Inativo").forEach { (value, label) ->
            SlimFilterBadge(
                modifier = Modifier.weight(1f),
                label = label,
                selected = status == value,
                onClick = { onStatusChange(value) }
            )
        }
    }
}
