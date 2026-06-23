package com.example.myapplication

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.data.WorkoutRoutePoint
import com.example.myapplication.data.WorkoutSplit
import kotlin.math.max

class WorkoutTrackingService : Service(), LocationListener {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var locationManager: LocationManager
    private var baseElapsedMs = 0L
    private var startedAtMs = 0L
    private var distanceMeters = 0.0
    private var lastLocation: Location? = null
    private val routePoints = mutableListOf<WorkoutRoutePoint>()
    private var running = false
    private var goalType = ""
    private var goalValue = 0.0
    private var goalLabel = ""
    private var goalBaseElapsedMs = 0L
    private var goalBaseDistanceMeters = 0.0
    private var goalAlerted = false

    private val ticker = object : Runnable {
        override fun run() {
            persist()
            broadcast()
            checkGoal()
            // Enquanto pausado, a notificação fica congelada (não vira "Treino pausado"); o
            // estado de pausa é sinalizado só dentro do app.
            if (running) updateNotification()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        restore()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking(reset = true)
            ACTION_RESUME -> startTracking(reset = false)
            // Pausar encerra a corrida de vez: nao ha retomada, tempo e distancia sao perdidos.
            ACTION_PAUSE -> stopTracking()
            ACTION_STOP -> stopTracking()
            ACTION_SET_GOAL -> configureGoal(intent)
            else -> {
                startForeground(NOTIFICATION_ID, notification())
                handler.removeCallbacks(ticker)
                handler.post(ticker)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Chamado quando o usuario fecha o app pelos recentes: a corrida e perdida, nao fica pendente para retomar.
    override fun onTaskRemoved(rootIntent: Intent?) {
        stopTracking()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        runCatching { locationManager.removeUpdates(this) }
        // Nao persiste estado ao morrer: qualquer corrida em andamento e perdida, sem retomada.
        prefs().edit().clear().apply()
        super.onDestroy()
    }

    override fun onLocationChanged(location: Location) {
        if (!running) return
        if (!isUsableLocation(location)) return

        val timestamp = location.time.takeIf { it > 0L } ?: System.currentTimeMillis()
        val candidate = Location(location).apply { time = timestamp }
        val previous = lastLocation
        if (previous == null) {
            acceptLocation(candidate, addDistance = false)
            return
        }

        val elapsedSeconds = (timestamp - previous.time) / 1000.0
        if (elapsedSeconds <= 0.0) return
        val rawDistance = previous.distanceTo(candidate).toDouble()
        val uncertainty = (previous.accuracy + candidate.accuracy).coerceAtLeast(4f).toDouble()
        val minimumMovement = max(MIN_MOVEMENT_METERS, uncertainty * 0.25)
        if (rawDistance < minimumMovement) return

        val plausibleSpeed = MAX_RUNNING_SPEED_MPS + (uncertainty * 0.35 / elapsedSeconds)
        if (rawDistance / elapsedSeconds > plausibleSpeed) return
        if (candidate.hasSpeed() && candidate.speed > MAX_REPORTED_SPEED_MPS) return

        val alpha = when {
            rawDistance >= 30.0 -> 0.85
            candidate.accuracy <= 8f -> 0.68
            candidate.accuracy <= 15f -> 0.52
            else -> 0.38
        }
        val smoothed = Location(candidate).apply {
            latitude = previous.latitude + (candidate.latitude - previous.latitude) * alpha
            longitude = previous.longitude + (candidate.longitude - previous.longitude) * alpha
        }
        acceptLocation(smoothed, addDistance = true)
    }

    private fun isUsableLocation(location: Location): Boolean {
        if (!location.hasAccuracy() || location.accuracy <= 0f || location.accuracy > MAX_ACCURACY_METERS) return false
        if (location.latitude !in -90.0..90.0 || location.longitude !in -180.0..180.0) return false
        return location.latitude != 0.0 || location.longitude != 0.0
    }

    private fun acceptLocation(location: Location, addDistance: Boolean) {
        if (addDistance) {
            lastLocation?.distanceTo(location)?.toDouble()?.takeIf { it > 0.0 }?.let { distanceMeters += it }
        }
        lastLocation = location
        routePoints += WorkoutRoutePoint(location.latitude, location.longitude, location.time)
        persist()
        broadcast()
        checkGoal()
    }

    private fun startTracking(reset: Boolean) {
        if (reset) {
            baseElapsedMs = 0L
            distanceMeters = 0.0
            lastLocation = null
            routePoints.clear()
            goalAlerted = false
        }
        running = true
        startedAtMs = System.currentTimeMillis()
        startForeground(NOTIFICATION_ID, notification())
        requestLocationUpdates()
        persist()
        handler.removeCallbacks(ticker)
        handler.post(ticker)
    }

    private fun stopTracking() {
        running = false
        baseElapsedMs = 0L
        distanceMeters = 0.0
        lastLocation = null
        prefs().edit().clear().apply()
        routePoints.clear()
        broadcast()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) return
        runCatching {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 2f, this)
        }
        runCatching {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 5f, this)
        }
    }

