package com.example.myapplication

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.ToneGenerator
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.myapplication.data.Challenge
import com.example.myapplication.data.DirectoryItem
import com.example.myapplication.data.Event
import com.example.myapplication.data.RunHistoryEntry
import com.example.myapplication.data.Student
import com.example.myapplication.data.Workout
import com.example.myapplication.data.WorkoutSessionPayload
import com.example.myapplication.data.WorkoutSessionStep
import com.example.myapplication.ui.AgeGoUiState
import com.example.myapplication.ui.theme.Lime
import com.example.myapplication.ui.theme.LimeMuted
import com.example.myapplication.ui.theme.PurpleBackground
import com.example.myapplication.ui.theme.PurpleDeep
import com.example.myapplication.ui.theme.PurpleSurface
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StudentPortalApp(
    state: AgeGoUiState,
    initialEventId: String? = null,
    pixKey: String,
    onProfileSave: (String, Uri?) -> Unit,
    onCheckInEvent: (String) -> Unit,
    onSaveEventRunResult: (String, WorkoutSessionPayload, () -> Unit) -> Unit,
    onSaveWorkoutSession: (WorkoutSessionPayload, () -> Unit) -> Unit,
    onRefresh: () -> Unit,
    onUploadMedia: suspend (Uri) -> String,
    onSubmitPaymentProof: (String) -> Unit = {},
    onLogout: () -> Unit,
    onClearMessage: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentStudent = state.students.firstOrNull()
    val paymentBlocked = currentStudent?.paymentStatus == "pending" && currentStudent.daysOverdue > 4
    fun runIfPaymentAllowed(action: () -> Unit) {
        if (paymentBlocked) {
            scope.launch {
                snackbarHostState.showSnackbar("Pagamento pendente há mais de 4 dias. Regularize no Financeiro para continuar treinando.")
            }
        } else {
            action()
        }
    }
    LaunchedEffect(state.message) {
        val text = state.message
        if (!text.isNullOrBlank()) {
            snackbarHostState.showSnackbar(text)
            onClearMessage()
        }
    }
    var currentRoute by rememberSaveable { mutableStateOf(initialEventId?.let { "student_event:$it" } ?: "hub_fit") }
    LaunchedEffect(initialEventId) {
        if (!initialEventId.isNullOrBlank()) currentRoute = "student_event:$initialEventId"
    }
    var selectedRoutineId by rememberSaveable { mutableStateOf<String?>(null) }
    val assignedWorkoutRef = state.students.firstOrNull()?.routine.orEmpty()
    val selectedWorkout = state.workouts.firstOrNull { it.id == selectedRoutineId }
        ?: state.workouts.firstOrNull { it.id == assignedWorkoutRef || it.name.equals(assignedWorkoutRef, true) }
        ?: state.workouts.firstOrNull()
    val availableRoutines = remember(state.workouts) { state.workouts.map { it.toStudentRoutineDirectory() } }
    val selectedRoutine = selectedWorkout?.toStudentRoutineDirectory()
    var scheduledDates by rememberSaveable { mutableStateOf(setOf<String>()) }
    LaunchedEffect(Unit) {
        val persisted = readScheduledDates(context)
        if (persisted.isNotEmpty() && scheduledDates.isEmpty()) scheduledDates = persisted
        scheduleOverdueWorkoutCheck(context)
    }
    var completedCycleSteps by rememberSaveable(selectedRoutine?.id) { mutableStateOf(0) }
    var showScheduleDialog by rememberSaveable { mutableStateOf(selectedRoutine != null && scheduledDates.isEmpty()) }
    var showPix by remember { mutableStateOf(false) }
    var showRunChooser by rememberSaveable { mutableStateOf(false) }
    var freeRun by rememberSaveable { mutableStateOf(false) }
    val showBottomBar = studentDestinations.any { it.route == currentRoute }
    var bottomBarCollapsed by remember { mutableStateOf(false) }
    val bottomBarScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: androidx.compose.ui.input.nestedscroll.NestedScrollSource): Offset {
                if (available.y < -2f) bottomBarCollapsed = true
                if (available.y > 2f) bottomBarCollapsed = false
                return Offset.Zero
            }
        }
    }

    Box(Modifier.fillMaxSize().background(PurpleBackground).nestedScroll(bottomBarScroll)) {
        val routineDays = remember(selectedRoutine?.description) { parseRoutineDays(selectedRoutine?.description.orEmpty()) }
        val currentRoutineDay = routineDays.getOrNull(if (routineDays.isEmpty()) 0 else completedCycleSteps % routineDays.size)
        when {
            currentRoute == "hub_fit" -> StudentHomeScreen(
                routine = selectedRoutine,
                day = currentRoutineDay,
                workouts = state.workouts,
                events = state.events.filter { it.eventDate.take(10) == todayKey() },
                allEvents = state.events,
                runHistory = state.runHistory,
                challenges = state.challenges,
                instructorName = state.instructorName,
                instructorAvatarUrl = state.instructorAvatarUrl,
                cycleStep = completedCycleSteps + 1,
                studentName = state.authSession?.name.orEmpty(),
                avatarUrl = state.authSession?.avatarUrl.orEmpty(),
                scheduledWeekdays = scheduledDates,
                announcements = state.announcements,
                student = currentStudent,
                paymentBlocked = paymentBlocked,
                onSettingsClick = { currentRoute = "perfil" },
                onPaymentClick = { currentRoute = "financeiro" },
                onStart = { runIfPaymentAllowed { freeRun = false; currentRoute = "workout_player" } },
                onStartChallenge = { challenge -> runIfPaymentAllowed { currentRoute = "challenge_run:${challenge.id}" } }
            )
            currentRoute == "workout_player" -> StudentWorkoutPlayerScreen(
                routine = selectedRoutine,
                day = currentRoutineDay,
                cycleStep = completedCycleSteps + 1,
                workouts = state.workouts,
                freeRun = freeRun,
                onBack = { currentRoute = "hub_fit" },
                onComplete = { payload ->
                    onSaveWorkoutSession(payload) {
                        markWorkoutCompletedToday(context)
                        completedCycleSteps += 1
                        currentRoute = "hub_fit"
                    }
                }
            )
            currentRoute == "minhas_rotinas" -> StudentRoutinesScreen(
                routines = availableRoutines,
                selectedRoutine = selectedRoutine,
                onSelectRoutine = {
                    selectedRoutineId = it.id
                    scheduledDates = emptySet()
                    showScheduleDialog = true
                    currentRoute = "hub_fit"
                },
                onBack = { currentRoute = "hub_fit" }
            )
            currentRoute == "financeiro" && showPix -> PixPaymentScreen(
                pixKey = pixKey,
                amount = selectedRoutine?.description?.let { extractRoutinePrice(it) }.orEmpty(),
                student = state.students.firstOrNull(),
                onBack = { showPix = false },
                onUploadProof = { uri ->
                    val url = onUploadMedia(uri)
                    onSubmitPaymentProof(url)
                }
            )
            currentRoute == "financeiro" -> StudentFinanceScreen(
                routine = selectedRoutine,
                pixKey = pixKey,
                student = state.students.firstOrNull(),
                onPay = { showPix = true },
                onBack = { currentRoute = "settings" }
            )
            currentRoute == "eventos" -> StudentEventsScreen(events = state.events) { currentRoute = "student_event:${it.id}" }
            currentRoute.startsWith("student_event:") -> {
                val eventId = currentRoute.substringAfter(":")
                val event = state.events.firstOrNull { it.id == eventId }
                if (event != null) {
                    LaunchedEffect(eventId) {
                        while (true) { kotlinx.coroutines.delay(3_000); onRefresh() }
                    }
                    LaunchedEffect(event.groupStatus, event.checkedIn) {
                        if (event.groupStatus == "running" && event.checkedIn) currentRoute = "event_run:$eventId"
                    }
                    StudentEventDetailScreen(
                        event = event,
                        onBack = { currentRoute = "eventos" },
                        onCheckIn = { runIfPaymentAllowed { onCheckInEvent(event.id) } }
                    )
                } else EmptyDetailState("Evento não encontrado.")
            }
            currentRoute.startsWith("event_run:") -> {
                val eventId = currentRoute.substringAfter(":")
                val event = state.events.firstOrNull { it.id == eventId }
                LaunchedEffect(eventId) {
                    while (true) { kotlinx.coroutines.delay(3_000); onRefresh() }
                }
                StudentWorkoutPlayerScreen(
                    routine = null,
                    day = null,
                    cycleStep = 1,
                    workouts = emptyList(),
                    freeRun = true,
                    autoStart = true,
                    forceFinish = event?.groupStatus == "finished",
                    headerTitle = event?.name ?: "Corrida em grupo",
                    headerSubtitle = "${event?.attendees?.size ?: 0} participantes",
                    onBack = { currentRoute = "student_event:$eventId" },
                    onComplete = { payload ->
                        onSaveEventRunResult(eventId, payload.copy(routineName = "Evento: ${event?.name.orEmpty()}")) {
                            markWorkoutCompletedToday(context)
                            currentRoute = "student_event:$eventId"
                        }
                    }
                )
            }
            currentRoute.startsWith("challenge_run:") -> {
                val challengeId = currentRoute.substringAfter(":")
                val challenge = state.challenges.firstOrNull { it.id == challengeId }
                StudentWorkoutPlayerScreen(
                    routine = null,
                    day = null,
                    cycleStep = 1,
                    workouts = emptyList(),
                    freeRun = true,
                    headerTitle = challenge?.name ?: "Desafio",
                    headerSubtitle = if (challenge?.targetType == "distance") {
                        "Meta: ${challenge.targetValue.cleanNumber()} km"
                    } else {
                        "Meta: ${challenge?.targetValue?.cleanNumber()} min"
                    },
                    onBack = { currentRoute = "hub_fit" },
                    onComplete = { payload ->
                        onSaveWorkoutSession(payload.copy(challengeId = challengeId, routineName = "Desafio: ${challenge?.name.orEmpty()}")) {
                            markWorkoutCompletedToday(context)
                            currentRoute = "hub_fit"
                        }
                    }
                )
            }
            currentRoute == "perfil" -> StudentProfileSummaryScreen(
                studentName = state.authSession?.name.orEmpty(),
                avatarUrl = state.authSession?.avatarUrl.orEmpty(),
                runHistory = state.runHistory,
                onBack = { currentRoute = "hub_fit" },
                onSettingsClick = { currentRoute = "settings" },
                onSelectRun = { currentRoute = "run_detail:${it.id}" }
            )
            currentRoute.startsWith("run_detail:") -> {
                val runId = currentRoute.substringAfter(":")
                val run = state.runHistory.firstOrNull { it.id == runId }
                if (run != null) {
                    RunDetailScreen(run = run, onBack = { currentRoute = "perfil" })
                } else EmptyDetailState("Corrida não encontrada.")
            }
            currentRoute == "settings" -> StudentSettingsScreen(
                profileName = state.authSession?.name.orEmpty(),
                profileAvatarUrl = state.authSession?.avatarUrl.orEmpty(),
                onProfileSave = onProfileSave,
                onBack = { currentRoute = "perfil" },
                onOpenFinanceiro = { currentRoute = "financeiro" },
                onLogout = onLogout
            )
        }

        if (showBottomBar) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PillBottomBar(
                    currentRoute = currentRoute,
                    collapsed = bottomBarCollapsed,
                    onNavigate = {
                        showPix = false
                        currentRoute = it
                    },
                    onPlay = { runIfPaymentAllowed { showRunChooser = true } },
                    items = studentDestinations
                )
            }
        }

        if (showRunChooser) {
            RunChooserDialog(
                hasRoutine = selectedRoutine != null && currentRoutineDay != null,
                onDismiss = { showRunChooser = false },
                onFreeRun = {
                    freeRun = true
                    showRunChooser = false
                    currentRoute = "workout_player"
                },
                onRoutineRun = {
                    freeRun = false
                    showRunChooser = false
                    currentRoute = "workout_player"
                }
            )
        }

        if (showScheduleDialog && selectedRoutine != null) {
            WeekScheduleDialog(
                routineName = selectedRoutine?.name.orEmpty(),
                routineDescription = selectedRoutine?.description.orEmpty(),
                selectedDays = scheduledDates,
                onSelectedDaysChange = { scheduledDates = it; saveScheduledDates(context, it) },
                onDismiss = { showScheduleDialog = false }
            )
        }

        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun RunChooserDialog(
    hasRoutine: Boolean,
    onDismiss: () -> Unit,
    onFreeRun: () -> Unit,
    onRoutineRun: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(color = PurpleDeep, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Como voce quer correr?", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("Escolha liberdade total ou siga os dias preparados pelo professor.", color = Color.White.copy(alpha = .65f), fontSize = 13.sp)
                Button(
                    onClick = onFreeRun,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) { Text("Corrida livre", fontWeight = FontWeight.Bold) }
                Button(
                    onClick = onRoutineRun,
                    enabled = hasRoutine,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleSurface, contentColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) { Text(if (hasRoutine) "Play no plano" else "Nenhum plano para hoje", fontWeight = FontWeight.Bold) }
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Agora nao", color = Color.White.copy(alpha = .7f)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    routine: DirectoryItem?,
    day: StudentRoutineDayPlan?,
    workouts: List<Workout> = emptyList(),
    events: List<Event>,
    allEvents: List<Event> = events,
    runHistory: List<RunHistoryEntry> = emptyList(),
    challenges: List<Challenge> = emptyList(),
    instructorName: String = "",
    instructorAvatarUrl: String = "",
    cycleStep: Int,
    studentName: String,
    avatarUrl: String,
    scheduledWeekdays: Set<String>,
    announcements: List<com.example.myapplication.data.Announcement>,
    student: Student? = null,
    paymentBlocked: Boolean = false,
    onSettingsClick: () -> Unit,
    onPaymentClick: () -> Unit = {},
    onStart: () -> Unit,
    onStartChallenge: (Challenge) -> Unit = {}
) {
    var selectedDayKey by remember { mutableStateOf<String?>(null) }
    var showCalendar by remember { mutableStateOf(false) }
    var selectedChallenge by remember { mutableStateOf<Challenge?>(null) }
    val eventDates = remember(allEvents) { allEvents.map { it.eventDate.take(10) }.toSet() }
    val panelEvents = remember(allEvents, selectedDayKey) {
        val key = selectedDayKey ?: return@remember emptyList()
        allEvents.filter { it.eventDate.take(10) == key }
    }
    val panelIsToday = selectedDayKey == todayKey()
    val panelDateLabel = remember(selectedDayKey) {
        selectedDayKey?.let { key ->
            runCatching { SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR")).format(SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(key)!!) }.getOrNull()
        }
    }
    val weekDays = remember(scheduledWeekdays) {
        val cal = Calendar.getInstance()
        val deltaToMonday = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
        cal.add(Calendar.DAY_OF_YEAR, -deltaToMonday)
        val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val labelFormat = SimpleDateFormat("EEE", Locale("pt", "BR"))
        List(7) {
            val key = keyFormat.format(cal.time)
            val label = labelFormat.format(cal.time).replaceFirstChar { it.uppercase() }.removeSuffix(".")
            val option = DayOption(key, label, key == todayKey())
            cal.add(Calendar.DAY_OF_YEAR, 1)
            option
        }
    }
    val weeklyGoalKm = remember(routine, workouts) {
        parseRoutineDays(routine?.description.orEmpty())
            .sumOf { plan -> plannedStepsForDay(plan, workouts).filter { it.targetType == "distance" }.sumOf { it.targetValue } }
    }
    val recentRuns = remember(runHistory) {
        val weekAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
        runHistory.filter { entry -> parseIsoDate(entry.completedAt)?.after(weekAgo) == true }
    }
    val currentKm = remember(recentRuns) { recentRuns.sumOf { it.distanceMeters } / 1000.0 }
    val totalTimeMs = remember(recentRuns) { recentRuns.sumOf { it.elapsedMs } }
    val dayPlannedSteps = remember(day, workouts) { plannedStepsForDay(day, workouts) }
    val dayDistanceKm = remember(dayPlannedSteps) { dayPlannedSteps.filter { it.targetType == "distance" }.sumOf { it.targetValue } }
    val dayTimeMin = remember(dayPlannedSteps) { dayPlannedSteps.filter { it.targetType == "time" }.sumOf { it.targetValue } }
    val latestAnnouncement = announcements.firstOrNull()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(top = 18.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(
                avatarUrl = avatarUrl,
                contentDescription = null,
                modifier = Modifier.size(42.dp).clickable(onClick = onSettingsClick)
            )
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text("Olá, ${studentName.ifBlank { "aluno" }}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(routine?.name ?: "Sem rotina ativa", color = Color.White.copy(alpha = .62f), fontSize = 12.sp)
            }
            IconButton(onClick = { showCalendar = true }) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = "Calendário de eventos", tint = Color.White)
            }
        }
        if (student?.paymentStatus == "pending") {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp).clickable(onClick = onPaymentClick),
                color = if (paymentBlocked) Color(0xFFFF6B6B) else Color(0xFFFFD166),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Payments, contentDescription = null, tint = PurpleDeep)
                    Column(Modifier.padding(start = 10.dp).weight(1f)) {
                        Text(
                            if (paymentBlocked) "Pagamento pendente · treinos bloqueados" else "Pagamento pendente",
                            color = PurpleDeep,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            if (paymentBlocked) {
                                "Mais de 4 dias de atraso. Regularize para voltar a treinar."
                            } else {
                                "Vence dia ${student.billingDay}. Toque para ver o financeiro."
                            },
                            color = PurpleDeep.copy(alpha = .8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(if (student?.paymentStatus == "pending") 6.dp else 18.dp))
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) { page ->
            if (page == 0) {
                Row(Modifier.fillMaxWidth().height(78.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    weekDays.forEach { weekDay ->
                        val hasWorkout = scheduledWeekdays.contains(weekDay.key)
                        Surface(
                            modifier = Modifier.weight(1f).fillMaxHeight().clickable {
                                selectedDayKey = if (selectedDayKey == weekDay.key) null else weekDay.key
                            },
                            color = when {
                                weekDay.isToday -> Lime
                                hasWorkout -> PurpleSurface
                                else -> PurpleSurface.copy(alpha = .4f)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                Modifier.fillMaxSize().padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    weekDay.label,
                                    color = if (weekDay.isToday) PurpleDeep else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(6.dp))
                                Box(
                                    Modifier.size(6.dp).clip(CircleShape)
                                        .background(if (hasWorkout) (if (weekDay.isToday) PurpleDeep else Lime) else Color.Transparent)
                                )
                            }
                        }
                    }
                }
            } else {
                Box(Modifier.fillMaxWidth().height(78.dp)) {
                    Icon(
                        Icons.Outlined.Flag,
                        contentDescription = "Meta",
                        tint = Lime,
                        modifier = Modifier.align(Alignment.TopEnd).size(20.dp)
                    )
                    Column(Modifier.fillMaxWidth().align(Alignment.BottomStart)) {
                        Text("Meta de distância da semana", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { if (weeklyGoalKm > 0) (currentKm / weeklyGoalKm).toFloat().coerceIn(0f, 1f) else 0f },
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(50)),
                            color = Lime,
                            trackColor = PurpleSurface
                        )
                        Row(Modifier.fillMaxWidth().padding(top = 6.dp)) {
                            Text(
                                "${currentKm.cleanNumber()} km atual",
                                color = Lime,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(formatElapsed(totalTimeMs) + " no total", color = Color.White.copy(alpha = .65f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center) {
            repeat(2) { index ->
                Box(
                    Modifier
                        .padding(3.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (pagerState.currentPage == index) Lime else Color.White.copy(alpha = .3f))
                        .clickable { scope.launch { pagerState.animateScrollToPage(index) } }
                )
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 18.dp),
            color = if (selectedDayKey != null) Color.Black else PurpleSurface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp, 22.dp, 16.dp, 140.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (selectedDayKey != null) {
                    item {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(if (panelIsToday) "Hoje" else panelDateLabel ?: "Dia selecionado", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text("Eventos, rotina e treino atual", color = Lime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            IconButton(onClick = { selectedDayKey = null }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Fechar", tint = Color.White)
                            }
                        }
                    }
                    item {
                        TodaySection(title = "Eventos") {
                            if (panelEvents.isEmpty()) {
                                Text("Nenhum evento neste dia.", color = Color.White.copy(alpha = .62f), fontSize = 13.sp)
                            } else {
                                panelEvents.sortedBy { it.eventDate }.forEach { event ->
                                    EventCard(event, containerColor = PurpleBackground)
                                }
                            }
                        }
                    }
                    if (panelIsToday) item {
                        Surface(color = PurpleDeep, shape = RoundedCornerShape(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().height(62.dp).padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (routine != null && day != null) "Rotina | Dia ${day.number}" else "Rotina | sem treino",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = onStart,
                                    enabled = routine != null && day != null,
                                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Text("Iniciar", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Surface(color = Lime, shape = RoundedCornerShape(20.dp)) {
                            Row(
                                Modifier.fillMaxWidth().padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text("Iniciar seu treino", color = PurpleDeep, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "Dia ${day?.number ?: cycleStep} | ${dayDistanceKm.cleanNumber()}km ${dayTimeMin.cleanNumber()}min",
                                        color = PurpleDeep.copy(alpha = .72f),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Surface(
                                    modifier = Modifier.size(60.dp).clickable(onClick = onStart),
                                    shape = CircleShape,
                                    color = Color.Black
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Rounded.PlayArrow, contentDescription = "Iniciar treino", tint = Color.White, modifier = Modifier.size(30.dp))
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Surface(color = PurpleBackground, shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ProfileAvatar(avatarUrl = instructorAvatarUrl, contentDescription = null, modifier = Modifier.size(30.dp))
                                    Text(
                                        instructorName.ifBlank { "Professor" },
                                        Modifier.padding(start = 10.dp),
                                        color = Lime,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    latestAnnouncement?.content ?: "Nenhum aviso publicado ainda.",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Desafios extras", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            if (challenges.isEmpty()) {
                                Text("Nenhum desafio disponível ainda.", color = Color.White.copy(alpha = .62f), fontSize = 13.sp)
                            } else {
                                ChallengeCarousel(challenges, onSelect = { selectedChallenge = it })
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCalendar) {
        EventsCalendarDialog(
            eventDates = eventDates,
            onDaySelected = { key ->
                selectedDayKey = key
                showCalendar = false
            },
            onDismiss = { showCalendar = false }
        )
    }

    if (selectedChallenge != null) {
        val challenge = selectedChallenge!!
        ModalBottomSheet(onDismissRequest = { selectedChallenge = null }, containerColor = PurpleBackground) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(challenge.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (challenge.targetType == "distance") "Meta: ${challenge.targetValue.cleanNumber()} km" else "Meta: ${challenge.targetValue.cleanNumber()} min",
                    color = Lime,
                    fontWeight = FontWeight.SemiBold
                )
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TrainingMetric("Concluíram", challenge.completions.toString(), Modifier.weight(1f))
                    TrainingMetric("Distância total", "${"%.1f".format(challenge.totalDistanceMeters / 1000.0)} km", Modifier.weight(1f))
                    TrainingMetric("Tempo total", formatElapsed(challenge.totalElapsedMs), Modifier.weight(1f))
                }
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Lime
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(20.dp).clickable {
                            selectedChallenge = null
                            onStartChallenge(challenge)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Iniciar desafio", color = PurpleDeep, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Color.Black) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ChallengeCarousel(challenges: List<Challenge>, onSelect: (Challenge) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { challenges.size })
    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 44.dp),
        pageSpacing = 14.dp,
        modifier = Modifier.fillMaxWidth().height(150.dp)
    ) { page ->
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val absOffset = kotlin.math.abs(pageOffset).coerceIn(0f, 1f)
        ChallengeCarouselCard(
            challenge = challenges[page],
            onClick = { onSelect(challenges[page]) },
            modifier = Modifier
                .fillMaxHeight()
                .graphicsLayer {
                    rotationY = pageOffset * -28f
                    val scale = 1f - 0.12f * absOffset
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - 0.35f * absOffset
                    cameraDistance = 10f * density
                }
        )
    }
}

@Composable
fun ChallengeCarouselCard(challenge: Challenge, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxWidth().clickable(onClick = onClick), color = PurpleBackground, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Flag, contentDescription = null, tint = Lime)
                if (challenge.myCompleted) {
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Outlined.Check, contentDescription = "Concluído", tint = Lime, modifier = Modifier.size(18.dp))
                }
            }
            Column {
                Text(challenge.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    if (challenge.targetType == "distance") "Meta: ${challenge.targetValue.cleanNumber()} km" else "Meta: ${challenge.targetValue.cleanNumber()} min",
                    color = Lime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text("${challenge.completions} concluíram", color = Color.White.copy(alpha = .55f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun EventsCalendarDialog(
    eventDates: Set<String>,
    onDaySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var monthOffset by rememberSaveable { mutableStateOf(0) }
    val month = remember(monthOffset) { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1); add(Calendar.MONTH, monthOffset) } }
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val monthLabel = remember(month.timeInMillis) { SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(month.time) }
    fun dateKey(day: Int) = formatter.format((month.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }.time)
    Dialog(onDismissRequest = onDismiss) {
        Surface(color = PurpleDeep, shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Calendário de eventos", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { monthOffset-- }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Mês anterior", tint = Color.White) }
                    Text(monthLabel, Modifier.weight(1f), color = Color.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { monthOffset++ }) { Icon(Icons.AutoMirrored.Outlined.ArrowForward, "Próximo mês", tint = Color.White) }
                }
                Row(Modifier.fillMaxWidth()) {
                    listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb").forEach { dayLabel ->
                        Text(dayLabel, Modifier.weight(1f), color = Color.White.copy(alpha = .5f), fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
                val emptyCells = month.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
                val cells = List<Int?>(emptyCells) { null } + (1..month.getActualMaximum(Calendar.DAY_OF_MONTH)).map { it }
                cells.chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (week + List<Int?>(7 - week.size) { null }).forEach { day ->
                            if (day == null) Spacer(Modifier.weight(1f).height(40.dp)) else {
                                val key = dateKey(day)
                                val hasEvent = key in eventDates
                                val isToday = key == todayKey()
                                Surface(
                                    modifier = Modifier.weight(1f).height(40.dp).clickable(enabled = hasEvent) { onDaySelected(key) },
                                    color = when { hasEvent -> Lime; isToday -> Color.White.copy(alpha = .14f); else -> Color.White.copy(alpha = .06f) },
                                    shape = RoundedCornerShape(10.dp)
                                ) { Box(contentAlignment = Alignment.Center) {
                                    Text(day.toString(), color = if (hasEvent) PurpleDeep else Color.White.copy(alpha = .85f), fontWeight = FontWeight.SemiBold)
                                } }
                            }
                        }
                    }
                }
                Text("Dias em verde têm eventos. Toque para ver os detalhes.", color = Color.White.copy(alpha = .58f), fontSize = 11.sp)
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Fechar", color = Color.White.copy(alpha = .7f)) }
            }
        }
    }
}

@Composable
fun StudentShortcut(label: String, iconRes: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(92.dp)) {
        Box(
            modifier = Modifier.size(68.dp).clip(CircleShape).background(PurpleBackground).clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(iconRes), contentDescription = label, tint = Color.White, modifier = Modifier.size(38.dp))
        }
        Text(label, Modifier.padding(top = 8.dp), color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 2)
    }
}

data class StudentRoutineDayPlan(
    val number: Int,
    val workouts: List<String>,
    val restDaysAfter: Int
)

@Composable
fun TodayWorkoutScreen(
    routine: DirectoryItem?,
    day: StudentRoutineDayPlan?,
    events: List<Event>,
    cycleStep: Int,
    onBack: () -> Unit,
    onStart: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.Black).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Hoje", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Text("Agenda, rotina e treino atual", color = Lime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
            }
        }
        item {
            TodaySection(title = "Eventos") {
                if (events.isEmpty()) {
                    Text("Nenhum evento para hoje.", color = Color.White.copy(alpha = .62f), fontSize = 13.sp)
                } else {
                    events.sortedBy { it.eventDate }.forEach { event ->
                        EventCard(event, containerColor = PurpleBackground)
                    }
                }
            }
        }
        if (routine == null || day == null) {
            item {
                TodaySection(title = "Rotina") {
                    Text("Nenhuma rotina ativa para hoje.", color = Color.White.copy(alpha = .62f), fontSize = 13.sp)
                }
            }
        } else {
            item {
                TodaySection(title = "Rotina") {
                    Surface(color = Lime, shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(routine.name, color = PurpleDeep.copy(alpha = .72f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Dia ${day.number}", color = PurpleDeep, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            Text("Passo $cycleStep. O ciclo avanca ao concluir, nao por semana.", color = PurpleDeep.copy(alpha = .76f), fontSize = 13.sp)
                        }
                    }
                    WorkoutPlanCard(day, dark = true)
                }
            }
            item {
                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Iniciar treino", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TodaySection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(color = PurpleDeep, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            content()
        }
    }
}

@Composable
fun WorkoutReadyScreen(
    routine: DirectoryItem?,
    day: StudentRoutineDayPlan?,
    cycleStep: Int,
    workouts: List<Workout>,
    onBack: () -> Unit,
    onComplete: (WorkoutSessionPayload) -> Unit
) {
    val context = LocalContext.current
    var running by remember { mutableStateOf(false) }
    var elapsedMs by remember { mutableStateOf(0L) }
    var distanceMeters by remember { mutableStateOf(0.0) }
    var paceSecondsPerKm by remember { mutableStateOf(0.0) }
    var started by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var stepBaseElapsedMs by remember { mutableStateOf(0L) }
    var stepBaseDistanceMeters by remember { mutableStateOf(0.0) }
    var alertedStep by remember { mutableStateOf(-1) }
    var finalSnapshot by remember { mutableStateOf<WorkoutTrackingSnapshot?>(null) }
    val hasLocationPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        hasLocationPermission.value = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action != WorkoutTrackingService.ACTION_UPDATE) return
                running = intent.getBooleanExtra(WorkoutTrackingService.EXTRA_RUNNING, false)
                elapsedMs = intent.getLongExtra(WorkoutTrackingService.EXTRA_ELAPSED_MS, 0L)
                distanceMeters = intent.getDoubleExtra(WorkoutTrackingService.EXTRA_DISTANCE_M, 0.0)
                paceSecondsPerKm = intent.getDoubleExtra(WorkoutTrackingService.EXTRA_PACE_SEC_KM, 0.0)
            }
        }
        val filter = IntentFilter(WorkoutTrackingService.ACTION_UPDATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(receiver, filter)
        }
        onDispose { runCatching { context.unregisterReceiver(receiver) } }
    }
    DisposableEffect(toneGenerator) {
        onDispose { toneGenerator.release() }
    }

    val plannedSteps = remember(day, workouts) { plannedStepsForDay(day, workouts) }
    val currentStep = plannedSteps.getOrNull(currentStepIndex)
    val stepElapsedMs = (elapsedMs - stepBaseElapsedMs).coerceAtLeast(0L)
    val stepDistanceMeters = (distanceMeters - stepBaseDistanceMeters).coerceAtLeast(0.0)
    val currentProgress = currentStep?.progressFor(stepElapsedMs, stepDistanceMeters) ?: 0f
    val currentStepCompleted = currentStep == null || currentProgress >= 1f
    val totalTimeMinutes = plannedSteps.filter { it.targetType == "time" }.sumOf { it.targetValue }
    val totalRestMinutes = plannedSteps.filter { it.targetType == "rest" }.sumOf { it.targetValue }
    val totalDistanceKm = plannedSteps.filter { it.targetType == "distance" }.sumOf { it.targetValue }

    LaunchedEffect(currentStepIndex, currentProgress) {
        if (started && !finished && currentStep != null && currentProgress >= 1f && alertedStep != currentStepIndex) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 500)
            alertedStep = currentStepIndex
            sendTrackingAction(context, WorkoutTrackingService.ACTION_PAUSE)
        }
    }

    fun startOrResume() {
        if (!hasLocationPermission.value) {
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            return
        }
        started = true
        sendTrackingAction(
            context,
            if (elapsedMs > 0L || distanceMeters > 0.0) WorkoutTrackingService.ACTION_RESUME else WorkoutTrackingService.ACTION_START
        )
    }

    fun finishWorkout() {
        val snapshot = readWorkoutTrackingSnapshot(context)
        finalSnapshot = snapshot
        finished = true
        started = false
        sendTrackingAction(context, WorkoutTrackingService.ACTION_PAUSE)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.Black).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 20.dp, 16.dp, 42.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Tudo pronto para começarmos o treino?", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
            }
            Text(routine?.name.orEmpty(), color = Lime, fontWeight = FontWeight.SemiBold)
        }
        if (day != null) {
            item { WorkoutPlanCard(day, dark = true, plannedSteps = plannedSteps) }
            item {
                Surface(color = PurpleDeep, shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Health check", color = Color.White, fontWeight = FontWeight.Bold)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TrainingMetric("Tempo", formatElapsed(elapsedMs), Modifier.weight(1f))
                            TrainingMetric("Distancia", "${"%.2f".format(distanceMeters / 1000.0)} km", Modifier.weight(1f))
                            TrainingMetric("Pace", formatPace(paceSecondsPerKm), Modifier.weight(1f))
                        }
                        Text(
                            if (hasLocationPermission.value) {
                                "O treino continua com a tela apagada enquanto a notificacao estiver ativa."
                            } else {
                                "Permita localizacao para medir distancia e pace com GPS."
                            },
                            color = Color.White.copy(alpha = .7f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            if (!hasLocationPermission.value) {
                                locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                            } else {
                                sendTrackingAction(context, if (elapsedMs > 0L || distanceMeters > 0.0) WorkoutTrackingService.ACTION_RESUME else WorkoutTrackingService.ACTION_START)
                            }
                        },
                        modifier = Modifier.weight(1f).height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(if (running) "Rodando" else if (elapsedMs > 0L) "Retomar" else "Comecar", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { sendTrackingAction(context, WorkoutTrackingService.ACTION_PAUSE) },
                        enabled = running,
                        modifier = Modifier.weight(1f).height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleDeep, contentColor = Color.White),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Pausar", fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                Button(
                    onClick = { shareWorkoutToInstagramStories(context, distanceMeters, elapsedMs, paceSecondsPerKm) },
                    enabled = elapsedMs > 0L || distanceMeters > 0.0,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDeep, contentColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Compartilhar nos stories", fontWeight = FontWeight.Bold)
                }
            }
            item {
                Button(
                    onClick = {
                        val snapshot = readWorkoutTrackingSnapshot(context)
                        onComplete(
                            WorkoutSessionPayload(
                                routineId = routine?.id.orEmpty(),
                                routineName = routine?.name.orEmpty(),
                                dayNumber = day.number,
                                cycleStep = cycleStep,
                                elapsedMs = snapshot.elapsedMs,
                                distanceMeters = snapshot.distanceMeters,
                                paceSecondsPerKm = snapshot.paceSecondsPerKm,
                                status = "completed",
                                plannedSteps = plannedSteps,
                                routePoints = snapshot.routePoints,
                                splits = snapshot.splits
                            )
                        )
                        sendTrackingAction(context, WorkoutTrackingService.ACTION_STOP)
                    },
                    enabled = elapsedMs > 0L || distanceMeters > 0.0,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Finalizar e avancar ciclo", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StudentWorkoutPlayerScreen(
    routine: DirectoryItem?,
    day: StudentRoutineDayPlan?,
    cycleStep: Int,
    workouts: List<Workout>,
    freeRun: Boolean = false,
    autoStart: Boolean = false,
    forceFinish: Boolean = false,
    headerTitle: String? = null,
    headerSubtitle: String? = null,
    onBack: () -> Unit,
    onComplete: (WorkoutSessionPayload) -> Unit
) {
    val context = LocalContext.current
    val initialSnapshot = remember { readWorkoutTrackingSnapshot(context) }
    var running by remember { mutableStateOf(false) }
    var elapsedMs by remember { mutableStateOf(initialSnapshot.elapsedMs) }
    var distanceMeters by remember { mutableStateOf(initialSnapshot.distanceMeters) }
    var paceSecondsPerKm by remember { mutableStateOf(initialSnapshot.paceSecondsPerKm) }
    var routePoints by remember { mutableStateOf(initialSnapshot.routePoints) }
    var started by rememberSaveable { mutableStateOf(initialSnapshot.elapsedMs > 0L || initialSnapshot.distanceMeters > 0.0) }
    var finished by rememberSaveable { mutableStateOf(false) }
    var currentStepIndex by rememberSaveable { mutableStateOf(0) }
    var stepBaseElapsedMs by rememberSaveable { mutableStateOf(0L) }
    var stepBaseDistanceMeters by rememberSaveable { mutableStateOf(0.0) }
    var alertedStep by rememberSaveable { mutableStateOf(-1) }
    var finalSnapshot by remember { mutableStateOf<WorkoutTrackingSnapshot?>(null) }
    val hasLocationPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        hasLocationPermission.value = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action != WorkoutTrackingService.ACTION_UPDATE) return
                running = intent.getBooleanExtra(WorkoutTrackingService.EXTRA_RUNNING, false)
                elapsedMs = intent.getLongExtra(WorkoutTrackingService.EXTRA_ELAPSED_MS, 0L)
                distanceMeters = intent.getDoubleExtra(WorkoutTrackingService.EXTRA_DISTANCE_M, 0.0)
                paceSecondsPerKm = intent.getDoubleExtra(WorkoutTrackingService.EXTRA_PACE_SEC_KM, 0.0)
                routePoints = parseRoutePoints(intent.getStringExtra(WorkoutTrackingService.EXTRA_ROUTE_POINTS).orEmpty())
            }
        }
        val filter = IntentFilter(WorkoutTrackingService.ACTION_UPDATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(receiver, filter)
        }
        onDispose { runCatching { context.unregisterReceiver(receiver) } }
    }
    DisposableEffect(toneGenerator) {
        onDispose { toneGenerator.release() }
    }

    val plannedSteps = remember(day, workouts, freeRun) { if (freeRun) emptyList() else plannedStepsForDay(day, workouts) }
    val currentStep = plannedSteps.getOrNull(currentStepIndex)
    val stepElapsedMs = (elapsedMs - stepBaseElapsedMs).coerceAtLeast(0L)
    val stepDistanceMeters = (distanceMeters - stepBaseDistanceMeters).coerceAtLeast(0.0)
    val currentProgress = currentStep?.progressFor(stepElapsedMs, stepDistanceMeters) ?: 0f
    val currentStepCompleted = freeRun || currentStep == null || currentProgress >= 1f
    val totalTimeMinutes = plannedSteps.filter { it.targetType == "time" }.sumOf { it.targetValue }
    val totalRestMinutes = plannedSteps.filter { it.targetType == "rest" }.sumOf { it.targetValue }
    val totalDistanceKm = plannedSteps.filter { it.targetType == "distance" }.sumOf { it.targetValue }

    LaunchedEffect(started, currentStepIndex, currentStep?.name) {
        if (started && currentStep != null) {
            sendTrackingGoal(context, currentStep, stepBaseElapsedMs, stepBaseDistanceMeters)
        }
    }

    LaunchedEffect(currentStepIndex, currentProgress) {
        if (started && currentStep != null && currentProgress >= 1f) alertedStep = currentStepIndex
    }

    fun startOrResume() {
        if (!hasLocationPermission.value) {
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            return
        }
        started = true
        sendTrackingAction(
            context,
            if (elapsedMs > 0L || distanceMeters > 0.0) WorkoutTrackingService.ACTION_RESUME else WorkoutTrackingService.ACTION_START
        )
    }

    fun finishWorkout() {
        val snapshot = readWorkoutTrackingSnapshot(context)
        finalSnapshot = snapshot
        finished = true
        started = false
        sendTrackingAction(context, WorkoutTrackingService.ACTION_PAUSE)
    }

    LaunchedEffect(autoStart) {
        if (autoStart && !started) startOrResume()
    }

    LaunchedEffect(forceFinish, started) {
        if (forceFinish && started) finishWorkout()
    }

    LaunchedEffect(forceFinish, finished) {
        if (forceFinish && finished) {
            val snapshot = finalSnapshot ?: readWorkoutTrackingSnapshot(context)
            onComplete(
                WorkoutSessionPayload(
                    routineId = routine?.id.orEmpty(),
                    routineName = routine?.name.orEmpty(),
                    dayNumber = day?.number ?: 0,
                    cycleStep = cycleStep,
                    elapsedMs = snapshot.elapsedMs,
                    distanceMeters = snapshot.distanceMeters,
                    paceSecondsPerKm = snapshot.paceSecondsPerKm,
                    status = "completed",
                    plannedSteps = plannedSteps,
                    routePoints = snapshot.routePoints,
                    splits = snapshot.splits
                )
            )
            sendTrackingAction(context, WorkoutTrackingService.ACTION_STOP)
        }
    }

    if (started) {
        WorkoutTrackingMapScreen(
            stepName = headerTitle ?: (if (freeRun) "Corrida livre" else currentStep?.name ?: "Última etapa"),
            headerSubtitle = headerSubtitle,
            stepTarget = if (freeRun) "Sem limite de tempo ou distância" else currentStep?.let { formatStepTarget(it) } ?: "Treino concluído",
            progress = currentProgress,
            running = running,
            elapsedMs = elapsedMs,
            distanceMeters = distanceMeters,
            paceSecondsPerKm = paceSecondsPerKm,
            routePoints = routePoints,
            nextLabel = if (freeRun) "Finalizar corrida" else if (currentStepIndex < plannedSteps.lastIndex) "Próxima etapa" else "Finalizar treino",
            nextEnabled = currentStepCompleted,
            onClose = onBack,
            onToggleRunning = {
                if (running) sendTrackingAction(context, WorkoutTrackingService.ACTION_PAUSE) else startOrResume()
            },
            onNext = {
                if (currentStepIndex < plannedSteps.lastIndex) {
                    currentStepIndex += 1
                    stepBaseElapsedMs = elapsedMs
                    stepBaseDistanceMeters = distanceMeters
                    alertedStep = -1
                    startOrResume()
                } else {
                    finishWorkout()
                }
            }
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.Black).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 20.dp, 16.dp, 42.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        if (day == null && !freeRun) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Sem treino para hoje", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
                }
            }
        } else if (!started && !finished) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(if (freeRun) "Sua corrida, no seu ritmo" else "Tudo pronto para comecarmos o treino?", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text(if (freeRun) "Sem limite de tempo ou distancia" else "Plano | Dia ${day?.number}", color = Lime, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
                }
            }
            item {
                Surface(color = PurpleDeep, shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(if (freeRun) "Corrida livre" else routine?.name.orEmpty(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TrainingMetric("Caminhada", "${totalTimeMinutes.cleanNumber()} min", Modifier.weight(1f))
                            TrainingMetric("Distancia min.", "${totalDistanceKm.cleanNumber()} km", Modifier.weight(1f))
                            TrainingMetric("Descanso", "${totalRestMinutes.cleanNumber()} min", Modifier.weight(1f))
                        }
                        Text(
                            if (hasLocationPermission.value) {
                                "O GPS e o timer continuam ativos com a tela apagada pela notificacao do treino."
                            } else {
                                "Permita localizacao para medir distancia, pace e progresso por km."
                            },
                            color = Color.White.copy(alpha = .7f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
            if (!freeRun && day != null) item { WorkoutPlanCard(day, dark = true, plannedSteps = plannedSteps) }
            item {
                Button(
                    onClick = { startOrResume() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Comecar", fontWeight = FontWeight.Bold)
                }
            }
        } else if (started) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Treino em andamento", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        Text(currentStep?.name ?: "Ultima etapa", color = Lime, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
                }
            }
            item {
                Surface(color = PurpleDeep, shape = RoundedCornerShape(18.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WorkoutProgressCircle(
                            progress = currentProgress,
                            centerText = if (running) "Pause" else "Play",
                            onCenterClick = {
                                if (running) sendTrackingAction(context, WorkoutTrackingService.ACTION_PAUSE) else startOrResume()
                            }
                        )
                        Text(
                            currentStep?.let { formatStepTarget(it) } ?: "Treino concluido",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TrainingMetric("Tempo", formatElapsed(elapsedMs), Modifier.weight(1f))
                            TrainingMetric("Distancia", "${"%.2f".format(distanceMeters / 1000.0)} km", Modifier.weight(1f))
                            TrainingMetric("Pace", formatPace(paceSecondsPerKm), Modifier.weight(1f))
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        if (currentStepIndex < plannedSteps.lastIndex) {
                            currentStepIndex += 1
                            stepBaseElapsedMs = elapsedMs
                            stepBaseDistanceMeters = distanceMeters
                            alertedStep = -1
                            startOrResume()
                        } else {
                            finishWorkout()
                        }
                    },
                    enabled = currentStepCompleted,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(if (currentStepIndex < plannedSteps.lastIndex) "Proxima etapa" else "Finalizar treino", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            val snapshot = finalSnapshot ?: readWorkoutTrackingSnapshot(context)
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Parabens pelo treino", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
                }
                Text("Seu rendimento ficou salvo para acompanhar a evolucao.", color = Lime, fontWeight = FontWeight.SemiBold)
            }
            item {
                Surface(color = PurpleDeep, shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Resumo geral", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TrainingMetric("Tempo", formatElapsed(snapshot.elapsedMs), Modifier.weight(1f))
                            TrainingMetric("Distancia", "${"%.2f".format(snapshot.distanceMeters / 1000.0)} km", Modifier.weight(1f))
                            TrainingMetric("Pace", formatPace(snapshot.paceSecondsPerKm), Modifier.weight(1f))
                        }
                        Text("Etapas concluidas: ${plannedSteps.size}", color = Color.White.copy(alpha = .7f), fontSize = 13.sp)
                    }
                }
            }
            item {
                Button(
                    onClick = { shareWorkoutToInstagramStories(context, snapshot.distanceMeters, snapshot.elapsedMs, snapshot.paceSecondsPerKm) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Compartilhar em redes externas", fontWeight = FontWeight.Bold)
                }
            }
            item {
                Button(
                    onClick = {
                        onComplete(
                            WorkoutSessionPayload(
                                routineId = routine?.id.orEmpty(),
                                routineName = routine?.name.orEmpty(),
                                dayNumber = day?.number ?: 0,
                                cycleStep = cycleStep,
                                elapsedMs = snapshot.elapsedMs,
                                distanceMeters = snapshot.distanceMeters,
                                paceSecondsPerKm = snapshot.paceSecondsPerKm,
                                status = "completed",
                                plannedSteps = plannedSteps,
                                routePoints = snapshot.routePoints,
                                splits = snapshot.splits
                            )
                        )
                        sendTrackingAction(context, WorkoutTrackingService.ACTION_STOP)
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Salvar e avancar ciclo", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WorkoutProgressCircle(
    progress: Float,
    centerText: String,
    onCenterClick: () -> Unit
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(230.dp)) {
        CircularProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            color = Lime,
            trackColor = Color.Black.copy(alpha = .35f),
            strokeWidth = 14.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("${(progress.coerceIn(0f, 1f) * 100).toInt()}%", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onCenterClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PurpleDeep),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 0.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text(centerText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TrainingMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(color = Color.Black.copy(alpha = .28f), shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = Color.White.copy(alpha = .58f), fontSize = 11.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun WorkoutPlanCard(day: StudentRoutineDayPlan, dark: Boolean = false, plannedSteps: List<WorkoutSessionStep> = emptyList()) {
    Surface(color = if (dark) PurpleDeep else PurpleSurface, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Planejamento", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            if (day.workouts.isEmpty()) {
                Text("Sem atividades definidas para este dia.", color = Color.White.copy(alpha = .62f))
            } else {
                day.workouts.forEachIndexed { index, workout ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(Modifier.size(24.dp).clip(CircleShape).background(Lime), contentAlignment = Alignment.Center) {
                            Text("${index + 1}", color = PurpleDeep, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(workout, Modifier.padding(start = 10.dp).weight(1f), color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            if (plannedSteps.isNotEmpty()) {
                Text("Atividades do dia", color = Lime, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                plannedSteps.forEachIndexed { index, step ->
                    Text(
                        "${index + 1}. ${step.name} - ${formatStepTarget(step)}",
                        color = Color.White.copy(alpha = .78f),
                        fontSize = 13.sp
                    )
                }
            }
            Text("Descanso apos este dia: ${day.restDaysAfter} dia(s)", color = Color.White.copy(alpha = .66f), fontSize = 12.sp)
        }
    }
}

@Composable
fun WeekScheduleDialog(
    routineName: String,
    routineDescription: String,
    selectedDays: Set<String>,
    onSelectedDaysChange: (Set<String>) -> Unit,
    onDismiss: () -> Unit,
    title: String = "Programar rotina",
    singleSelection: Boolean = false,
    helpText: String = "Escolha datas reais. O descanso da rotina é bloqueado automaticamente."
) {
    val restDays = remember(routineDescription, singleSelection) {
        if (singleSelection) 0 else parseRoutineDays(routineDescription).maxOfOrNull { it.restDaysAfter } ?: 0
    }
    var monthOffset by rememberSaveable { mutableStateOf(0) }
    val month = remember(monthOffset) { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1); add(Calendar.MONTH, monthOffset) } }
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val monthLabel = remember(month.timeInMillis) { SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(month.time) }
    fun dateKey(day: Int) = formatter.format((month.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }.time)
    fun blockedByRest(key: String): Boolean = selectedDays.any { selected ->
        val start = runCatching { formatter.parse(selected) }.getOrNull() ?: return@any false
        val target = runCatching { formatter.parse(key) }.getOrNull() ?: return@any false
        ((target.time - start.time) / 86_400_000L).toInt() in 1..restDays
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(color = PurpleDeep, shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(routineName, color = Lime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { monthOffset-- }, enabled = monthOffset > 0) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Mês anterior", tint = Color.White) }
                    Text(monthLabel, Modifier.weight(1f), color = Color.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { monthOffset++ }) { Icon(Icons.AutoMirrored.Outlined.ArrowForward, "Próximo mês", tint = Color.White) }
                }
                Row(Modifier.fillMaxWidth()) {
                    listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb").forEach { dayLabel ->
                        Text(dayLabel, Modifier.weight(1f), color = Color.White.copy(alpha = .5f), fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
                val emptyCells = month.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
                val cells = List<Int?>(emptyCells) { null } + (1..month.getActualMaximum(Calendar.DAY_OF_MONTH)).map { it }
                cells.chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (week + List<Int?>(7 - week.size) { null }).forEach { day ->
                            if (day == null) Spacer(Modifier.weight(1f).height(40.dp)) else {
                                val key = dateKey(day)
                                val selected = key in selectedDays
                                val blocked = key < todayKey() || (blockedByRest(key) && !selected)
                                Surface(
                                    modifier = Modifier.weight(1f).height(40.dp).clickable(enabled = !blocked) {
                                        onSelectedDaysChange(
                                            if (singleSelection) setOf(key)
                                            else if (selected) selectedDays - key else selectedDays + key
                                        )
                                    },
                                    color = when { selected -> Lime; blocked -> Color.White.copy(alpha = .04f); else -> Color.White.copy(alpha = .09f) },
                                    shape = RoundedCornerShape(10.dp)
                                ) { Box(contentAlignment = Alignment.Center) {
                                    Text(day.toString(), color = if (selected) PurpleDeep else Color.White.copy(alpha = if (blocked) .25f else .9f), fontWeight = FontWeight.SemiBold)
                                } }
                            }
                        }
                    }
                }
                Text(helpText, color = Color.White.copy(alpha = .58f), fontSize = 11.sp)
                Button(
                    onClick = onDismiss,
                    enabled = selectedDays.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Salvar programação", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StudentRoutinesScreen(
    routines: List<DirectoryItem>,
    selectedRoutine: DirectoryItem?,
    onSelectRoutine: (DirectoryItem) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 20.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Minhas rotinas", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
            }
            Text("Visualizacao do aluno, sem edicao de treinos.", color = Color.White.copy(alpha = .62f))
        }
        if (routines.isEmpty()) {
            item { EmptyDetailState("Nenhuma rotina disponivel.") }
        } else {
            items(routines, key = { it.id }) { routine ->
                val isSelected = routine.id == selectedRoutine?.id
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onSelectRoutine(routine) },
                    color = if (isSelected) Lime else PurpleSurface,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.ViewWeek,
                            contentDescription = null,
                            tint = if (isSelected) PurpleDeep else Lime,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(Modifier.padding(start = 12.dp).weight(1f)) {
                            Text(
                                routine.name,
                                color = if (isSelected) PurpleDeep else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                routine.description.ifBlank { "Estrutura personalizada pelo professor" },
                                color = if (isSelected) PurpleDeep.copy(alpha = .72f) else Color.White.copy(alpha = .62f),
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (isSelected) Icon(Icons.Outlined.Check, contentDescription = null, tint = PurpleDeep)
                    }
                }
            }
        }
    }
}

@Composable
fun StudentFinanceScreen(
    routine: DirectoryItem?,
    pixKey: String,
    student: Student?,
    onPay: () -> Unit,
    onBack: () -> Unit = {}
) {
    val amount = routine?.description?.let { extractRoutinePrice(it) }.orEmpty()
    val pending = student?.paymentStatus != "paid"
    val hasProofSubmitted = !student?.paymentProofUrl.isNullOrBlank()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 24.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Financeiro", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Seu extrato e faturas", color = Color.White.copy(alpha = .62f))
                }
                IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
            }
        }
        item {
            Surface(color = Lime, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Fatura atual", color = LimeMuted)
                    Text(formatMoneyLabel(amount), color = PurpleDeep, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text(routine?.name ?: "Sem rotina ativa", color = PurpleDeep.copy(alpha = .72f), fontSize = 13.sp)
                    if (pending && (student?.daysOverdue ?: 0) > 0) {
                        Text(
                            "${student?.daysOverdue}d de atraso · vence dia ${student?.billingDay ?: 5}",
                            color = PurpleDeep,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = onPay,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleDeep, contentColor = Color.White),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(Icons.Outlined.Payments, contentDescription = null)
                        Text("Pagar fatura atual", Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        if (pending) item {
            Surface(color = PurpleSurface, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Comprovante de pagamento", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (!student?.paymentProofRejectionReason.isNullOrBlank()) {
                        Surface(color = Color(0xFFFF6B6B).copy(alpha = .15f), shape = RoundedCornerShape(12.dp)) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Comprovante recusado", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(student?.paymentProofRejectionReason.orEmpty(), color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
                            }
                        }
                    }
                    Text(
                        if (hasProofSubmitted) {
                            "Comprovante enviado. Aguardando aprovação do professor."
                        } else {
                            "Depois de pagar, toque em \"Pagar fatura atual\" para anexar o comprovante na tela do Pix."
                        },
                        color = Color.White.copy(alpha = .65f),
                        fontSize = 13.sp
                    )
                }
            }
        }
        item { StudentExtractRow("Mensalidade", formatMoneyLabel(amount), if (pending) "Pendente" else "Pago", !pending) }
        item { StudentExtractRow("Mensalidade anterior", formatMoneyLabel(amount), "Pago", true) }
    }
}

@Composable
fun StudentExtractRow(title: String, amount: String, status: String, paid: Boolean) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Payments, contentDescription = null, tint = if (paid) Lime else Color(0xFFFFD166), modifier = Modifier.size(24.dp))
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(status, color = Color.White.copy(alpha = .62f), fontSize = 12.sp)
            }
            Text(amount, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PixPaymentScreen(
    pixKey: String,
    amount: String,
    student: Student? = null,
    onBack: () -> Unit,
    onUploadProof: suspend (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    val hasProofSubmitted = !student?.paymentProofUrl.isNullOrBlank()
    val scope = rememberCoroutineScope()
    var uploading by remember { mutableStateOf(false) }
    val documentPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            uploading = true
            scope.launch {
                runCatching { onUploadProof(uri) }
                uploading = false
            }
        }
    }
    val brCode = remember(pixKey, amount) { if (pixKey.isBlank()) "" else buildPixBrCode(pixKey = pixKey, amount = amount) }
    val qrBitmap = remember(brCode) {
        if (brCode.isBlank()) null else runCatching {
            val matrix = com.google.zxing.qrcode.QRCodeWriter().encode(brCode, com.google.zxing.BarcodeFormat.QR_CODE, 640, 640)
            android.graphics.Bitmap.createBitmap(640, 640, android.graphics.Bitmap.Config.RGB_565).apply {
                for (x in 0 until 640) for (y in 0 until 640) {
                    setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
        }.getOrNull()
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleDeep).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 20.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Pix", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
            }
        }
        item {
            Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
                Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    if (qrBitmap != null) {
                        Image(qrBitmap.asImageBitmap(), "QR Code Pix", Modifier.size(200.dp))
                    } else {
                        Icon(Icons.Outlined.QrCode2, contentDescription = null, tint = PurpleDeep, modifier = Modifier.size(150.dp))
                    }
                }
            }
        }
        item {
            Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Valor", color = Color.White.copy(alpha = .62f), fontSize = 12.sp)
                    Text(formatMoneyLabel(amount), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text("Chave Pix", color = Color.White.copy(alpha = .62f), fontSize = 12.sp)
                    Text(pixKey.ifBlank { "Chave Pix nao configurada" }, color = Lime, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        if (brCode.isNotBlank()) item {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(android.content.ClipboardManager::class.java)
                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Pix", brCode))
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                shape = RoundedCornerShape(50)
            ) {
                Text("Copiar código Pix", fontWeight = FontWeight.Bold)
            }
        }
        item {
            Surface(color = PurpleSurface, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Comprovante de pagamento", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (!student?.paymentProofRejectionReason.isNullOrBlank()) {
                        Surface(color = Color(0xFFFF6B6B).copy(alpha = .15f), shape = RoundedCornerShape(12.dp)) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Comprovante recusado", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(student?.paymentProofRejectionReason.orEmpty(), color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
                            }
                        }
                    }
                    Text(
                        if (hasProofSubmitted) {
                            "Comprovante enviado. Aguardando aprovação do professor."
                        } else {
                            "Depois de pagar, anexe o comprovante (foto ou PDF) para o professor aprovar."
                        },
                        color = Color.White.copy(alpha = .65f),
                        fontSize = 13.sp
                    )
                    Button(
                        onClick = { documentPicker.launch("*/*") },
                        enabled = !uploading,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            when {
                                uploading -> "Enviando..."
                                hasProofSubmitted -> "Reenviar comprovante"
                                else -> "Anexar comprovante"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        item {
            Text(
                "Confirme manualmente o recebimento apos o pagamento. Nao ha validacao automatica.",
                color = Color.White.copy(alpha = .55f),
                fontSize = 12.sp
            )
        }
    }
}


@Composable
fun StudentProfileSummaryScreen(
    studentName: String,
    avatarUrl: String,
    runHistory: List<RunHistoryEntry>,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onSelectRun: (RunHistoryEntry) -> Unit
) {
    val totalDistanceKm = remember(runHistory) { runHistory.sumOf { it.distanceMeters } / 1000.0 }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 14.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar", tint = Color.White) }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onSettingsClick) { Icon(Icons.Outlined.Settings, "Configurações", tint = Color.White) }
            }
        }
        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                ProfileAvatar(avatarUrl = avatarUrl, contentDescription = null, modifier = Modifier.size(84.dp))
                Spacer(Modifier.height(10.dp))
                Text(studentName.ifBlank { "Aluno" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${runHistory.size} corridas registradas · ${"%.1f".format(totalDistanceKm)} km no total",
                    color = Lime,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        item {
            Surface(color = PurpleSurface, shape = RoundedCornerShape(18.dp)) {
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    MonthlyKmChart(runHistory)
                }
            }
        }
        item {
            Text("Histórico de corridas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        if (runHistory.isEmpty()) {
            item { EmptyDetailState("Nenhuma corrida registrada ainda.") }
        } else {
            items(runHistory, key = { it.id }) { run ->
                RunHistoryRow(run, onClick = { onSelectRun(run) })
            }
        }
    }
}

@Composable
fun MonthlyKmChart(runHistory: List<RunHistoryEntry>) {
    val monthsBack = 6
    val now = remember { Calendar.getInstance() }
    val buckets = remember(runHistory) {
        (monthsBack - 1 downTo 0).map { offset ->
            val cal = (now.clone() as Calendar).apply { add(Calendar.MONTH, -offset) }
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val label = SimpleDateFormat("MMM", Locale("pt", "BR")).format(cal.time).replace(".", "").uppercase()
            val km = runHistory.filter { entry ->
                val entryCal = parseIsoDate(entry.completedAt) ?: return@filter false
                entryCal.get(Calendar.YEAR) == year && entryCal.get(Calendar.MONTH) == month
            }.sumOf { it.distanceMeters } / 1000.0
            label to km
        }
    }
    val maxKm = (buckets.maxOfOrNull { it.second } ?: 0.0).coerceAtLeast(1.0)
    Column(Modifier.fillMaxWidth()) {
        Text("Evolução mensal (km)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth().height(140.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            buckets.forEach { (label, km) ->
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.weight(1f))
                    Text("%.1f".format(km), color = Color.White.copy(alpha = .7f), fontSize = 9.sp)
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .fillMaxHeight(fraction = (km / maxKm).toFloat().coerceIn(0.04f, 1f))
                            .background(Lime, RoundedCornerShape(6.dp))
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(label, color = Color.White.copy(alpha = .55f), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun RunHistoryRow(run: RunHistoryEntry, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Outlined.DirectionsRun, contentDescription = null, tint = Lime, modifier = Modifier.size(26.dp))
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(run.routineName.ifBlank { "Corrida livre" }, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(formatRunDateLabel(run.completedAt), color = Color.White.copy(alpha = .58f), fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${"%.2f".format(run.distanceMeters / 1000.0)} km", color = Color.White, fontWeight = FontWeight.Bold)
                Text(formatElapsed(run.elapsedMs), color = Color.White.copy(alpha = .58f), fontSize = 12.sp)
            }
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = .5f),
                modifier = Modifier.padding(start = 8.dp).size(16.dp)
            )
        }
    }
}

@Composable
fun RunDetailScreen(run: RunHistoryEntry, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(PurpleDeep)) {
        MultiRouteMap(routes = listOf("run" to run.routePoints), modifier = Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Surface(shape = CircleShape, color = PurpleDeep) {
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
                }
            }
            Spacer(Modifier.weight(1f))
            Surface(color = PurpleDeep, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(run.routineName.ifBlank { "Corrida livre" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(formatRunDateLabel(run.completedAt), color = Color.White.copy(alpha = .6f), fontSize = 12.sp)
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TrainingMetric("Tempo", formatElapsed(run.elapsedMs), Modifier.weight(1f))
                        TrainingMetric("Distancia", "${"%.2f".format(run.distanceMeters / 1000.0)} km", Modifier.weight(1f))
                        TrainingMetric("Pace", formatPace(run.paceSecondsPerKm), Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

fun parseIsoDate(value: String?): Calendar? {
    if (value.isNullOrBlank()) return null
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )
    for (pattern in patterns) {
        val parsed = runCatching {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            sdf.parse(value)
        }.getOrNull()
        if (parsed != null) return Calendar.getInstance().apply { time = parsed }
    }
    return null
}

fun formatRunDateLabel(iso: String?): String {
    val cal = parseIsoDate(iso) ?: return "Data desconhecida"
    return SimpleDateFormat("dd 'de' MMMM, HH:mm", Locale("pt", "BR")).format(cal.time)
}

@Composable
fun StudentSettingsScreen(
    profileName: String,
    profileAvatarUrl: String,
    onProfileSave: (String, Uri?) -> Unit,
    onBack: () -> Unit,
    onOpenFinanceiro: () -> Unit,
    onLogout: () -> Unit
) {
    var draftName by remember(profileName) { mutableStateOf(profileName.ifBlank { "Usuario" }) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var editingProfile by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> selectedPhotoUri = uri }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleDeep).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 14.dp, 16.dp, 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
            }
        }
        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                if (selectedPhotoUri != null || profileAvatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = selectedPhotoUri ?: profileAvatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(92.dp).clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(92.dp).clip(CircleShape)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(draftName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Editar",
                        tint = Lime,
                        modifier = Modifier.padding(start = 6.dp).size(18.dp).clickable { editingProfile = !editingProfile }
                    )
                }
                if (editingProfile) {
                    Spacer(Modifier.height(10.dp))
                    FormTextField(draftName, { draftName = it }, "Nome exibido")
                    TextButton(onClick = { photoPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Escolher foto do dispositivo", color = Lime, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onProfileSave(draftName, selectedPhotoUri); editingProfile = false },
                        enabled = draftName.length > 1,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Salvar perfil", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            SettingsListRow(
                title = "Financeiro",
                subtitle = "Faturas, plano e pagamento via Pix",
                icon = Icons.Outlined.Payments,
                onClick = onOpenFinanceiro
            )
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { confirmClear = true },
                color = Color(0xFF17071E),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Sair", Modifier.fillMaxWidth().padding(16.dp), color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (confirmClear) {
        Dialog(onDismissRequest = { confirmClear = false }) {
            Surface(color = PurpleDeep, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Sair da conta?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "Isso apaga a sessao salva neste aparelho.",
                        color = Color.White.copy(alpha = .68f),
                        fontSize = 13.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextButton(onClick = { confirmClear = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar", color = Color.White)
                        }
                        Button(
                            onClick = { confirmClear = false; onLogout() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PurpleDeep),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Sair", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentEventsScreen(events: List<Event>, onEventClick: (Event) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 24.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Eventos", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold) }
        if (events.isEmpty()) item { EmptyDetailState("Nenhum evento disponivel.") }
        items(events.sortedBy { it.eventDate }, key = { it.id }) { event ->
            Box(Modifier.clickable { onEventClick(event) }) { EventCard(event) }
        }
    }
}

@Composable
fun StudentEventDetailScreen(event: Event, onBack: () -> Unit, onCheckIn: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { TextButton(onClick = onBack) { Text("Voltar", color = Lime) } }
        item { Text(event.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold) }
        item { DetailInfoRow("Data", event.eventDate) }
        item { DetailInfoRow("Local", event.location.orEmpty().ifBlank { "Sem local" }) }
        item { DetailInfoRow("Descrição", event.description.orEmpty().ifBlank { "Sem descrição" }) }
        item {
            Button(
                onClick = onCheckIn,
                enabled = !event.checkedIn,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                shape = RoundedCornerShape(50)
            ) { Text(if (event.checkedIn) "Presença confirmada" else "Confirmar presença", fontWeight = FontWeight.Bold) }
        }
        item {
            Text(
                when (event.groupStatus) {
                    "running" -> "A corrida começou. Abrindo o GPS…"
                    "finished" -> "Corrida finalizada pelo professor."
                    "checkin" -> if (event.checkedIn) "Presença confirmada. Aguarde o professor iniciar a corrida." else "Confirme sua presença para participar."
                    else -> "Aguardando o professor iniciar a corrida em grupo."
                },
                color = if (event.groupStatus == "running") Lime else Color.White.copy(alpha = .65f)
            )
        }
    }
}

@Composable
fun StudentSimpleRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Lime, modifier = Modifier.size(24.dp))
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle.ifBlank { "Sem descricao" }, color = Color.White.copy(alpha = .62f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = Color.White.copy(alpha = .72f), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun InstructorSettingsScreen(
    profileName: String,
    profileAvatarUrl: String,
    pixKey: String,
    onProfileSave: (String, Uri?) -> Unit,
    onPixKeyChange: (String) -> Unit,
    onBack: () -> Unit,
    onClearLocalData: () -> Unit,
    showFinancialSection: Boolean = true,
    profileFieldLabel: String = "Nome exibido",
    clearDataLabel: String = "Delete meus dados locais",
    securityDescription: String = "O acesso usa token. Para criar um professor do zero, limpe os dados locais e volte para o cadastro."
) {
    var draftPix by remember(pixKey) { mutableStateOf(pixKey) }
    var professorName by remember(profileName) { mutableStateOf(profileName.ifBlank { "Usuario" }) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var editingProfile by remember { mutableStateOf(false) }
    var openSection by remember { mutableStateOf("financeiro") }
    var pushNotifications by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(true) }
    var compactMode by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedPhotoUri = uri
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleDeep).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 14.dp, 16.dp, 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
            }
        }
        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                if (selectedPhotoUri != null || profileAvatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = selectedPhotoUri ?: profileAvatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(92.dp).clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(92.dp).clip(CircleShape)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(professorName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Editar",
                        tint = Lime,
                        modifier = Modifier.padding(start = 6.dp).size(18.dp).clickable { editingProfile = !editingProfile }
                    )
                }
                if (editingProfile) {
                    Spacer(Modifier.height(10.dp))
                    FormTextField(professorName, { professorName = it }, profileFieldLabel)
                    TextButton(onClick = { photoPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Escolher foto do dispositivo", color = Lime, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            onProfileSave(professorName, selectedPhotoUri)
                            editingProfile = false
                        },
                        enabled = professorName.length > 1,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Salvar perfil", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        if (showFinancialSection) item {
            EditableSettingsSection(
                title = "Financeiro",
                subtitle = "Chave Pix e dados de cobranca",
                icon = Icons.Outlined.Payments,
                expanded = openSection == "financeiro",
                onClick = { openSection = if (openSection == "financeiro") "" else "financeiro" }
            ) {
                FormTextField(draftPix, { draftPix = it }, "Chave Pix do professor")
                Button(
                    onClick = { onPixKeyChange(draftPix) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Salvar financeiro", fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            EditableSettingsSection(
                title = "Notificacoes",
                subtitle = "Alertas de cobranca e treinos",
                icon = Icons.Outlined.Settings,
                expanded = openSection == "notificacoes",
                onClick = { openSection = if (openSection == "notificacoes") "" else "notificacoes" }
            ) {
                SettingsSwitchRow("Push no app", pushNotifications) { pushNotifications = it }
                SettingsSwitchRow("Email de cobranca", emailNotifications) { emailNotifications = it }
            }
        }
        item {
            EditableSettingsSection(
                title = "Preferencias",
                subtitle = "Tema e experiencia",
                icon = Icons.Outlined.Settings,
                expanded = openSection == "preferencias",
                onClick = { openSection = if (openSection == "preferencias") "" else "preferencias" }
            ) {
                SettingsSwitchRow("Modo escuro", darkMode) { darkMode = it }
                SettingsSwitchRow("Listas compactas", compactMode) { compactMode = it }
            }
        }
        item {
            EditableSettingsSection(
                title = "Seguranca e acesso",
                subtitle = "Sessao salva neste aparelho",
                icon = Icons.Outlined.Settings,
                expanded = openSection == "seguranca",
                onClick = { openSection = if (openSection == "seguranca") "" else "seguranca" }
            ) {
                Text(
                    securityDescription,
                    color = Color.White.copy(alpha = .68f),
                    fontSize = 12.sp
                )
            }
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { confirmClear = true },
                color = Color(0xFF17071E),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(clearDataLabel, Modifier.fillMaxWidth().padding(16.dp), color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (confirmClear) {
        Dialog(onDismissRequest = { confirmClear = false }) {
            Surface(color = PurpleDeep, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Limpar login salvo?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "Isso apaga a sessao salva neste aparelho e volta para a tela inicial para criar um professor do zero.",
                        color = Color.White.copy(alpha = .68f),
                        fontSize = 13.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextButton(onClick = { confirmClear = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar", color = Color.White)
                        }
                        Button(
                            onClick = {
                                confirmClear = false
                                onClearLocalData()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PurpleDeep),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Limpar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditableSettingsSection(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    expanded: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth().clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Lime, modifier = Modifier.size(24.dp))
                Column(Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = Color.White.copy(alpha = .58f), fontSize = 12.sp)
                }
                Text(if (expanded) "Fechar" else "Editar", color = Lime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            if (expanded) content()
        }
    }
}

@Composable
fun SettingsSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsListRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit = {}) {
    Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Lime, modifier = Modifier.size(24.dp))
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(alpha = .58f), fontSize = 12.sp)
            }
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = Color.White.copy(alpha = .72f), modifier = Modifier.size(18.dp))
        }
    }
}

fun dayOfWeek(dateKey: String): Int {
    val calendar = Calendar.getInstance()
    runCatching {
        calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateKey)!!
    }
    return calendar.get(Calendar.DAY_OF_WEEK)
}

fun todayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)

