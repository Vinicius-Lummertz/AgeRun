package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

private val destinations = listOf(
    Destination("hub_fit", "Hub Fit", R.drawable.ic_nav_hub_fit, R.drawable.ic_nav_hub_fit_active),
    Destination("financeiro", "Financeiro", R.drawable.ic_nav_financeiro, R.drawable.ic_nav_financeiro_active),
    Destination("comunidade", "Comunidade", R.drawable.ic_nav_comunidade, R.drawable.ic_nav_comunidade_active),
    Destination("eventos", "Eventos", R.drawable.ic_nav_eventos, R.drawable.ic_nav_eventos_active)
)

@Composable
private fun AgeGoApp(viewModel: AgeGoViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = destinations.any { it.route == currentRoute }

    Scaffold(containerColor = PurpleBackground) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding( top = 16.dp)
        ) {
            NavHost(
                navController = navController,
                startDestination = "hub_fit",
                modifier = Modifier.fillMaxSize()
            ) {
            composable("hub_fit") { HomeScreenV2(state) { navController.navigate(it) } }
            composable("financeiro") { EarningsScreen() }
            composable("comunidade") { AnnouncementsScreen(state.announcements, state.isLoading, navController::popBackStack) }
            composable("eventos") { EventsScreen(state.events, state.isLoading, navController::popBackStack) }
            composable("modalities") { PlaceholderScreen("Modalidades", "A estrutura desta area sera definida depois.", navController::popBackStack) }
            composable("groups") { PlaceholderScreen("Grupos", "A estrutura desta area sera definida depois.", navController::popBackStack) }
            composable("students") {
                StudentsScreen(
                    students = state.students,
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onGroupsClick = { navController.navigate("groups") },
                    onStudentClick = { navController.navigate("student/${it.id}") }
                )
            }
            composable("student/{studentId}") { entry ->
                val student = state.students.firstOrNull { it.id == entry.arguments?.getString("studentId") }
                DetailScreen("Perfil do aluno", student?.name ?: "Aluno", student?.email.orEmpty(), navController::popBackStack)
            }
            composable("workouts") {
                WorkoutsScreen(
                    workouts = state.workouts,
                    loading = state.isLoading,
                    onBack = navController::popBackStack,
                    onWorkoutClick = { navController.navigate("workout/${it.id}") }
                )
            }
            composable("workout/{workoutId}") { entry ->
                val workout = state.workouts.firstOrNull { it.id == entry.arguments?.getString("workoutId") }
                DetailScreen("Detalhe do treino", workout?.name ?: "Treino", workout?.description.orEmpty(), navController::popBackStack)
            }
            composable("announcements") {
                AnnouncementsScreen(state.announcements, state.isLoading, navController::popBackStack)
            }
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
                    TrainingNowBar()
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
                        ShortcutCircleSvg("Modalidades", R.drawable.ic_option_modalidades) { navigate("modalities") }
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
private fun ShortcutCircleSvg(label: String, iconRes: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(PurpleBackground)
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
    val barHeight by animateDpAsState(
        targetValue = if (barInteracting) 74.dp else 68.dp,
        animationSpec = tween(durationMillis = 140),
        label = "bottom-bar-height"
    )

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
                .pointerInput(destinations) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        barInteracting = true
                        var lastIndex = selectedIndex

                        fun navigateByX(x: Float) {
                            val widthPerItem = size.width / destinations.size.toFloat()
                            val index = (x / widthPerItem)
                                .toInt()
                                .coerceIn(0, destinations.lastIndex)
                            if (index != lastIndex) {
                                lastIndex = index
                                onNavigate(destinations[index].route)
                            }
                        }

                        navigateByX(down.position.x)
                        do {
                            val event = awaitPointerEvent()
                            event.changes.firstOrNull()?.let { change ->
                                if (change.pressed) {
                                    navigateByX(change.position.x)
                                }
                            }
                        } while (event.changes.any { it.pressed })

                        barInteracting = false
                    }
                }
        ) {
            val itemWidth = maxWidth / destinations.size
            val itemInset = 4.dp
            val indicatorWidth = itemWidth - (itemInset * 2)
            val indicatorHeight = barHeight - (itemInset * 2)
            val indicatorOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex + itemInset,
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
                    val selected = index == selectedIndex
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

@Composable
private fun StudentsScreen(
    students: List<Student>,
    loading: Boolean,
    onBack: () -> Unit,
    onGroupsClick: () -> Unit,
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
                .padding(start = 14.dp, top = 22.dp, end = 14.dp, bottom = 28.dp),
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
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            StudentActionCircle("Novo aluno", "X") { }
            ShortcutCircleSvg("Grupos", R.drawable.ic_option_grupos, onGroupsClick)
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
                        top = 18.dp,
                        bottom = if (searchMode) 112.dp else 150.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (!searchMode) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
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
                        items(filtered) { student ->
                            StudentListRow(student = student, onClick = { onStudentClick(student) })
                        }
                    } else {
                        items(filtered) { student ->
                            StudentListRow(student = student, onClick = { onStudentClick(student) })
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
                            start = 12.dp,
                            end = 12.dp,
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
                .background(PurpleBackground)
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
private fun StudentSearchButton(value: String, onClick: () -> Unit) {
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
                if (value.isBlank()) "Pesquisar aluno" else value,
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
        placeholder = { Text("Pesquisar aluno", color = PurpleBackground.copy(alpha = .55f)) },
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PurpleBackground)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                student.name,
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        HorizontalDivider(color = PurpleSurface, thickness = 1.dp)
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

@Composable
private fun EventsScreen(events: List<Event>, loading: Boolean, onBack: () -> Unit) {
    ListScaffold("Eventos", "Criar evento", onBack) {
        Text("Agenda de eventos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        if (loading) LoadingBox()
        if (events.isEmpty() && !loading) {
            Surface(color = PurpleBackground, shape = RoundedCornerShape(14.dp)) {
                Text("Nenhum evento cadastrado.", Modifier.fillMaxWidth().padding(18.dp))
            }
        }
        events.sortedBy { it.eventDate }.forEach { EventCard(it) }
    }
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

private fun workoutStatusLabel(status: String) = when (status) {
    "active" -> "Ativos"
    "draft" -> "Em edição"
    else -> "Inativos"
}