    private fun elapsedMs(): Long =
        if (running) baseElapsedMs + (System.currentTimeMillis() - startedAtMs) else baseElapsedMs

    private fun paceSecondsPerKm(): Double {
        val km = distanceMeters / 1000.0
        if (km <= 0.01) return 0.0
        return elapsedMs() / 1000.0 / km
    }

    private fun configureGoal(intent: Intent) {
        startForeground(NOTIFICATION_ID, notification())
        goalType = intent.getStringExtra(EXTRA_GOAL_TYPE).orEmpty()
        goalValue = intent.getDoubleExtra(EXTRA_GOAL_VALUE, 0.0)
        goalLabel = intent.getStringExtra(EXTRA_GOAL_LABEL).orEmpty()
        goalBaseElapsedMs = intent.getLongExtra(EXTRA_GOAL_BASE_ELAPSED_MS, elapsedMs())
        goalBaseDistanceMeters = intent.getDoubleExtra(EXTRA_GOAL_BASE_DISTANCE_M, distanceMeters)
        goalAlerted = false
        persist()
        checkGoal()
    }

    private fun checkGoal() {
        if (!running || goalAlerted || goalValue <= 0.0) return
        val reached = when (goalType) {
            "time", "rest" -> (elapsedMs() - goalBaseElapsedMs) >= (goalValue * 60_000.0)
            "distance" -> (distanceMeters - goalBaseDistanceMeters) >= (goalValue * 1000.0)
            else -> false
        }
        if (!reached) return
        goalAlerted = true
        persist()
        ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100).apply {
            startTone(ToneGenerator.TONE_PROP_BEEP2, 900)
            handler.postDelayed({ release() }, 1200)
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(
            GOAL_NOTIFICATION_ID,
            NotificationCompat.Builder(this, GOAL_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nav_hub_fit)
                .setContentTitle("Desafio concluído")
                .setContentText(goalLabel.ifBlank { "Você alcançou o objetivo desta etapa." })
                .setStyle(NotificationCompat.BigTextStyle().bigText("Objetivo alcançado: ${goalLabel.ifBlank { "etapa do treino" }}. Abra o app e toque em seguir para o próximo treino."))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
        )
    }

    private fun broadcast() {
        sendBroadcast(Intent(ACTION_UPDATE).apply {
            setPackage(packageName)
            putExtra(EXTRA_RUNNING, running)
            putExtra(EXTRA_ELAPSED_MS, elapsedMs())
            putExtra(EXTRA_DISTANCE_M, distanceMeters)
            putExtra(EXTRA_PACE_SEC_KM, paceSecondsPerKm())
            putExtra(EXTRA_ROUTE_POINTS, routePoints.joinToString(";") { "${it.lat},${it.lon},${it.timestamp}" })
        })
    }

    private fun persist() {
        prefs().edit()
            .putLong(EXTRA_LAST_PERSIST_AT, System.currentTimeMillis())
            .putBoolean(EXTRA_RUNNING, running)
            .putLong(EXTRA_ELAPSED_MS, elapsedMs())
            .putLong("startedAtMs", startedAtMs)
            .putFloat(EXTRA_DISTANCE_M, distanceMeters.toFloat())
            .putString(EXTRA_ROUTE_POINTS, routePoints.joinToString(";") { "${it.lat},${it.lon},${it.timestamp}" })
            .putString(EXTRA_GOAL_TYPE, goalType)
            .putFloat(EXTRA_GOAL_VALUE, goalValue.toFloat())
            .putString(EXTRA_GOAL_LABEL, goalLabel)
            .putLong(EXTRA_GOAL_BASE_ELAPSED_MS, goalBaseElapsedMs)
            .putFloat(EXTRA_GOAL_BASE_DISTANCE_M, goalBaseDistanceMeters.toFloat())
            .putBoolean("goalAlerted", goalAlerted)
            .apply()
    }

