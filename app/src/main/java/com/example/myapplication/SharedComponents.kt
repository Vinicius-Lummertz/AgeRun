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
import com.example.myapplication.data.Challenge
import com.example.myapplication.data.Student
import com.example.myapplication.data.Workout
import com.example.myapplication.data.Event
import com.example.myapplication.ui.AgeGoUiState
import com.example.myapplication.ui.AgeGoViewModel
import com.example.myapplication.ui.theme.AgeGoTheme
import com.example.myapplication.ui.theme.Lime
import com.example.myapplication.ui.theme.NavigationPurple
import com.example.myapplication.ui.theme.PurpleBackground
import com.example.myapplication.ui.theme.PurpleDeep
import com.example.myapplication.ui.theme.PurpleSurface
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.roundToInt


@Composable
fun AnnouncementsScreen(announcements: List<Announcement>, loading: Boolean, onBack: () -> Unit, onCreate: () -> Unit = {}) {
    ListScaffold("Avisos", "Criar aviso", onBack, onAction = onCreate) {
        Text("Comunicados recentes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        if (loading) LoadingBox()
        if (announcements.isEmpty() && !loading) {
            Text("Nenhum aviso publicado ainda.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        announcements.forEach { AnnouncementCard(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementFormScreen(onBack: () -> Unit, onSave: (String, String) -> Unit) {
    var content by remember { mutableStateOf("") }
    var targetType by remember { mutableStateOf("all") }
    Scaffold(
        containerColor = PurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text("Novo aviso", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { targetType = "all" },
                    label = { Text("Todos") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (targetType == "all") Lime else PurpleSurface,
                        labelColor = if (targetType == "all") PurpleDeep else Color.White
                    )
                )
                AssistChip(
                    onClick = { targetType = "group" },
                    label = { Text("Turma") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (targetType == "group") Lime else PurpleSurface,
                        labelColor = if (targetType == "group") PurpleDeep else Color.White
                    )
                )
            }
            TextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp),
                placeholder = { Text("Escreva o aviso para seus alunos...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PurpleSurface,
                    unfocusedContainerColor = PurpleSurface
                )
            )
            Button(
                onClick = { if (content.isNotBlank()) onSave(content.trim(), targetType) },
                enabled = content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar aviso")
            }
        }
    }
}

fun challengeTargetLabel(challenge: Challenge): String = if (challenge.targetType == "distance") "Distância" else "Tempo"

@Composable
fun ChallengesScreen(
    challenges: List<Challenge>,
    loading: Boolean,
    onBack: () -> Unit,
    onCreate: () -> Unit = {},
    onChallengeClick: (Challenge) -> Unit = {}
) {
    var filter by remember { mutableStateOf("Todos") }
    DirectoryScreen(
        title = "Hub Fit - Desafios",
        searchPlaceholder = "Pesquisar desafio",
        filters = listOf("Todos", "Distância", "Tempo"),
        selectedFilter = filter,
        onFilterSelected = { filter = it },
        actions = listOf(DirectoryAction("Novo desafio", R.drawable.ic_workouts, onCreate)),
        items = challenges,
        loading = loading,
        itemId = { it.id },
        itemTitle = { it.name },
        itemStatus = { challengeTargetLabel(it) },
        itemMatchesQuery = { challenge, query -> challenge.name.contains(query, true) },
        onBack = onBack,
        onItemClick = onChallengeClick
    )
}

@Composable
fun ChallengeDetailScreen(
    challenge: Challenge?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit
) {
    Column(Modifier.fillMaxSize().background(PurpleBackground)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Text(
                challenge?.name ?: "Desafio",
                modifier = Modifier.padding(start = 4.dp).weight(1f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
            if (challenge != null) {
                TextButton(onClick = onEdit) { Text("Editar", color = Lime, fontWeight = FontWeight.Bold) }
            }
        }
        if (challenge == null) {
            Text("Desafio nao encontrado.", color = Color.White.copy(alpha = .6f), modifier = Modifier.padding(16.dp))
            return@Column
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(Modifier.fillMaxWidth(), color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        WorkoutDetailRow("Meta", if (challenge.targetType == "distance") "${challenge.targetValue} km" else "${challenge.targetValue} min")
                        WorkoutDetailRow("Tipo", challengeTargetLabel(challenge))
                        if (challenge.description.isNotBlank()) {
                            WorkoutDetailRow("Descrição", challenge.description)
                        }
                    }
                }
            }
            item {
                Surface(Modifier.fillMaxWidth(), color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Desempenho", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            "${challenge.completions} concluíram · ${"%.1f".format(challenge.totalDistanceMeters / 1000.0)} km no total",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = .7f)
                        )
                    }
                }
            }
            item {
                TextButton(
                    onClick = { onDelete(challenge.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Excluir desafio", color = Color(0xFFFFB3BE), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeFormScreen(
    challenge: Challenge? = null,
    onBack: () -> Unit,
    onSave: (String, String, String, String, Double) -> Unit
) {
    var name by remember(challenge?.id) { mutableStateOf(challenge?.name.orEmpty()) }
    var description by remember(challenge?.id) { mutableStateOf(challenge?.description.orEmpty()) }
    var targetType by remember(challenge?.id) { mutableStateOf(challenge?.targetType ?: "distance") }
    var targetValue by remember(challenge?.id) { mutableStateOf(challenge?.targetValue?.takeIf { it > 0 }?.toString().orEmpty()) }
    Scaffold(
        containerColor = PurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text(if (challenge == null) "Novo desafio" else "Editar desafio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nome do desafio") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PurpleSurface,
                    unfocusedContainerColor = PurpleSurface
                )
            )
            TextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Descrição (opcional)") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PurpleSurface,
                    unfocusedContainerColor = PurpleSurface
                )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { targetType = "distance" },
                    label = { Text("Distância") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (targetType == "distance") Lime else PurpleSurface,
                        labelColor = if (targetType == "distance") PurpleDeep else Color.White
                    )
                )
                AssistChip(
                    onClick = { targetType = "time" },
                    label = { Text("Tempo") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (targetType == "time") Lime else PurpleSurface,
                        labelColor = if (targetType == "time") PurpleDeep else Color.White
                    )
                )
            }
            TextField(
                value = targetValue,
                onValueChange = { targetValue = it.filter { ch -> ch.isDigit() || ch == '.' } },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (targetType == "distance") "Meta em km" else "Meta em minutos") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PurpleSurface,
                    unfocusedContainerColor = PurpleSurface
                )
            )
            Text(
                "Desafios são treinos de um único dia que o aluno pode fazer quando quiser.",
                color = Color.White.copy(alpha = .58f),
                fontSize = 12.sp
            )
            Button(
                onClick = {
                    val value = targetValue.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) onSave(challenge?.id.orEmpty(), name.trim(), description.trim(), targetType, value)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (challenge == null) "Criar desafio" else "Salvar desafio")
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: Announcement) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PurpleSurface)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Campaign, null, tint = Lime)
                Text(if (announcement.targetType == "all") "Todos" else "Turma", Modifier.padding(start = 8.dp), color = Lime, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text(announcement.publishedAt.orEmpty(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(announcement.content, Modifier.padding(top = 14.dp), lineHeight = 21.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScaffold(
    title: String,
    actionLabel: String,
    onBack: () -> Unit,
    onAction: () -> Unit = {},
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
            FloatingActionButton(onClick = onAction, containerColor = Lime, contentColor = PurpleDeep) {
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

@Composable
fun SearchField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
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
fun FilterRow(filters: List<String>, selected: String, onSelected: (String) -> Unit) {
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
fun StatusBadge(text: String, color: Color) {
    Surface(color = color.copy(alpha = .2f), shape = RoundedCornerShape(50)) {
        Text(text, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LoadingBox() {
    Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Lime)
    }
}

fun statusLabel(status: String) = when (status) {
    "active" -> "Em dia"
    "pending_payment" -> "A pagar"
    else -> "Inativos"
}

fun statusColor(status: String) = when (status) {
    "active" -> Color(0xFF4CAF50)
    "pending_payment" -> Color(0xFFFFC107)
    else -> Color(0xFFFF6B6B)
}

fun directoryStatusLabel(status: String) = when (status) {
    "active" -> "Ativos"
    "paused" -> "Pausados"
    else -> "Inativos"
}

fun workoutStatusLabel(status: String) = when (status) {
    "active" -> "Ativos"
    "draft" -> "Em edição"
    else -> "Inativos"
}