fun parseRoutineDays(description: String): List<StudentRoutineDayPlan> =
    description.lineSequence()
        .mapNotNull { line ->
            val trimmed = line.trim()
            if (!trimmed.startsWith("Dia ")) return@mapNotNull null
            val number = trimmed.substringAfter("Dia ").substringBefore(":").toIntOrNull() ?: return@mapNotNull null
            val body = trimmed.substringAfter(":", "")
            val workoutText = body.substringBefore("|").trim()
            val restDays = body.substringAfter("descanso", "0")
                .filter { it.isDigit() }
                .toIntOrNull() ?: 0
            StudentRoutineDayPlan(
                number = number,
                workouts = workoutText
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() && it != "sem treinos" },
                restDaysAfter = restDays
            )
        }
        .toList()

fun restBlockedWeekdays(selectedDays: Set<Int>, restDaysAfter: Int): Set<Int> {
    if (restDaysAfter <= 0 || selectedDays.isEmpty()) return emptySet()
    val week = listOf(
        Calendar.SUNDAY,
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY
    )
    return selectedDays.flatMap { selected ->
        val index = week.indexOf(selected).coerceAtLeast(0)
        (1..restDaysAfter).map { offset -> week[(index + offset) % week.size] }
    }.toSet()
}

fun sendTrackingAction(context: Context, action: String) {
    val intent = Intent(context, WorkoutTrackingService::class.java).setAction(action)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ContextCompat.startForegroundService(context, intent)
    } else {
        context.startService(intent)
    }
}

