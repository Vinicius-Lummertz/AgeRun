package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import com.example.myapplication.data.WorkoutRoutePoint

fun shareWorkoutToInstagramStories(
    context: Context,
    distanceMeters: Double,
    elapsedMs: Long,
    paceSecondsPerKm: Double
) {
    val uri = createWorkoutShareImage(context, distanceMeters, elapsedMs, paceSecondsPerKm)
    val instagramIntent = Intent("com.instagram.share.ADD_TO_STORY").apply {
        setDataAndType(uri, "image/png")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra("interactive_asset_uri", uri)
        setPackage("com.instagram.android")
    }
    context.grantUriPermission("com.instagram.android", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

    val packageManager = context.packageManager
    if (instagramIntent.resolveActivity(packageManager) != null) {
        context.startActivity(instagramIntent)
    } else {
        val fallback = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(fallback, "Compartilhar treino"))
    }
}

fun createWorkoutShareImage(
    context: Context,
    distanceMeters: Double,
    elapsedMs: Long,
    paceSecondsPerKm: Double,
    routePoints: List<WorkoutRoutePoint> = emptyList()
): Uri {
    val width = 900
    val height = 720
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val background = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(224, 23, 7, 30) }
    val lime = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(199, 255, 71) }
    val white = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    val muted = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(178, 255, 255, 255) }

    canvas.drawRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), 42f, 42f, background)
    val mapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(49, 30, 64) }
    canvas.drawRoundRect(RectF(28f, 28f, 872f, 440f), 30f, 30f, mapPaint)
    if (routePoints.size > 1) {
        val minLat = routePoints.minOf { it.lat }; val maxLat = routePoints.maxOf { it.lat }
        val minLon = routePoints.minOf { it.lon }; val maxLon = routePoints.maxOf { it.lon }
        val latSpan = (maxLat - minLat).takeIf { it > 0.0 } ?: 0.001
        val lonSpan = (maxLon - minLon).takeIf { it > 0.0 } ?: 0.001
        val path = Path()
        routePoints.forEachIndexed { index, point ->
            val x = 70f + (((point.lon - minLon) / lonSpan) * 760f).toFloat()
            val y = 400f - (((point.lat - minLat) / latSpan) * 330f).toFloat()
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(199, 255, 71); style = Paint.Style.STROKE; strokeWidth = 14f; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND })
    }

    ContextCompat.getDrawable(context, R.drawable.logo_sem_fundo)?.let { logo ->
        logo.setBounds(42, 475, 178, 611)
        logo.draw(canvas)
    }

    white.textSize = 44f
    white.isFakeBoldText = true
    canvas.drawText("AgeGo", 44f, 660f, white)

    lime.textSize = 62f
    lime.isFakeBoldText = true
    canvas.drawText("%.2f km".format(distanceMeters / 1000.0), 250f, 535f, lime)

    white.textSize = 38f
    white.isFakeBoldText = true
    canvas.drawText(formatElapsed(elapsedMs), 250f, 603f, white)

    muted.textSize = 28f
    muted.isFakeBoldText = false
    canvas.drawText(formatPace(paceSecondsPerKm), 250f, 660f, muted)

    val directory = File(context.cacheDir, "shared").apply { mkdirs() }
    val file = File(directory, "agego_story_workout.png")
    FileOutputStream(file).use { output -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, output) }
    bitmap.recycle()

    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
