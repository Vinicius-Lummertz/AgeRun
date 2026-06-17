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
import androidx.compose.ui.draw.drawBehind
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
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

    Box(Modifier.fillMaxSize().background(PurpleBackground)) {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PurpleBackground)
                    .padding(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 18.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(end = 4.dp)
                ) {
                    items(storyAuthors) { author ->
                        CommunityStoryBubble(author)
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
                    contentPadding = PaddingValues(top = 14.dp, bottom = 174.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    if (posts.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(18.dp)) {
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
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(Icons.Outlined.Add, "Criar publicacao")
        }
    }
}

@Composable
fun CommunityStoryBubble(authorName: String) {
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
fun CommunityPostCard(
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
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = Color.White.copy(alpha = .12f),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 0.8.dp.toPx()
                        )
                    }
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                    CommunityActionButton(if (post.liked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder, post.likes.toString(), if (post.liked) Color(0xFFFF6B8A) else Color.White, onLike)
                    CommunityActionButton(Icons.Outlined.ChatBubbleOutline, post.comments.toString(), Color.White, onComment)
                }
                CommunityActionButton(Icons.Outlined.Share, post.shares.toString(), Color.White, onShare)
            }
        }
        HorizontalDivider(color = NavigationPurple, thickness = 0.8.dp)
    }
}

@Composable
fun CommunityPostAttachments(post: CommunityPost) {
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
fun CommunityAttachmentRow(icon: ImageVector, label: String) {
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
fun CommunityActionButton(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
        modifier = Modifier.defaultMinSize(minWidth = 0.dp, minHeight = 32.dp)
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        Text(label, Modifier.padding(start = 6.dp), color = tint, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostDetailScreen(
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
fun CommunityCommentRow(
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

fun List<com.example.myapplication.data.CommunityComment>.findComment(commentId: String?): com.example.myapplication.data.CommunityComment? {
    if (commentId == null) return null
    for (comment in this) {
        if (comment.id == commentId) return comment
        val nested = comment.replies.findComment(commentId)
        if (nested != null) return nested
    }
    return null
}

fun postTypeLabel(type: CommunityPostType) = when (type) {
    CommunityPostType.POST -> ""
    CommunityPostType.POLL -> "Enquete"
    CommunityPostType.CHALLENGE -> "Desafio"
}

