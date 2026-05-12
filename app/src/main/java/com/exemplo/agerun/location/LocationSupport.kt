package com.exemplo.agerun.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.exemplo.agerun.model.LocationPoint

@Composable
fun rememberDeviceLocation(
    hasPermission: Boolean,
    context: Context,
): LocationPoint? {
    val locationState = remember(hasPermission, context) { mutableStateOf<LocationPoint?>(null) }

    DisposableEffect(hasPermission, context) {
        if (!hasPermission) {
            locationState.value = null
            return@DisposableEffect onDispose { }
        }

        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (manager == null) {
            locationState.value = null
            return@DisposableEffect onDispose { }
        }

        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        providers
            .mapNotNull { provider -> manager.safeLastKnownLocation(provider) }
            .maxByOrNull { it.time }
            ?.let { locationState.value = it.toPoint() }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationState.value = location.toPoint()
            }
        }

        providers.forEach { provider ->
            manager.requestUpdatesSafely(provider, listener)
        }

        onDispose {
            manager.removeUpdates(listener)
        }
    }

    return locationState.value
}

fun Context.hasLocationPermission(): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

private fun LocationManager.safeLastKnownLocation(provider: String): Location? {
    return runCatching { getLastKnownLocation(provider) }.getOrNull()
}

private fun LocationManager.requestUpdatesSafely(
    provider: String,
    listener: LocationListener,
) {
    runCatching {
        if (isProviderEnabled(provider)) {
            requestLocationUpdates(provider, 3_000L, 10f, listener, Looper.getMainLooper())
        }
    }
}

private fun Location.toPoint(): LocationPoint = LocationPoint(
    latitude = latitude,
    longitude = longitude,
)
