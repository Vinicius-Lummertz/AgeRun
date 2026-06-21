package com.example.myapplication

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.data.Event
import com.example.myapplication.ui.theme.Lime
import com.example.myapplication.ui.theme.PurpleDeep
import com.example.myapplication.ui.theme.PurpleSurface
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun EventQrCode(eventId: String) {
    val bitmap = remember(eventId) {
        val matrix = QRCodeWriter().encode("agego://event/$eventId/check-in", BarcodeFormat.QR_CODE, 640, 640)
        Bitmap.createBitmap(640, 640, Bitmap.Config.RGB_565).apply {
            for (x in 0 until 640) for (y in 0 until 640) {
                setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
    }
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Image(bitmap.asImageBitmap(), "QR Code para confirmar presença", Modifier.size(230.dp).padding(12.dp))
    }
}

@Composable
fun EventGroupControlPanel(event: Event, onStart: () -> Unit, onFinish: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Corrida em grupo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    when (event.groupStatus) { "running" -> "Em andamento"; "finished" -> "Finalizada"; "checkin" -> "Participantes entrando"; else -> "Inicie o evento primeiro" },
                    color = if (event.groupStatus == "running") Lime else Color.White.copy(alpha = .65f)
                )
                Button(
                    onClick = if (event.groupStatus == "running") onFinish else onStart,
                    enabled = event.groupStatus == "checkin" || event.groupStatus == "running",
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = PurpleDeep)
                ) {
                    Icon(if (event.groupStatus == "running") Icons.Outlined.Stop else Icons.Outlined.PlayArrow, null)
                    Text(if (event.groupStatus == "running") "Finalizar corrida" else "Iniciar corrida em grupo", Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
                }
            }
        }
        Text("Presenças (${event.attendees.size})", color = Color.White, fontWeight = FontWeight.Bold)
        event.attendees.forEach { attendee ->
            Row(Modifier.fillMaxWidth().background(PurpleSurface, RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                ProfileAvatar(attendee.avatarUrl, "Foto de ${attendee.name}", Modifier.size(36.dp))
                Text(attendee.name, Modifier.padding(start = 10.dp).weight(1f), color = Color.White)
                Icon(Icons.Outlined.CheckCircle, "Presença confirmada", tint = Lime)
            }
        }
        if (event.groupStatus == "finished" && event.results.isNotEmpty()) {
            val totalDistanceMeters = event.results.sumOf { it.distanceMeters }
            val totalElapsedMs = event.results.maxOf { it.elapsedMs }
            val overallPaceSecondsPerKm = event.results.sumOf { it.elapsedMs } / 1000.0 / (totalDistanceMeters / 1000.0).coerceAtLeast(0.01)
            Text("Análise da corrida", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Distância total", color = Color.White.copy(alpha = .6f), fontSize = 12.sp)
                        Text("${"%.2f".format(totalDistanceMeters / 1000.0)} km", color = Lime, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Pace geral", color = Color.White.copy(alpha = .6f), fontSize = 12.sp)
                        Text(formatPace(overallPaceSecondsPerKm), color = Lime, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Tempo da corrida", color = Color.White.copy(alpha = .6f), fontSize = 12.sp)
                        Text(formatElapsed(totalElapsedMs), color = Lime, fontWeight = FontWeight.Bold)
                    }
                }
            }
            val routes = event.results.filter { it.routePoints.size >= 2 }.map { it.studentId to it.routePoints }
            if (routes.isNotEmpty()) {
                Surface(color = PurpleSurface, shape = RoundedCornerShape(14.dp)) {
                    MultiRouteMap(routes, Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(14.dp)))
                }
            }
        }
        if (event.results.isNotEmpty()) Text("Resultados", color = Color.White, fontWeight = FontWeight.Bold)
        event.results.forEach { result ->
            Column(Modifier.fillMaxWidth().background(PurpleSurface, RoundedCornerShape(12.dp)).padding(12.dp)) {
                Text(result.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text("${"%.2f".format(result.distanceMeters / 1000.0)} km · ${formatElapsed(result.elapsedMs)} · ${formatPace(result.paceSecondsPerKm)}", color = Lime, fontSize = 13.sp)
            }
        }
    }
}
