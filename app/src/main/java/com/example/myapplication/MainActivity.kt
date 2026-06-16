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
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(PurpleBackground.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(PurpleDeep.toArgb())
        )
        setContent { AgeGoTheme { AgeGoApp() } }
    }
}

private data class Destination(
    val route: String,
    val label: String,
    val iconRes: Int,
    val activeIconRes: Int
)

private data class DirectoryEntry(
    val id: String,
    val name: String,
    val status: String,
    val description: String
)

private data class DirectoryAction(
    val label: String,
    val iconRes: Int,
    val onClick: () -> Unit
)

private val destinations = listOf(
    Destination("hub_fit", "Hub Fit", R.drawable.ic_nav_hub_fit, R.drawable.ic_nav_hub_fit_active),
    Destination("financeiro", "Financeiro", R.drawable.ic_nav_financeiro, R.drawable.ic_nav_financeiro_active),
    Destination("comunidade", "Comunidade", R.drawable.ic_nav_comunidade, R.drawable.ic_nav_comunidade_active),
    Destination("eventos", "Eventos", R.drawable.ic_nav_eventos, R.drawable.ic_nav_eventos_active)
)

private val demoModalities = listOf(
    DirectoryEntry("corrida", "Corrida", "active", "Treinos de rua, pista e provas."),
    DirectoryEntry("fortalecimento", "Fortalecimento", "active", "Base de forca, mobilidade e estabilidade."),
    DirectoryEntry("mobilidade", "Mobilidade", "draft", "Sessoes de recuperacao e prevencao.")
)

private val demoGroups = listOf(
    DirectoryEntry("iniciante", "Iniciantes", "active", "Grupo para alunos em fase inicial."),
    DirectoryEntry("performance", "Performance", "active", "Treinos focados em evolucao de pace."),
    DirectoryEntry("longao", "Longao de sabado", "paused", "Organizacao dos treinos longos do fim de semana.")
)

@Composable
private fun AgeGoApp(viewModel: AgeGoViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val routines = remember { mutableStateListOf<DirectoryEntry>().apply { addAll(demoModalities) } }
    val groups = remember { mutableStateListOf<DirectoryEntry>().apply { addAll(demoGroups) } }
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = destinations.any { it.route == currentRoute }

    Scaffold(containerColor = PurpleBackground) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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
            composable("financeiro") { EarningsScreen() }
            composable("comunidade") {
                CommunityScreen(
                    posts = state.communityPosts,
                    workouts = state.workouts,
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onCreatePost = { target -> navController.navigate("community/new/$target") },
                    onPostClick = { post -> navController.navigate("community/post/${post.id}") },
                    onLike = viewModel::toggleCommunityLike,
                    onComment = viewModel::addCommunityComment,
                    onShare = viewModel::shareCommunityPost
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
            composable("modalities") {
                RoutinesScreen(
                    routines = routines,
                    onBack = navController::popBackStack,
                    onGroupsClick = { navController.navigate("groups") },
                    onNewRoutine = { navController.navigate("routine/new") },
                    onRoutineClick = { navController.navigate("routine/${it.id}") }
                )
            }
            composable("groups") {
                GroupsScreen(
                    groups = groups,
                    onBack = navController::popBackStack,
                    onStudentsClick = { navController.navigate("students") },
                    onNewGroup = { navController.navigate("group/new") },
                    onGroupClick = { navController.navigate("group/${it.id}") }
                )
            }
            composable("students") {
                StudentsScreen(
                    students = state.students,
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onGroupsClick = { navController.navigate("groups") },
                    onNewStudent = { navController.navigate("student/new") },
                    onStudentClick = { navController.navigate("student/${it.id}") }
                )
            }
            composable("student/new") {
                StudentFormScreen(
                    student = null,
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveStudent(it)
                        navController.popBackStack()
                    },
                    onDelete = null
                )
            }
            composable("student/{studentId}") { entry ->
                val student = state.students.firstOrNull { it.id == entry.arguments?.getString("studentId") }
                StudentFormScreen(
                    student = student,
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
            composable("routine/new") {
                DirectoryFormScreen(
                    title = "Nova rotina",
                    entry = null,
                    nameLabel = "Nome da rotina",
                    descriptionLabel = "Descricao",
                    onBack = navController::popBackStack,
                    onSave = {
                        routines.add(0, it.copy(id = it.id.ifBlank { java.util.UUID.randomUUID().toString() }))
                        navController.popBackStack()
                    },
                    onDelete = null
                )
            }
            composable("routine/{routineId}") { entry ->
                val routine = routines.firstOrNull { it.id == entry.arguments?.getString("routineId") }
                DirectoryFormScreen(
                    title = "Editar rotina",
                    entry = routine,
                    nameLabel = "Nome da rotina",
                    descriptionLabel = "Descricao",
                    onBack = navController::popBackStack,
                    onSave = { saved ->
                        val index = routines.indexOfFirst { it.id == saved.id }
                        if (index >= 0) routines[index] = saved
                        navController.popBackStack()
                    },
                    onDelete = { id ->
                        routines.removeAll { it.id == id }
                        navController.popBackStack()
                    }
                )
            }
            composable("group/{groupId}") { entry ->
                val group = groups.firstOrNull { it.id == entry.arguments?.getString("groupId") }
                DirectoryFormScreen(
                    title = "Editar grupo",
                    entry = group,
                    nameLabel = "Nome do grupo",
                    descriptionLabel = "Descricao",
                    onBack = navController::popBackStack,
                    onSave = { saved ->
                        val index = groups.indexOfFirst { it.id == saved.id }
                        if (index >= 0) groups[index] = saved
                        navController.popBackStack()
                    },
                    onDelete = { id ->
                        groups.removeAll { it.id == id }
                        navController.popBackStack()
                    }
                )
            }
            composable("group/new") {
                DirectoryFormScreen(
                    title = "Novo grupo",
                    entry = null,
                    nameLabel = "Nome do grupo",
                    descriptionLabel = "Descricao",
                    onBack = navController::popBackStack,
                    onSave = {
                        groups.add(0, it.copy(id = it.id.ifBlank { java.util.UUID.randomUUID().toString() }))
                        navController.popBackStack()
                    },
                    onDelete = null
                )
            }
            composable("workouts") {
                WorkoutsScreen(
                    workouts = state.workouts,
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onModalitiesClick = { navController.navigate("modalities") },
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
                    },
                    onDelete = null
                )
            }
            composable("workout/{workoutId}") { entry ->
                val workout = state.workouts.firstOrNull { it.id == entry.arguments?.getString("workoutId") }
                WorkoutFormScreen(
                    workout = workout,
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveWorkout(it)
                    },
                    onDelete = {
                        viewModel.deleteWorkout(it)
                        navController.popBackStack()
                    }
                )
            }
            composable("announcements") {
                CommunityScreen(
                    posts = state.communityPosts,
                    workouts = state.workouts,
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onCreatePost = { target -> navController.navigate("community/new/$target") },
                    onPostClick = { post -> navController.navigate("community/post/${post.id}") },
                    onLike = viewModel::toggleCommunityLike,
                    onComment = viewModel::addCommunityComment,
                    onShare = viewModel::shareCommunityPost
                )
            }
            composable("community/new/{target}") { entry ->
                val target = entry.arguments?.getString("target") ?: "groups"
                CommunityPostFormScreen(
                    workouts = state.workouts,
                    target = target,
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveCommunityPost(it)
                        navController.popBackStack()
                    }
                )
            }
            composable("community/post/{postId}") { entry ->
                val post = state.communityPosts.firstOrNull { it.id == entry.arguments?.getString("postId") }
                if (post != null) {
                    CommunityPostDetailScreen(
                        post = post,
                        workoutName = state.workouts.firstOrNull { it.id == post.linkedWorkoutId }?.name,
                        onBack = navController::popBackStack,
                        onLike = { viewModel.toggleCommunityLike(post.id) },
                        onComment = { content -> viewModel.addCommunityComment(post.id, content) },
                        onReply = { commentId, content -> viewModel.replyCommunityComment(post.id, commentId, content) },
                        onCommentLike = { commentId -> viewModel.toggleCommunityCommentLike(post.id, commentId) },
                        onShare = { viewModel.shareCommunityPost(post.id) }
                    )
                }
            }
            composable("event/new") {
                EventFormScreen(
                    event = null,
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveEvent(it)
                        navController.popBackStack()
                    },
                    onDelete = null
                )
            }
            composable("event/{eventId}") { entry ->
                val event = state.events.firstOrNull { it.id == entry.arguments?.getString("eventId") }
                EventFormScreen(
                    event = event,
                    onBack = navController::popBackStack,
                    onSave = {
                        viewModel.saveEvent(it)
                        navController.popBackStack()
                    },
                    onDelete = {
                        viewModel.deleteEvent(it)
                        navController.popBackStack()
                    }
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
                        TrainingNowBar()
                    }
                    PillBottomBar(
                        currentRoute = currentRoute,
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

@Composable
private fun HomeScreenV2(state: AgeGoUiState, navigate: (String) -> Unit) {
    val days = remember { daysAroundToday(previous = 7, next = 7) }
    val todayIndex = remember(days) { days.indexOfFirst { it.isToday }.coerceAtLeast(0) }
    val dayListState = rememberLazyListState(initialFirstVisibleItemIndex = todayIndex)
    var selectedDay by remember { mutableStateOf<String?>(null) }
    val selectedEvents = state.events
        .filter { selectedDay != null && it.eventDate.take(10) == selectedDay }
        .sortedBy { it.eventDate }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpleBackground)
                .padding( top = 24.dp, bottom = 26.dp)
        ) {
            Box(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = "Perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(42.dp).clip(CircleShape)
                )
            }
            Spacer(Modifier.height(34.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                state = dayListState,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(end = 4.dp)
            ) {
                items(days) { day ->
                    DayEventCard(
                        label = day.label,
                        count = state.events.count { it.eventDate.take(10) == day.key },
                        selected = selectedDay == day.key,
                        isToday = day.isToday,
                        modifier = Modifier.width(126.dp),
                        onClick = {
                            selectedDay = if (selectedDay == day.key) null else day.key
                        }
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = PurpleSurface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        ShortcutCircleSvg("Alunos", R.drawable.ic_option_alunos) { navigate("students") }
                        ShortcutCircleSvg("Rotinas", R.drawable.ic_option_modalidades) { navigate("modalities") }
                        ShortcutCircleSvg("Treinos", R.drawable.ic_option_treinos) { navigate("workouts") }
                        ShortcutCircleSvg("Grupos", R.drawable.ic_option_grupos) { navigate("groups") }
                    }
                }
                if (selectedDay != null) {
                    item {
                        Text(
                            "Eventos do dia - ${days.first { it.key == selectedDay }.label}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (selectedEvents.isEmpty()) {
                        item {
                            Surface(color = PurpleBackground, shape = RoundedCornerShape(18.dp)) {
                                Text("Nenhum evento programado para este dia.", Modifier.fillMaxWidth().padding(16.dp))
                            }
                        }
                    } else {
                        items(selectedEvents) { EventCard(it) }
                    }
                }
                item {
                    Text("Monitoramento", fontSize = 18.sp, fontWeight = FontWeight.Normal)
                }
                item {
                    Surface(color = PurpleBackground, shape = RoundedCornerShape(14.dp)) {
                        Text(
                            "Nenhum atalho definido ainda.",
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (state.isLoading) item { LoadingBox() }
            }
        }
    }
}

@Composable
private fun HomeScreen(state: AgeGoUiState, navigate: (String) -> Unit) {
    val days = remember { nextDays(4) }
    var selectedDay by remember { mutableStateOf<String?>(null) }
    val selectedEvents = state.events
        .filter { selectedDay != null && it.eventDate.take(10) == selectedDay }
        .sortedBy { it.eventDate }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PurpleBackground),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        item {
            Surface(color = PurpleDeep) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 26.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = "Perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(42.dp).clip(CircleShape)
                    )
                    Spacer(Modifier.height(34.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        days.forEach { day ->
                            DayEventCard(
                                label = day.label,
                                count = state.events.count { it.eventDate.take(10) == day.key },
                                selected = selectedDay == day.key,
                                isToday = day == days.first(),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedDay = if (selectedDay == day.key) null else day.key
                                }
                            )
                        }
                    }
                }
            }
        }
        if (selectedDay != null) {
            item {
                Text(
                    "Eventos do dia · ${days.first { it.key == selectedDay }.label}",
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp, end = 16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (selectedEvents.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp),
                        color = PurpleSurface,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Nenhum evento programado para este dia.", Modifier.fillMaxWidth().padding(16.dp))
                    }
                }
            } else {
                items(selectedEvents) {
                    Box(Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp)) { EventCard(it) }
                }
            }
        }
        item {
            Row(
                Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ShortcutCircle("Alunos", Icons.Outlined.Group) { navigate("students") }
                ShortcutCircle("Treinos", Icons.Outlined.DirectionsRun) { navigate("workouts") }
                ShortcutCircle("Avisos", Icons.Outlined.Campaign) { navigate("announcements") }
            }
        }
        item {
            Text(
                "Atividade recente",
                modifier = Modifier.padding(start = 16.dp, top = 28.dp, end = 16.dp),
                fontSize = 20.sp
            )
        }
        items(state.announcements.take(2)) {
            Box(Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp)) { AnnouncementCard(it) }
        }
        if (state.isLoading) item { LoadingBox() }
    }
}