fun sendTrackingGoal(
    context: Context,
    step: WorkoutSessionStep,
    baseElapsedMs: Long,
    baseDistanceMeters: Double
) {
    val intent = Intent(context, WorkoutTrackingService::class.java)
        .setAction(WorkoutTrackingService.ACTION_SET_GOAL)
        .putExtra(WorkoutTrackingService.EXTRA_GOAL_TYPE, step.targetType)
        .putExtra(WorkoutTrackingService.EXTRA_GOAL_VALUE, step.targetValue)
        .putExtra(WorkoutTrackingService.EXTRA_GOAL_LABEL, "${step.name}: ${formatStepTarget(step)}")
        .putExtra(WorkoutTrackingService.EXTRA_GOAL_BASE_ELAPSED_MS, baseElapsedMs)
        .putExtra(WorkoutTrackingService.EXTRA_GOAL_BASE_DISTANCE_M, baseDistanceMeters)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ContextCompat.startForegroundService(context, intent)
    else context.startService(intent)
}

fun plannedStepsForDay(day: StudentRoutineDayPlan?, workouts: List<Workout>): List<WorkoutSessionStep> {
    if (day == null) return emptyList()
    val byName = workouts.associateBy { it.name.lowercase() }
    return day.workouts.flatMap { workoutName ->
        val workout = byName[workoutName.lowercase()]
        val steps = parseWorkoutSteps(workout?.description.orEmpty(), day.number)
        if (steps.isEmpty()) {
            listOf(WorkoutSessionStep(name = workoutName, targetType = "open", targetValue = 0.0, unit = ""))
        } else {
            steps.map { it.copy(name = "${workoutName} - ${it.name}") }
        }
    }
}

