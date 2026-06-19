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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.myapplication.data.CommunityPost
import com.example.myapplication.data.DirectoryItem
import com.example.myapplication.data.Event
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

@Composable
fun StudentPortalApp(
    state: AgeGoUiState,
    pixKey: String,
    onProfileSave: (String, Uri?) -> Unit,
    onSaveCommunityPost: (CommunityPost) -> Unit,
    onToggleCommunityLike: (String) -> Unit,
    onAddCommunityComment: (String, String) -> Unit,
    onReplyCommunityComment: (String, String, String) -> Unit,
    onToggleCommunityCommentLike: (String, String) -> Unit,
    onShareCommunityPost: (String) -> Unit,
    onSaveWorkoutSession: (WorkoutSessionPayload, () -> Unit) -> Unit,
    onRefresh: () -> Unit,
    onUploadMedia: suspend (Uri) -> String,
    onLogout: () -> Unit
) {
    var currentRoute by remember { mutableStateOf("hub_fit") }
    var selectedRoutine by remember(state.routines) { mutableStateOf(state.routines.firstOrNull()) }
    var scheduledWeekdays by remember { mutableStateOf(setOf<Int>()) }
    var completedCycleSteps by remember(selectedRoutine?.id) { mutableStateOf(0) }
    var showScheduleDialog by remember { mutableStateOf(selectedRoutine != null && scheduledWeekdays.isEmpty()) }
    var showPix by remember { mutableStateOf(false) }
    val showBottomBar = destinations.any { it.route == currentRoute }

    Box(Modifier.fillMaxSize().background(PurpleBackground)) {
        val routineDays = remember(selectedRoutine?.description) { parseRoutineDays(selectedRoutine?.description.orEmpty()) }
        val currentRoutineDay = routineDays.getOrNull(if (routineDays.isEmpty()) 0 else completedCycleSteps % routineDays.size)
        when {
            currentRoute == "hub_fit" -> StudentHomeScreen(
                routine = selectedRoutine,
                day = currentRoutineDay,
                events = state.events.filter { it.eventDate.take(10) == todayKey() },
                cycleStep = completedCycleSteps + 1,
                avatarUrl = state.authSession?.avatarUrl.orEmpty(),
                scheduledWeekdays = scheduledWeekdays,
                onRoutineClick = { currentRoute = "minhas_rotinas" },
                onGroupsClick = { currentRoute = "grupos_aluno" },
                onSettingsClick = { currentRoute = "settings" },
                onStart = { currentRoute = "workout_player" }
            )
            currentRoute == "workout_player" -> StudentWorkoutPlayerScreen(
                routine = selectedRoutine,
                day = currentRoutineDay,
                cycleStep = completedCycleSteps + 1,
                workouts = state.workouts,
                onBack = { currentRoute = "hub_fit" },
                onComplete = { payload ->
                    onSaveWorkoutSession(payload) {
                        completedCycleSteps += 1
                        currentRoute = "hub_fit"
                    }
                }
            )
            currentRoute == "minhas_rotinas" -> StudentRoutinesScreen(
                routines = state.routines,
                selectedRoutine = selectedRoutine,
                onSelectRoutine = {
                    selectedRoutine = it
                    scheduledWeekdays = emptySet()
                    showScheduleDialog = true
                    currentRoute = "hub_fit"
                },
                onBack = { currentRoute = "hub_fit" }
            )
            currentRoute == "grupos_aluno" -> StudentGroupsScreen(groups = state.groups)
            currentRoute == "financeiro" && showPix -> PixPaymentScreen(
                pixKey = pixKey,
                amount = selectedRoutine?.description?.let { extractRoutinePrice(it) }.orEmpty(),
                onBack = { showPix = false }
            )
            currentRoute == "financeiro" -> StudentFinanceScreen(
                routine = selectedRoutine,
                pixKey = pixKey,
                onPay = { showPix = true }
            )
            currentRoute == "comunidade" -> CommunityScreen(
                posts = state.communityPosts,
                workouts = state.workouts,
                loading = state.isLoading,
                onBack = { currentRoute = "hub_fit" },
                onCreatePost = { currentRoute = "community_new" },
                onPostClick = { currentRoute = "community_post:${it.id}" },
                onLike = onToggleCommunityLike,
                onComment = { currentRoute = "community_post:$it" },
                onShare = onShareCommunityPost,
                onRefresh = onRefresh
            )
            currentRoute == "community_new" -> CommunityPostFormScreen(
                workouts = state.workouts,
                target = "all",
                onBack = { currentRoute = "comunidade" },
                onSave = {
                    onSaveCommunityPost(it)
                    currentRoute = "comunidade"
                },
                onUploadMedia = onUploadMedia
            )
            currentRoute.startsWith("community_post:") -> {
                val postId = currentRoute.substringAfter(":")
                val post = state.communityPosts.firstOrNull { it.id == postId }
                if (post != null) {
                    CommunityPostDetailScreen(
                        post = post,
                        workoutName = state.workouts.firstOrNull { it.id == post.linkedWorkoutId }?.name,
                        focusComments = false,
                        onBack = { currentRoute = "comunidade" },
                        onLike = { onToggleCommunityLike(post.id) },
                        onComment = { content -> onAddCommunityComment(post.id, content) },
                        onReply = { commentId, content -> onReplyCommunityComment(post.id, commentId, content) },
                        onCommentLike = { commentId -> onToggleCommunityCommentLike(post.id, commentId) },
                        onShare = { onShareCommunityPost(post.id) }
                    )
                } else {
                    StudentGroupsScreen(groups = state.groups)
                }
            }
            currentRoute == "eventos" -> StudentEventsScreen(events = state.events)
            currentRoute == "settings" -> InstructorSettingsScreen(
                profileName = state.authSession?.name.orEmpty(),
                profileAvatarUrl = state.authSession?.avatarUrl.orEmpty(),
                pixKey = pixKey,
                onProfileSave = onProfileSave,
                onPixKeyChange = {},
                onBack = { currentRoute = "hub_fit" },
                onClearLocalData = onLogout,
                showFinancialSection = false,
                profileFieldLabel = "Nome exibido",
                clearDataLabel = "Sair",
                securityDescription = "O acesso usa token. Limpar a sessao remove este aparelho da conta ate o proximo acesso."
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
                    onNavigate = {
                        showPix = false
                        currentRoute = it
                    }
                )
            }
        }

        if (showScheduleDialog && selectedRoutine != null) {
            WeekScheduleDialog(
                routineName = selectedRoutine?.name.orEmpty(),
                routineDescription = selectedRoutine?.description.orEmpty(),
                selectedDays = scheduledWeekdays,
                onSelectedDaysChange = { scheduledWeekdays = it },
                onDismiss = { showScheduleDialog = false }
            )
        }
    }
}

