package com.exemplo.agerun

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.exemplo.agerun.ui.theme.AgeRunTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ProfessorBackground = Color(0xFFF6F7F2)
private val ProfessorInk = Color(0xFF10221C)
private val ProfessorMuted = Color(0xFF587069)
private val ProfessorGreen = Color(0xFF0B6B3A)
private val ProfessorSurface = Color.White
private val ProfessorLine = Color(0xFFD7E3DA)

private data class PlaceSuggestion(
    val title: String,
    val details: String,
)

@Composable
fun ProfessorHomeScreen(
    authResponse: AuthResponse,
    modifier: Modifier = Modifier,
) {
    val accessToken = authResponse.session?.accessToken.orEmpty()
    val coroutineScope = rememberCoroutineScope()
    var showCreateDialog by remember { mutableStateOf(false) }
    var escalas by remember { mutableStateOf<List<Escala>>(emptyList()) }
    var isLoadingEscalas by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }

    fun loadEscalas() {
        if (accessToken.isBlank()) return

        isLoadingEscalas = true
        coroutineScope.launch {
            AuthApi.listarEscalas(accessToken)
                .onSuccess {
                    escalas = it
                    feedback = null
                }
                .onFailure {
                    feedback = it.message ?: "Nao foi possivel carregar as escalas."
                }
            isLoadingEscalas = false
        }
    }

    LaunchedEffect(accessToken) {
        loadEscalas()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = ProfessorBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            ProfessorHeader(nome = authResponse.user.nome)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ProfessorMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Escalas",
                    value = escalas.size.toString(),
                )
                ProfessorMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Perfil",
                    value = "professor",
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Acoes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProfessorInk,
                )

                ProfessorActionCard(
                    title = "Criar escala",
                    subtitle = "Agende treino, local e horario.",
                    onClick = { showCreateDialog = true },
                )
                ProfessorActionCard(
                    title = "Enviar recado",
                    subtitle = "Em breve: aviso rapido para alunos.",
                    enabled = false,
                    onClick = {},
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Proximas escalas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ProfessorInk,
                    )
                    TextButton(onClick = { loadEscalas() }) {
                        Text(text = "Atualizar", color = ProfessorGreen, fontWeight = FontWeight.Bold)
                    }
                }

                feedback?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }

                if (isLoadingEscalas) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Text(text = "Carregando escalas...", color = ProfessorMuted)
                    }
                } else if (escalas.isEmpty()) {
                    EmptyEscalasCard()
                } else {
                    escalas.take(4).forEach { escala ->
                        EscalaListCard(escala = escala)
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateEscalaDialog(
            accessToken = accessToken,
            onDismiss = { showCreateDialog = false },
            onCreated = {
                showCreateDialog = false
                loadEscalas()
            },
        )
    }
}

@Composable
private fun ProfessorHeader(nome: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Bem vindo(a), professor",
            style = MaterialTheme.typography.labelLarge,
            color = ProfessorMuted,
            fontWeight = FontWeight.Bold,
        )
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(ProfessorGreen),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "AR",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Black,
            )
        }
        Text(
            text = nome,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = ProfessorInk,
        )
    }
}

@Composable
private fun ProfessorMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ProfessorSurface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = ProfessorMuted, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = ProfessorInk, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ProfessorActionCard(
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ProfessorSurface),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = ProfessorInk, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = ProfessorMuted, style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedButton(
                enabled = enabled,
                shape = RoundedCornerShape(8.dp),
                onClick = onClick,
            ) {
                Text(text = if (enabled) "Abrir" else "Logo", color = if (enabled) ProfessorGreen else ProfessorMuted)
            }
        }
    }
}