fun parseWorkoutSteps(description: String, dayNumber: Int? = null): List<WorkoutSessionStep> =
    description.lineSequence()
        .filter { line ->
            val trimmed = line.trim()
            (trimmed.startsWith("Dia ") || trimmed.startsWith("Secao ")) &&
                (dayNumber == null || Regex("""\d+""").find(trimmed.substringBefore(":"))?.value?.toIntOrNull() == dayNumber)
        }
        .flatMap { line ->
            line.substringAfter(":")
                .split(";")
                .flatMap { rawGoal ->
                    val name = rawGoal.substringBefore(":").trim().ifBlank { "Etapa" }
                    val valueText = rawGoal.substringAfter(":", "").trim()
                    val reps = valueText.substringAfterLast("x", "1").filter { it.isDigit() }.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    val number = valueText.replace(',', '.').let { Regex("""\d+(\.\d+)?""").find(it)?.value?.toDoubleOrNull() ?: 0.0 }
                    val targetType = when {
                        valueText.contains("km", ignoreCase = true) -> "distance"
                        valueText.contains("desc", ignoreCase = true) -> "rest"
                        valueText.contains("min", ignoreCase = true) -> "time"
                        else -> "open"
                    }
                    val unit = when (targetType) {
                        "distance" -> "km"
                        "time", "rest" -> "min"
                        else -> ""
                    }
                    List(reps) { index ->
                        WorkoutSessionStep(
                            name = if (reps > 1) "$name ${index + 1}/$reps" else name,
                            targetType = targetType,
                            targetValue = number,
                            unit = unit
                        )
                    }
                }
        }
        .toList()