    private fun restore() {
        val prefs = prefs()
        running = prefs.getBoolean(EXTRA_RUNNING, false)
        baseElapsedMs = prefs.getLong(EXTRA_ELAPSED_MS, 0L)
        startedAtMs = prefs.getLong("startedAtMs", System.currentTimeMillis())
        distanceMeters = prefs.getFloat(EXTRA_DISTANCE_M, 0f).toDouble()
        routePoints.clear()
        routePoints += parseRoutePoints(prefs.getString(EXTRA_ROUTE_POINTS, "").orEmpty())
        goalType = prefs.getString(EXTRA_GOAL_TYPE, "").orEmpty()
        goalValue = prefs.getFloat(EXTRA_GOAL_VALUE, 0f).toDouble()
        goalLabel = prefs.getString(EXTRA_GOAL_LABEL, "").orEmpty()
        goalBaseElapsedMs = prefs.getLong(EXTRA_GOAL_BASE_ELAPSED_MS, 0L)
        goalBaseDistanceMeters = prefs.getFloat(EXTRA_GOAL_BASE_DISTANCE_M, 0f).toDouble()
        goalAlerted = prefs.getBoolean("goalAlerted", false)
        if (running) {
            baseElapsedMs = elapsedMs()
            startedAtMs = System.currentTimeMillis()
        }
    }

    private fun prefs() = getSharedPreferences("agego_workout_tracking", Context.MODE_PRIVATE)

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Treino em andamento", NotificationManager.IMPORTANCE_LOW)
            )
            manager.createNotificationChannel(
                NotificationChannel(GOAL_CHANNEL_ID, "Desafios do treino", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Apita e avisa quando uma meta de tempo ou distância é alcançada"
                    enableVibration(true)
                }
            )
        }
    }

    private fun updateNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification())
    }

    private fun notification(): Notification {
        val text = "${formatElapsed(elapsedMs())} • ${"%.2f".format(distanceMeters / 1000.0)} km • ${formatPace(paceSecondsPerKm())}"
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nav_hub_fit)
            .setContentTitle(if (running) "Treino em andamento" else "Treino pausado")
            .setContentText(text)
            .setOngoing(running)
            .setOnlyAlertOnce(true)
            .build()
    }

    companion object {
        const val ACTION_START = "com.example.myapplication.workout.START"
        const val ACTION_RESUME = "com.example.myapplication.workout.RESUME"
        const val ACTION_PAUSE = "com.example.myapplication.workout.PAUSE"
        const val ACTION_STOP = "com.example.myapplication.workout.STOP"
        const val ACTION_SET_GOAL = "com.example.myapplication.workout.SET_GOAL"
        const val ACTION_UPDATE = "com.example.myapplication.workout.UPDATE"
        const val EXTRA_RUNNING = "running"
        const val EXTRA_ELAPSED_MS = "elapsedMs"
        const val EXTRA_DISTANCE_M = "distanceMeters"
        const val EXTRA_PACE_SEC_KM = "paceSecondsPerKm"
        const val EXTRA_ROUTE_POINTS = "routePoints"
        const val EXTRA_GOAL_TYPE = "goalType"
        const val EXTRA_GOAL_VALUE = "goalValue"
        const val EXTRA_GOAL_LABEL = "goalLabel"
        const val EXTRA_GOAL_BASE_ELAPSED_MS = "goalBaseElapsedMs"
        const val EXTRA_GOAL_BASE_DISTANCE_M = "goalBaseDistanceMeters"
        const val EXTRA_LAST_PERSIST_AT = "lastPersistAt"
        const val STALE_SESSION_THRESHOLD_MS = 90_000L
        private const val CHANNEL_ID = "agego_workout_tracking"
        private const val GOAL_CHANNEL_ID = "agego_workout_goals"
        private const val NOTIFICATION_ID = 7201
        private const val GOAL_NOTIFICATION_ID = 7202
        private const val MAX_ACCURACY_METERS = 40f
        private const val MIN_MOVEMENT_METERS = 2.5
        private const val MAX_RUNNING_SPEED_MPS = 12.0
        private const val MAX_REPORTED_SPEED_MPS = 15f
    }
}

data class WorkoutTrackingSnapshot(
    val elapsedMs: Long,
    val distanceMeters: Double,
    val paceSecondsPerKm: Double,
    val routePoints: List<WorkoutRoutePoint>,
    val splits: List<WorkoutSplit>
)

