package com.example.myapplication

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.example.myapplication.data.DirectoryItem
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
import kotlin.math.roundToInt


data class Destination(
    val route: String,
    val label: String,
    val iconRes: Int,
    val activeIconRes: Int
)

typealias DirectoryEntry = DirectoryItem

data class DirectoryAction(
    val label: String,
    val iconRes: Int,
    val onClick: () -> Unit
)

val destinations = listOf(
    Destination("hub_fit", "Hub Fit", R.drawable.ic_nav_hub_fit, R.drawable.ic_nav_hub_fit_active),
    Destination("financeiro", "Financeiro", R.drawable.ic_nav_financeiro, R.drawable.ic_nav_financeiro_active),
    Destination("eventos", "Eventos", R.drawable.ic_nav_eventos, R.drawable.ic_nav_eventos_active)
)

val studentDestinations = listOf(
    Destination("hub_fit", "Hub Fit", R.drawable.ic_nav_hub_fit, R.drawable.ic_nav_hub_fit_active),
    Destination("eventos", "Eventos", R.drawable.ic_nav_eventos, R.drawable.ic_nav_eventos_active)
)

@Composable
fun AgeGoApp(viewModel: AgeGoViewModel = viewModel(), initialEventId: String? = null) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val session = state.authSession
    val context = LocalContext.current
    if (session == null) {
        RealAuthScreen(
            loading = state.authLoading,
            message = state.authMessage,
            onStartLogin = { identifier, onToken -> viewModel.startLogin(identifier, onToken) },
            onStartLoginResult = { identifier, onResult -> viewModel.startLogin(identifier, onResult) },
            onVerifyLogin = { identifier, token -> viewModel.verifyLogin(identifier, token) },
            onRegisterInstructor = { name, email, phone, onToken ->
                viewModel.registerInstructor(name, email, phone, onToken)
            },
            onVerifyInstructor = { email, token, displayName, photoUri, onSuccess, onFailure ->
                viewModel.verifyInstructor(context, email, token, displayName, photoUri, onSuccess, onFailure)
            },
            onStartStudent = { phone, onToken -> viewModel.startStudentFirstAccess(phone, onToken) },
            onCompleteStudent = { phone, email, nickname, photoUri, frequencyDays, token ->
                viewModel.completeStudentFirstAccess(phone, email, nickname, photoUri, frequencyDays, token, context) {}
            }
        )
        return
    }
    if (session.role == "student") {
        StudentPortalApp(
            state = state,
            initialEventId = initialEventId,
            pixKey = state.instructorSettings.pixKey,
            onProfileSave = { name, uri -> viewModel.saveProfile(context, name, uri) },
            onCheckInEvent = viewModel::checkInEvent,
            onSaveEventRunResult = viewModel::saveEventRunResult,
            onSaveWorkoutSession = viewModel::saveWorkoutSession,
            onRefresh = viewModel::refresh,
            onUploadMedia = { uri -> viewModel.uploadMedia(context, uri) },
            onSubmitPaymentProof = viewModel::submitPaymentProof,
            onLogout = viewModel::logout,
            onClearMessage = viewModel::clearMessage
        )
        return
    }
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = destinations.any { it.route == currentRoute }
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
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        val text = state.message
        if (!text.isNullOrBlank()) {
            snackbarHostState.showSnackbar(text)
            viewModel.clearMessage()
        }
    }

    Scaffold(containerColor = PurpleBackground, snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(bottomBarScroll)
                .statusBarsPadding()
                .padding( top = 16.dp)
        ) {
            NavHost(
                navController = navController,
                startDestination = "hub_fit",
                modifier = Modifier.fillMaxSize(),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
            composable("hub_fit") { HomeScreenV2(state) { navController.navigate(it) } }
            composable("financeiro") {
                EarningsScreen(
                    students = state.students,
                    routines = state.routines,
                    onReviewPayment = { student -> navController.navigate("payment_review/${student.id}") }
                )
            }
            composable("payment_review/{studentId}") { entry ->
                val student = state.students.firstOrNull { it.id == entry.arguments?.getString("studentId") }
                if (student != null) {
                    PaymentReviewScreen(
                        student = student,
                        onBack = navController::popBackStack,
                        onApprove = {
                            viewModel.approvePayment(student.id)
                            navController.popBackStack()
                        },
                        onReject = { reason ->
                            viewModel.rejectPayment(student.id, reason)
                            navController.popBackStack()
                        }
                    )
                }
            }
            composable("settings") {
                InstructorSettingsScreen(
                    profileName = state.authSession?.name.orEmpty(),
                    profileAvatarUrl = state.authSession?.avatarUrl.orEmpty(),
                    pixKey = state.instructorSettings.pixKey,
                    onProfileSave = { name, uri -> viewModel.saveProfile(context, name, uri) },
                    onPixKeyChange = { viewModel.saveInstructorSettings(state.instructorSettings.copy(pixKey = it)) },
                    onBack = navController::popBackStack,
                    onClearLocalData = viewModel::logout
                )
            }
            composable("eventos") {
                EventsScreen(
                    events = state.events,
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onNewEvent = { navController.navigate("event/new") },
                    onEventClick = { navController.navigate("event/${it.id}") }
                )
            }
            composable("students") {
                StudentsScreen(
                    students = state.students,
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onNewStudent = { navController.navigate("student/new") },
                    onStudentClick = { navController.navigate("student/${it.id}") }
                )
            }
            composable("student/new") {
                val context = LocalContext.current
                NewStudentFlowScreen(
                    workouts = state.workouts,
                    onBack = navController::popBackStack,
                    onSave = { student, customWorkout, onComplete ->
                        val saveStudent: (Student) -> Unit = { studentToSave ->
                            viewModel.saveStudent(studentToSave) { saved ->
                            if (saved.accessCode.isNotBlank()) {
                                Toast.makeText(
                                    context,
                                    "Codigo do aluno: ${saved.accessCode}. Valido por 24h.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            onComplete()
                            }
                        }
                        if (customWorkout != null) {
                            viewModel.saveWorkout(customWorkout) { savedWorkout ->
                                saveStudent(student.copy(routine = savedWorkout.id, planName = savedWorkout.name))
                            }
                        } else saveStudent(student)
                    }
                )
            }
            composable("student/{studentId}") { entry ->
                val student = state.students.firstOrNull { it.id == entry.arguments?.getString("studentId") }
                StudentDetailScreen(
                    student = student,
                    routines = state.workouts
                        .filter { workout ->
                            val private = workout.description.orEmpty().lineSequence().any { line -> line.trim() == "Visibilidade: privado" }
                            !private || workout.id == student?.routine
                        }
                        .map { workout -> DirectoryEntry(workout.id, workout.name, workout.status, workout.description.orEmpty()) },
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveStudent(it)
                        navController.popBackStack()
                    },
                    onDelete = {
                        viewModel.deleteStudent(it)
                        navController.popBackStack()
                    }
                )
            }
            composable("workouts") {
                WorkoutsScreen(
                    workouts = state.workouts.filterNot { it.description.orEmpty().lineSequence().any { line -> line.trim() == "Visibilidade: privado" } },
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onNewWorkout = { navController.navigate("workout/new") },
                    onWorkoutClick = { navController.navigate("workout/${it.id}") }
                )
            }
            composable("workout/new") {
                WorkoutFormScreen(
                    workout = null,
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveWorkout(it)
                        navController.popBackStack()
                    },
                    onDelete = null
                )
            }
            composable("workout/{workoutId}") { entry ->
                val workout = state.workouts.firstOrNull { it.id == entry.arguments?.getString("workoutId") }
                WorkoutDetailScreen(
                    workout = workout,
                    onBack = navController::popBackStack,
                    onEdit = { if (workout != null) navController.navigate("workout/${workout.id}/edit") },
                    onDelete = {
                        viewModel.deleteWorkout(it)
                        navController.popBackStack()
                    }
                )
            }
            composable("workout/{workoutId}/edit") { entry ->
                val workout = state.workouts.firstOrNull { it.id == entry.arguments?.getString("workoutId") }
                WorkoutFormScreen(
                    workout = workout,
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveWorkout(it)
                        navController.popBackStack()
                    },
                    onDelete = {
                        viewModel.deleteWorkout(it)
                        navController.popBackStack()
                    }
                )
            }
            composable("announcements") {
                AnnouncementsScreen(
                    announcements = state.announcements,
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onCreate = { navController.navigate("announcement/new") }
                )
            }
            composable("announcement/new") {
                AnnouncementFormScreen(
                    onBack = navController::popBackStack,
                    onSave = { content, targetType ->
                        viewModel.saveAnnouncement(content, targetType)
                        navController.popBackStack()
                    }
                )
            }
            composable("challenges") {
                ChallengesScreen(
                    challenges = state.challenges,
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onCreate = { navController.navigate("challenge/new") },
                    onChallengeClick = { navController.navigate("challenge/${it.id}") }
                )
            }
            composable("challenge/new") {
                ChallengeFormScreen(
                    challenge = null,
                    onBack = navController::popBackStack,
                    onSave = { _, name, description, targetType, targetValue ->
                        viewModel.saveChallenge("", name, description, targetType, targetValue)
                        navController.popBackStack()
                    }
                )
            }
            composable("challenge/{challengeId}") { entry ->
                val challenge = state.challenges.firstOrNull { it.id == entry.arguments?.getString("challengeId") }
                ChallengeDetailScreen(
                    challenge = challenge,
                    onBack = navController::popBackStack,
                    onEdit = { if (challenge != null) navController.navigate("challenge/${challenge.id}/edit") },
                    onDelete = {
                        viewModel.deleteChallenge(it)
                        navController.popBackStack()
                    }
                )
            }
            composable("challenge/{challengeId}/edit") { entry ->
                val challenge = state.challenges.firstOrNull { it.id == entry.arguments?.getString("challengeId") }
                ChallengeFormScreen(
                    challenge = challenge,
                    onBack = navController::popBackStack,
                    onSave = { id, name, description, targetType, targetValue ->
                        viewModel.saveChallenge(id, name, description, targetType, targetValue)
                        navController.popBackStack()
                    }
                )
            }
            composable("event/new") {
                val context = LocalContext.current
                EventDetailScreen(
                    event = null,
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveEvent(it)
                        navController.popBackStack()
                    },
                    onDelete = null,
                    onUploadMedia = { uri -> viewModel.uploadMedia(context, uri) }
                )
            }
            composable("event/{eventId}") { entry ->
                val context = LocalContext.current
                val event = state.events.firstOrNull { it.id == entry.arguments?.getString("eventId") }
                EventDetailScreen(
                    event = event,
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveEvent(it)
                        navController.popBackStack()
                    },
                    onDelete = {
                        viewModel.deleteEvent(it)
                        navController.popBackStack()
                    },
                    onOpenEvent = { event?.id?.let(viewModel::openEventCheckIn) },
                    onStartGroupRun = { event?.id?.let(viewModel::startGroupEvent) },
                    onFinishGroupRun = { event?.id?.let(viewModel::finishGroupEvent) },
                    onUploadMedia = { uri -> viewModel.uploadMedia(context, uri) }
                )
            }
        }
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                0.42f to PurpleBackground.copy(alpha = 0.72f),
                                1f to PurpleDeep
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentRoute == "hub_fit") {
                        TrainingNowBar(
                            users = state.trainingNow,
                            students = state.students
                        )
                    }
                    PillBottomBar(
                        currentRoute = currentRoute,
                        collapsed = bottomBarCollapsed,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}

