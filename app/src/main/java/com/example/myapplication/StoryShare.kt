package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun shareWorkoutToInstagramStories(
    context: Context,
    distanceMeters: Double,
    elapsedMs: Long,
    paceSecondsPerKm: Double
) {
    val uri = createWorkoutStorySticker(context, distanceMeters, elapsedMs, paceSecondsPerKm)
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

private fun createWorkoutStorySticker(
    context: Context,
    distanceMeters: Double,
    elapsedMs: Long,
    paceSecondsPerKm: Double
): Uri {
    val width = 900
    val height = 260
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val background = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(224, 23, 7, 30) }
    val lime = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(199, 255, 71) }
    val white = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    val muted = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(178, 255, 255, 255) }

    canvas.drawRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), 42f, 42f, background)

    ContextCompat.getDrawable(context, R.drawable.logo_sem_fundo)?.let { logo ->
        logo.setBounds(42, 44, 178, 180)
        logo.draw(canvas)
    }

    white.textSize = 44f
    white.isFakeBoldText = true
    canvas.drawText("AgeGo", 44f, 220f, white)

    lime.textSize = 62f
    lime.isFakeBoldText = true
    canvas.drawText("%.2f km".format(distanceMeters / 1000.0), 250f, 106f, lime)

    white.textSize = 38f
    white.isFakeBoldText = true
    canvas.drawText(formatElapsed(elapsedMs), 250f, 174f, white)

    muted.textSize = 28f
    muted.isFakeBoldText = false
    canvas.drawText(formatPace(paceSecondsPerKm), 250f, 220f, muted)

    val directory = File(context.cacheDir, "shared").apply { mkdirs() }
    val file = File(directory, "agego_story_workout.png")
    FileOutputStream(file).use { output -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, output) }
    bitmap.recycle()

    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
