package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
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


@Composable
fun CommunityPostFormScreen(
    workouts: List<Workout>,
    target: String,
    onBack: () -> Unit,
    onSave: (CommunityPost) -> Unit,
    onUploadMedia: (suspend (Uri) -> String)? = null
) {
    var type by remember { mutableStateOf(CommunityPostType.POST) }
    var content by remember { mutableStateOf("") }
    val pollOptions = remember { mutableStateListOf("", "") }
    var workoutQuery by remember { mutableStateOf("") }
    var mediaLabel by remember { mutableStateOf<String?>(null) }
    var gifLabel by remember { mutableStateOf<String?>(null) }
    var generatedImagePrompt by remember { mutableStateOf("") }
    var showEmojiPanel by remember { mutableStateOf(false) }
    var scheduledAt by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contentWarning by remember { mutableStateOf("") }
    var activeComposerPanel by remember { mutableStateOf<String?>(null) }
    var selectedWorkoutId by remember { mutableStateOf(workouts.firstOrNull()?.id) }
    var isUploadingMedia by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val filteredWorkouts = workouts.filter { workout ->
        workout.name.contains(workoutQuery, ignoreCase = true) ||
            workout.description.orEmpty().contains(workoutQuery, ignoreCase = true)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && onUploadMedia != null) {
            isUploadingMedia = true
            activeComposerPanel = null
            scope.launch {
                try {
                    mediaLabel = onUploadMedia(uri)
                } catch (e: Exception) {
                    android.util.Log.e("AgeGoUpload", "upload falhou: ${e.message}", e)
                    mediaLabel = null
                } finally {
                    isUploadingMedia = false
                }
            }
        } else if (uri != null) {
            mediaLabel = uri.toString()
            activeComposerPanel = null
        }
    }

    SimpleFormScaffold(title = if (target == "events") "Publicar em eventos" else "Publicar em grupos", onBack = onBack) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = content,
                    onValueChange = { content = it.take(300) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                    placeholder = { Text("O que esta acontecendo?") },
                    shape = RoundedCornerShape(18.dp),
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
                Text("${content.length}/300", color = Color.White.copy(alpha = .55f), fontSize = 12.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                CommunityComposerToolbar(
                    selectedType = type,
                    onMedia = { galleryLauncher.launch("image/*") },
                    onPoll = { type = if (type == CommunityPostType.POLL) CommunityPostType.POST else CommunityPostType.POLL },
                    onChallenge = { type = if (type == CommunityPostType.CHALLENGE) CommunityPostType.POST else CommunityPostType.CHALLENGE }
                )
            }
        }
        if (isUploadingMedia) {
            item {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Lime, strokeWidth = 2.dp)
                    Text(" Enviando imagem...", color = Color.White.copy(alpha = .7f), fontSize = 13.sp, modifier = Modifier.padding(start = 8.dp))
                }
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
                    contentWarning = contentWarning,
                    onRemoveMedia = { mediaLabel = null }
                )
            }
        }
        if (type == CommunityPostType.POLL) {
            items(pollOptions.size) { index ->
                FormTextField(pollOptions[index], { pollOptions[index] = it }, "Opcao ${index + 1}")
            }
            item {
                TextButton(onClick = { pollOptions.add("") }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = Lime)
                    Text("Adicionar opcao", color = Lime, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        if (type == CommunityPostType.CHALLENGE) {
            item {
                StudentSearchBar(
                    value = workoutQuery,
                    onValueChange = { workoutQuery = it },
                    placeholder = "Pesquisar treino",
                    modifier = Modifier.shadow(14.dp, RoundedCornerShape(50)).imePadding()
                )
            }
            items(filteredWorkouts) { workout ->
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
                        pollOptions = if (type == CommunityPostType.POLL) pollOptions.map { it.trim() }.filter { it.isNotBlank() } else emptyList(),
                        mediaLabel = mediaLabel,
                        gifLabel = null,
                        generatedImagePrompt = null,
                        scheduledAt = null,
                        location = null,
                        contentWarning = null
                    )
                )
            }
        }
    }
}

@Composable
fun CommunityComposerToolbar(
    selectedType: CommunityPostType,
    onMedia: () -> Unit,
    onPoll: () -> Unit,
    onChallenge: () -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        item { ComposerToolButton(Icons.Outlined.Image, "Midia", onMedia) }
        item { ComposerToolButton(Icons.Outlined.Poll, if (selectedType == CommunityPostType.POLL) "Remover enquete" else "Enquete", onPoll) }
        item { ComposerToolButton(Icons.Outlined.FitnessCenter, if (selectedType == CommunityPostType.CHALLENGE) "Remover desafio" else "Desafio", onChallenge) }
    }
}

@Composable
fun ComposerToolButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Icon(icon, contentDescription = contentDescription, tint = Lime, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun CommunityComposerPreview(
    mediaLabel: String?,
    gifLabel: String?,
    generatedImagePrompt: String,
    scheduledAt: String,
    location: String,
    contentWarning: String,
    onRemoveMedia: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        mediaLabel?.let {
            if (isGalleryMediaUri(it)) {
                ComposerImagePreview(uri = it, onRemove = onRemoveMedia)
            } else {
                ComposerPreviewChip(Icons.Outlined.Image, it)
            }
        }
        gifLabel?.let { ComposerPreviewChip(Icons.Outlined.GifBox, it) }
        if (generatedImagePrompt.isNotBlank()) ComposerPreviewChip(Icons.Outlined.AutoAwesome, "Gerar: $generatedImagePrompt")
        if (scheduledAt.isNotBlank()) ComposerPreviewChip(Icons.Outlined.Schedule, "Agendado: $scheduledAt")
        if (location.isNotBlank()) ComposerPreviewChip(Icons.Outlined.LocationOn, location)
        if (contentWarning.isNotBlank()) ComposerPreviewChip(Icons.Outlined.Flag, contentWarning)
    }
}

@Composable
fun ComposerImagePreview(uri: String, onRemove: () -> Unit) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GalleryImage(
                uri = uri,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp, max = 360.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Image, null, tint = Lime, modifier = Modifier.size(18.dp))
                Text("Foto selecionada da galeria", Modifier.padding(start = 8.dp).weight(1f), color = Color.White.copy(alpha = .82f), fontSize = 13.sp)
                TextButton(onClick = onRemove, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text("Remover", color = Lime, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ComposerPreviewChip(icon: ImageVector, label: String) {
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
fun CommunityComposerPanel(
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
fun ComposerChoiceGrid(options: List<String>, selected: String?, onSelect: (String) -> Unit) {
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
fun GalleryImage(uri: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = Uri.parse(uri),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.background(PurpleBackground)
    )
}

fun isGalleryMediaUri(value: String): Boolean =
    value.startsWith("content://") || value.startsWith("file://") ||
    value.startsWith("http://") || value.startsWith("https://")

@Composable
fun PostTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
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

