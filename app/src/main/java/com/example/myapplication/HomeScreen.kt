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
import androidx.compose.material.icons.outlined.Close
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
import com.example.myapplication.data.TrainingNowUser
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
import coil.compose.AsyncImage
import kotlin.math.roundToInt


@Composable
fun HomeScreenV2(state: AgeGoUiState, navigate: (String) -> Unit) {
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
                ProfileAvatar(
                    avatarUrl = state.authSession?.avatarUrl.orEmpty(),
                    contentDescription = "Perfil",
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { navigate("settings") }
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
            color = if (selectedDay != null) PurpleDeep else PurpleSurface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (selectedDay != null) {
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Eventos do dia",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    days.first { it.key == selectedDay }.label,
                                    color = Lime,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            IconButton(onClick = { selectedDay = null }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Fechar eventos", tint = Color.White)
                            }
                        }
                    }
                    if (selectedEvents.isEmpty()) {
                        item {
                            Surface(color = PurpleBackground, shape = RoundedCornerShape(18.dp)) {
                                Text(
                                    "Nenhum evento programado para este dia.",
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    color = Color.White.copy(alpha = .72f)
                                )
                            }
                        }
                    } else {
                        items(selectedEvents) { EventCard(it, containerColor = PurpleBackground) }
                    }
                } else {
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
                }
                if (state.isLoading) item { LoadingBox() }
            }
        }
    }
}

@Composable
fun HomeScreen(state: AgeGoUiState, navigate: (String) -> Unit) {
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
fun DayEventCard(
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
fun ShortcutCircle(label: String, icon: ImageVector, onClick: () -> Unit) {
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
fun ShortcutCircleSvg(
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
fun TrainingNowBar(users: List<TrainingNowUser>) {
    if (users.isEmpty()) return
    val visibleUsers = users.take(3)
    val remaining = (users.size - visibleUsers.size).coerceAtLeast(0)
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

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Lime)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                visibleUsers.forEachIndexed { index, user ->
                    if (index > 0) Spacer(Modifier.width(2.dp))
                    TrainingAvatar(user)
                }
            }

            Spacer(Modifier.width(6.dp))

            if (remaining > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Lime)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+$remaining", color = PurpleDeep, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Abrir", tint = Color.White)
        }
    }
}

@Composable
fun TrainingAvatar(user: TrainingNowUser) {
    ProfileAvatar(
        avatarUrl = user.avatarUrl,
        contentDescription = "Foto de ${user.name}",
        modifier = Modifier.size(28.dp)
    )
}

@Composable
fun ProfileAvatar(
    avatarUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    if (avatarUrl.isNotBlank()) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(CircleShape)
        )
    } else {
        Image(
            painter = painterResource(R.drawable.profile),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(CircleShape)
        )
    }
}

@Composable
fun PillBottomBar(
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
fun EventCard(event: Event, containerColor: Color = PurpleSurface) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = containerColor)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(PurpleDeep), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.DirectionsRun, null, tint = Lime)
            }
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(event.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    listOfNotNull(eventTime(event.eventDate), event.location).joinToString(" · "),
                    color = Color.White.copy(alpha = .64f),
                    fontSize = 13.sp
                )
            }
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(18.dp), tint = Color.White.copy(alpha = .72f))
        }
    }
}

data class DayOption(val key: String, val label: String, val isToday: Boolean = false)

fun daysAroundToday(previous: Int, next: Int): List<DayOption> {
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

fun nextDays(amount: Int): List<DayOption> {
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

fun eventTime(value: String): String = runCatching {
    val time = value.substringAfter('T').take(5)
    "${time}h"
}.getOrDefault("")

fun defaultEventDate(): String {
    val calendar = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 7)
        set(java.util.Calendar.MINUTE, 0)
    }
    return java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.US).format(calendar.time)
}