@Composable
private fun DayEventCard(
    label: String,
    count: Int,
    selected: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .alpha(if (selected || isToday) 1f else .6f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Lime
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(30f / 25f)
                .padding(horizontal = 6.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                label,
                color = LimeMuted,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    count.toString(),
                    color = LimeMuted,
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ShortcutCircle(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(PurpleSurface)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, label, Modifier.size(31.dp), tint = Lime)
        }
        Text(label, Modifier.padding(top = 8.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun ShortcutCircleSvg(
    label: String,
    iconRes: Int,
    containerColor: Color = PurpleBackground,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(containerColor)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(40.dp),
                tint = Color.White,

            )
        }
        Text(
            label,
            Modifier.padding(top = 6.dp),
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun TrainingNowBar() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(start = 6.dp , end = 6.dp),
        color = PurpleBackground,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Treinando agora", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
            Spacer(Modifier.width(12.dp))

            // Pill com avatares lado a lado
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Lime)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrainingAvatar()
                Spacer(Modifier.width(2.dp))
                TrainingAvatar()
            }

            Spacer(Modifier.width(6.dp))

// Badge "+99" separado
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Lime)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("+99", color = PurpleDeep, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Abrir", tint = Color.White)
        }
    }
}

@Composable
private fun TrainingAvatar() {
    Image(
        painter = painterResource(R.drawable.profile),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
    )
}

@Composable
private fun PillBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val selectedIndex = destinations.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    var barInteracting by remember { mutableStateOf(false) }
    var pendingIndex by remember { mutableStateOf(selectedIndex) }
    val barHeight by animateDpAsState(
        targetValue = if (barInteracting) 74.dp else 68.dp,
        animationSpec = tween(durationMillis = 140),
        label = "bottom-bar-height"
    )
    LaunchedEffect(selectedIndex) {
        if (!barInteracting) {
            pendingIndex = selectedIndex
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 36.dp),
        shape = RoundedCornerShape(50),
        color = NavigationPurple.copy(alpha = 0.60f),
        shadowElevation = 12.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .pointerInput(destinations, currentRoute) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        barInteracting = true

                        fun indexByPosition(position: Offset): Int {
                            val widthPerItem = size.width / destinations.size.toFloat()
                            val clampedX = position.x.coerceIn(0f, size.width.toFloat())
                            return ((clampedX / widthPerItem) - 0.5f)
                                .roundToInt()
                                .coerceIn(0, destinations.lastIndex)
                        }

                        pendingIndex = indexByPosition(down.position)
                        do {
                            val event = awaitPointerEvent()
                            event.changes.firstOrNull()?.let { change ->
                                if (change.pressed) {
                                    pendingIndex = indexByPosition(change.position)
                                }
                            }
                        } while (event.changes.any { it.pressed })

                        val targetRoute = destinations[pendingIndex].route
                        barInteracting = false
                        if (targetRoute != currentRoute) {
                            onNavigate(targetRoute)
                        }
                    }
                }
        ) {
            val itemWidth = maxWidth / destinations.size
            val itemInset = 4.dp
            val indicatorWidth = itemWidth - (itemInset * 2)
            val indicatorHeight = barHeight - (itemInset * 2)
            val indicatorIndex = if (barInteracting || pendingIndex != selectedIndex) pendingIndex else selectedIndex
            val indicatorOffset by animateDpAsState(
                targetValue = itemWidth * indicatorIndex + itemInset,
                animationSpec = tween(durationMillis = 260),
                label = "bottom-bar-indicator"
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = indicatorOffset)
                    .width(indicatorWidth)
                    .height(indicatorHeight)
                    .clip(RoundedCornerShape(50))
                    .background(Lime)
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEachIndexed { index, destination ->
                    val selected = index == indicatorIndex
                    val interactionSource = remember { MutableInteractionSource() }
                    val pressed by interactionSource.collectIsPressedAsState()
                    val glowAlpha by animateFloatAsState(
                        targetValue = if (pressed) 0.42f else 0f,
                        animationSpec = tween(durationMillis = 140),
                        label = "bottom-bar-press-glow"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(barHeight)
                            .padding(itemInset)
                            .clip(RoundedCornerShape(50))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onNavigate(destination.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (glowAlpha > 0f) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.White.copy(alpha = glowAlpha))
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(if (selected) destination.activeIconRes else destination.iconRes),
                                contentDescription = destination.label,
                                modifier = Modifier.size(40.dp),
                                tint = if (selected) PurpleBackground else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCard(event: Event) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PurpleSurface)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(PurpleDeep), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.DirectionsRun, null, tint = Lime)
            }
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(event.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    listOfNotNull(eventTime(event.eventDate), event.location).joinToString(" · "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(18.dp))
        }
    }
}

