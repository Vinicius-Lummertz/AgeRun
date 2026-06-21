package com.example.myapplication

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myapplication.ui.theme.Lime
import com.example.myapplication.ui.theme.PurpleDeep
import com.example.myapplication.ui.theme.PurpleSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import java.util.Locale

private const val EVENT_OSM_STYLE = """{
  "version": 8,
  "sources": {"osm": {"type": "raster", "tiles": ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"], "tileSize": 256}},
  "layers": [{"id": "osm", "type": "raster", "source": "osm"}]
}"""

@Composable
fun EventLocationPicker(
    location: String,
    latitude: Double?,
    longitude: Double?,
    onLocationSelected: (String, Double, Double) -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()
    var query by remember(location) { mutableStateOf(location) }
    var error by remember { mutableStateOf("") }
    val mapView = remember {
        MapLibre.getInstance(context)
        MapView(context).also { it.onCreate(null) }
    }
    var map by remember { mutableStateOf<org.maplibre.android.maps.MapLibreMap?>(null) }

    androidx.compose.foundation.layout.Column {
        Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.TextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Pesquisar endereço") },
                    singleLine = true
                )
                IconButton(onClick = {
                    scope.launch {
                        val address = withContext(Dispatchers.IO) {
                            runCatching { Geocoder(context, Locale.getDefault()).getFromLocationName(query, 1)?.firstOrNull() }.getOrNull()
                        }
                        if (address == null) error = "Local não encontrado" else {
                            error = ""
                            val label = address.getAddressLine(0) ?: query
                            query = label
                            onLocationSelected(label, address.latitude, address.longitude)
                            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(address.latitude, address.longitude), 16.0), 700)
                        }
                    }
                }) { Icon(Icons.Outlined.Search, "Pesquisar no mapa", tint = Lime) }
            }
        }
        if (error.isNotBlank()) Text(error, color = Color(0xFFFFB3BE), modifier = Modifier.padding(top = 6.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(230.dp).padding(top = 10.dp).background(PurpleDeep, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxWidth().height(230.dp))
            Icon(Icons.Outlined.LocationOn, "Local selecionado", tint = Lime, modifier = Modifier.size(36.dp))
        }
    }

    DisposableEffect(mapView, lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        mapView.getMapAsync { readyMap ->
            map = readyMap
            readyMap.setStyle(Style.Builder().fromJson(EVENT_OSM_STYLE)) {
                if (latitude != null && longitude != null) {
                    readyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16.0))
                }
            }
            readyMap.addOnMapClickListener { point ->
                scope.launch {
                    val label = withContext(Dispatchers.IO) {
                        runCatching {
                            Geocoder(context, Locale.getDefault()).getFromLocation(point.latitude, point.longitude, 1)
                                ?.firstOrNull()?.getAddressLine(0)
                        }.getOrNull()
                    } ?: "${"%.5f".format(point.latitude)}, ${"%.5f".format(point.longitude)}"
                    query = label
                    onLocationSelected(label, point.latitude, point.longitude)
                }
                true
            }
        }
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onPause(); mapView.onStop(); mapView.onDestroy()
        }
    }

    LaunchedEffect(latitude, longitude, map) {
        if (latitude != null && longitude != null) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16.0), 500)
        }
    }
}
