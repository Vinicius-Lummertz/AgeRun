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
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.myapplication.data.AsaasCardInput
import com.example.myapplication.data.AsaasPixResult
import com.example.myapplication.data.AsaasSubscriptionResult
import com.example.myapplication.data.Challenge
import com.example.myapplication.data.DirectoryItem
import com.example.myapplication.data.Event
import com.example.myapplication.data.InstructorSettings
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
    onSubmitPaymentProof: suspend (String) -> Unit = {},
    onUpdateBillingDay: (Int) -> Unit = {},
    onPayPix: suspend (cpf: String) -> AsaasPixResult? = { null },
    onSubscribeCard: suspend (cpf: String, postalCode: String, addressNumber: String, card: AsaasCardInput) -> AsaasSubscriptionResult? = { _, _, _, _ -> null },
    onCancelSubscription: () -> Unit = {},
    onLogout: () -> Unit,
    onClearMessage: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentStudent = state.students.firstOrNull()
    val paymentPending = currentStudent?.paymentStatus == "pending"
    val paymentBlocked = paymentPending && (currentStudent?.daysOverdue ?: 0) > 4
    var paymentAlertDismissed by rememberSaveable(currentStudent?.id, currentStudent?.paymentStatus) { mutableStateOf(false) }
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
    // Mantem um fix de localizacao "morno" enquanto o aluno navega no app, para o mapa do treino
    // abrir ja centralizado em vez de esperar o primeiro ponto de GPS da corrida.
    DisposableEffect(Unit) {
        LiveLocationCache.start(context)
        onDispose { LiveLocationCache.stop() }
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
    var runChooserInitialPlano by rememberSaveable { mutableStateOf(true) }
    var autoStartWorkout by rememberSaveable { mutableStateOf(false) }
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
                isRefreshing = state.isLoading,
                onRefresh = onRefresh,
                onSettingsClick = { currentRoute = "perfil" },
                onPaymentClick = { currentRoute = "financeiro" },
                onStart = { runIfPaymentAllowed { runChooserInitialPlano = true; showRunChooser = true } },
                onStartChallenge = { challenge -> runIfPaymentAllowed { currentRoute = "challenge_run:${challenge.id}" } }
            )
            currentRoute == "workout_player" -> StudentWorkoutPlayerScreen(
                routine = selectedRoutine,
                day = currentRoutineDay,
                cycleStep = completedCycleSteps + 1,
                workouts = state.workouts,
                freeRun = freeRun,
                autoStart = autoStartWorkout,
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
                amount = currentStudent?.monthlyFee.orEmpty().ifBlank {
                    selectedRoutine?.description?.let { extractRoutinePrice(it) }.orEmpty()
                },
                student = state.students.firstOrNull(),
                onBack = { showPix = false },
                onPayPix = onPayPix,
                onCheckStatus = onRefresh,
                onSubscribeCard = onSubscribeCard,
                onCancelSubscription = onCancelSubscription
            )
            currentRoute == "financeiro" -> StudentFinanceScreen(
                routine = selectedRoutine,
                pixKey = pixKey,
                student = state.students.firstOrNull(),
                onPay = { showPix = true },
                onBillingDayChange = onUpdateBillingDay,
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
                    targetType = challenge?.targetType,
                    targetValue = challenge?.targetValue ?: 0.0,
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
                paymentAlert = paymentBlocked,
                onBack = { currentRoute = "hub_fit" },
                onSettingsClick = { currentRoute = "settings" },
                onSelectRun = { currentRoute = "run_detail:${it.id}:perfil" },
                onOpenHistory = { currentRoute = "run_history" }
            )
            currentRoute == "run_history" -> RunHistoryListScreen(
                runHistory = state.runHistory,
                onBack = { currentRoute = "perfil" },
                onSelectRun = { currentRoute = "run_detail:${it.id}:run_history" }
            )
            currentRoute.startsWith("run_detail:") -> {
                val parts = currentRoute.substringAfter(":").split(":")
                val runId = parts.getOrElse(0) { "" }
                val backRoute = parts.getOrElse(1) { "perfil" }
                val run = state.runHistory.firstOrNull { it.id == runId }
                if (run != null) {
                    RunDetailScreen(run = run, onBack = { currentRoute = backRoute })
                } else EmptyDetailState("Corrida não encontrada.")
            }
            currentRoute == "settings" -> StudentSettingsScreen(
                profileName = state.authSession?.name.orEmpty(),
                profileAvatarUrl = state.authSession?.avatarUrl.orEmpty(),
                onProfileSave = onProfileSave,
                onBack = { currentRoute = "perfil" },
                paymentAlert = paymentBlocked,
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
                    items = studentDestinations
                )
            }
        }

        if (showRunChooser) {
            RunStartSheet(
                hasRoutine = selectedRoutine != null && currentRoutineDay != null,
                routine = selectedRoutine,
                day = currentRoutineDay,
                workouts = state.workouts,
                initialPlano = runChooserInitialPlano,
                onDismiss = { showRunChooser = false },
                onStartFree = {
                    freeRun = true
                    autoStartWorkout = false
                    showRunChooser = false
                    currentRoute = "workout_player"
                },
                onStartRoutine = {
                    freeRun = false
                    autoStartWorkout = true
                    showRunChooser = false
                    currentRoute = "workout_player"
                }
            )
        }

        if (paymentPending && !paymentAlertDismissed) {
            PaymentOverdueDialog(
                amount = currentStudent?.monthlyFee.orEmpty(),
                daysOverdue = currentStudent?.daysOverdue ?: 0,
                blocked = paymentBlocked,
                onDismiss = { paymentAlertDismissed = true },
                onGoToPayment = {
                    paymentAlertDismissed = true
                    currentRoute = "financeiro"
                    showPix = true
                }
            )
        } else if (showScheduleDialog && selectedRoutine != null) {
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
fun PaymentOverdueDialog(amount: String, daysOverdue: Int, blocked: Boolean, onDismiss: () -> Unit, onGoToPayment: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(color = PurpleDeep, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                PaymentWarningBadge(modifier = Modifier.size(46.dp), fontSize = 28.sp)
                Spacer(Modifier.height(14.dp))
                Text("Pagamento pendente", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    formatMoneyLabel(amount),
                    color = Lime,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    if (blocked) {
                        "Sua mensalidade está $daysOverdue dias atrasada. Regularize o pagamento para liberar os treinos."
                    } else if (daysOverdue > 0) {
                        "Sua mensalidade está $daysOverdue dias atrasada."
                    } else {
                        "Essa é a mensalidade do seu plano atual, definida pelo seu professor."
                    },
                    color = Color.White.copy(alpha = .72f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 18.dp)
                )
                Button(
                    onClick = onGoToPayment,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (blocked) Color(0xFFFF4D5E) else Lime, contentColor = if (blocked) Color.White else PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) { Text("Ir para pagamento", fontWeight = FontWeight.Bold) }
                TextButton(onClick = onDismiss) { Text("Agora não", color = Color.White.copy(alpha = .68f)) }
            }
        }
    }
}