private data class DayOption(val key: String, val label: String, val isToday: Boolean = false)

private fun daysAroundToday(previous: Int, next: Int): List<DayOption> {
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    val weekdayFormat = java.text.SimpleDateFormat("EEE", java.util.Locale("pt", "BR"))
    val calendar = java.util.Calendar.getInstance().apply {
        add(java.util.Calendar.DAY_OF_YEAR, -previous)
    }
    return List(previous + next + 1) { index ->
        val offset = index - previous
        val label = when (offset) {
            -1 -> "Ontem"
            0 -> "Hoje"
            1 -> "Amanha"
            else -> weekdayFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
        }
        DayOption(dateFormat.format(calendar.time), label, offset == 0).also {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
    }
}

private fun nextDays(amount: Int): List<DayOption> {
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    val weekdayFormat = java.text.SimpleDateFormat("EEE", java.util.Locale("pt", "BR"))
    val calendar = java.util.Calendar.getInstance()
    return List(amount) { index ->
        val label = when (index) {
            0 -> "Hoje"
            1 -> "Amanhã"
            else -> weekdayFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
        }
        DayOption(dateFormat.format(calendar.time), label).also {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
    }
}

private fun eventTime(value: String): String = runCatching {
    val time = value.substringAfter('T').take(5)
    "${time}h"
}.getOrDefault("")

private fun defaultEventDate(): String {
    val calendar = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 7)
        set(java.util.Calendar.MINUTE, 0)
    }
    return java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.US).format(calendar.time)
}

@Composable
private fun <T> DirectoryScreen(
    title: String,
    searchPlaceholder: String,
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    actions: List<DirectoryAction>,
    items: List<T>,
    loading: Boolean,
    itemTitle: (T) -> String,
    itemStatus: (T) -> String,
    itemMatchesQuery: (T, String) -> Boolean,
    onBack: () -> Unit,
    onItemClick: (T) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var searchMode by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val keyboardVisible = WindowInsets.ime.getBottom(density) > 0
    val filtered = items.filter { item ->
        itemMatchesQuery(item, query) && (selectedFilter == "Todos" || itemStatus(item) == selectedFilter)
    }

    BackHandler(enabled = searchMode) {
        focusManager.clearFocus()
        searchMode = false
    }

    LaunchedEffect(searchMode) {
        if (searchMode) {
            kotlinx.coroutines.delay(60)
            searchFocusRequester.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpleBackground)
                .padding(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Text(
                title,
                modifier = Modifier.padding(start = 4.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }
        AnimatedVisibility(
            visible = !searchMode,
            enter = fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = fadeOut(animationSpec = tween(durationMillis = 140))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                actions.forEach { action ->
                    ShortcutCircleSvg(
                        label = action.label,
                        iconRes = action.iconRes,
                        containerColor = PurpleSurface,
                        onClick = action.onClick
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = PurpleSurface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 18.dp,
                        end = 16.dp,
                        bottom = if (searchMode) 112.dp else 150.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    if (!searchMode) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp),
                                color = PurpleSurface,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        filters.forEach { option ->
                                            SlimFilterBadge(
                                                modifier = Modifier.weight(1f),
                                                label = option,
                                                selected = selectedFilter == option,
                                                onClick = { onFilterSelected(option) }
                                            )
                                        }
                                    }
                                    StudentSearchButton(
                                        value = query,
                                        placeholder = searchPlaceholder,
                                        onClick = { searchMode = true }
                                    )
                                }
                            }
                        }
                    }
                    if (loading) {
                        repeat(8) { index ->
                            item { DirectorySkeletonRow(index) }
                        }
                    } else {
                        items(filtered) { item ->
                            DirectoryListRow(title = itemTitle(item), onClick = { onItemClick(item) })
                        }
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = searchMode,
                    enter = fadeIn(animationSpec = tween(durationMillis = 180)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 140)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = if (keyboardVisible) 6.dp else 12.dp
                        )
                ) {
                    StudentSearchBar(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = searchPlaceholder,
                        modifier = Modifier.shadow(14.dp, RoundedCornerShape(50)),
                        focusRequester = searchFocusRequester
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentsScreen(
    students: List<Student>,
    loading: Boolean,
    onBack: () -> Unit,
    onGroupsClick: () -> Unit,
    onNewStudent: () -> Unit,
    onStudentClick: (Student) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Todos") }
    var searchMode by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val keyboardVisible = WindowInsets.ime.getBottom(density) > 0
    val filtered = students.filter { student ->
        student.name.contains(query, true) && (filter == "Todos" || statusLabel(student.status) == filter)
    }

    BackHandler(enabled = searchMode) {
        focusManager.clearFocus()
        searchMode = false
    }

    LaunchedEffect(searchMode) {
        if (searchMode) {
            kotlinx.coroutines.delay(60)
            searchFocusRequester.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpleBackground)
                .padding(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Text(
                "Hub Fit - Alunos",
                modifier = Modifier.padding(start = 4.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }
        AnimatedVisibility(
            visible = !searchMode,
            enter = fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = fadeOut(animationSpec = tween(durationMillis = 140))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                StudentActionCircle("Novo aluno", "+") { onNewStudent() }
                ShortcutCircleSvg(
                    label = "Grupos",
                    iconRes = R.drawable.ic_option_grupos,
                    containerColor = PurpleSurface,
                    onClick = onGroupsClick
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = PurpleSurface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 18.dp,
                        end = 16.dp,
                        bottom = if (searchMode) 112.dp else 150.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    if (!searchMode) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp),
                                color = PurpleSurface,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("Todos", "Em dia", "A pagar").forEach { option ->
                                            SlimFilterBadge(
                                                modifier = Modifier.weight(1f),
                                                label = option,
                                                selected = filter == option,
                                                onClick = { filter = option }
                                            )
                                        }
                                    }
                                    StudentSearchButton(
                                        value = query,
                                        onClick = { searchMode = true }
                                    )
                                }
                            }
                        }
                        if (loading) {
                            repeat(8) { index ->
                                item { DirectorySkeletonRow(index) }
                            }
                        } else {
                            items(filtered) { student ->
                                StudentListRow(student = student, onClick = { onStudentClick(student) })
                            }
                        }
                    } else {
                        if (loading) {
                            repeat(8) { index ->
                                item { DirectorySkeletonRow(index) }
                            }
                        } else {
                            items(filtered) { student ->
                                StudentListRow(student = student, onClick = { onStudentClick(student) })
                            }
                        }
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = searchMode,
                    enter = fadeIn(animationSpec = tween(durationMillis = 180)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 140)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = if (keyboardVisible) 6.dp else 12.dp
                        )
                ) {
                    StudentSearchBar(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.shadow(14.dp, RoundedCornerShape(50)),
                        focusRequester = searchFocusRequester
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentActionCircle(label: String, symbol: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(PurpleSurface)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(symbol, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Normal)
        }
        Text(
            label,
            Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun SlimFilterBadge(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = Lime.copy(alpha = if (selected) 1f else 0.62f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
            color = PurpleBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentSearchButton(value: String, onClick: () -> Unit, placeholder: String = "Pesquisar aluno") {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (value.isBlank()) placeholder else value,
                modifier = Modifier.weight(1f),
                color = PurpleBackground.copy(alpha = if (value.isBlank()) .55f else 1f),
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(Icons.Outlined.Search, contentDescription = "Pesquisar", tint = PurpleBackground)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Pesquisar aluno",
    focusRequester: FocusRequester? = null,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged { onFocusChanged(it.isFocused) },
        placeholder = { Text(placeholder, color = PurpleBackground.copy(alpha = .55f)) },
        trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Pesquisar", tint = PurpleBackground) },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            focusedTextColor = PurpleBackground,
            unfocusedTextColor = PurpleBackground,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = PurpleBackground
        )
    )
}

@Composable
private fun StudentListRow(student: Student, onClick: () -> Unit) {
    DirectoryListRow(title = student.name, onClick = onClick)
}

@Composable
private fun DirectoryListRow(title: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Abrir aluno",
                modifier = Modifier.size(18.dp),
                tint = Color.White.copy(alpha = 0.78f)
            )
        }
        HorizontalDivider(color = NavigationPurple, thickness = 0.8.dp)
    }
}

@Composable
private fun DirectorySkeletonRow(index: Int) {
    val widthFraction = when (index % 4) {
        0 -> 0.72f
        1 -> 0.58f
        2 -> 0.82f
        else -> 0.66f
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(widthFraction)
                    .height(14.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.16f))
            )
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )
        }
        HorizontalDivider(color = NavigationPurple.copy(alpha = 0.55f), thickness = 0.8.dp)
    }
}

