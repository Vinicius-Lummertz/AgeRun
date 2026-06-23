package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

/**
 * Keeps a low-power location fix warm while the student is anywhere in the app, so the
 * run map can center immediately instead of starting at an empty/default position while it
 * waits for the workout's own first GPS fix.
 */
object LiveLocationCache {
    var location: Location? by mutableStateOf(null)
        private set
    private var manager: LocationManager? = null
    private val listener = LocationListener { loc -> location = loc }

    fun start(context: Context) {
        if (manager != null) return
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) return
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        manager = lm
        if (location == null) {
            location = runCatching { lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) }.getOrNull()
                ?: runCatching { lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) }.getOrNull()
        }
        runCatching { lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5_000L, 10f, listener) }
        runCatching { lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 8_000L, 20f, listener) }
    }

    fun stop() {
        manager?.let { runCatching { it.removeUpdates(listener) } }
        manager = null
    }
}