fun Workout.toStudentRoutineDirectory(): DirectoryItem {
    val dayLines = parseWorkoutStructure(description).mapIndexed { index, section ->
        val number = Regex("""\d+""").find(section.label)?.value?.toIntOrNull() ?: index + 1
        "Dia $number: $name | descanso 0d"
    }
    return DirectoryItem(
        id = id,
        name = name,
        status = status,
        description = listOfNotNull(
            description.orEmpty().lineSequence().firstOrNull { it.startsWith("Valor: R$") },
            dayLines.joinToString("\n").takeIf { it.isNotBlank() }
        ).joinToString("\n")
    )
}

fun formatStepTarget(step: WorkoutSessionStep): String =
    when (step.targetType) {
        "distance" -> "${step.targetValue.cleanNumber()} km"
        "time" -> "${step.targetValue.cleanNumber()} min"
        "rest" -> "${step.targetValue.cleanNumber()} min descanso"
        else -> "livre"
    }

fun WorkoutSessionStep.progressFor(elapsedMs: Long, distanceMeters: Double): Float =
    when (targetType) {
        "distance" -> {
            val targetMeters = targetValue * 1000.0
            if (targetMeters <= 0.0) 0f else (distanceMeters / targetMeters).toFloat().coerceIn(0f, 1f)
        }
        "time", "rest" -> {
            val targetMs = targetValue * 60_000.0
            if (targetMs <= 0.0) 0f else (elapsedMs / targetMs).toFloat().coerceIn(0f, 1f)
        }
        else -> if (elapsedMs > 0L) 1f else 0f
    }

fun Double.cleanNumber(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(Locale.US, this)