@Composable
private fun StudentCard(student: Student, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = PurpleSurface)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(LimeMuted), contentAlignment = Alignment.Center) {
                Text(student.name.take(1), color = Lime, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(student.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(student.planName, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
            StatusBadge(statusLabel(student.status), statusColor(student.status))
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.padding(start = 6.dp).size(18.dp))
        }
    }
}

@Composable
private fun WorkoutsScreen(
    workouts: List<Workout>,
    loading: Boolean,
    onBack: () -> Unit,
    onModalitiesClick: () -> Unit,
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
            DirectoryAction("Novo treino", R.drawable.ic_option_treinos, onNewWorkout),
            DirectoryAction("Rotinas", R.drawable.ic_option_modalidades, onModalitiesClick)
        ),
        items = workouts,
        loading = loading,
        itemTitle = { it.name },
        itemStatus = { if (it.status == "active") "Ativos" else "Inativos" },
        itemMatchesQuery = { workout, query -> workout.name.contains(query, true) },
        onBack = onBack,
        onItemClick = onWorkoutClick
    )
}

@Composable
private fun RoutinesScreen(
    routines: List<DirectoryEntry>,
    onBack: () -> Unit,
    onGroupsClick: () -> Unit,
    onNewRoutine: () -> Unit,
    onRoutineClick: (DirectoryEntry) -> Unit
) {
    var filter by remember { mutableStateOf("Todos") }
    DirectoryScreen(
        title = "Hub Fit - Rotinas",
        searchPlaceholder = "Pesquisar rotina",
        filters = listOf("Todos", "Ativas", "Inativas"),
        selectedFilter = filter,
        onFilterSelected = { filter = it },
        actions = listOf(
            DirectoryAction("Nova rotina", R.drawable.ic_option_modalidades, onNewRoutine),
            DirectoryAction("Grupos", R.drawable.ic_option_grupos, onGroupsClick)
        ),
        items = routines,
        loading = false,
        itemTitle = { it.name },
        itemStatus = { directoryStatusLabel(it.status) },
        itemMatchesQuery = { modality, query -> modality.name.contains(query, true) },
        onBack = onBack,
        onItemClick = onRoutineClick
    )
}