@Composable
fun StudentHomeScreen(
    routine: DirectoryItem?,
    day: StudentRoutineDayPlan?,
    events: List<Event>,
    cycleStep: Int,
    avatarUrl: String,
    scheduledWeekdays: Set<Int>,
    onRoutineClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onStart: () -> Unit
) {
    val days = remember { daysAroundToday(previous = 0, next = 13) }
    var todayPanelOpen by remember { mutableStateOf(false) }
    val todayHasWorkout = scheduledWeekdays.contains(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
    var shake by remember { mutableStateOf(0f) }
    LaunchedEffect(todayHasWorkout) {
        if (todayHasWorkout) {
            repeat(6) { index ->
                shake = if (index % 2 == 0) -1.2f else 1.2f
                delay(70)
            }
            shake = 0f
        }
    }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(top = 18.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(
                avatarUrl = avatarUrl,
                contentDescription = null,
                modifier = Modifier.size(42.dp).clickable(onClick = onSettingsClick)
            )
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text("Minha area", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(routine?.name ?: "Sem rotina ativa", color = Color.White.copy(alpha = .62f), fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(26.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
        ) {
            items(days) { day ->
                val hasWorkout = scheduledWeekdays.contains(dayOfWeek(day.key))
                DayEventCard(
                    label = day.label,
                    count = if (hasWorkout) 1 else 0,
                    selected = hasWorkout,
                    isToday = day.isToday,
                    modifier = Modifier.width(126.dp).offset(x = if (day.isToday && todayHasWorkout) shake.dp else 0.dp),
                    onClick = { if (day.isToday) todayPanelOpen = !todayPanelOpen }
                )
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 28.dp),
            color = if (todayPanelOpen) Color.Black else PurpleSurface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp, 22.dp, 16.dp, 140.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (todayPanelOpen) {
                    item {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Hoje", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text("Eventos, rotina e treino atual", color = Lime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            IconButton(onClick = { todayPanelOpen = false }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Fechar", tint = Color.White)
                            }
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
                    item {
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
                        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                            StudentShortcut("Minhas rotinas", R.drawable.ic_option_modalidades, onRoutineClick)
                            StudentShortcut("Grupos", R.drawable.ic_option_grupos, onGroupsClick)
                        }
                    }
                    item {
                        Surface(
                            modifier = Modifier.clickable { todayPanelOpen = true },
                            color = PurpleBackground,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Treino de hoje", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    if (todayHasWorkout) "Voce tem treino programado hoje." else "Nenhum treino programado para hoje.",
                                    color = if (todayHasWorkout) Lime else Color.White.copy(alpha = .62f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
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
        if (day == null) {
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
                        Text("Tudo pronto para comecarmos o treino?", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text("Rotina | Dia ${day.number}", color = Lime, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
                }
            }
            item {
                Surface(color = PurpleDeep, shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(routine?.name.orEmpty(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
            item { WorkoutPlanCard(day, dark = true, plannedSteps = plannedSteps) }
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
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleDeep, contentColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Compartilhar resultado", fontWeight = FontWeight.Bold)
                }
            }
            item {
                Button(
                    onClick = {
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
                Text("Sem treinos definidos para esta etapa.", color = Color.White.copy(alpha = .62f))
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
                Text("Etapas", color = Lime, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                plannedSteps.forEachIndexed { index, step ->
                    Text(
                        "${index + 1}. ${step.name} - ${formatStepTarget(step)}",
                        color = Color.White.copy(alpha = .78f),
                        fontSize = 13.sp
                    )
                }
            }
            Text("Descanso apos esta etapa: ${day.restDaysAfter} dia(s)", color = Color.White.copy(alpha = .66f), fontSize = 12.sp)
        }
    }
}

@Composable
fun WeekScheduleDialog(
    routineName: String,
    routineDescription: String,
    selectedDays: Set<Int>,
    onSelectedDaysChange: (Set<Int>) -> Unit,
    onDismiss: () -> Unit
) {
    val maxRestDays = remember(routineDescription) { parseRoutineDays(routineDescription).maxOfOrNull { it.restDaysAfter } ?: 0 }
    val blockedDays = remember(selectedDays, maxRestDays) { restBlockedWeekdays(selectedDays, maxRestDays) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(color = PurpleDeep, shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Programar rotina", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(routineName, color = Lime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                listOf(
                    Calendar.MONDAY to "Seg",
                    Calendar.TUESDAY to "Ter",
                    Calendar.WEDNESDAY to "Qua",
                    Calendar.THURSDAY to "Qui",
                    Calendar.FRIDAY to "Sex",
                    Calendar.SATURDAY to "Sab",
                    Calendar.SUNDAY to "Dom"
                ).chunked(4).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { (day, label) ->
                            val blocked = day in blockedDays && day !in selectedDays
                            SlimFilterBadge(
                                label = if (blocked) "$label descanso" else label,
                                selected = day in selectedDays,
                                onClick = {
                                    if (!blocked) {
                                        onSelectedDaysChange(if (day in selectedDays) selectedDays - day else selectedDays + day)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size < 4) Spacer(Modifier.weight((4 - row.size).toFloat()))
                    }
                }
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
            items(routines) { routine ->
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
fun StudentFinanceScreen(routine: DirectoryItem?, pixKey: String, onPay: () -> Unit) {
    val amount = routine?.description?.let { extractRoutinePrice(it) }.orEmpty()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 24.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Financeiro", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Seu extrato e faturas", color = Color.White.copy(alpha = .62f))
        }
        item {
            Surface(color = Lime, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Fatura atual", color = LimeMuted)
                    Text(formatMoneyLabel(amount), color = PurpleDeep, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text(routine?.name ?: "Sem rotina ativa", color = PurpleDeep.copy(alpha = .72f), fontSize = 13.sp)
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
        item { StudentExtractRow("Mensalidade", formatMoneyLabel(amount), "Pendente", false) }
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
fun PixPaymentScreen(pixKey: String, amount: String, onBack: () -> Unit) {
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
                    Icon(Icons.Outlined.QrCode2, contentDescription = null, tint = PurpleDeep, modifier = Modifier.size(150.dp))
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
    }
}

@Composable
fun StudentGroupsScreen(groups: List<DirectoryItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 24.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Grupos", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold) }
        if (groups.isEmpty()) {
            item { EmptyDetailState("Voce ainda nao participa de grupos.") }
        } else {
            items(groups) { group -> StudentSimpleRow(group.name, group.description, Icons.Outlined.Group) }
        }
    }
}

@Composable
fun StudentEventsScreen(events: List<Event>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground).statusBarsPadding(),
        contentPadding = PaddingValues(16.dp, 24.dp, 16.dp, 140.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Eventos", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold) }
        if (events.isEmpty()) item { EmptyDetailState("Nenhum evento disponivel.") }
        items(events.sortedBy { it.eventDate }) { event -> EventCard(event) }
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
fun SettingsListRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
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

fun plannedStepsForDay(day: StudentRoutineDayPlan?, workouts: List<Workout>): List<WorkoutSessionStep> {
    if (day == null) return emptyList()
    val byName = workouts.associateBy { it.name.lowercase() }
    return day.workouts.flatMap { workoutName ->
        val workout = byName[workoutName.lowercase()]
        val steps = parseWorkoutSteps(workout?.description.orEmpty())
        if (steps.isEmpty()) {
            listOf(WorkoutSessionStep(name = workoutName, targetType = "open", targetValue = 0.0, unit = ""))
        } else {
            steps.map { it.copy(name = "${workoutName} - ${it.name}") }
        }
    }
}

fun parseWorkoutSteps(description: String): List<WorkoutSessionStep> =
    description.lineSequence()
        .filter { it.trim().startsWith("Secao ") }
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
