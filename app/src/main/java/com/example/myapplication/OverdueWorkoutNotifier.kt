package com.example.myapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.Calendar
import java.util.concurrent.TimeUnit

private const val PREFS_NAME = "agego_overdue_workout"
private const val KEY_SCHEDULED_DATES = "scheduledDates"
private const val KEY_LAST_COMPLETED_DATE = "lastCompletedDate"
private const val WORK_NAME = "agego-overdue-workout-check"
private const val CHANNEL_ID = "agego_overdue_workout"
private const val NOTIFICATION_ID = 7301

private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

fun saveScheduledDates(context: Context, dates: Set<String>) {
    prefs(context).edit().putStringSet(KEY_SCHEDULED_DATES, dates).apply()
}

fun readScheduledDates(context: Context): Set<String> =
    prefs(context).getStringSet(KEY_SCHEDULED_DATES, emptySet()).orEmpty()

fun markWorkoutCompletedToday(context: Context) {
    prefs(context).edit().putString(KEY_LAST_COMPLETED_DATE, todayKey()).apply()
    cancelOverdueWorkoutNotification(context)
}

fun cancelOverdueWorkoutNotification(context: Context) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.cancel(NOTIFICATION_ID)
}

fun scheduleOverdueWorkoutCheck(context: Context) {
    val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build()
    val initialDelayMs = millisUntilNextCheckTime(20, 0)
    val request = PeriodicWorkRequestBuilder<OverdueWorkoutWorker>(1, TimeUnit.DAYS)
        .setConstraints(constraints)
        .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
}

private fun millisUntilNextCheckTime(hour: Int, minute: Int): Long {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    if (!target.after(now)) target.add(Calendar.DAY_OF_MONTH, 1)
    return target.timeInMillis - now.timeInMillis
}

class OverdueWorkoutWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val context = applicationContext
        val today = todayKey()
        val scheduledDates = readScheduledDates(context)
        if (today !in scheduledDates) return Result.success()
        val lastCompleted = prefs(context).getString(KEY_LAST_COMPLETED_DATE, "")
        if (lastCompleted == today) return Result.success()
        showOverdueNotification(context)
        return Result.success()
    }

    private fun showOverdueNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Treino atrasado", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nav_hub_fit)
            .setContentTitle("Treino atrasado")
            .setContentText("Hoje e dia de treino na sua rotina. Abra o AgeGo para nao perder o ritmo.")
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }
}
