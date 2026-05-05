package com.exemplo.agerun

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.exemplo.agerun.ui.theme.AgeRunTheme
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private sealed interface LocationUiState {
    data object Idle : LocationUiState
    data object Loading : LocationUiState
    data class Success(
        val latitude: Double,
        val longitude: Double,
        val placeName: String,
    ) : LocationUiState

    data class Error(val message: String) : LocationUiState
}

@Composable
fun AgeRunHomeScreen(
    modifier: Modifier = Modifier,
    authResponse: AuthResponse? = null,
    onOpenEscalas: () -> Unit = {},
    onOpenRecados: () -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var uiState by remember { mutableStateOf<LocationUiState>(LocationUiState.Idle) }

    fun loadLocation() {
        uiState = LocationUiState.Loading
        requestCurrentLocation(
            context = context,
            onLocation = { location ->
                if (location == null) {
                    uiState = LocationUiState.Error("Nao consegui ler o GPS agora. Confira se a localizacao esta ligada.")
                    return@requestCurrentLocation
                }

                coroutineScope.launch {
                    val placeName = findPlaceName(context, location)
                    uiState = LocationUiState.Success(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        placeName = placeName,
                    )
                }
            },
            onError = { message ->
                uiState = LocationUiState.Error(message)
            },
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            loadLocation()
        } else {
            uiState = LocationUiState.Error("Permissao de localizacao negada. Libere o GPS para usar esta tela.")
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF6F7F2),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column {
                Text(
                    text = authResponse?.user?.nome?.let { "Oi, $it" } ?: "AgeRun",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF10221C),
                )
                Text(
                    text = "Acompanhe treinos, recados e sua posicao.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF587069),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StudentActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Escalas",
                        subtitle = "Proximos treinos",
                        onClick = onOpenEscalas,
                    )
                    StudentActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Recados",
                        subtitle = "Avisos da turma",
                        onClick = onOpenRecados,
                    )
                }

                MiniMapPreview()

                Spacer(modifier = Modifier.height(2.dp))

                LocationCard(uiState = uiState)
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    if (hasLocationPermission(context)) {
                        loadLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            ),
                        )
                    }
                },
            ) {
                Text(
                    text = "onde estou?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun StudentActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = title, color = Color(0xFF10221C), fontWeight = FontWeight.Black)
            Text(text = subtitle, color = Color(0xFF587069), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun MiniMapPreview(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFBFE5D2),
                        Color(0xFFD7EAF2),
                        Color(0xFFF4E2B9),
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(92.dp)
                .clip(CircleShape)
                .background(Color(0x332D7A50)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF0B6B3A)),
        )
        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            text = "mapa demo",
            color = Color(0xFF24423A),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun LocationCard(uiState: LocationUiState, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (uiState) {
                LocationUiState.Idle -> {
                    Text(
                        text = "Toque no botao para buscar sua posicao atual.",
                        color = Color(0xFF587069),
                    )
                }

                LocationUiState.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        Text(text = "Consultando GPS...")
                    }
                }

                is LocationUiState.Success -> {
                    Text(
                        text = uiState.placeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10221C),
                    )
                    Text(text = "Latitude: ${"%.6f".format(Locale.US, uiState.latitude)}")
                    Text(text = "Longitude: ${"%.6f".format(Locale.US, uiState.longitude)}")
                }

                is LocationUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}

private fun requestCurrentLocation(
    context: Context,
    onLocation: (Location?) -> Unit,
    onError: (String) -> Unit,
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val provider = when {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> null
    }

    if (provider == null) {
        onError("Ative a localizacao do aparelho para o AgeRun encontrar voce.")
        return
    }

    try {
        locationManager.getCurrentLocation(
            provider,
            CancellationSignal(),
            ContextCompat.getMainExecutor(context),
            onLocation,
        )
    } catch (_: SecurityException) {
        onError("Sem permissao para acessar o GPS.")
    }
}

private suspend fun findPlaceName(context: Context, location: Location): String = withContext(Dispatchers.IO) {
    val geocoder = Geocoder(context, Locale.getDefault())
    val fallback = "Local atual"

    runCatching {
        @Suppress("DEPRECATION")
        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
        address?.let {
            listOfNotNull(it.thoroughfare, it.subLocality, it.locality, it.countryName)
                .distinct()
                .joinToString(", ")
                .ifBlank { it.getAddressLine(0) ?: fallback }
        } ?: fallback
    }.getOrElse { fallback }
}

@Preview(showBackground = true)
@Composable
private fun AgeRunHomePreview() {
    AgeRunTheme {
        AgeRunHomeScreen()
    }
}