@Composable
fun PaymentWarningBadge(modifier: Modifier = Modifier.size(20.dp), fontSize: androidx.compose.ui.unit.TextUnit = 13.sp) {
    Box(modifier.clip(CircleShape).background(Color(0xFFFF3347)), contentAlignment = Alignment.Center) {
        Text("!", color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Black)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunStartSheet(
    hasRoutine: Boolean,
    routine: DirectoryItem?,
    day: StudentRoutineDayPlan?,
    workouts: List<Workout>,
    initialPlano: Boolean,
    onDismiss: () -> Unit,
    onStartFree: () -> Unit,
    onStartRoutine: () -> Unit
) {
    var planoSelected by remember { mutableStateOf(initialPlano && hasRoutine) }
    val plannedSteps = remember(day, workouts) { plannedStepsForDay(day, workouts) }
    val totalTimeMinutes = remember(plannedSteps) { plannedSteps.filter { it.targetType == "time" }.sumOf { it.targetValue } }
    val totalRestMinutes = remember(plannedSteps) { plannedSteps.filter { it.targetType == "rest" }.sumOf { it.targetValue } }
    val totalDistanceKm = remember(plannedSteps) { plannedSteps.filter { it.targetType == "distance" }.sumOf { it.targetValue } }
    val planSections = remember(routine, day) {
        if (day == null) emptyList() else parseWorkoutStructure(routine?.description.orEmpty()).filter { section ->
            Regex("""\d+""").find(section.label)?.value?.toIntOrNull() == day.number
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = PurpleBackground) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Como voce quer treinar?", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(PurpleSurface).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(true, false).forEach { isPlano ->
                    val selected = planoSelected == isPlano
                    val enabled = isPlano && !hasRoutine
                    Surface(
                        modifier = Modifier.weight(1f).clickable(enabled = !enabled) { planoSelected = isPlano },
                        color = if (selected) Lime else Color.Transparent,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            if (isPlano) "Plano" else "Livre",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            color = if (selected) PurpleDeep else Color.White.copy(alpha = if (enabled) .35f else .8f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            if (planoSelected) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TrainingMetric("Caminhada", "${totalTimeMinutes.cleanNumber()} min", Modifier.weight(1f))
                    TrainingMetric("Distancia min.", "${totalDistanceKm.cleanNumber()} km", Modifier.weight(1f))
                    TrainingMetric("Descanso", "${totalRestMinutes.cleanNumber()} min", Modifier.weight(1f))
                }
                Text("Planejamento", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (planSections.isEmpty()) {
                    Text("Estrutura ainda nao definida para este dia.", color = Color.White.copy(alpha = .62f), fontSize = 13.sp)
                } else {
                    WorkoutStructureTimeline(planSections)
                }
            } else {
                Text("Sua corrida, no seu ritmo", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "O GPS acompanha sua distancia e pace em tempo real, mesmo com a tela apagada pela notificacao do treino.",
                    color = Color.White.copy(alpha = .7f),
                    fontSize = 13.sp
                )
                Text("Sem limite de tempo ou distancia.", color = Lime, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            val confirmEnabled = !planoSelected || hasRoutine
            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                shape = RoundedCornerShape(20.dp),
                color = if (confirmEnabled) Lime else Lime.copy(alpha = .4f)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(20.dp).clickable(enabled = confirmEnabled) {
                        if (planoSelected) onStartRoutine() else onStartFree()
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (!confirmEnabled) "Sem treino hoje" else if (planoSelected) "Iniciar treino" else "Iniciar corrida",
                        color = PurpleDeep,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Color.Black.copy(alpha = if (confirmEnabled) 1f else .4f)) {
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
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
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
    val completedDayKeys = remember(runHistory) {
        val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        runHistory.mapNotNull { entry -> parseIsoDate(entry.completedAt)?.let { keyFormat.format(it.time) } }.toSet()
    }
    val pagerState = rememberPagerState(pageCount = { 3 })
    var showAllAvisos by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(top = 18.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp)) {
                ProfileAvatar(
                    avatarUrl = avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp).clickable(onClick = onSettingsClick)
                )
                if (paymentBlocked) {
                    PaymentWarningBadge(Modifier.size(18.dp).align(Alignment.TopEnd), 12.sp)
                }
            }
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
            if (page == 2) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(78.dp).clickable { showAllAvisos = true },
                    color = PurpleSurface,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(Modifier.fillMaxWidth().fillMaxHeight().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Campaign, contentDescription = null, tint = Lime, modifier = Modifier.size(20.dp))
                        Column(Modifier.padding(start = 10.dp).weight(1f)) {
                            Text("Aviso", color = Lime, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(
                                latestAnnouncement?.content ?: "Nenhum aviso publicado ainda.",
                                color = Color.White,
                                fontSize = 13.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = .5f), modifier = Modifier.size(18.dp))
                    }
                }
            } else if (page == 0) {
                Row(Modifier.fillMaxWidth().height(78.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    weekDays.forEach { weekDay ->
                        val hasWorkout = scheduledWeekdays.contains(weekDay.key)
                        val isCompleted = completedDayKeys.contains(weekDay.key)
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
                                if (isCompleted) {
                                    Icon(
                                        Icons.Outlined.Check,
                                        contentDescription = "Treino concluído",
                                        tint = if (weekDay.isToday) PurpleDeep else Lime,
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else {
                                    Box(
                                        Modifier.size(6.dp).clip(CircleShape)
                                            .background(if (hasWorkout) (if (weekDay.isToday) PurpleDeep else Lime) else Color.Transparent)
                                    )
                                }
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
            repeat(3) { index ->
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
            PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = onRefresh) {
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
                                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
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
    }

    if (showAllAvisos) {
        ModalBottomSheet(onDismissRequest = { showAllAvisos = false }, containerColor = PurpleBackground) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Avisos", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                if (announcements.isEmpty()) {
                    Text("Nenhum aviso publicado ainda.", color = Color.White.copy(alpha = .62f), fontSize = 13.sp)
                } else {
                    announcements.forEach { announcement ->
                        Surface(color = PurpleSurface, shape = RoundedCornerShape(16.dp)) {
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
                                Text(announcement.content, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
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
        contentPadding = PaddingValues(horizontal = 52.dp),
        pageSpacing = 6.dp,
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
    targetType: String? = null,
    targetValue: Double = 0.0,
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
    val targetMet = when (targetType) {
        "distance" -> distanceMeters / 1000.0 >= targetValue
        "time" -> elapsedMs / 60_000.0 >= targetValue
        else -> true
    }
    val currentStepCompleted = if (freeRun) targetMet else (currentStep == null || currentProgress >= 1f)
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

    // Pausar ou fechar agora encerra a corrida de vez: tempo, distancia e progresso de etapa sao perdidos.
    fun abandonRun() {
        sendTrackingAction(context, WorkoutTrackingService.ACTION_PAUSE)
        started = false
        currentStepIndex = 0
        stepBaseElapsedMs = 0L
        stepBaseDistanceMeters = 0.0
        alertedStep = -1
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
                    routineName = if (freeRun) "Livre" else routine?.name.orEmpty(),
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
            stepTarget = if (freeRun) {
                if (targetType != null) (headerSubtitle ?: "Meta nao atingida") else "Sem limite de tempo ou distância"
            } else {
                currentStep?.let { formatStepTarget(it) } ?: "Treino concluído"
            },
            progress = currentProgress,
            running = running,
            elapsedMs = elapsedMs,
            distanceMeters = distanceMeters,
            paceSecondsPerKm = paceSecondsPerKm,
            routePoints = routePoints,
            nextLabel = if (freeRun) "Finalizar corrida" else if (currentStepIndex < plannedSteps.lastIndex) "Próxima etapa" else "Finalizar treino",
            nextEnabled = currentStepCompleted,
            onClose = { abandonRun(); onBack() },
            onToggleRunning = {
                if (running) abandonRun() else startOrResume()
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
                        Text(headerTitle ?: (if (freeRun) "Sua corrida, no seu ritmo" else "Tudo pronto para comecarmos o treino?"), color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text(
                            if (freeRun) (if (targetType != null) (headerSubtitle ?: "") else "Sem limite de tempo ou distancia") else "Plano | Dia ${day?.number}",
                            color = Lime,
                            fontWeight = FontWeight.SemiBold
                        )
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
                                routineName = if (freeRun) "Livre" else routine?.name.orEmpty(),
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
    onBillingDayChange: (Int) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val amount = student?.monthlyFee.orEmpty().ifBlank { routine?.description?.let { extractRoutinePrice(it) }.orEmpty() }
    val pending = student?.paymentStatus != "paid"
    val hasProofSubmitted = !student?.paymentProofUrl.isNullOrBlank()
    var editingBillingDay by remember { mutableStateOf(false) }
    var pendingBillingDay by remember(student?.billingDay) { mutableStateOf(student?.billingDay ?: 5) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 24.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Financeiro", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        if (pending) PaymentWarningBadge(Modifier.padding(start = 8.dp).size(22.dp), 14.sp)
                    }
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
        item {
            Surface(color = PurpleSurface, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Dia de pagamento", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Vence todo dia ${student?.billingDay ?: 5}", color = Color.White.copy(alpha = .62f), fontSize = 13.sp)
                        }
                        TextButton(onClick = { editingBillingDay = !editingBillingDay }) {
                            Text(if (editingBillingDay) "Cancelar" else "Alterar", color = Lime, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (editingBillingDay) {
                        BillingDayPicker(pendingBillingDay) { pendingBillingDay = it }
                        Button(
                            onClick = {
                                onBillingDayChange(pendingBillingDay)
                                editingBillingDay = false
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Salvar novo dia", fontWeight = FontWeight.Bold)
                        }
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
    amount: String,
    student: Student? = null,
    onBack: () -> Unit,
    onPayPix: suspend (cpf: String) -> AsaasPixResult? = { null },
    onCheckStatus: () -> Unit = {},
    onSubscribeCard: suspend (cpf: String, postalCode: String, addressNumber: String, card: AsaasCardInput) -> AsaasSubscriptionResult? = { _, _, _, _ -> null },
    onCancelSubscription: () -> Unit = {}
) {
    var paymentTab by remember { mutableStateOf("pix") }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleDeep).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 20.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Pagamento", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("pix" to "Pix", "assinatura" to "Assinatura cartão").forEach { (key, label) ->
                    val selected = paymentTab == key
                    Surface(
                        modifier = Modifier.weight(1f).clickable { paymentTab = key },
                        color = if (selected) Lime else PurpleSurface,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            label,
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = if (selected) PurpleDeep else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        if (paymentTab == "assinatura") {
            item {
                CardSubscriptionForm(
                    amount = amount,
                    subscriptionActive = student?.asaasSubscriptionActive == true,
                    onSubscribe = onSubscribeCard,
                    onCancelSubscription = onCancelSubscription
                )
            }
        }
        if (paymentTab == "pix") {
            item {
                AsaasPixForm(
                    amount = amount,
                    alreadyPaidRecently = student?.paymentStatus == "paid",
                    subscriptionActive = student?.asaasSubscriptionActive == true,
                    onPayPix = onPayPix,
                    onCheckStatus = onCheckStatus
                )
            }
        }
    }
}

@Composable
private fun AsaasPixForm(
    amount: String,
    alreadyPaidRecently: Boolean,
    subscriptionActive: Boolean,
    onPayPix: suspend (cpf: String) -> AsaasPixResult?,
    onCheckStatus: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var cpf by remember { mutableStateOf("") }
    var generating by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<AsaasPixResult?>(null) }
    var feedback by remember { mutableStateOf<String?>(null) }
    val qrBitmap = remember(result?.encodedImage) {
        result?.encodedImage?.takeIf { it.isNotBlank() }?.let { encoded ->
            runCatching {
                val bytes = android.util.Base64.decode(encoded, android.util.Base64.DEFAULT)
                android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }.getOrNull()
        }
    }

    Surface(color = PurpleSurface, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Pagar com Pix", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "Geramos um Pix de ${formatMoneyLabel(chargeAmountWithPlatformFee(amount))} pelo Asaas (mensalidade de ${formatMoneyLabel(amount)} + taxa de processamento de 5%). A confirmação é automática, não precisa enviar comprovante.",
                color = Color.White.copy(alpha = .65f),
                fontSize = 12.sp
            )
            if (subscriptionActive) {
                Surface(color = Color(0xFFFFD166).copy(alpha = .18f), shape = RoundedCornerShape(12.dp)) {
                    Text(
                        "Você já tem uma assinatura no cartão ativa. Pagar também por Pix pode gerar cobrança duplicada neste mês.",
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        color = Color(0xFFFFD166),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            if (alreadyPaidRecently && result == null) {
                Text("Sua mensalidade já está em dia.", color = Lime, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            if (result == null) {
                CardFormField(cpf, { cpf = it.filter(Char::isDigit).take(11) }, "CPF")
                if (cpf.length == 11 && !isValidCpf(cpf)) {
                    Text("CPF invalido", color = Color(0xFFFF6B6B), fontSize = 12.sp)
                }
                feedback?.let { Text(it, color = Color(0xFFFF6B6B), fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                Button(
                    onClick = {
                        generating = true
                        feedback = null
                        scope.launch {
                            val pix = runCatching { onPayPix(cpf) }.getOrNull()
                            if (pix != null && pix.paymentId.isNotBlank()) {
                                result = pix
                            } else {
                                feedback = "Não foi possível gerar o Pix. Tente novamente em alguns instantes."
                            }
                            generating = false
                        }
                    },
                    enabled = isValidCpf(cpf) && !generating,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(if (generating) "Gerando..." else "Gerar Pix", fontWeight = FontWeight.Bold)
                }
            } else {
                Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
                    Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                        if (qrBitmap != null) {
                            Image(qrBitmap.asImageBitmap(), "QR Code Pix", Modifier.size(200.dp))
                        } else {
                            Icon(Icons.Outlined.QrCode2, contentDescription = null, tint = PurpleDeep, modifier = Modifier.size(150.dp))
                        }
                    }
                }
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(android.content.ClipboardManager::class.java)
                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Pix", result?.payload.orEmpty()))
                        android.widget.Toast.makeText(context, "Código Pix copiado", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Copiar código Pix", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onCheckStatus, modifier = Modifier.fillMaxWidth()) {
                    Text("Já paguei, verificar agora", color = Lime, fontWeight = FontWeight.Bold)
                }
                Text(
                    "Confirmação automática assim que o pagamento for recebido.",
                    color = Color.White.copy(alpha = .55f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun CardSubscriptionForm(
    amount: String,
    subscriptionActive: Boolean,
    onSubscribe: suspend (cpf: String, postalCode: String, addressNumber: String, card: AsaasCardInput) -> AsaasSubscriptionResult?,
    onCancelSubscription: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var cpf by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var addressNumber by remember { mutableStateOf("") }
    var holderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryMonth by remember { mutableStateOf("") }
    var expiryYear by remember { mutableStateOf("") }
    var ccv by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var feedbackIsError by remember { mutableStateOf(false) }

    if (subscriptionActive) {
        Surface(color = PurpleSurface, shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Assinatura ativa", color = Lime, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "Seu cartão é cobrado automaticamente todo mês (${formatMoneyLabel(amount)}).",
                    color = Color.White.copy(alpha = .72f),
                    fontSize = 13.sp
                )
                Button(
                    onClick = onCancelSubscription,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B), contentColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Cancelar assinatura", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Surface(color = PurpleSurface, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Assinar com cartão", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "Cobrança automática de ${formatMoneyLabel(chargeAmountWithPlatformFee(amount))} todo mês (inclui taxa de processamento de 5% sobre os ${formatMoneyLabel(amount)} do plano), sem precisar pagar manualmente.",
                color = Color.White.copy(alpha = .65f),
                fontSize = 12.sp
            )
            CardFormField(cpf, { cpf = it.filter(Char::isDigit).take(11) }, "CPF")
            if (cpf.length == 11 && !isValidCpf(cpf)) {
                Text("CPF invalido", color = Color(0xFFFF6B6B), fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.weight(1f)) { CardFormField(postalCode, { postalCode = it.filter(Char::isDigit).take(8) }, "CEP") }
                Box(Modifier.weight(1f)) { CardFormField(addressNumber, { addressNumber = it.take(10) }, "Número") }
            }
            CardFormField(holderName, { holderName = it }, "Nome impresso no cartão")
            CardFormField(cardNumber, { cardNumber = it.filter(Char::isDigit).take(19) }, "Número do cartão")
            if (cardNumber.length >= 13 && !isValidCardNumber(cardNumber)) {
                Text("Número do cartão invalido", color = Color(0xFFFF6B6B), fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.weight(1f)) { CardFormField(expiryMonth, { expiryMonth = it.filter(Char::isDigit).take(2) }, "Mês (MM)") }
                Box(Modifier.weight(1f)) { CardFormField(expiryYear, { expiryYear = it.filter(Char::isDigit).take(4) }, "Ano (AAAA)") }
                Box(Modifier.weight(1f)) { CardFormField(ccv, { ccv = it.filter(Char::isDigit).take(4) }, "CVV") }
            }
            if (expiryMonth.length == 2 && expiryYear.length == 4 && !isCardExpiryValid(expiryMonth, expiryYear)) {
                Text("Validade invalida ou cartao vencido", color = Color(0xFFFF6B6B), fontSize = 12.sp)
            }
            feedback?.let {
                Text(it, color = if (feedbackIsError) Color(0xFFFF6B6B) else Lime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            val canSubmit = isValidCpf(cpf) && postalCode.length == 8 && addressNumber.isNotBlank() &&
                holderName.isNotBlank() && isValidCardNumber(cardNumber) && expiryMonth.length == 2 && expiryYear.length == 4 &&
                isCardExpiryValid(expiryMonth, expiryYear) && ccv.length >= 3
            Button(
                onClick = {
                    submitting = true
                    feedback = null
                    scope.launch {
                        val result = runCatching {
                            onSubscribe(cpf, postalCode, addressNumber, AsaasCardInput(holderName, cardNumber, expiryMonth, expiryYear, ccv))
                        }.getOrNull()
                        if (result != null) {
                            feedbackIsError = false
                            feedback = "Assinatura criada com sucesso."
                        } else {
                            feedbackIsError = true
                            feedback = "Não foi possível assinar. Verifique os dados do cartão."
                        }
                        submitting = false
                    }
                },
                enabled = canSubmit && !submitting,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                shape = RoundedCornerShape(50)
            ) {
                Text(if (submitting) "Assinando..." else "Assinar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CardFormField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Lime,
            unfocusedLabelColor = Color.White.copy(alpha = .6f),
            focusedBorderColor = Lime,
            unfocusedBorderColor = Color.White.copy(alpha = .3f)
        )
    )
}

private const val PLATFORM_FEE_RATE = 0.05

private fun chargeAmountWithPlatformFee(monthlyFee: String): String {
    val value = monthlyFee.trim().replace(".", "").replace(",", ".").toDoubleOrNull() ?: return monthlyFee
    return "%.2f".format(value * (1 + PLATFORM_FEE_RATE)).replace(".", ",")
}

private fun isValidCpf(value: String): Boolean {
    val cpf = value.filter(Char::isDigit)
    if (cpf.length != 11 || cpf.all { it == cpf[0] }) return false
    val digits = cpf.map { it - '0' }
    for (checkIndex in listOf(9, 10)) {
        var sum = 0
        for (i in 0 until checkIndex) sum += digits[i] * (checkIndex + 1 - i)
        val remainder = (sum * 10) % 11
        if ((if (remainder == 10) 0 else remainder) != digits[checkIndex]) return false
    }
    return true
}

private fun isValidCardNumber(value: String): Boolean {
    val digits = value.filter(Char::isDigit)
    if (digits.length < 13) return false
    var sum = 0
    var alternate = false
    for (i in digits.length - 1 downTo 0) {
        var n = digits[i] - '0'
        if (alternate) { n *= 2; if (n > 9) n -= 9 }
        sum += n
        alternate = !alternate
    }
    return sum % 10 == 0
}

private fun isCardExpiryValid(month: String, year: String): Boolean {
    val m = month.toIntOrNull() ?: return false
    val y = year.toIntOrNull() ?: return false
    if (m !in 1..12) return false
    val now = Calendar.getInstance()
    val currentYear = now.get(Calendar.YEAR)
    val currentMonth = now.get(Calendar.MONTH) + 1
    return y > currentYear || (y == currentYear && m >= currentMonth)
}

@Composable
fun StudentProfileSummaryScreen(
    studentName: String,
    avatarUrl: String,
    runHistory: List<RunHistoryEntry>,
    paymentAlert: Boolean = false,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onSelectRun: (RunHistoryEntry) -> Unit,
    onOpenHistory: () -> Unit = {}
) {
    val totalDistanceKm = remember(runHistory) { runHistory.sumOf { it.distanceMeters } / 1000.0 }
    val recentRuns = remember(runHistory) { runHistory.sortedByDescending { it.completedAt }.take(3) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 14.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar", tint = Color.White) }
                Spacer(Modifier.weight(1f))
                Box {
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Outlined.Settings, "Configurações", tint = Color.White) }
                    if (paymentAlert) PaymentWarningBadge(Modifier.size(18.dp).align(Alignment.TopEnd), 12.sp)
                }
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
            Surface(color = PurpleSurface, shape = RoundedCornerShape(18.dp), modifier = Modifier.clickable(onClick = onOpenHistory)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Histórico de corridas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = Color.White.copy(alpha = .5f), modifier = Modifier.size(18.dp))
                    }
                    if (recentRuns.isEmpty()) {
                        Text("Nenhuma corrida registrada ainda.", color = Color.White.copy(alpha = .62f), fontSize = 13.sp)
                    } else {
                        recentRuns.forEach { run -> RunHistoryRow(run, onClick = { onSelectRun(run) }) }
                        Text("Ver tudo e filtrar por data ou local", color = Lime, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
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
        Box(Modifier.fillMaxWidth().height(140.dp)) {
            Canvas(Modifier.fillMaxWidth().height(108.dp)) {
                val stepX = if (buckets.size > 1) size.width / (buckets.size - 1) else 0f
                val points = buckets.mapIndexed { index, (_, km) ->
                    val x = stepX * index
                    val y = size.height * (1f - (km / maxKm).toFloat().coerceIn(0f, 1f))
                    Offset(x, y)
                }
                if (points.size > 1) {
                    val linePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    val fillPath = Path().apply {
                        addPath(linePath)
                        lineTo(points.last().x, size.height)
                        lineTo(points.first().x, size.height)
                        close()
                    }
                    drawPath(fillPath, brush = Brush.verticalGradient(listOf(Lime.copy(alpha = .28f), Color.Transparent)))
                    drawPath(linePath, color = Lime, style = Stroke(width = 6f, cap = StrokeCap.Round))
                }
                points.forEach { point ->
                    drawCircle(color = Lime, radius = 8f, center = point)
                    drawCircle(color = PurpleSurface, radius = 4f, center = point)
                }
            }
            Row(Modifier.fillMaxWidth().align(Alignment.TopCenter), horizontalArrangement = Arrangement.SpaceBetween) {
                buckets.forEach { (_, km) ->
                    Text(
                        "%.1f".format(km),
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = .7f),
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            buckets.forEach { (label, _) ->
                Text(label, modifier = Modifier.weight(1f), color = Color.White.copy(alpha = .55f), fontSize = 10.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun RunHistoryListScreen(
    runHistory: List<RunHistoryEntry>,
    onBack: () -> Unit,
    onSelectRun: (RunHistoryEntry) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    val monthKeyFormat = remember { SimpleDateFormat("yyyy-MM", Locale.US) }
    val monthLabelFormat = remember { SimpleDateFormat("MMM yyyy", Locale("pt", "BR")) }
    val months = remember(runHistory) {
        runHistory.mapNotNull { parseIsoDate(it.completedAt)?.let { cal -> monthKeyFormat.format(cal.time) } }
            .distinct()
            .sortedDescending()
    }
    val filtered = remember(runHistory, query, selectedMonth) {
        runHistory.filter { run ->
            val matchesQuery = query.isBlank() || run.routineName.contains(query, ignoreCase = true)
            val matchesMonth = selectedMonth == null ||
                parseIsoDate(run.completedAt)?.let { monthKeyFormat.format(it.time) } == selectedMonth
            matchesQuery && matchesMonth
        }.sortedByDescending { it.completedAt }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 14.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar", tint = Color.White) }
                Text("Histórico de corridas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
            }
        }
        item {
            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Buscar por treino ou local") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = PurpleSurface,
                    unfocusedContainerColor = PurpleSurface,
                    focusedIndicatorColor = Lime,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Lime,
                    focusedPlaceholderColor = Color.White.copy(alpha = .5f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = .5f)
                )
            )
        }
        if (months.isNotEmpty()) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        MonthFilterChip(label = "Todas as datas", selected = selectedMonth == null) { selectedMonth = null }
                    }
                    items(months) { monthKey ->
                        val label = runCatching { monthLabelFormat.format(monthKeyFormat.parse(monthKey)!!) }.getOrDefault(monthKey)
                            .replaceFirstChar { it.uppercase() }
                        MonthFilterChip(label = label, selected = selectedMonth == monthKey) { selectedMonth = monthKey }
                    }
                }
            }
        }
        if (filtered.isEmpty()) {
            item { EmptyDetailState("Nenhuma corrida encontrada.") }
        } else {
            items(filtered, key = { it.id }) { run -> RunHistoryRow(run, onClick = { onSelectRun(run) }) }
        }
    }
}

@Composable
private fun MonthFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (selected) Lime else PurpleSurface,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = if (selected) PurpleDeep else Color.White.copy(alpha = .8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
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
    paymentAlert: Boolean = false,
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
                Box(
                    modifier = Modifier.size(92.dp).clip(CircleShape).clickable { photoPicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedPhotoUri != null || profileAvatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = selectedPhotoUri ?: profileAvatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.profile),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).size(26.dp).clip(CircleShape).background(Lime),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Trocar foto", tint = PurpleDeep, modifier = Modifier.size(14.dp))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(draftName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Editar nome",
                        tint = Lime,
                        modifier = Modifier.padding(start = 6.dp).size(18.dp).clickable { editingProfile = !editingProfile }
                    )
                }
                if (selectedPhotoUri != null && !editingProfile) {
                    Button(
                        onClick = { onProfileSave(draftName, selectedPhotoUri) },
                        modifier = Modifier.fillMaxWidth().height(46.dp).padding(top = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Salvar nova foto", fontWeight = FontWeight.Bold)
                    }
                }
                if (editingProfile) {
                    Spacer(Modifier.height(10.dp))
                    FormTextField(draftName, { draftName = it.take(80) }, "Nome exibido")
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
                showAlert = paymentAlert,
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
    var mapExpanded by remember(event.id) { mutableStateOf(false) }
    val lat = event.latitude
    val lng = event.longitude

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
            contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, 140.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { TextButton(onClick = onBack) { Text("Voltar", color = Lime) } }
            item { Text(event.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold) }
            item { DetailInfoRow("Data", event.eventDate) }
            item { DetailInfoRow("Local", event.location.orEmpty().ifBlank { "Sem local" }) }
            if (lat != null && lng != null) item {
                EventLocationMap(
                    latitude = lat,
                    longitude = lng,
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { mapExpanded = true }
                )
            }
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

        if (mapExpanded && lat != null && lng != null) {
            Box(Modifier.fillMaxSize().background(PurpleBackground)) {
                EventLocationMap(latitude = lat, longitude = lng, modifier = Modifier.fillMaxSize())
                IconButton(
                    onClick = { mapExpanded = false },
                    modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(8.dp)
                ) { Icon(Icons.Outlined.Close, "Fechar mapa", tint = Color.White) }
            }
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
    instructorSettings: InstructorSettings,
    onOpenProfile: () -> Unit,
    onOpenFinance: () -> Unit,
    onNotificationsChange: (InstructorSettings) -> Unit,
    onBack: () -> Unit,
    onClearLocalData: () -> Unit,
    showFinancialSection: Boolean = true,
    clearDataLabel: String = "Sair",
    securityDescription: String = "O acesso usa token. Para criar um professor do zero, saia e volte para o cadastro."
) {
    var openSection by remember { mutableStateOf("") }
    var confirmClear by remember { mutableStateOf(false) }

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
            Column(
                Modifier.fillMaxWidth().clickable(onClick = onOpenProfile),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (profileAvatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = profileAvatarUrl,
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
                    Text(profileName.ifBlank { "Usuario" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Editar perfil",
                        tint = Lime,
                        modifier = Modifier.padding(start = 6.dp).size(18.dp)
                    )
                }
            }
        }
        if (showFinancialSection) item {
            SettingsListRow(
                title = "Financeiro",
                subtitle = "Chave Pix e dados de cobranca",
                icon = Icons.Outlined.Payments,
                onClick = onOpenFinance
            )
        }
        item {
            EditableSettingsSection(
                title = "Notificacoes",
                subtitle = "Alertas de cobranca e treinos",
                icon = Icons.Outlined.Settings,
                expanded = openSection == "notificacoes",
                onClick = { openSection = if (openSection == "notificacoes") "" else "notificacoes" }
            ) {
                SettingsSwitchRow("Push no app", instructorSettings.notificationPush) {
                    onNotificationsChange(instructorSettings.copy(notificationPush = it))
                }
                SettingsSwitchRow("Email de cobranca", instructorSettings.notificationEmail) {
                    onNotificationsChange(instructorSettings.copy(notificationEmail = it))
                }
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
            EditableSettingsSection(
                title = "Entrar no site",
                subtitle = "Painel para computador",
                icon = Icons.Outlined.QrCode2,
                expanded = openSection == "site",
                onClick = { openSection = if (openSection == "site") "" else "site" }
            ) {
                Text(
                    "No computador, abra o site do AgeGo. Depois, aponte a camera do seu celular para o QR Code mostrado na tela de login do site.",
                    color = Color.White.copy(alpha = .68f),
                    fontSize = 12.sp
                )
                Text(
                    "Ao reconhecer o codigo, o celular vai abrir o AgeGo e conectar o site automaticamente.",
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
                    Text("Sair da conta?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "Isso apaga a sessao salva neste aparelho e volta para a tela inicial de login.",
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
                            Text("Sair", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InstructorProfileScreen(
    profileName: String,
    profileAvatarUrl: String,
    onBack: () -> Unit,
    onSave: (String, Uri?) -> Unit
) {
    var draftName by remember(profileName) { mutableStateOf(profileName.ifBlank { "Usuario" }) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) selectedPhotoUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize().background(PurpleDeep).statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp, 14.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar", tint = Color.White) }
            Text("Editar perfil", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
        }
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(96.dp).clip(CircleShape).clickable { photoPicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedPhotoUri != null || profileAvatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = selectedPhotoUri ?: profileAvatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                }
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).size(28.dp).clip(CircleShape).background(Lime),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Trocar foto", tint = PurpleDeep, modifier = Modifier.size(16.dp))
                }
            }
            Text("Toque na foto para trocar", color = Color.White.copy(alpha = .58f), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(16.dp))
            FormTextField(draftName, { draftName = it.take(80) }, "Nome exibido")
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onSave(draftName.trim(), selectedPhotoUri) },
                enabled = draftName.trim().length > 1,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                shape = RoundedCornerShape(50)
            ) {
                Text("Salvar perfil", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InstructorFinanceScreen(
    settings: InstructorSettings,
    onBack: () -> Unit,
    onSave: (InstructorSettings) -> Unit
) {
    var draft by remember(settings) { mutableStateOf(settings) }

    Column(modifier = Modifier.fillMaxSize().background(PurpleDeep).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(16.dp, 14.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar", tint = Color.White) }
            Text("Financeiro", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Dados do Pix", color = Color.White.copy(alpha = .72f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            item { FormTextField(draft.pixKey, { draft = draft.copy(pixKey = it.take(140)) }, "Chave Pix") }
            item { FormTextField(draft.pixOwnerName, { draft = draft.copy(pixOwnerName = it.take(255)) }, "Nome do titular") }
            item { FormTextField(draft.pixDocument, { draft = draft.copy(pixDocument = it.filter(Char::isDigit).take(14)) }, "CPF ou CNPJ do titular") }
            item {
                Text(
                    "Endereco de cobranca",
                    color = Color.White.copy(alpha = .72f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            item { FormTextField(draft.addressZipCode, { draft = draft.copy(addressZipCode = it.filter(Char::isDigit).take(8)) }, "CEP") }
            item { FormTextField(draft.addressStreet, { draft = draft.copy(addressStreet = it.take(255)) }, "Rua") }
            item { FormTextField(draft.addressNumber, { draft = draft.copy(addressNumber = it.take(20)) }, "Numero") }
            item { FormTextField(draft.addressNeighborhood, { draft = draft.copy(addressNeighborhood = it.take(255)) }, "Bairro") }
            item { FormTextField(draft.addressCity, { draft = draft.copy(addressCity = it.take(100)) }, "Cidade") }
            item { FormTextField(draft.addressState, { draft = draft.copy(addressState = it.uppercase().filter(Char::isLetter).take(2)) }, "UF") }
            item {
                Button(
                    onClick = { onSave(draft) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Salvar financeiro", fontWeight = FontWeight.Bold)
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
fun SettingsListRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    showAlert: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Lime, modifier = Modifier.size(24.dp))
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.White.copy(alpha = .58f), fontSize = 12.sp)
            }
            if (showAlert) PaymentWarningBadge(Modifier.size(20.dp), 13.sp)
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
