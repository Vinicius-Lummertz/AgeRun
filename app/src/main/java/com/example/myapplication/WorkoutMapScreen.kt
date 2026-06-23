package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.myapplication.data.WorkoutRoutePoint
import com.example.myapplication.ui.theme.Lime
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.layers.PropertyFactory.lineCap
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineJoin
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.layers.Property.LINE_CAP_ROUND
import org.maplibre.android.style.layers.Property.LINE_JOIN_ROUND
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

private const val OSM_STYLE = """{
  "version": 8,
  "sources": {
    "openstreetmap": {
      "type": "raster",
      "tiles": ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
      "tileSize": 256,
      "attribution": "© OpenStreetMap contributors",
      "maxzoom": 19
    }
  },
  "layers": [{"id":"openstreetmap","type":"raster","source":"openstreetmap"}]
}"""
private const val ROUTE_SOURCE = "agego-route-source"
private const val ROUTE_LAYER = "agego-route-layer"
private const val POSITION_SOURCE = "agego-position-source"
private const val POSITION_LAYER = "agego-position-layer"

@Composable
fun WorkoutTrackingMapScreen(
    stepName: String,
    headerSubtitle: String? = null,
    stepTarget: String,
    progress: Float,
    running: Boolean,
    elapsedMs: Long,
    distanceMeters: Double,
    paceSecondsPerKm: Double,
    routePoints: List<WorkoutRoutePoint>,
    nextLabel: String,
    nextEnabled: Boolean,
    onClose: () -> Unit,
    onToggleRunning: () -> Unit,
    onNext: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(Color(0xFF171719))) {
        WorkoutRouteMap(routePoints, Modifier.fillMaxSize())

        Text(
            "© OpenStreetMap contributors",
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 8.dp, bottom = 304.dp)
                .background(Color.White.copy(alpha = .82f), RoundedCornerShape(3.dp)).padding(horizontal = 4.dp, vertical = 2.dp),
            color = Color.Black, fontSize = 9.sp
        )

        Surface(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().statusBarsPadding().padding(16.dp),
            color = Color.Black.copy(alpha = 0.72f),
            shape = RoundedCornerShape(22.dp)
        ) {
            Row(Modifier.padding(start = 18.dp, top = 10.dp, end = 8.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(headerSubtitle ?: "Treino em andamento", color = Color.White.copy(alpha = .68f), fontSize = 12.sp)
                    Text(stepName, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onClose) { Icon(Icons.Outlined.Close, "Fechar", tint = Color.White) }
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            color = Color(0xFF0B0B0D),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            shadowElevation = 18.dp
        ) {
            Column(
                Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(Modifier.size(42.dp, 4.dp).background(Color.White.copy(alpha = .22f), RoundedCornerShape(50)))
                Text(stepTarget, color = Lime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(formatElapsed(elapsedMs), color = Color.White, fontSize = 46.sp, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(5.dp),
                    color = Lime,
                    trackColor = Color.White.copy(alpha = .13f)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MapMetric("TEMPO", formatElapsed(elapsedMs), Modifier.weight(1f))
                    MapMetric("DISTÂNCIA", "${"%.2f".format(distanceMeters / 1000.0)} km", Modifier.weight(1f))
                    MapMetric("PACE", formatPace(paceSecondsPerKm), Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleRunning,
                        modifier = Modifier.size(62.dp).background(Lime, CircleShape)
                    ) {
                        Icon(if (running) Icons.Outlined.Stop else Icons.Outlined.PlayArrow, if (running) "Encerrar corrida" else "Comecar", tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                    Button(
                        onClick = onNext,
                        enabled = nextEnabled,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black, disabledContainerColor = Color.White.copy(alpha = .16f)),
                        shape = RoundedCornerShape(18.dp)
                    ) { Text(nextLabel, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
private fun MapMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White.copy(alpha = .45f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun WorkoutRouteMap(points: List<WorkoutRoutePoint>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember {
        MapLibre.getInstance(context)
        MapView(context).also { it.onCreate(null) }
    }
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleReady by remember { mutableStateOf(false) }
    val cleanPoints = remember(points) { cleanRoutePoints(points) }

    AndroidView(factory = { mapView }, modifier = modifier)

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
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) mapView.onStart()
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) mapView.onResume()
        mapView.getMapAsync { readyMap ->
            map = readyMap
            readyMap.setStyle(Style.Builder().fromJson(OSM_STYLE)) { styleReady = true }
        }
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    // Antes do primeiro ponto de GPS da corrida, centraliza no fix "morno" do LiveLocationCache
    // para nao abrir o mapa descentralizado enquanto a corrida ainda nao tem rota propria.
    LaunchedEffect(map, styleReady, cleanPoints.isEmpty(), LiveLocationCache.location) {
        val readyMap = map ?: return@LaunchedEffect
        if (!styleReady || cleanPoints.isNotEmpty()) return@LaunchedEffect
        val cached = LiveLocationCache.location ?: return@LaunchedEffect
        readyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(cached.latitude, cached.longitude), 16.5))
    }

    LaunchedEffect(cleanPoints, map, styleReady) {
        val readyMap = map ?: return@LaunchedEffect
        if (!styleReady || cleanPoints.isEmpty()) return@LaunchedEffect
        val coordinates = cleanPoints.map { Point.fromLngLat(it.lon, it.lat) }
        readyMap.getStyle { style ->
            if (coordinates.size >= 2) {
                val source = style.getSourceAs<GeoJsonSource>(ROUTE_SOURCE)
                if (source == null) {
                    style.addSource(GeoJsonSource(ROUTE_SOURCE, LineString.fromLngLats(coordinates)))
                    style.addLayer(
                        LineLayer(ROUTE_LAYER, ROUTE_SOURCE).withProperties(
                            lineColor("#C8FF3D"), lineWidth(6f), lineCap(LINE_CAP_ROUND), lineJoin(LINE_JOIN_ROUND)
                        )
                    )
                } else {
                    source.setGeoJson(LineString.fromLngLats(coordinates))
                }
            }
            cleanPoints.lastOrNull()?.let { last ->
                val currentPoint = Point.fromLngLat(last.lon, last.lat)
                val positionSource = style.getSourceAs<GeoJsonSource>(POSITION_SOURCE)
                if (positionSource == null) {
                    style.addSource(GeoJsonSource(POSITION_SOURCE, currentPoint))
                    style.addLayer(
                        CircleLayer(POSITION_LAYER, POSITION_SOURCE).withProperties(
                            circleRadius(8f), circleColor("#C8FF3D"), circleStrokeColor("#0B0B0D"), circleStrokeWidth(3f)
                        )
                    )
                } else {
                    positionSource.setGeoJson(currentPoint)
                }
                // A animação curta mantém a posição fluida sem saltar a cada leitura do GPS.
                readyMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(last.lat, last.lon), 16.5), 900)
            }
        }
    }
}

private val ROUTE_PALETTE = listOf("#C8FF3D", "#3DC8FF", "#FF6FB5", "#FFB23D", "#A06FFF", "#3DFFA0")

@Composable
fun MultiRouteMap(routes: List<Pair<String, List<WorkoutRoutePoint>>>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember {
        MapLibre.getInstance(context)
        MapView(context).also { it.onCreate(null) }
    }
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleReady by remember { mutableStateOf(false) }

    AndroidView(factory = { mapView }, modifier = modifier)

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
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) mapView.onStart()
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) mapView.onResume()
        mapView.getMapAsync { readyMap ->
            map = readyMap
            readyMap.setStyle(Style.Builder().fromJson(OSM_STYLE)) { styleReady = true }
        }
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    LaunchedEffect(routes, map, styleReady) {
        val readyMap = map ?: return@LaunchedEffect
        if (!styleReady) return@LaunchedEffect
        readyMap.getStyle { style ->
            val allPoints = mutableListOf<LatLng>()
            routes.forEachIndexed { index, (studentId, points) ->
                val cleanPoints = cleanRoutePoints(points)
                if (cleanPoints.size < 2) return@forEachIndexed
                val coordinates = cleanPoints.map { Point.fromLngLat(it.lon, it.lat) }
                allPoints += cleanPoints.map { LatLng(it.lat, it.lon) }
                val sourceId = "$ROUTE_SOURCE-$studentId"
                val layerId = "$ROUTE_LAYER-$studentId"
                val color = ROUTE_PALETTE[index % ROUTE_PALETTE.size]
                val source = style.getSourceAs<GeoJsonSource>(sourceId)
                if (source == null) {
                    style.addSource(GeoJsonSource(sourceId, LineString.fromLngLats(coordinates)))
                    style.addLayer(
                        LineLayer(layerId, sourceId).withProperties(
                            lineColor(color), lineWidth(5f), lineCap(LINE_CAP_ROUND), lineJoin(LINE_JOIN_ROUND)
                        )
                    )
                } else {
                    source.setGeoJson(LineString.fromLngLats(coordinates))
                }
            }
            if (allPoints.isNotEmpty()) {
                val bounds = LatLngBounds.Builder().includes(allPoints).build()
                runCatching { readyMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 48)) }
            }
        }
    }
}