fun readWorkoutTrackingSnapshot(context: Context): WorkoutTrackingSnapshot {
    val prefs = context.getSharedPreferences("agego_workout_tracking", Context.MODE_PRIVATE)
    val lastPersistAt = prefs.getLong(WorkoutTrackingService.EXTRA_LAST_PERSIST_AT, 0L)
    val isFresh = lastPersistAt > 0L &&
        (System.currentTimeMillis() - lastPersistAt) <= WorkoutTrackingService.STALE_SESSION_THRESHOLD_MS
    if (!isFresh) {
        if (lastPersistAt > 0L) prefs.edit().clear().apply()
        return WorkoutTrackingSnapshot(0L, 0.0, 0.0, emptyList(), emptyList())
    }
    val elapsedMs = prefs.getLong(WorkoutTrackingService.EXTRA_ELAPSED_MS, 0L)
    val distanceMeters = prefs.getFloat(WorkoutTrackingService.EXTRA_DISTANCE_M, 0f).toDouble()
    val routePoints = parseRoutePoints(prefs.getString(WorkoutTrackingService.EXTRA_ROUTE_POINTS, "").orEmpty())
    val pace = if (distanceMeters > 10.0) elapsedMs / 1000.0 / (distanceMeters / 1000.0) else 0.0
    return WorkoutTrackingSnapshot(
        elapsedMs = elapsedMs,
        distanceMeters = distanceMeters,
        paceSecondsPerKm = pace,
        routePoints = routePoints,
        splits = calculateSplits(routePoints)
    )
}

fun parseRoutePoints(raw: String): List<WorkoutRoutePoint> =
    raw.split(";")
        .mapNotNull { point ->
            val parts = point.split(",")
            if (parts.size != 3) return@mapNotNull null
            val lat = parts[0].toDoubleOrNull() ?: return@mapNotNull null
            val lon = parts[1].toDoubleOrNull() ?: return@mapNotNull null
            val timestamp = parts[2].toLongOrNull() ?: return@mapNotNull null
            WorkoutRoutePoint(lat, lon, timestamp)
        }

fun calculateSplits(points: List<WorkoutRoutePoint>): List<WorkoutSplit> {
    val cleanPoints = cleanRoutePoints(points)
    if (cleanPoints.size < 2) return emptyList()
    val splits = mutableListOf<WorkoutSplit>()
    var distance = 0.0
    var nextKm = 1
    var previous = cleanPoints.first()
    val start = previous.timestamp
    cleanPoints.drop(1).forEach { point ->
        val results = FloatArray(1)
        Location.distanceBetween(previous.lat, previous.lon, point.lat, point.lon, results)
        distance += results[0].coerceAtLeast(0f).toDouble()
        while (distance >= nextKm * 1000.0) {
            val elapsed = (point.timestamp - start).coerceAtLeast(0L)
            splits += WorkoutSplit(
                km = nextKm,
                elapsedMs = elapsed,
                paceSeconds = elapsed / 1000.0 / nextKm
            )
            nextKm += 1
        }
        previous = point
    }
    return splits
}

/** Removes invalid coordinates, duplicate readings and physically implausible jumps from saved routes. */
fun cleanRoutePoints(points: List<WorkoutRoutePoint>): List<WorkoutRoutePoint> {
    if (points.size < 2) return points.filter { it.hasValidCoordinates() }
    val clean = mutableListOf<WorkoutRoutePoint>()
    points.sortedBy { it.timestamp }.forEach { point ->
        if (!point.hasValidCoordinates()) return@forEach
        val previous = clean.lastOrNull()
        if (previous == null) {
            clean += point
            return@forEach
        }
        val elapsedSeconds = (point.timestamp - previous.timestamp) / 1000.0
        if (elapsedSeconds <= 0.0) return@forEach
        val results = FloatArray(1)
        Location.distanceBetween(previous.lat, previous.lon, point.lat, point.lon, results)
        val distance = results[0].coerceAtLeast(0f).toDouble()
        if (distance < 0.75) return@forEach
        if (distance / elapsedSeconds > 15.0) return@forEach
        clean += point
    }
    return clean
}

private fun WorkoutRoutePoint.hasValidCoordinates(): Boolean =
    lat in -90.0..90.0 && lon in -180.0..180.0 && (lat != 0.0 || lon != 0.0) && timestamp > 0L

fun formatElapsed(ms: Long): String {
    val total = (ms / 1000).coerceAtLeast(0)
    val hours = total / 3600
    val minutes = (total % 3600) / 60
    val seconds = total % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds) else "%02d:%02d".format(minutes, seconds)
}

fun formatPace(secondsPerKm: Double): String {
    if (secondsPerKm <= 0.0 || secondsPerKm.isNaN()) return "--/km"
    val minutes = (secondsPerKm / 60).toInt()
    val seconds = (secondsPerKm % 60).toInt()
    return "%d:%02d/km".format(minutes, seconds)
}