@Composable
private fun EmptyEscalasCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ProfessorSurface),
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Nenhuma escala criada ainda.",
            color = ProfessorMuted,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun EscalaListCard(escala: Escala) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ProfessorSurface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = escala.titulo, color = ProfessorInk, fontWeight = FontWeight.Bold)
            Text(text = escala.inicioAt, color = ProfessorMuted, style = MaterialTheme.typography.bodyMedium)
            escala.local?.let {
                Text(text = it, color = ProfessorMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun CreateEscalaDialog(
    accessToken: String,
    onDismiss: () -> Unit,
    onCreated: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var local by remember { mutableStateOf("") }
    var inicioAtIso by remember { mutableStateOf("") }
    var inicioAtLabel by remember { mutableStateOf("") }
    var fimAtIso by remember { mutableStateOf("") }
    var fimAtLabel by remember { mutableStateOf("") }
    var placeSuggestions by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    var isSearchingPlaces by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var placeError by remember { mutableStateOf<String?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (!granted) {
            placeError = "Permissao de localizacao negada."
            return@rememberLauncherForActivityResult
        }

        coroutineScope.launch {
            placeError = null
            loadCurrentPlace(context)
                .onSuccess { local = it }
                .onFailure { placeError = it.message ?: "Nao consegui ler sua localizacao." }
        }
    }

    LaunchedEffect(local) {
        placeError = null
        if (local.trim().length < 3) {
            placeSuggestions = emptyList()
            return@LaunchedEffect
        }

        delay(450)
        isSearchingPlaces = true
        placeSuggestions = searchPlaces(context, local)
        isSearchingPlaces = false
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) onDismiss()
        },
        title = {
            Text(text = "Criar escala", color = ProfessorInk, fontWeight = FontWeight.Black)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Titulo") },
                    singleLine = true,
                    colors = professorTextFieldColors(),
                )
                LocationPickerField(
                    local = local,
                    suggestions = placeSuggestions,
                    isSearching = isSearchingPlaces,
                    error = placeError,
                    onLocalChange = { local = it },
                    onSuggestionClick = {
                        local = if (it.details.isBlank()) it.title else "${it.title}, ${it.details}"
                        placeSuggestions = emptyList()
                    },
                    onUseCurrentLocation = {
                        if (hasLocationPermission(context)) {
                            coroutineScope.launch {
                                placeError = null
                                loadCurrentPlace(context)
                                    .onSuccess { local = it }
                                    .onFailure { placeError = it.message ?: "Nao consegui ler sua localizacao." }
                            }
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                ),
                            )
                        }
                    },
                )
                DateTimePickerField(
                    label = "Inicio",
                    value = inicioAtLabel,
                    required = true,
                    onClick = {
                        showDateTimePicker(context) { iso, label ->
                            inicioAtIso = iso
                            inicioAtLabel = label
                        }
                    },
                )
                DateTimePickerField(
                    label = "Fim",
                    value = fimAtLabel,
                    required = false,
                    onClick = {
                        showDateTimePicker(context) { iso, label ->
                            fimAtIso = iso
                            fimAtLabel = label
                        }
                    },
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descricao") },
                    minLines = 2,
                    colors = professorTextFieldColors(),
                )

                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving,
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    isSaving = true
                    error = null
                    coroutineScope.launch {
                        AuthApi.criarEscala(
                            accessToken = accessToken,
                            titulo = titulo,
                            descricao = descricao,
                            local = local,
                            inicioAt = inicioAtIso,
                            fimAt = fimAtIso,
                        )
                            .onSuccess { onCreated() }
                            .onFailure { error = it.message ?: "Nao foi possivel criar a escala." }
                        isSaving = false
                    }
                },
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(text = "Salvar", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(enabled = !isSaving, onClick = onDismiss) {
                Text(text = "Cancelar", color = ProfessorGreen, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = ProfessorSurface,
        shape = RoundedCornerShape(8.dp),
    )
}

@Composable
private fun LocationPickerField(
    local: String,
    suggestions: List<PlaceSuggestion>,
    isSearching: Boolean,
    error: String?,
    onLocalChange: (String) -> Unit,
    onSuggestionClick: (PlaceSuggestion) -> Unit,
    onUseCurrentLocation: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = local,
            onValueChange = onLocalChange,
            label = { Text("Local") },
            placeholder = { Text("Busque por parque, rua ou bairro") },
            singleLine = true,
            colors = professorTextFieldColors(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onUseCurrentLocation) {
                Text(text = "Usar minha localizacao", color = ProfessorGreen, fontWeight = FontWeight.Bold)
            }

            if (isSearching) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            }
        }

        suggestions.take(3).forEach { suggestion ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionClick(suggestion) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFCF8)),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = suggestion.title, color = ProfessorInk, fontWeight = FontWeight.Bold)
                    if (suggestion.details.isNotBlank()) {
                        Text(text = suggestion.details, color = ProfessorMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DateTimePickerField(
    label: String,
    value: String,
    required: Boolean,
    onClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = if (required) label else "$label opcional",
            color = ProfessorMuted,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            onClick = onClick,
        ) {
            Text(
                text = value.ifBlank { "Selecionar data e hora" },
                color = if (value.isBlank()) ProfessorMuted else ProfessorInk,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun professorTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = ProfessorInk,
    unfocusedTextColor = ProfessorInk,
    cursorColor = ProfessorGreen,
    focusedBorderColor = ProfessorGreen,
    unfocusedBorderColor = ProfessorLine,
    focusedLabelColor = ProfessorGreen,
    unfocusedLabelColor = ProfessorMuted,
    focusedContainerColor = Color(0xFFFBFCF8),
    unfocusedContainerColor = Color(0xFFFBFCF8),
)

private fun showDateTimePicker(
    context: Context,
    onSelected: (isoValue: String, displayValue: String) -> Unit,
) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    val isoValue = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
                        .format(calendar.time)
                    val displayValue = SimpleDateFormat("dd/MM/yyyy 'as' HH:mm", Locale("pt", "BR"))
                        .format(calendar.time)

                    onSelected(isoValue, displayValue)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true,
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
    ).show()
}

private suspend fun searchPlaces(context: Context, query: String): List<PlaceSuggestion> = withContext(Dispatchers.IO) {
    runCatching {
        val geocoder = Geocoder(context, Locale.getDefault())

        @Suppress("DEPRECATION")
        geocoder.getFromLocationName(query, 5)
            .orEmpty()
            .mapNotNull { address ->
                val title = listOfNotNull(address.featureName, address.thoroughfare, address.subLocality)
                    .firstOrNull { it.isNotBlank() }
                    ?: address.getAddressLine(0)
                    ?: return@mapNotNull null
                val details = listOfNotNull(address.locality, address.adminArea, address.countryName)
                    .distinct()
                    .joinToString(", ")

                PlaceSuggestion(title = title, details = details)
            }
            .distinctBy { "${it.title}|${it.details}" }
    }.getOrElse { emptyList() }
}

private suspend fun loadCurrentPlace(context: Context): Result<String> = withContext(Dispatchers.IO) {
    runCatching {
        val location = getCurrentLocation(context) ?: throw IllegalStateException("Nao consegui ler o GPS agora.")
        val geocoder = Geocoder(context, Locale.getDefault())

        @Suppress("DEPRECATION")
        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.firstOrNull()

        address?.getAddressLine(0)
            ?: "Lat ${"%.6f".format(Locale.US, location.latitude)}, Long ${"%.6f".format(Locale.US, location.longitude)}"
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}

private suspend fun getCurrentLocation(context: Context): Location? = withContext(Dispatchers.Main) {
    kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> null
        }

        if (provider == null) {
            continuation.resume(null) {}
            return@suspendCancellableCoroutine
        }

        val cancellationSignal = CancellationSignal()
        continuation.invokeOnCancellation { cancellationSignal.cancel() }

        try {
            locationManager.getCurrentLocation(
                provider,
                cancellationSignal,
                ContextCompat.getMainExecutor(context),
            ) { location ->
                continuation.resume(location) {}
            }
        } catch (_: SecurityException) {
            continuation.resume(null) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfessorHomePreview() {
    AgeRunTheme {
        ProfessorHomeScreen(
            authResponse = AuthResponse(
                user = AuthUser(
                    id = "1",
                    nome = "Vinicius",
                    email = "professor@agerun.com",
                    role = "professor",
                ),
                session = AuthSession(
                    accessToken = "preview",
                    refreshToken = "preview",
                ),
            ),
        )
    }
}
