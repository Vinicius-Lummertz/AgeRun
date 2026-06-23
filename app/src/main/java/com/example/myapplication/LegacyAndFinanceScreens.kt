package com.example.myapplication

import android.os.Bundle
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
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
fun LegacyWorkoutsScreen(
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
fun EarningsScreen(
    students: List<Student>,
    routines: List<DirectoryEntry>,
    onReviewPayment: (Student) -> Unit = {}
) {
    val billableStudents = students.filter { it.status != "inactive" }
    val paidStudents = billableStudents.filter { it.paymentStatus == "paid" }
    val pendingStudents = billableStudents.filter { it.paymentStatus != "paid" }
    val proofsToReview = billableStudents.filter { !it.paymentProofUrl.isNullOrBlank() }
    val overdueStudents = pendingStudents.filter { it.daysOverdue > 4 }
    val targetTotal = billableStudents.sumOf { parseMoneyValue(studentRoutineMonthlyFee(it, routines)) }
    val receivedTotal = paidStudents.sumOf { parseMoneyValue(studentRoutineMonthlyFee(it, routines)) }
    val progress = if (targetTotal > 0) (receivedTotal / targetTotal).toFloat().coerceIn(0f, 1f) else 0f
    var filter by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    val filterOptions = listOf(
        "A aprovar (${proofsToReview.size})",
        "Atrasados (${overdueStudents.size})",
        "Em dia (${paidStudents.size})",
        "A receber (${pendingStudents.size})"
    )
    val visibleStudents = billableStudents
        .filter { student ->
            student.name.contains(query.trim(), ignoreCase = true) && when (filter) {
                "approve" -> !student.paymentProofUrl.isNullOrBlank()
                "overdue" -> student.paymentStatus != "paid" && student.daysOverdue > 4
                "paid" -> student.paymentStatus == "paid"
                "receivable" -> student.paymentStatus != "paid"
                else -> true
            }
        }
        .sortedWith(
            compareByDescending<Student> { !it.paymentProofUrl.isNullOrBlank() }
                .thenByDescending { it.daysOverdue }
                .thenBy { it.name.lowercase() }
        )

    Column(Modifier.fillMaxSize().background(PurpleBackground)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 22.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = Lime), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Receita prevista", color = LimeMuted)
                    Text(
                        "${formatCurrency(receivedTotal)} / ${formatCurrency(targetTotal)}",
                        color = PurpleDeep,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)),
                        color = PurpleDeep,
                        trackColor = PurpleDeep.copy(alpha = .18f)
                    )
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f),
            color = PurpleSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().imePadding(),
                contentPadding = PaddingValues(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 150.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    StudentSearchBar(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = "Pesquisar aluno"
                    )
                }
                item {
                    SegmentedFilterBar(
                        options = filterOptions,
                        selected = when (filter) {
                            "approve" -> filterOptions[0]
                            "overdue" -> filterOptions[1]
                            "paid" -> filterOptions[2]
                            "receivable" -> filterOptions[3]
                            else -> ""
                        },
                        onSelected = { selected ->
                            val selectedKey = when (selected) {
                                filterOptions[0] -> "approve"
                                filterOptions[1] -> "overdue"
                                filterOptions[2] -> "paid"
                                else -> "receivable"
                            }
                            filter = if (filter == selectedKey) null else selectedKey
                        }
                    )
                }
                item {
                    Text(
                        if (query.isBlank()) "Cobranças" else "Resultados para \"${query.trim()}\"",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (visibleStudents.isEmpty()) {
                    item {
                        Surface(color = PurpleBackground, shape = RoundedCornerShape(14.dp)) {
                            Text("Nenhuma cobrança encontrada.", Modifier.fillMaxWidth().padding(18.dp), color = Color.White.copy(alpha = .7f))
                        }
                    }
                } else {
                    items(visibleStudents, key = { it.id }) { student ->
                        BillingMovementRow(
                            student = student,
                            monthlyFee = studentRoutineMonthlyFee(student, routines),
                            onReviewClick = { onReviewPayment(student) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BillingMovementRow(student: Student, monthlyFee: String, onReviewClick: () -> Unit) {
    val pending = student.paymentStatus != "paid"
    val hasProof = !student.paymentProofUrl.isNullOrBlank()
    Surface(
        color = PurpleBackground,
        shape = RoundedCornerShape(14.dp),
        modifier = if (pending) Modifier.clickable(onClick = onReviewClick) else Modifier
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape).background(if (pending) Color(0xFFFFC107) else PurpleDeep),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Payments, contentDescription = null, tint = if (pending) PurpleDeep else Lime)
                }
                Column(Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(student.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Vence dia ${student.billingDay}", color = Color.White.copy(alpha = .62f), fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(formatMoneyLabel(monthlyFee), color = Color.White, fontWeight = FontWeight.Bold)
                    if (pending && student.daysOverdue > 0) {
                        Text("${student.daysOverdue}d de atraso", color = Color(0xFFFF6B6B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    } else if (pending) {
                        Text("Aguardando pagamento", color = Color(0xFFFFD166), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            if (hasProof) {
                HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color.White.copy(alpha = .12f))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Comprovante enviado pelo aluno", color = Lime, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    AssistChip(
                        onClick = onReviewClick,
                        label = { Text("Validar", fontWeight = FontWeight.Bold) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Lime, labelColor = PurpleDeep)
                    )
                }
                if (!student.paymentProofRejectionReason.isNullOrBlank()) {
                    Text(
                        "Recusado anteriormente: ${student.paymentProofRejectionReason}",
                        color = Color(0xFFFF6B6B),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            } else if (pending) {
                HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color.White.copy(alpha = .12f))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Sem comprovante anexado", color = Color.White.copy(alpha = .55f), fontSize = 12.sp, modifier = Modifier.weight(1f))
                    AssistChip(
                        onClick = onReviewClick,
                        label = { Text("Confirmar pagamento", fontWeight = FontWeight.Bold) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Lime, labelColor = PurpleDeep)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentReviewScreen(
    student: Student,
    onBack: () -> Unit,
    onApprove: () -> Unit,
    onReject: (String) -> Unit
) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isPdf by remember { mutableStateOf(false) }
    var pageBitmaps by remember { mutableStateOf<List<android.graphics.Bitmap>>(emptyList()) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var showDeclineDialog by remember { mutableStateOf(false) }
    var declineReason by remember { mutableStateOf("") }

    val hasProof = !student.paymentProofUrl.isNullOrBlank()
    LaunchedEffect(student.paymentProofUrl) {
        val url = student.paymentProofUrl
        if (url.isNullOrBlank()) {
            loading = false
            error = "Sem comprovante anexado. Se você já recebeu o pagamento (Pix, cartão ou outro meio), confirme manualmente abaixo."
            return@LaunchedEffect
        }
        loading = true
        error = null
        val result = kotlin.runCatching {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                val connection = (java.net.URL(url).openConnection() as java.net.HttpURLConnection).apply {
                    connectTimeout = 15_000
                    readTimeout = 30_000
                }
                connection.connect()
                val contentType = connection.contentType.orEmpty()
                val bytes = connection.inputStream.use { it.readBytes() }
                connection.disconnect()
                val pdf = contentType.contains("pdf", true) || url.lowercase().endsWith(".pdf")
                if (pdf) {
                    val file = java.io.File(context.cacheDir, "payment_proof_${student.id}.pdf")
                    file.writeBytes(bytes)
                    val pfd = android.os.ParcelFileDescriptor.open(file, android.os.ParcelFileDescriptor.MODE_READ_ONLY)
                    val renderer = android.graphics.pdf.PdfRenderer(pfd)
                    val bitmaps = (0 until renderer.pageCount).map { index ->
                        renderer.openPage(index).use { page ->
                            android.graphics.Bitmap.createBitmap(page.width * 2, page.height * 2, android.graphics.Bitmap.Config.ARGB_8888).also { bitmap ->
                                bitmap.eraseColor(android.graphics.Color.WHITE)
                                page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            }
                        }
                    }
                    renderer.close()
                    pfd.close()
                    Triple(true, bitmaps, null as android.graphics.Bitmap?)
                } else {
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    Triple(false, emptyList<android.graphics.Bitmap>(), bitmap)
                }
            }
        }
        result.onSuccess { (pdf, bitmaps, bitmap) ->
            isPdf = pdf
            pageBitmaps = bitmaps
            imageBitmap = bitmap
            loading = false
        }.onFailure {
            error = "Não foi possível abrir o comprovante"
            loading = false
        }
    }

    Scaffold(
        containerColor = PurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text(student.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when {
                    loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Lime) }
                    error != null -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(error.orEmpty(), color = Color.White.copy(alpha = .7f), textAlign = TextAlign.Center)
                    }
                    isPdf -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pageBitmaps) { bitmap ->
                            Image(bitmap.asImageBitmap(), contentDescription = "Página do comprovante", modifier = Modifier.fillMaxWidth())
                        }
                    }
                    imageBitmap != null -> Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Image(imageBitmap!!.asImageBitmap(), contentDescription = "Comprovante", modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (hasProof) {
                    Button(
                        onClick = { showDeclineDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleSurface, contentColor = Color.White),
                        shape = RoundedCornerShape(50)
                    ) { Text("Recusar", fontWeight = FontWeight.Bold) }
                }
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                    shape = RoundedCornerShape(50)
                ) { Text(if (hasProof) "Aprovar" else "Confirmar pagamento", fontWeight = FontWeight.Bold) }
            }
        }
    }

    if (showDeclineDialog) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            title = { Text("Recusar comprovante") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Explique o motivo para o aluno. Ele poderá enviar novamente.")
                    TextField(
                        value = declineReason,
                        onValueChange = { declineReason = it },
                        placeholder = { Text("Ex: comprovante ilegível") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onReject(declineReason.trim()) },
                    enabled = declineReason.isNotBlank()
                ) { Text("Recusar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeclineDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

fun parseMoneyValue(value: String): Double =
    value.replace("R$", "")
        .replace(".", "")
        .replace(",", ".")
        .trim()
        .toDoubleOrNull() ?: 0.0

fun studentRoutineMonthlyFee(student: Student, routines: List<DirectoryEntry>): String {
    val routine = routines.firstOrNull {
        it.name.equals(student.routine, ignoreCase = true) ||
            it.name.equals(student.planName, ignoreCase = true)
    }
    return routine?.description?.let { extractRoutinePrice(it) }.orEmpty().ifBlank { student.monthlyFee }
}

fun formatCurrency(value: Double): String =
    "R$ " + java.text.NumberFormat.getNumberInstance(java.util.Locale("pt", "BR")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(value)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    events: List<Event>,
    loading: Boolean,
    onBack: () -> Unit,
    onNewEvent: () -> Unit,
    onEventClick: (Event) -> Unit
) {
    var filter by remember { mutableStateOf("Todos") }
    DirectoryScreen(
        title = "Eventos",
        searchPlaceholder = "Pesquisar evento",
        filters = listOf("Todos", "Proximos", "Sem local"),
        selectedFilter = filter,
        onFilterSelected = { filter = it },
        actions = listOf(DirectoryAction("Novo evento", R.drawable.ic_events, onNewEvent)),
        items = events.sortedBy { it.eventDate },
        loading = loading,
        itemId = { it.id },
        itemTitle = { it.name },
        itemStatus = { event ->
            when {
                event.location.isNullOrBlank() -> "Sem local"
                else -> "Proximos"
            }
        },
        itemMatchesQuery = { event, query ->
            event.name.contains(query, true) ||
                event.description.orEmpty().contains(query, true) ||
                event.location.orEmpty().contains(query, true)
        },
        onBack = onBack,
        onItemClick = onEventClick,
        showBackButton = false
    )
}

