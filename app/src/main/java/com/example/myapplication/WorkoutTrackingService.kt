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
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.data.WorkoutRoutePoint
import com.example.myapplication.data.WorkoutSplit

class WorkoutTrackingService : Service(), LocationListener {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var locationManager: LocationManager
    private var baseElapsedMs = 0L
    private var startedAtMs = 0L
    private var distanceMeters = 0.0
    private var lastLocation: Location? = null
    private val routePoints = mutableListOf<WorkoutRoutePoint>()
    private var running = false

    private val ticker = object : Runnable {
        override fun run() {
            persist()
            broadcast()
            updateNotification()
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
            ACTION_PAUSE -> pauseTracking()
            ACTION_STOP -> stopTracking()
            else -> {
                startForeground(NOTIFICATION_ID, notification())
                handler.removeCallbacks(ticker)
                handler.post(ticker)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        runCatching { locationManager.removeUpdates(this) }
        persist()
        super.onDestroy()
    }

    override fun onLocationChanged(location: Location) {
        if (!running) return
        lastLocation?.let { previous ->
            if (location.accuracy <= 60f) {
                val delta = previous.distanceTo(location).toDouble()
                if (delta in 0.5..250.0) distanceMeters += delta
            }
        }
        lastLocation = location
        routePoints += WorkoutRoutePoint(location.latitude, location.longitude, location.time.takeIf { it > 0L } ?: System.currentTimeMillis())
        persist()
        broadcast()
    }

    private fun startTracking(reset: Boolean) {
        if (reset) {
            baseElapsedMs = 0L
            distanceMeters = 0.0
            lastLocation = null
            routePoints.clear()
        }
        running = true
        startedAtMs = System.currentTimeMillis()
        startForeground(NOTIFICATION_ID, notification())
        requestLocationUpdates()
        handler.removeCallbacks(ticker)
        handler.post(ticker)
    }

    private fun pauseTracking() {
        if (running) baseElapsedMs = elapsedMs()
        running = false
        runCatching { locationManager.removeUpdates(this) }
        persist()
        broadcast()
        updateNotification()
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

    private fun broadcast() {
        sendBroadcast(Intent(ACTION_UPDATE).apply {
            setPackage(packageName)
            putExtra(EXTRA_RUNNING, running)
            putExtra(EXTRA_ELAPSED_MS, elapsedMs())
            putExtra(EXTRA_DISTANCE_M, distanceMeters)
            putExtra(EXTRA_PACE_SEC_KM, paceSecondsPerKm())
        })
    }

    private fun persist() {
        prefs().edit()
            .putBoolean(EXTRA_RUNNING, running)
            .putLong(EXTRA_ELAPSED_MS, elapsedMs())
            .putLong("startedAtMs", startedAtMs)
            .putFloat(EXTRA_DISTANCE_M, distanceMeters.toFloat())
            .putString(EXTRA_ROUTE_POINTS, routePoints.joinToString(";") { "${it.lat},${it.lon},${it.timestamp}" })
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
        const val ACTION_UPDATE = "com.example.myapplication.workout.UPDATE"
        const val EXTRA_RUNNING = "running"
        const val EXTRA_ELAPSED_MS = "elapsedMs"
        const val EXTRA_DISTANCE_M = "distanceMeters"
        const val EXTRA_PACE_SEC_KM = "paceSecondsPerKm"
        const val EXTRA_ROUTE_POINTS = "routePoints"
        private const val CHANNEL_ID = "agego_workout_tracking"
        private const val NOTIFICATION_ID = 7201
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
    if (points.size < 2) return emptyList()
    val splits = mutableListOf<WorkoutSplit>()
    var distance = 0.0
    var nextKm = 1
    var previous = points.first()
    val start = previous.timestamp
    points.drop(1).forEach { point ->
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
