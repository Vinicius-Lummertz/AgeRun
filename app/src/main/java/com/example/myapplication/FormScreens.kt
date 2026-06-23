package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import coil.compose.AsyncImage
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.BuildConfig
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(
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
        item { EventDatePickerField(eventDate, { eventDate = it }) }
        item { FormTextField(location, { location = it }, "Local") }
        item { FormTextField(description, { description = it }, "Descricao") }
        item {
            SaveButton(enabled = name.isNotBlank() && eventDate.isNotBlank()) {
                onSave(Event(event?.id.orEmpty(), name.trim(), description.trim(), eventDate.trim(), location.trim()))
            }
        }
    }
}

@Composable
fun DirectoryFormScreen(
    title: String,
    entry: DirectoryEntry?,
    nameLabel: String,
    descriptionLabel: String,
    selectableStudents: List<Student> = emptyList(),
    onBack: () -> Unit,
    onSave: (DirectoryEntry) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    var name by remember(entry?.id) { mutableStateOf(entry?.name.orEmpty()) }
    var description by remember(entry?.id) { mutableStateOf(entry?.description.orEmpty()) }
    var status by remember(entry?.id) { mutableStateOf(entry?.status ?: "active") }
    var selectedStudentIds by remember(entry?.id) { mutableStateOf(entry?.studentIds.orEmpty().toSet()) }
    var studentQuery by remember { mutableStateOf("") }
    val filteredStudents = selectableStudents.filter { student ->
        student.name.contains(studentQuery, true) ||
            student.email.contains(studentQuery, true) ||
            student.phone.contains(studentQuery, true)
    }

    SimpleFormScaffold(
        title = title,
        onBack = onBack,
        onDelete = if (entry != null && onDelete != null) ({ onDelete(entry.id) }) else null
    ) {
        item { FormTextField(name, { name = it }, nameLabel) }
        item { FormTextField(description, { description = it }, descriptionLabel) }
        if (selectableStudents.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Alunos do grupo", modifier = Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${selectedStudentIds.size} selecionados", color = Lime, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    StudentSearchBar(
                        value = studentQuery,
                        onValueChange = { studentQuery = it },
                        placeholder = "Pesquisar aluno",
                        modifier = Modifier.shadow(14.dp, RoundedCornerShape(50)).imePadding()
                    )
                }
            }
            items(filteredStudents, key = { it.id }) { student ->
                val selected = student.id in selectedStudentIds
                SelectableStudentRow(
                    student = student,
                    selected = selected,
                    onClick = {
                        selectedStudentIds = if (selected) selectedStudentIds - student.id else selectedStudentIds + student.id
                    }
                )
            }
        }
        item {
            SaveButton(enabled = name.isNotBlank()) {
                onSave(DirectoryEntry(entry?.id.orEmpty(), name.trim(), status, description.trim(), selectedStudentIds.toList()))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    student: Student?,
    routines: List<DirectoryEntry> = emptyList(),
    onBack: () -> Unit,
    onSave: (Student) -> Unit,
    onDelete: ((String) -> Unit)?
) {
    val context = LocalContext.current
    var editing by remember(student?.id) { mutableStateOf(student == null) }
    var tab by remember { mutableStateOf("Dados") }
    var name by remember(student?.id) { mutableStateOf(student?.name.orEmpty()) }
    var phone by remember(student?.id) { mutableStateOf(student?.phone.orEmpty()) }
    var email by remember(student?.id) { mutableStateOf(student?.email.orEmpty()) }
    var routine by remember(student?.id) { mutableStateOf(student?.routine.orEmpty().ifBlank { student?.planName.orEmpty() }) }
    var routineQuery by remember(student?.id) { mutableStateOf(student?.routine.orEmpty().ifBlank { student?.planName.orEmpty() }) }
    var routinePickerOpen by remember(student?.id) { mutableStateOf(student == null) }
    var status by remember(student?.id) { mutableStateOf(student?.status ?: "active") }
    var billingDay by remember(student?.id) { mutableStateOf((student?.billingDay ?: 5).coerceIn(1, 28).toString()) }
    val title = if (student == null) "Novo aluno" else student.name
    val currentRoutine = routines.firstOrNull {
        it.id == routine || it.name.equals(routine, ignoreCase = true) || it.name.equals(student?.planName, ignoreCase = true)
    }
    val routineMonthlyFee = currentRoutine?.description?.let { extractRoutinePrice(it) }.orEmpty()
    val filteredRoutines = routines.filter { entry ->
        entry.name.contains(routineQuery, ignoreCase = true) ||
            entry.description.contains(routineQuery, ignoreCase = true) ||
            entry.status.contains(routineQuery, ignoreCase = true)
    }
    val billingDayInt = billingDay.toIntOrNull()?.coerceIn(1, 28) ?: 5
    val isBillingDueToday = billingDayInt == Calendar.getInstance().get(Calendar.DAY_OF_MONTH).coerceAtMost(28)
    val tabs = if (student == null) listOf("Dados", "Plano de treino") else listOf("Dados", "Plano de treino", "Faturamento")
    val canSave = name.isNotBlank() && phone.isNotBlank() && routine.isNotBlank()

    EditableDetailScaffold(
        title = title.ifBlank { "Aluno" },
        editing = editing,
        onBack = onBack,
        onEdit = { editing = true },
        onSave = {
            val updated = student?.copy(
                    name = name.trim(),
                    email = email.trim(),
                    phone = phone.trim(),
                    routine = routine.trim(),
                    planName = routine.trim().ifBlank { "Sem rotina" },
                    status = status,
                    billingDay = billingDayInt,
                    monthlyFee = routineMonthlyFee
                ) ?: Student(
                    id = "",
                    name = name.trim(),
                    email = email.trim(),
                    phone = phone.trim(),
                    routine = routine.trim(),
                    planName = routine.trim().ifBlank { "Sem rotina" },
                    status = status,
                    billingDay = billingDayInt,
                    monthlyFee = routineMonthlyFee
                )
            onSave(updated)
            editing = false
        },
        onDelete = if (student != null && onDelete != null) ({ onDelete(student.id) }) else null,
        saveEnabled = if (student == null) canSave else name.isNotBlank()
    ) {
        item { DetailTabs(tabs, tab) { tab = it } }
        when (tab) {
            "Dados" -> {
                if (editing) {
                    item { DetailInfoRow("Dados pessoais", "Comece pelo essencial. O aluno completa foto e email no primeiro acesso.") }
                    item { FormTextField(name, { name = it }, "Nome") }
                    item { FormTextField(phone, { phone = it }, "Telefone", maxLength = 20) }
                    if (student != null) {
                        item {
                            DetailInfoRow(
                                "Acesso do aluno",
                                "Email e foto sao definidos pelo aluno no primeiro acesso."
                            )
                        }
                    } else {
                        item {
                            DetailInfoRow(
                                "Acesso do aluno",
                                "Ao salvar, o app gera um codigo de 24h para voce enviar ao aluno."
                            )
                        }
                    }
                } else {
                    item { DetailInfoRow("Nome", student?.name.orEmpty()) }
                    item { DetailInfoRow("Telefone", student?.phone.orEmpty().ifBlank { "Sem telefone" }) }
                    if (!student?.accessCode.isNullOrBlank()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("PIN do aluno", student.accessCode))
                                    Toast.makeText(context, "PIN copiado", Toast.LENGTH_SHORT).show()
                                },
                                color = PurpleSurface,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = Lime)
                                    Column(Modifier.weight(1f)) {
                                        Text("PIN de primeiro acesso", color = Color.White.copy(alpha = .64f), fontSize = 12.sp)
                                        Text(student.accessCode, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                        Text("Toque para copiar • válido por 24 horas", color = Lime, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    val accessLink = "${BuildConfig.AGEGO_API_URL.trimEnd('/')}/student-access/${Uri.encode(student.phone)}/${student.accessCode}"
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Link de acesso do aluno", accessLink))
                                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(android.content.Intent.EXTRA_TEXT, "Complete seu cadastro no AgeGo: $accessLink")
                                    }
                                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Enviar link de acesso"))
                                },
                                color = PurpleSurface,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Icon(Icons.Outlined.Share, contentDescription = null, tint = Lime)
                                    Column(Modifier.weight(1f)) {
                                        Text("Link de acesso do aluno", color = Color.White.copy(alpha = .64f), fontSize = 12.sp)
                                        Text("Enviar para o aluno preencher os dados", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("Copiado e pronto para compartilhar • válido por 24 horas", color = Lime, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "Plano de treino" -> {
                if (editing) {
                    item { DetailInfoRow("Como o aluno vai treinar?", "Selecione um treino existente ou crie um treino exclusivo para personalizar os dias.") }
                    item {
                        RoutineSearchSelectorHeader(
                            selectedRoutine = routine,
                            query = routineQuery,
                            onQueryChange = {
                                routineQuery = it
                                routinePickerOpen = true
                            },
                            open = routinePickerOpen,
                            onToggle = { routinePickerOpen = !routinePickerOpen }
                        )
                    }
                    if (routinePickerOpen) {
                        if (routines.isEmpty()) {
                            item { EmptyDetailState("Crie um treino antes de vinculá-lo ao aluno.") }
                        } else if (filteredRoutines.isEmpty()) {
                            item { EmptyDetailState("Nenhum treino encontrado.") }
                        } else {
                            items(filteredRoutines, key = { it.id }) { entry ->
                                SelectableRoutineListRow(
                                    routine = entry,
                                    selected = entry.id == routine || entry.name.equals(routine, ignoreCase = true),
                                    onClick = {
                                        routine = entry.id
                                        routineQuery = entry.name
                                        routinePickerOpen = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    item {
                        if (currentRoutine == null && routine.isBlank()) {
                            EmptyDetailState("Nenhum plano de treino vinculado ainda.")
                        } else {
                            StudentRoutineCard(
                                routineName = currentRoutine?.name ?: routine.ifBlank { student?.planName ?: "Sem rotina" },
                                description = currentRoutine?.description.orEmpty(),
                                status = currentRoutine?.status ?: "active"
                            )
                        }
                    }
                }
            }
            else -> {
                if (editing) {
                    item {
                        DetailInfoRow("Valor mensal", formatMoneyLabel(routineMonthlyFee.ifBlank { student?.monthlyFee.orEmpty() }))
                    }
                    item {
                        DetailInfoRow(
                            "Dia de pagamento",
                            if (student?.billingDay != null) {
                                "Escolhido pelo aluno: vence todo dia ${student.billingDay}."
                            } else {
                                "O proprio aluno escolhe o dia de pagamento no primeiro acesso ou no financeiro do app."
                            }
                        )
                    }
                } else {
                    item {
                        BillingSummaryCard(
                            billingDay = student?.billingDay ?: billingDayInt,
                            monthlyFee = routineMonthlyFee.ifBlank { student?.monthlyFee.orEmpty() },
                            dueToday = isBillingDueToday,
                            onGenerateCharge = {
                                Toast.makeText(
                                    context,
                                    "Cobrança gerada para ${student?.name ?: name} no dia $billingDayInt.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event?,
    onBack: () -> Unit,
    onSave: (Event) -> Unit,
    onDelete: ((String) -> Unit)?,
    onOpenEvent: (() -> Unit)? = null,
    onStartGroupRun: (() -> Unit)? = null,
    onFinishGroupRun: (() -> Unit)? = null,
    onUploadMedia: (suspend (Uri) -> String)? = null
) {
    var editing by remember(event?.id) { mutableStateOf(event == null) }
    var tab by remember { mutableStateOf("Dados") }
    var name by remember(event?.id) { mutableStateOf(event?.name.orEmpty()) }
    var eventDate by remember(event?.id) { mutableStateOf(event?.eventDate ?: defaultEventDate()) }
    var location by remember(event?.id) { mutableStateOf(event?.location.orEmpty()) }
    var latitude by remember(event?.id) { mutableStateOf(event?.latitude) }
    var longitude by remember(event?.id) { mutableStateOf(event?.longitude) }
    var description by remember(event?.id) { mutableStateOf(event?.description.orEmpty()) }
    var coverPhotoUrl by remember(event?.id) { mutableStateOf(event?.coverPhotoUrl) }
    var isUploadingCover by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val coverPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && onUploadMedia != null) {
            isUploadingCover = true
            scope.launch {
                runCatching { onUploadMedia(uri) }
                    .onSuccess { coverPhotoUrl = it }
                isUploadingCover = false
            }
        }
    }

    EditableDetailScaffold(
        title = event?.name ?: "Novo evento",
        editing = editing,
        onBack = onBack,
        onEdit = { editing = true },
        onSave = {
            onSave(
                Event(
                    id = event?.id.orEmpty(),
                    name = name.trim(),
                    description = description.trim(),
                    eventDate = eventDate.trim(),
                    location = location.trim(),
                    latitude = latitude,
                    longitude = longitude,
                    coverPhotoUrl = coverPhotoUrl
                )
            )
            editing = false
        },
        onDelete = if (event != null && onDelete != null) ({ onDelete(event.id) }) else null,
        saveEnabled = name.isNotBlank() && eventDate.isNotBlank()
    ) {
        item {
            Box(Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(16.dp)).background(PurpleSurface)) {
                if (coverPhotoUrl != null) {
                    AsyncImage(
                        model = coverPhotoUrl,
                        contentDescription = "Capa do evento",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                if (editing && onUploadMedia != null) {
                    Button(
                        onClick = { coverPickerLauncher.launch("image/*") },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = .6f), contentColor = Color.White)
                    ) {
                        Text(if (isUploadingCover) "Enviando..." else "Escolher capa")
                    }
                }
            }
        }
        item { DetailTabs(listOf("Dados", "Presença", "Corrida"), tab) { tab = it } }
        when (tab) {
            "Dados" -> {
                if (editing) {
                    item { FormTextField(name, { name = it }, "Nome do evento") }
                    item { EventDatePickerField(eventDate, { eventDate = it }) }
                    item {
                        EventLocationPicker(location, latitude, longitude) { label, lat, lon ->
                            location = label; latitude = lat; longitude = lon
                        }
                    }
                    item { FormTextField(description, { description = it }, "Descricao") }
                } else {
                    item { DetailInfoRow("Nome", event?.name.orEmpty()) }
                    item { DetailInfoRow("Data", event?.eventDate.orEmpty()) }
                    item { DetailInfoRow("Local", event?.location.orEmpty().ifBlank { "Sem local" }) }
                    item { DetailInfoRow("Descricao", event?.description.orEmpty().ifBlank { "Sem descricao" }) }
                }
            }
            "Presença" -> {
                if (event == null) item { EmptyDetailState("Salve o evento para gerar o QR Code.") }
                else {
                    if (event.groupStatus == "waiting" && onOpenEvent != null) {
                        item {
                            Button(
                                onClick = onOpenEvent,
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                                shape = RoundedCornerShape(50)
                            ) { Text("Iniciar evento", fontWeight = FontWeight.Bold) }
                        }
                        item { Text("Ao iniciar, o QR Code será liberado para confirmação de presença.", color = Color.White.copy(alpha = .65f), fontSize = 13.sp) }
                    } else {
                        item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { EventQrCode(event.id) } }
                        item { Text("Os alunos apontam a câmera para este código e confirmam a presença na tela do evento.", color = Color.White.copy(alpha = .65f), fontSize = 13.sp) }
                        if (onStartGroupRun != null && onFinishGroupRun != null && event.groupStatus != "finished") {
                            item {
                                Button(
                                    onClick = if (event.groupStatus == "running") onFinishGroupRun else onStartGroupRun,
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(if (event.groupStatus == "running") "Finalizar corrida" else "Iniciar corrida em grupo", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        items(event.attendees, key = { it.studentId }) { attendee -> DirectoryListRow(attendee.name, onClick = {}) }
                    }
                }
            }
            else -> {
                if (event != null && onStartGroupRun != null && onFinishGroupRun != null) {
                    item { EventGroupControlPanel(event, onStartGroupRun, onFinishGroupRun) }
                } else item { EmptyDetailState("Salve o evento para iniciar a corrida em grupo.") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableDetailScaffold(
    title: String,
    editing: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onDelete: (() -> Unit)?,
    saveEnabled: Boolean = true,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    Scaffold(
        containerColor = PurpleBackground,
        topBar = {
            TopAppBar(
                title = { Text(title.ifBlank { "Detalhes" }, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Voltar") } },
                actions = {
                    if (onDelete != null) IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, "Excluir") }
                    TextButton(onClick = if (editing) onSave else onEdit, enabled = if (editing) saveEnabled else true) {
                        Text(if (editing) "Salvar" else "Editar", color = Lime, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding(),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun EventDatePickerField(value: String, onValueChange: (String) -> Unit) {
    var showCalendar by remember { mutableStateOf(false) }
    val calendar = remember(value) { calendarFromEventDate(value) }
    val displayValue = remember(value) {
        SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(calendar.time)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { showCalendar = true },
        color = PurpleSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Lime, modifier = Modifier.size(20.dp))
            Column(Modifier.padding(start = 10.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Data", color = Color.White.copy(alpha = .58f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(displayValue, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
    if (showCalendar) {
        WeekScheduleDialog(
            routineName = "Selecione o dia do evento",
            routineDescription = "",
            selectedDays = setOf(value.take(10)).filter { it.isNotBlank() }.toSet(),
            onSelectedDaysChange = { selected ->
                selected.firstOrNull()?.let { key ->
                    val next = calendarFromEventDate(value)
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(key)
                    if (date != null) {
                        val chosen = Calendar.getInstance().apply { time = date }
                        next.set(Calendar.YEAR, chosen.get(Calendar.YEAR))
                        next.set(Calendar.MONTH, chosen.get(Calendar.MONTH))
                        next.set(Calendar.DAY_OF_MONTH, chosen.get(Calendar.DAY_OF_MONTH))
                        onValueChange(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(next.time))
                    }
                }
            },
            onDismiss = { showCalendar = false },
            title = "Data do evento",
            singleSelection = true,
            helpText = "Escolha uma data para o encontro."
        )
    }
}

fun calendarFromEventDate(value: String): Calendar {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 7)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    runCatching {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).parse(value)
    }.getOrNull()?.let { calendar.time = it }
    return calendar
}

@Composable
fun DetailTabs(tabs: List<String>, selected: String, onSelected: (String) -> Unit) {
    SegmentedFilterBar(options = tabs, selected = selected, onSelected = onSelected)
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = Color.White.copy(alpha = .58f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(value.ifBlank { "-" }, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun StudentRoutineCard(routineName: String, description: String, status: String) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = Lime, modifier = Modifier.size(22.dp))
                Text(
                    routineName.ifBlank { "Sem rotina" },
                    modifier = Modifier.padding(start = 10.dp).weight(1f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
            val sections = parseWorkoutStructure(description)
            if (sections.isEmpty()) {
                Text("Estrutura ainda nao definida.", color = Color.White.copy(alpha = .68f), fontSize = 13.sp)
            } else {
                WorkoutStructureTimeline(sections)
            }
        }
    }
}

@Composable
fun RoutineSearchSelectorHeader(
    selectedRoutine: String,
    query: String,
    onQueryChange: (String) -> Unit,
    open: Boolean,
    onToggle: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Treino do cliente", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = onToggle, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text(if (open) "Fechar" else "Trocar", color = Lime, fontWeight = FontWeight.Bold)
            }
        }
        if (!open && selectedRoutine.isNotBlank()) {
            StudentRoutineCard(
                routineName = selectedRoutine,
                description = "Toque em Trocar para selecionar outro treino.",
                status = "active"
            )
        } else {
            StudentSearchBar(
                value = query,
                onValueChange = onQueryChange,
                placeholder = "Pesquisar treino",
                modifier = Modifier.shadow(14.dp, RoundedCornerShape(50)).imePadding()
            )
        }
    }
}

@Composable
fun SelectableRoutineListRow(routine: DirectoryEntry, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (selected) Lime else PurpleSurface),
                contentAlignment = Alignment.Center
            ) {
                if (selected) Icon(Icons.Outlined.Check, contentDescription = null, tint = PurpleDeep, modifier = Modifier.size(15.dp))
            }
            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                Text(
                    routine.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (routine.description.isNotBlank()) {
                    Text(
                        routine.description,
                        color = Color.White.copy(alpha = .56f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Selecionar treino",
                modifier = Modifier.size(18.dp),
                tint = Color.White.copy(alpha = 0.78f)
            )
        }
        HorizontalDivider(color = NavigationPurple, thickness = 0.8.dp)
    }
}

@Composable
fun BillingSummaryCard(
    billingDay: Int,
    monthlyFee: String,
    dueToday: Boolean,
    onGenerateCharge: () -> Unit
) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Payments, contentDescription = null, tint = Lime, modifier = Modifier.size(24.dp))
                Column(Modifier.padding(start = 10.dp).weight(1f)) {
                    Text("Cobrança mensal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Vence todo dia $billingDay", color = Color.White.copy(alpha = .62f), fontSize = 12.sp)
                }
                StatusBadge(if (dueToday) "Cobrar hoje" else "Agendada", if (dueToday) Color(0xFFFFC107) else Lime)
            }
            DetailInfoRow("Valor", formatMoneyLabel(monthlyFee))
            if (dueToday) {
                Surface(color = Color(0xFFFFC107).copy(alpha = .18f), shape = RoundedCornerShape(12.dp)) {
                    Text(
                        "Hoje é dia de cobrança deste cliente.",
                        Modifier.fillMaxWidth().padding(12.dp),
                        color = Color(0xFFFFD166),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
            Button(
                onClick = onGenerateCharge,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Outlined.Payments, contentDescription = null)
                Text("Gerar cobrança", Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BillingDayPicker(day: Int, onDayChange: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Dia da cobrança", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("S", "T", "Q", "Q", "S", "S", "D").forEach { label ->
                        Text(
                            label,
                            modifier = Modifier.weight(1f),
                            color = Color.White.copy(alpha = .52f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                (1..28).chunked(7).forEach { week ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        week.forEach { option ->
                            val selected = day == option
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clickable { onDayChange(option) },
                                color = if (selected) Lime else PurpleBackground,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        option.toString(),
                                        color = if (selected) PurpleDeep else Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatMoneyLabel(value: String): String =
    value.ifBlank { "R$ 0,00" }.let { raw ->
        if (raw.startsWith("R$")) raw else "R$ $raw"
    }

@Composable
fun EmptyDetailState(message: String) {
    Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
        Text(message, Modifier.fillMaxWidth().padding(18.dp), color = Color.White.copy(alpha = .72f))
    }
}

@Composable
fun SelectableStudentRow(student: Student, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        color = if (selected) Lime.copy(alpha = .18f) else PurpleSurface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(22.dp).clip(CircleShape).background(if (selected) Lime else PurpleBackground),
                contentAlignment = Alignment.Center
            ) {
                if (selected) Icon(Icons.Outlined.Check, null, tint = PurpleDeep, modifier = Modifier.size(15.dp))
            }
            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                Text(student.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                if (student.email.isNotBlank()) Text(student.email, color = Color.White.copy(alpha = .58f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StudentStatusPicker(status: String, onStatusChange: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("active" to "Em dia", "pending_payment" to "A pagar", "inactive" to "Inativo").forEach { (value, label) ->
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
fun SimpleFormScaffold(
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
fun SaveButton(enabled: Boolean, onClick: () -> Unit) {
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
@Composable
fun FormTextField(value: String, onValueChange: (String) -> Unit, label: String, maxLength: Int = 255) {
    TextField(
        value = value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
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
fun StatusPicker(status: String, onStatusChange: (String) -> Unit) {
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