@Composable
private fun GroupsScreen(
    groups: List<DirectoryEntry>,
    onBack: () -> Unit,
    onStudentsClick: () -> Unit,
    onNewGroup: () -> Unit,
    onGroupClick: (DirectoryEntry) -> Unit
) {
    var filter by remember { mutableStateOf("Todos") }
    DirectoryScreen(
        title = "Hub Fit - Grupos",
        searchPlaceholder = "Pesquisar grupo",
        filters = listOf("Todos", "Ativos", "Inativos"),
        selectedFilter = filter,
        onFilterSelected = { filter = it },
        actions = listOf(
            DirectoryAction("Novo grupo", R.drawable.ic_option_grupos, onNewGroup),
            DirectoryAction("Alunos", R.drawable.ic_option_alunos, onStudentsClick)
        ),
        items = groups,
        loading = false,
        itemTitle = { it.name },
        itemStatus = { directoryStatusLabel(it.status) },
        itemMatchesQuery = { group, query -> group.name.contains(query, true) },
        onBack = onBack,
        onItemClick = onGroupClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentFormScreen(
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
private fun WorkoutFormScreen(
    workout: Workout?,
    onBack: () -> Unit,
    onSave: (Workout) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var step by remember(workout?.id) { mutableStateOf(0) }
    var name by remember(workout?.id) { mutableStateOf(workout?.name.orEmpty()) }
    var sections by remember(workout?.id) { mutableStateOf(listOf(WorkoutBuilderSection(1))) }

    BackHandler { if (step > 0) step-- else onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        WorkoutFlowHeader(
            breadcrumb = when (step) {
                0 -> "Treinos"
                1 -> "Informacoes do treino"
                else -> "Construtor de treino"
            },
            title = when (step) {
                0 -> "Adicionar novo treino"
                1 -> "Construtor de treino"
                else -> "Treino criado"
            },
            onBack = { if (step > 0) step-- else onBack() }
        )

        Spacer(Modifier.weight(1f))

        WorkoutBottomPanel {
            when (step) {
                0 -> {
                    WorkoutNameStep(name = name, onNameChange = { name = it })
                    WorkoutNextButton(enabled = name.isNotBlank()) { step = 1 }
                }
                1 -> {
                    WorkoutBuilderStep(
                        sections = sections,
                        onSectionsChange = { sections = it },
                        onFinish = {
                            onSave(
                                Workout(
                                    id = workout?.id.orEmpty(),
                                    name = name.trim(),
                                    description = workout?.description ?: "Treino personalizado",
                                    iconName = workout?.iconName,
                                    status = "active"
                                )
                            )
                            step = 2
                        }
                    )
                }
                else -> WorkoutCreatedStep(
                    onBackToWorkouts = onBack,
                    onAddModality = { },
                    onDelete = if (workout != null && onDelete != null) ({ onDelete(workout.id) }) else null
                )
            }
        }
    }
}

private data class WorkoutBuilderSection(val number: Int, val expanded: Boolean = false)
private data class WorkoutGoal(val id: Int, val name: String = "Nome do objetivo", val value: String = "20 min", val mode: String = "tempo")

@Composable
private fun WorkoutFlowHeader(breadcrumb: String, title: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 22.dp, end = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Text(breadcrumb, color = Color.White.copy(alpha = .68f), fontSize = 12.sp)
        }
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_option_treinos),
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
private fun WorkoutBottomPanel(content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .heightIn(max = maxHeight * .78f),
            color = PurpleSurface,
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
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
private fun WorkoutNameStep(name: String, onNameChange: (String) -> Unit) {
    TextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth().height(58.dp),
        placeholder = { Text("Nome do treino", color = Color.White.copy(alpha = .60f)) },
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
private fun WorkoutNextButton(enabled: Boolean, onClick: () -> Unit) {
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
private fun WorkoutBuilderStep(
    sections: List<WorkoutBuilderSection>,
    onSectionsChange: (List<WorkoutBuilderSection>) -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        sections.forEach { section ->
            WorkoutSectionCard(section = section)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFF5529B0).copy(alpha = .7f), RoundedCornerShape(14.dp))
                .background(Color(0xFF2C1252))
                .clickable {
                    onSectionsChange(
                        sections.map { it.copy(expanded = false) } +
                            WorkoutBuilderSection(sections.size + 1, expanded = false)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Adicionar etapa +", color = Color.White.copy(alpha = .78f), fontSize = 14.sp)
        }
        WorkoutNextButton(enabled = true, onClick = onFinish)
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun WorkoutSectionCard(section: WorkoutBuilderSection) {
    var goals by remember(section.number) { mutableStateOf(listOf(WorkoutGoal(1))) }
    var expandedGoalId by remember(section.number) { mutableStateOf<Int?>(null) }
    var editingGoalId by remember(section.number) { mutableStateOf<Int?>(null) }
    var goalNameHadFocus by remember(section.number) { mutableStateOf(false) }
    val goalNameFocusRequester = remember { FocusRequester() }
    val sectionMode = goals.firstOrNull()?.mode ?: "tempo"
    val sectionTotal = sectionTotalLabel(goals)

    LaunchedEffect(editingGoalId) {
        if (editingGoalId != null) {
            kotlinx.coroutines.delay(40)
            goalNameFocusRequester.requestFocus()
        } else {
            goalNameHadFocus = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 110.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(PurpleBackground)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Secao ${section.number}",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    " - $sectionTotal",
                    color = Color.White.copy(alpha = .60f),
                    fontSize = 16.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White.copy(alpha = .64f), CircleShape)
                    .clickable {
                        goals = goals + WorkoutGoal(
                            id = (goals.maxOfOrNull { it.id } ?: 0) + 1,
                            value = if (sectionMode == "tempo") "20 min" else "3 km",
                            mode = sectionMode
                        )
                        expandedGoalId = null
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Adicionar objetivo", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            goals.forEach { goal ->
                val expanded = expandedGoalId == goal.id
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 60.dp)
                            .clip(
                                if (expanded) {
                                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                } else {
                                    RoundedCornerShape(8.dp)
                                }
                            )
                            .background(PurpleSurface)
                            .combinedClickable(
                                onClick = { expandedGoalId = if (expanded) null else goal.id },
                                onDoubleClick = { editingGoalId = goal.id }
                            )
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (editingGoalId == goal.id) {
                            TextField(
                                value = goal.name,
                                onValueChange = { next ->
                                    goals = goals.map { if (it.id == goal.id) it.copy(name = next) else it }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .focusRequester(goalNameFocusRequester)
                                    .onFocusChanged {
                                        if (it.isFocused) {
                                            goalNameHadFocus = true
                                        } else if (goalNameHadFocus) {
                                            editingGoalId = null
                                        }
                                    },
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
                            Text("${goal.name} 2x", modifier = Modifier.weight(1f), color = Color.White, fontSize = 16.sp)
                        }
                        Text(goal.value, color = Color.White.copy(alpha = .82f), fontSize = 16.sp)
                    }
                    if (expanded) {
                        WorkoutConfigDropdown(
                            goal = goal,
                            onGoalChange = { updated ->
                                goals = goals.map { if (it.id == goal.id) updated else it }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutModeBadge(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = if (selected) Lime else Color(0xFF2C0B59),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            label,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            color = if (selected) PurpleDeep else Color.White.copy(alpha = .72f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WorkoutConfigDropdown(goal: WorkoutGoal, onGoalChange: (WorkoutGoal) -> Unit) {
    val valueFocusRequester = remember { FocusRequester() }
    val items = if (goal.mode == "tempo") {
        listOf("Tempo" to goal.value, "Descanso" to "2 min", "Repeticoes" to "2x")
    } else {
        listOf("Distancia" to goal.value, "Descanso" to "2 min", "Repeticoes" to "2x")
    }
    var values by remember(goal.id, goal.mode) { mutableStateOf(items) }
    var editingIndex by remember(goal.id, goal.mode) { mutableStateOf<Int?>(null) }
    var draftValue by remember(goal.id, goal.mode) { mutableStateOf("") }
    var valueHadFocus by remember(goal.id, goal.mode) { mutableStateOf(false) }

    LaunchedEffect(editingIndex) {
        if (editingIndex != null) {
            kotlinx.coroutines.delay(40)
            valueFocusRequester.requestFocus()
        }
    }

    fun commitValue(index: Int?) {
        if (index == null) return
        val label = values.getOrNull(index)?.first ?: return
        val formatted = formatWorkoutValue(label, draftValue)
        values = values.mapIndexed { itemIndex, item ->
            if (itemIndex == index) item.first to formatted else item
        }
        if (index == 0) onGoalChange(goal.copy(value = formatted))
        editingIndex = null
        valueHadFocus = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            .background(Color(0xFF2C0B59))
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            WorkoutModeBadge(
                label = "Tempo",
                selected = goal.mode == "tempo",
                onClick = {
                    commitValue(editingIndex)
                    onGoalChange(goal.copy(mode = "tempo", value = "20 min"))
                    editingIndex = null
                },
                modifier = Modifier.weight(1f)
            )
            WorkoutModeBadge(
                label = "Distancia",
                selected = goal.mode == "distancia",
                onClick = {
                    commitValue(editingIndex)
                    onGoalChange(goal.copy(mode = "distancia", value = "3 km"))
                    editingIndex = null
                },
                modifier = Modifier.weight(1f)
            )
        }
        values.forEachIndexed { index, (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 28.dp)
                    .clickable {
                        commitValue(editingIndex)
                        editingIndex = index
                        draftValue = rawWorkoutValue(value)
                        valueHadFocus = false
                    }
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, modifier = Modifier.weight(1f), color = Color.White.copy(alpha = .62f), fontSize = 16.sp)
                if (editingIndex == index) {
                    TextField(
                        value = draftValue,
                        onValueChange = { next ->
                            draftValue = next.filter { it.isDigit() || it == ',' || it == '.' }
                        },
                        modifier = Modifier
                            .width(96.dp)
                            .height(44.dp)
                            .focusRequester(valueFocusRequester)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    valueHadFocus = true
                                } else if (valueHadFocus) {
                                    commitValue(index)
                                }
                            },
                        singleLine = true,
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
                } else {
                    Text(value, color = Color.White, fontSize = 16.sp, maxLines = 1)
                }
            }
        }
    }
}

private fun sectionTotalLabel(goals: List<WorkoutGoal>): String {
    val tempoTotal = goals
        .filter { it.mode == "tempo" }
        .sumOf { rawWorkoutValue(it.value).replace(',', '.').toDoubleOrNull() ?: 0.0 }
    val distanceTotal = goals
        .filter { it.mode == "distancia" }
        .sumOf { rawWorkoutValue(it.value).replace(',', '.').toDoubleOrNull() ?: 0.0 }
    return listOfNotNull(
        if (tempoTotal > 0.0) "${formatSectionNumber(tempoTotal)} min" else null,
        if (distanceTotal > 0.0) "${formatSectionNumber(distanceTotal)} km" else null
    ).joinToString(" | ").ifBlank { "0 min" }
}

private fun formatSectionNumber(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else "%.1f".format(java.util.Locale.US, value)

private fun rawWorkoutValue(value: String): String =
    value.filter { it.isDigit() || it == ',' || it == '.' }

private fun formatWorkoutValue(label: String, rawValue: String): String {
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
private fun WorkoutCreatedStep(onBackToWorkouts: () -> Unit, onAddModality: () -> Unit, onDelete: (() -> Unit)?) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventFormScreen(
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
        item { FormTextField(eventDate, { eventDate = it }, "Data e hora") }
        item { FormTextField(location, { location = it }, "Local") }
        item { FormTextField(description, { description = it }, "Descricao") }
        item {
            SaveButton(enabled = name.isNotBlank() && eventDate.isNotBlank()) {
                onSave(Event(event?.id.orEmpty(), name.trim(), description.trim(), eventDate.trim(), location.trim()))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DirectoryFormScreen(
    title: String,
    entry: DirectoryEntry?,
    nameLabel: String,
    descriptionLabel: String,
    onBack: () -> Unit,
    onSave: (DirectoryEntry) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var name by remember(entry?.id) { mutableStateOf(entry?.name.orEmpty()) }
    var description by remember(entry?.id) { mutableStateOf(entry?.description.orEmpty()) }
    var status by remember(entry?.id) { mutableStateOf(entry?.status ?: "active") }

    SimpleFormScaffold(
        title = title,
        onBack = onBack,
        onDelete = if (entry != null && onDelete != null) ({ onDelete(entry.id) }) else null
    ) {
        item { FormTextField(name, { name = it }, nameLabel) }
        item { FormTextField(description, { description = it }, descriptionLabel) }
        item { StatusPicker(status) { status = it } }
        item {
            SaveButton(enabled = name.isNotBlank()) {
                onSave(DirectoryEntry(entry?.id.orEmpty(), name.trim(), status, description.trim()))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleFormScaffold(
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
private fun SaveButton(enabled: Boolean, onClick: () -> Unit) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormTextField(value: String, onValueChange: (String) -> Unit, label: String) {
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
private fun StatusPicker(status: String, onStatusChange: (String) -> Unit) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommunityPostFormScreen(
    workouts: List<Workout>,
    target: String,
    onBack: () -> Unit,
    onSave: (CommunityPost) -> Unit
) {
    var type by remember { mutableStateOf(CommunityPostType.POST) }
    var content by remember { mutableStateOf("") }
    var options by remember { mutableStateOf("") }
    var mediaLabel by remember { mutableStateOf<String?>(null) }
    var gifLabel by remember { mutableStateOf<String?>(null) }
    var generatedImagePrompt by remember { mutableStateOf("") }
    var showEmojiPanel by remember { mutableStateOf(false) }
    var scheduledAt by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contentWarning by remember { mutableStateOf("") }
    var activeComposerPanel by remember { mutableStateOf<String?>(null) }
    var selectedWorkoutId by remember { mutableStateOf(workouts.firstOrNull()?.id) }

    SimpleFormScaffold(title = if (target == "events") "Publicar em eventos" else "Publicar em grupos", onBack = onBack) {
        item {
            Row(verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = "Sua foto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(42.dp).clip(CircleShape)
                )
                Column(Modifier.padding(start = 12.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormTextField(content, { content = it }, "O que esta acontecendo?")
                    CommunityComposerToolbar(
                        onMedia = { activeComposerPanel = if (activeComposerPanel == "media") null else "media" },
                        onGif = { activeComposerPanel = if (activeComposerPanel == "gif") null else "gif" },
                        onGenerate = { activeComposerPanel = if (activeComposerPanel == "generate") null else "generate" },
                        onPoll = {
                            type = if (type == CommunityPostType.POLL) CommunityPostType.POST else CommunityPostType.POLL
                            activeComposerPanel = "poll"
                        },
                        onEmoji = {
                            showEmojiPanel = !showEmojiPanel
                            activeComposerPanel = null
                        },
                        onSchedule = { activeComposerPanel = if (activeComposerPanel == "schedule") null else "schedule" },
                        onLocation = { activeComposerPanel = if (activeComposerPanel == "location") null else "location" },
                        onDisclosure = { activeComposerPanel = if (activeComposerPanel == "warning") null else "warning" }
                    )
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PostTypeChip("Post", type == CommunityPostType.POST) { type = CommunityPostType.POST }
                PostTypeChip("Enquete", type == CommunityPostType.POLL) { type = CommunityPostType.POLL }
                PostTypeChip("Desafio", type == CommunityPostType.CHALLENGE) { type = CommunityPostType.CHALLENGE }
            }
        }
        if (mediaLabel != null || gifLabel != null || generatedImagePrompt.isNotBlank() || scheduledAt.isNotBlank() || location.isNotBlank() || contentWarning.isNotBlank()) {
            item {
                CommunityComposerPreview(
                    mediaLabel = mediaLabel,
                    gifLabel = gifLabel,
                    generatedImagePrompt = generatedImagePrompt,
                    scheduledAt = scheduledAt,
                    location = location,
                    contentWarning = contentWarning
                )
            }
        }
        activeComposerPanel?.let { panel ->
            item {
                CommunityComposerPanel(
                    panel = panel,
                    mediaLabel = mediaLabel,
                    gifLabel = gifLabel,
                    generatedImagePrompt = generatedImagePrompt,
                    scheduledAt = scheduledAt,
                    location = location,
                    contentWarning = contentWarning,
                    onMediaSelected = { mediaLabel = it },
                    onGifSelected = { gifLabel = it },
                    onPromptChange = { generatedImagePrompt = it },
                    onScheduleChange = { scheduledAt = it },
                    onLocationChange = { location = it },
                    onWarningChange = { contentWarning = it },
                    onClose = { activeComposerPanel = null }
                )
            }
        }
        if (showEmojiPanel) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("👏", "🔥", "🏃", "💚", "💪").forEach { emoji ->
                        Surface(
                            modifier = Modifier.size(42.dp).clickable { content += emoji },
                            color = PurpleSurface,
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
        if (type == CommunityPostType.POLL) {
            item { FormTextField(options, { options = it }, "Opcoes separadas por virgula") }
        }
        if (type == CommunityPostType.CHALLENGE) {
            items(workouts) { workout ->
                SlimFilterBadge(workout.name, selectedWorkoutId == workout.id, { selectedWorkoutId = workout.id }, Modifier.fillMaxWidth())
            }
        }
        item {
            SaveButton(enabled = content.isNotBlank()) {
                onSave(
                    CommunityPost(
                        id = "",
                        type = type,
                        content = content.trim(),
                        target = target,
                        authorName = "Voce",
                        linkedWorkoutId = if (type == CommunityPostType.CHALLENGE) selectedWorkoutId else null,
                        pollOptions = if (type == CommunityPostType.POLL) options.split(",").map { it.trim() }.filter { it.isNotBlank() } else emptyList(),
                        mediaLabel = mediaLabel,
                        gifLabel = gifLabel,
                        generatedImagePrompt = generatedImagePrompt.ifBlank { null },
                        scheduledAt = scheduledAt.ifBlank { null },
                        location = location.ifBlank { null },
                        contentWarning = contentWarning.ifBlank { null }
                    )
                )
            }
        }
    }
}

@Composable
private fun CommunityComposerToolbar(
    onMedia: () -> Unit,
    onGif: () -> Unit,
    onGenerate: () -> Unit,
    onPoll: () -> Unit,
    onEmoji: () -> Unit,
    onSchedule: () -> Unit,
    onLocation: () -> Unit,
    onDisclosure: () -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        item { ComposerToolButton(Icons.Outlined.Image, "Midia", onMedia) }
        item { ComposerToolButton(Icons.Outlined.GifBox, "GIF", onGif) }
        item { ComposerToolButton(Icons.Outlined.AutoAwesome, "Gerar imagem", onGenerate) }
        item { ComposerToolButton(Icons.Outlined.Poll, "Enquete", onPoll) }
        item { ComposerToolButton(Icons.Outlined.InsertEmoticon, "Emoji", onEmoji) }
        item { ComposerToolButton(Icons.Outlined.Schedule, "Agendar", onSchedule) }
        item { ComposerToolButton(Icons.Outlined.LocationOn, "Local", onLocation) }
        item { ComposerToolButton(Icons.Outlined.Flag, "Aviso", onDisclosure) }
    }
}

@Composable
private fun ComposerToolButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Icon(icon, contentDescription = contentDescription, tint = Lime, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun CommunityComposerPreview(
    mediaLabel: String?,
    gifLabel: String?,
    generatedImagePrompt: String,
    scheduledAt: String,
    location: String,
    contentWarning: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        mediaLabel?.let { ComposerPreviewChip(Icons.Outlined.Image, it) }
        gifLabel?.let { ComposerPreviewChip(Icons.Outlined.GifBox, it) }
        if (generatedImagePrompt.isNotBlank()) ComposerPreviewChip(Icons.Outlined.AutoAwesome, "Gerar: $generatedImagePrompt")
        if (scheduledAt.isNotBlank()) ComposerPreviewChip(Icons.Outlined.Schedule, "Agendado: $scheduledAt")
        if (location.isNotBlank()) ComposerPreviewChip(Icons.Outlined.LocationOn, location)
        if (contentWarning.isNotBlank()) ComposerPreviewChip(Icons.Outlined.Flag, contentWarning)
    }
}

@Composable
private fun ComposerPreviewChip(icon: ImageVector, label: String) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Lime, modifier = Modifier.size(18.dp))
            Text(label, Modifier.padding(start = 8.dp), color = Color.White, fontSize = 13.sp)
        }
    }
}

@Composable
private fun CommunityComposerPanel(
    panel: String,
    mediaLabel: String?,
    gifLabel: String?,
    generatedImagePrompt: String,
    scheduledAt: String,
    location: String,
    contentWarning: String,
    onMediaSelected: (String?) -> Unit,
    onGifSelected: (String?) -> Unit,
    onPromptChange: (String) -> Unit,
    onScheduleChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onWarningChange: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    when (panel) {
                        "media" -> "Midia"
                        "gif" -> "GIF"
                        "generate" -> "Gerar imagem"
                        "poll" -> "Enquete"
                        "schedule" -> "Agendar"
                        "location" -> "Localizacao"
                        else -> "Aviso"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onClose) { Text("Fechar", color = Lime) }
            }
            when (panel) {
                "media" -> ComposerChoiceGrid(
                    options = listOf("Foto do treino", "Video curto", "Resultado do treino", "Remover midia"),
                    selected = mediaLabel,
                    onSelect = { onMediaSelected(if (it == "Remover midia") null else it) }
                )
                "gif" -> ComposerChoiceGrid(
                    options = listOf("Aplausos", "Bora correr", "Fogo no pace", "Remover GIF"),
                    selected = gifLabel,
                    onSelect = { onGifSelected(if (it == "Remover GIF") null else it) }
                )
                "generate" -> FormTextField(generatedImagePrompt, onPromptChange, "Descreva a imagem")
                "poll" -> Text("Use o campo de opcoes abaixo para separar as alternativas por virgula.", color = Color.White.copy(alpha = .72f), fontSize = 13.sp)
                "schedule" -> FormTextField(scheduledAt, onScheduleChange, "Quando publicar?")
                "location" -> FormTextField(location, onLocationChange, "Onde foi?")
                "warning" -> FormTextField(contentWarning, onWarningChange, "Aviso de conteudo")
            }
        }
    }
}

@Composable
private fun ComposerChoiceGrid(options: List<String>, selected: String?, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { option ->
                    SlimFilterBadge(
                        modifier = Modifier.weight(1f),
                        label = option,
                        selected = selected == option,
                        onClick = { onSelect(option) }
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PostTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = { if (selected) Icon(Icons.Outlined.Check, null, modifier = Modifier.size(16.dp)) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) Lime else PurpleSurface,
            labelColor = if (selected) PurpleDeep else Color.White,
            leadingIconContentColor = PurpleDeep
        )
    )
}

@Composable
private fun LegacyWorkoutsScreen(
    workouts: List<Workout>,
    loading: Boolean,
    onBack: () -> Unit,
    onWorkoutClick: (Workout) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Todos") }
    val filtered = workouts.filter { workout ->
        workout.name.contains(query, true) && (filter == "Todos" || workoutStatusLabel(workout.status) == filter)
    }
    ListScaffold("Treinos", "Criar treino", onBack) {
        SearchField(query, { query = it }, "Buscar treino")
        FilterRow(listOf("Todos", "Ativos", "Em edição", "Inativos"), filter) { filter = it }
        Text("${filtered.size} treinos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        if (loading) LoadingBox()
        filtered.forEach { workout ->
            Card(Modifier.fillMaxWidth().clickable { onWorkoutClick(workout) }, colors = CardDefaults.cardColors(containerColor = PurpleSurface)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(PurpleDeep), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.DirectionsRun, null, tint = Lime)
                    }
                    Column(Modifier.padding(start = 12.dp).weight(1f)) {
                        Text(workout.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(workout.description.orEmpty(), maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                    StatusBadge(workoutStatusLabel(workout.status), if (workout.status == "active") Color(0xFF4CAF50) else Color(0xFFFFC107))
                }
            }
        }
    }
}

@Composable
private fun EarningsScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Ganhos", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Resumo financeiro da assessoria", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Lime), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("Receita prevista", color = LimeMuted)
                    Text("R$ 0,00", color = PurpleDeep, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    Text("Os valores serão calculados pelos planos ativos.", color = PurpleDeep.copy(alpha = .75f), fontSize = 13.sp)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FinancialCard("Em dia", "0", Color(0xFF4CAF50), Modifier.weight(1f))
                FinancialCard("A receber", "0", Color(0xFFFFC107), Modifier.weight(1f))
            }
        }
        item { Text("Movimentações", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) }
        item {
            Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
                Text("Nenhuma movimentação encontrada.", Modifier.fillMaxWidth().padding(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsScreen(
    events: List<Event>,
    loading: Boolean,
    onBack: () -> Unit,
    onNewEvent: () -> Unit,
    onEventClick: (Event) -> Unit
) {
    Scaffold(
        containerColor = PurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text("Eventos", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewEvent, containerColor = Lime, contentColor = PurpleDeep) {
                Icon(Icons.Outlined.Add, "Criar evento")
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Agenda de eventos", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            if (loading) item { LoadingBox() }
            if (events.isEmpty() && !loading) {
                item {
                    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
                        Text("Nenhum evento cadastrado.", Modifier.fillMaxWidth().padding(18.dp))
                    }
                }
            }
            items(events.sortedBy { it.eventDate }) { event ->
                Box(Modifier.clickable { onEventClick(event) }) { EventCard(event) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommunityScreen(
    posts: List<CommunityPost>,
    workouts: List<Workout>,
    loading: Boolean,
    onBack: () -> Unit,
    onCreatePost: (String) -> Unit,
    onPostClick: (CommunityPost) -> Unit,
    onLike: (String) -> Unit,
    onComment: (String) -> Unit,
    onShare: (String) -> Unit
) {
    val storyAuthors = remember(posts) {
        posts.map { it.authorName }.distinct().ifEmpty { listOf("Voce", "Marina", "Coach Ana") }
    }

    Box(Modifier.fillMaxSize().background(PurpleSurface)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 174.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PurpleBackground)
                        .padding(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Comunidade", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(end = 4.dp)
                    ) {
                        items(storyAuthors) { author ->
                            CommunityStoryBubble(author)
                        }
                    }
                }
            }
            if (posts.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().background(PurpleSurface).padding(18.dp)) {
                        Text("Nenhuma publicacao criada ainda.", color = Color.White.copy(alpha = .72f))
                    }
                    HorizontalDivider(color = NavigationPurple, thickness = 0.8.dp)
                }
            }
            items(posts) { post ->
                CommunityPostCard(
                    post = post,
                    workoutName = workouts.firstOrNull { it.id == post.linkedWorkoutId }?.name,
                    onClick = { onPostClick(post) },
                    onLike = { onLike(post.id) },
                    onComment = { onComment(post.id) },
                    onShare = { onShare(post.id) }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.48f to PurpleBackground.copy(alpha = .62f),
                        1f to PurpleDeep
                    )
                )
        )
        FloatingActionButton(
            onClick = { onCreatePost("groups") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 112.dp),
            containerColor = Lime,
            contentColor = PurpleDeep,
            shape = RoundedCornerShape(22.dp)
        ) {
            Icon(Icons.Outlined.Add, "Criar publicacao")
        }
    }
}

@Composable
private fun CommunityStoryBubble(authorName: String) {
    Column(
        modifier = Modifier.width(66.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Lime),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = "Story de $authorName",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(50.dp).clip(CircleShape)
            )
        }
        Text(
            authorName,
            modifier = Modifier.padding(top = 6.dp),
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CommunityPostCard(
    post: CommunityPost,
    workoutName: String?,
    onClick: () -> Unit,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(PurpleSurface)
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = "Foto de ${post.authorName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(38.dp).clip(CircleShape)
                )
                Column(Modifier.padding(start = 10.dp).weight(1f)) {
                    Text(post.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(if (post.target == "events") "Eventos" else "Grupos", color = Color.White.copy(alpha = .56f), fontSize = 12.sp)
                }
                if (post.type != CommunityPostType.POST) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (post.type == CommunityPostType.POLL) Icons.Outlined.Poll else Icons.Outlined.DirectionsRun,
                            null,
                            tint = Lime,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(postTypeLabel(post.type), Modifier.padding(start = 5.dp), color = Lime, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            post.contentWarning?.let {
                Surface(color = Color(0xFFFFC107).copy(alpha = .18f), shape = RoundedCornerShape(10.dp)) {
                    Text(it, Modifier.fillMaxWidth().padding(10.dp), color = Color(0xFFFFD166), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
            Text(post.content, color = Color.White.copy(alpha = .86f), lineHeight = 21.sp)
            CommunityPostAttachments(post)
            if (post.type == CommunityPostType.POLL && post.pollOptions.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    post.pollOptions.forEach { option ->
                        Surface(color = PurpleBackground, shape = RoundedCornerShape(10.dp)) {
                            Text(option, Modifier.fillMaxWidth().padding(12.dp), color = Color.White)
                        }
                    }
                }
            }
            if (post.type == CommunityPostType.CHALLENGE && workoutName != null) {
                Surface(color = Lime.copy(alpha = .18f), shape = RoundedCornerShape(10.dp)) {
                    Text("Treino: $workoutName", Modifier.fillMaxWidth().padding(12.dp), color = Lime, fontWeight = FontWeight.SemiBold)
                }
            }
            HorizontalDivider(color = Color.White.copy(alpha = .12f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                CommunityActionButton(if (post.liked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder, post.likes.toString(), if (post.liked) Color(0xFFFF6B8A) else Color.White, onLike)
                CommunityActionButton(Icons.Outlined.ChatBubbleOutline, post.comments.toString(), Color.White, onComment)
                CommunityActionButton(Icons.Outlined.Share, post.shares.toString(), Color.White, onShare)
            }
        }
        HorizontalDivider(color = NavigationPurple, thickness = 0.8.dp)
    }
}

@Composable
private fun CommunityPostAttachments(post: CommunityPost) {
    val hasAttachments = post.mediaLabel != null ||
        post.gifLabel != null ||
        post.generatedImagePrompt != null ||
        post.scheduledAt != null ||
        post.location != null
    if (!hasAttachments) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        post.mediaLabel?.let { CommunityAttachmentRow(Icons.Outlined.Image, it) }
        post.gifLabel?.let { CommunityAttachmentRow(Icons.Outlined.GifBox, it) }
        post.generatedImagePrompt?.let { CommunityAttachmentRow(Icons.Outlined.AutoAwesome, "Imagem gerada: $it") }
        post.scheduledAt?.let { CommunityAttachmentRow(Icons.Outlined.Schedule, "Agendado: $it") }
        post.location?.let { CommunityAttachmentRow(Icons.Outlined.LocationOn, it) }
    }
}

@Composable
private fun CommunityAttachmentRow(icon: ImageVector, label: String) {
    Surface(color = PurpleBackground, shape = RoundedCornerShape(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Lime, modifier = Modifier.size(18.dp))
            Text(label, Modifier.padding(start = 8.dp), color = Color.White.copy(alpha = .82f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun CommunityActionButton(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        Text(label, Modifier.padding(start = 6.dp), color = tint, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommunityPostDetailScreen(
    post: CommunityPost,
    workoutName: String?,
    onBack: () -> Unit,
    onLike: () -> Unit,
    onComment: (String) -> Unit,
    onReply: (String, String) -> Unit,
    onCommentLike: (String) -> Unit,
    onShare: () -> Unit
) {
    var commentText by remember(post.id) { mutableStateOf("") }
    var replyingTo by remember(post.id) { mutableStateOf<String?>(null) }
    val replyAuthor = post.commentThreads.findComment(replyingTo)?.authorName

    Scaffold(
        containerColor = PurpleSurface,
        topBar = {
            TopAppBar(
                title = { Text("Postagem") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.profile),
                            contentDescription = "Foto de ${post.authorName}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(44.dp).clip(CircleShape)
                        )
                        Column(Modifier.padding(start = 12.dp).weight(1f)) {
                            Text(post.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(if (post.target == "events") "Eventos" else "Grupos", color = Color.White.copy(alpha = .56f), fontSize = 13.sp)
                        }
                        if (post.type != CommunityPostType.POST) {
                            Text(postTypeLabel(post.type), color = Lime, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    post.contentWarning?.let {
                        Surface(color = Color(0xFFFFC107).copy(alpha = .18f), shape = RoundedCornerShape(10.dp)) {
                            Text(it, Modifier.fillMaxWidth().padding(10.dp), color = Color(0xFFFFD166), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                    Text(post.content, color = Color.White.copy(alpha = .9f), fontSize = 18.sp, lineHeight = 25.sp)
                    CommunityPostAttachments(post)
                    if (post.type == CommunityPostType.CHALLENGE && workoutName != null) {
                        Surface(color = Lime.copy(alpha = .18f), shape = RoundedCornerShape(10.dp)) {
                            Text("Treino: $workoutName", Modifier.fillMaxWidth().padding(12.dp), color = Lime, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Text("${post.likes} curtidas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        CommunityActionButton(if (post.liked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder, post.likes.toString(), if (post.liked) Color(0xFFFF6B8A) else Color.White, onLike)
                        CommunityActionButton(Icons.Outlined.ChatBubbleOutline, post.comments.toString(), Color.White) { replyingTo = null }
                        CommunityActionButton(Icons.Outlined.Share, post.shares.toString(), Color.White, onShare)
                    }
                }
                HorizontalDivider(color = NavigationPurple, thickness = 0.8.dp)
            }
            item {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (replyingTo != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Respondendo $replyAuthor", color = Lime, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            TextButton(onClick = { replyingTo = null }) { Text("Cancelar", color = Color.White) }
                        }
                    }
                    FormTextField(commentText, { commentText = it }, if (replyingTo == null) "Adicionar comentario" else "Responder comentario")
                    Button(
                        onClick = {
                            val target = replyingTo
                            if (target == null) {
                                onComment(commentText.trim())
                            } else {
                                onReply(target, commentText.trim())
                            }
                            commentText = ""
                            replyingTo = null
                        },
                        enabled = commentText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (replyingTo == null) "Comentar" else "Responder", fontWeight = FontWeight.Bold)
                    }
                }
                HorizontalDivider(color = NavigationPurple, thickness = 0.8.dp)
            }
            item {
                Text(
                    "Comentarios",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            items(post.commentThreads) { comment ->
                CommunityCommentRow(
                    comment = comment,
                    depth = 0,
                    onLike = onCommentLike,
                    onReply = { replyingTo = it }
                )
            }
            if (post.commentThreads.isEmpty()) {
                item {
                    Text(
                        "Nenhum comentario ainda.",
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        color = Color.White.copy(alpha = .62f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommunityCommentRow(
    comment: com.example.myapplication.data.CommunityComment,
    depth: Int,
    onLike: (String) -> Unit,
    onReply: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = (16 + depth * 28).dp, end = 16.dp, top = 10.dp, bottom = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = "Foto de ${comment.authorName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(34.dp).clip(CircleShape)
            )
            Box(Modifier.width(1.dp).height(34.dp).background(NavigationPurple))
        }
        Column(Modifier.padding(start = 10.dp).weight(1f)) {
            Text(comment.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(comment.content, color = Color.White.copy(alpha = .82f), fontSize = 14.sp, lineHeight = 20.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { onLike(comment.id) }, contentPadding = PaddingValues(0.dp)) {
                    Icon(
                        if (comment.liked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        null,
                        tint = if (comment.liked) Color(0xFFFF6B8A) else Color.White.copy(alpha = .72f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(comment.likes.toString(), Modifier.padding(start = 4.dp), color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
                }
                TextButton(onClick = { onReply(comment.id) }, contentPadding = PaddingValues(0.dp)) {
                    Text("Responder", color = Lime, fontSize = 12.sp)
                }
            }
        }
    }
        comment.replies.forEach { reply ->
            CommunityCommentRow(reply, depth + 1, onLike, onReply)
        }
    }
}

private fun List<com.example.myapplication.data.CommunityComment>.findComment(commentId: String?): com.example.myapplication.data.CommunityComment? {
    if (commentId == null) return null
    for (comment in this) {
        if (comment.id == commentId) return comment
        val nested = comment.replies.findComment(commentId)
        if (nested != null) return nested
    }
    return null
}

private fun postTypeLabel(type: CommunityPostType) = when (type) {
    CommunityPostType.POST -> ""
    CommunityPostType.POLL -> "Enquete"
    CommunityPostType.CHALLENGE -> "Desafio"
}

@Composable
private fun PlaceholderScreen(title: String, message: String, onBack: () -> Unit) {
    ListScaffold(title, "Novo", onBack) {
        Surface(color = PurpleBackground, shape = RoundedCornerShape(14.dp)) {
            Text(message, Modifier.fillMaxWidth().padding(18.dp))
        }
    }
}

@Composable
private fun FinancialCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = PurpleSurface)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, color = color, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AnnouncementsScreen(announcements: List<Announcement>, loading: Boolean, onBack: () -> Unit) {
    ListScaffold("Avisos", "Criar aviso", onBack) {
        Text("Comunicados recentes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        if (loading) LoadingBox()
        announcements.forEach { AnnouncementCard(it) }
    }
}

@Composable
private fun AnnouncementCard(announcement: Announcement) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PurpleSurface)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Campaign, null, tint = Lime)
                Text(if (announcement.targetType == "all") "Todos" else "Turma", Modifier.padding(start = 8.dp), color = Lime, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text(announcement.publishedAt.orEmpty(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(announcement.content, Modifier.padding(top = 14.dp), lineHeight = 21.sp)
            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = .12f))
            Text("👍  8     🏃  3", fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScaffold(
    title: String,
    actionLabel: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = PurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}, containerColor = Lime, contentColor = PurpleDeep) {
                Icon(Icons.Outlined.Add, actionLabel)
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(title: String, name: String, description: String, onBack: () -> Unit) {
    Scaffold(
        containerColor = PurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(20.dp)) {
            Box(Modifier.size(84.dp).clip(CircleShape).background(LimeMuted), contentAlignment = Alignment.Center) {
                Text(name.take(1), color = Lime, fontWeight = FontWeight.Bold, fontSize = 34.sp)
            }
            Text(name, Modifier.padding(top = 18.dp), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(description, Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Card(Modifier.fillMaxWidth().padding(top = 24.dp), colors = CardDefaults.cardColors(containerColor = PurpleSurface)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Resumo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("A estrutura desta página já está pronta para receber os detalhes do banco.", Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Outlined.Search, null) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = PurpleDeep,
            unfocusedContainerColor = PurpleDeep,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun FilterRow(filters: List<String>, selected: String, onSelected: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        filters.forEach { filter ->
            AssistChip(
                onClick = { onSelected(filter) },
                label = { Text(filter, fontSize = 12.sp) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (filter == selected) Lime else PurpleSurface,
                    labelColor = if (filter == selected) PurpleDeep else Color.White
                )
            )
        }
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Surface(color = color.copy(alpha = .2f), shape = RoundedCornerShape(50)) {
        Text(text, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Lime)
    }
}

private fun statusLabel(status: String) = when (status) {
    "active" -> "Em dia"
    "pending_payment" -> "A pagar"
    else -> "Inativos"
}

private fun statusColor(status: String) = when (status) {
    "active" -> Color(0xFF4CAF50)
    "pending_payment" -> Color(0xFFFFC107)
    else -> Color(0xFFFF6B6B)
}

private fun directoryStatusLabel(status: String) = when (status) {
    "active" -> "Ativos"
    "paused" -> "Pausados"
    else -> "Inativos"
}

private fun workoutStatusLabel(status: String) = when (status) {
    "active" -> "Ativos"
    "draft" -> "Em edição"
    else -> "Inativos"
}
