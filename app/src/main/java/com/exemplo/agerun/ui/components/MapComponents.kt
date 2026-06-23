package com.exemplo.agerun.ui.components

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.exemplo.agerun.location.hasLocationPermission
import com.exemplo.agerun.model.LocationPoint
import com.exemplo.agerun.ui.theme.CardPurple
import com.exemplo.agerun.ui.theme.DeepPurple
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.NightPurple
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun LiveMapCard(
    hasLocationPermission: Boolean,
    currentLocation: LocationPoint?,
    onRequestPermission: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = DeepPurple,
        tonalElevation = 10.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mapa nativo da turma",
                        color = TextPrimary,
                        fontSize = 18.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (hasLocationPermission) {
                            "Centralizado com GPS real do aparelho."
                        } else {
                            "Ative a localizacao para acompanhar o mapa."
                        },
                        color = TextMuted,
                        fontSize = 13.sp,
                    )
                }
                TagChip(text = if (currentLocation != null) "GPS ativo" else "Aguardando")
            }

            if (!hasLocationPermission) {
                PermissionCard(onRequestPermission = onRequestPermission)
            } else {
                NativeMapView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    currentLocation = currentLocation,
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    onRequestPermission: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardPurple)
            .padding(horizontal = 18.dp, vertical = 22.dp),
    ) {
        Text(
            text = "Permita a localizacao para ver o mapa real do celular.",
            color = TextPrimary,
            fontSize = 15.sp,
        )
        Spacer(modifier = Modifier.height(14.dp))
        SmallActionButton(
            text = "Ativar GPS",
            onClick = onRequestPermission,
        )
    }
}

@Composable
private fun NativeMapView(
    modifier: Modifier = Modifier,
    currentLocation: LocationPoint?,
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val fallback = remember { LatLng(-23.55052, -46.633308) }

    AndroidView(
        modifier = modifier,
        factory = {
            MapsInitializer.initialize(context)
            mapView
        },
        update = { view ->
            view.getMapAsync { googleMap ->
                googleMap.uiSettings.isCompassEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true
                googleMap.uiSettings.isZoomControlsEnabled = false
                googleMap.isMyLocationEnabled = context.hasLocationPermission()

                val target = currentLocation?.toLatLng() ?: fallback
                googleMap.clear()
                googleMap.addMarker(
                    MarkerOptions()
                        .position(target)
                        .title(if (currentLocation != null) "Voce esta aqui" else "Base da assessoria"),
                )
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        target,
                        if (currentLocation != null) 13.5f else 10f,
                    ),
                )
            }
        },
    )
}

@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context).apply { onCreate(Bundle()) } }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return mapView
}
