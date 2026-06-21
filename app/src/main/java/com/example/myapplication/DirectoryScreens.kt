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
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Remove
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


@Composable
fun <T> DirectoryScreen(
    title: String,
    searchPlaceholder: String,
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    actions: List<DirectoryAction>,
    items: List<T>,
    loading: Boolean,
    itemId: (T) -> Any,
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
                                    StudentSearchButton(
                                        value = query,
                                        placeholder = searchPlaceholder,
                                        onClick = { searchMode = true }
                                    )
                                    SegmentedFilterBar(
                                        options = filters,
                                        selected = selectedFilter,
                                        onSelected = onFilterSelected
                                    )
                                }
                            }
                        }
                    }
                    if (!loading) {
                        items(filtered, key = { itemId(it) }) { item ->
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
fun StudentsScreen(
    students: List<Student>,
    loading: Boolean,
    onBack: () -> Unit,
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
        val matchesFilter = when (filter) {
            "Todos" -> true
            "Ciclo completo", "Evoluindo" -> student.status == "active"
            else -> statusLabel(student.status) == filter
        }
        student.name.contains(query, true) && matchesFilter
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
                                    StudentSearchButton(
                                        value = query,
                                        onClick = { searchMode = true }
                                    )
                                    SegmentedFilterBar(
                                        options = listOf("Todos", "Em dia", "A pagar", "Ciclo completo", "Evoluindo"),
                                        selected = filter,
                                        onSelected = { filter = it }
                                    )
                                }
                            }
                        }
                        if (!loading) {
                            items(filtered, key = { it.id }) { student ->
                                StudentListRow(student = student, onClick = { onStudentClick(student) })
                            }
                        }
                    } else {
                        if (!loading) {
                            items(filtered, key = { it.id }) { student ->
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
fun StudentActionCircle(label: String, symbol: String, onClick: () -> Unit) {
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
fun SlimFilterBadge(
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

@Composable
fun SegmentedFilterBar(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(end = 24.dp)) {
        items(options, key = { it }) { option ->
            SlimFilterBadge(option, selected == option, { onSelected(option) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSearchButton(value: String, onClick: () -> Unit, placeholder: String = "Pesquisar aluno") {
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
fun StudentSearchBar(
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
fun StudentListRow(student: Student, onClick: () -> Unit) {
    val workoutProgress = if (student.workoutsTotal > 0) {
        student.workoutsCompleted.toFloat() / student.workoutsTotal.toFloat()
    } else {
        0f
    }.coerceIn(0f, 1f)
    val performanceDelta = student.performanceDeltaPercent
    val performanceIcon = when {
        performanceDelta == null || performanceDelta == 0 -> Icons.Outlined.Remove
        performanceDelta > 0 -> Icons.Outlined.ArrowUpward
        else -> Icons.Outlined.ArrowDownward
    }
    val performanceColor = when {
        performanceDelta == null || performanceDelta == 0 -> Color.White.copy(alpha = .5f)
        performanceDelta > 0 -> Lime
        else -> Color(0xFFFFB3BE)
    }
    val performanceDescription = when {
        performanceDelta == null -> "Sem treino concluído para comparar"
        performanceDelta > 0 -> "$performanceDelta% acima da carga prevista"
        performanceDelta < 0 -> "${-performanceDelta}% abaixo da carga prevista"
        else -> "Carga realizada conforme o previsto"
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                avatarUrl = student.avatarUrl,
                contentDescription = "Foto de ${student.name}",
                modifier = Modifier.size(42.dp)
            )
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    student.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    student.phone.ifBlank { "Telefone nao informado" },
                    color = Color.White.copy(alpha = .58f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier.size(30.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { workoutProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = Lime,
                    trackColor = Color.White.copy(alpha = .18f),
                    strokeWidth = 3.dp
                )
                Text(
                    text = if (student.workoutsTotal > 0) {
                        "${student.workoutsCompleted}/${student.workoutsTotal}"
                    } else {
                        "—"
                    },
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                performanceIcon,
                contentDescription = performanceDescription,
                tint = performanceColor,
                modifier = Modifier.size(18.dp)
            )
        }
        HorizontalDivider(color = NavigationPurple, thickness = 0.8.dp)
    }
}

@Composable
fun DirectoryListRow(title: String, onClick: () -> Unit) {
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
fun DirectorySkeletonRow(index: Int) {
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
fun StudentCard(student: Student, onClick: () -> Unit) {
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

