package com.example.healthflow.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.healthflow.MainActivity
import com.example.healthflow.R
import java.util.concurrent.TimeUnit

/**
 * Manager class for hydration reminder notifications
 */
class HydrationReminderManager(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "hydration_reminders"
        private const val NOTIFICATION_ID = 1001
        private const val WORK_NAME = "hydration_reminder_work"
    }

    init {
        createNotificationChannel()
    }

    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_HIGH // Changed to HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Schedule periodic reminders
     */
    fun scheduleReminders(intervalMinutes: Int) {
        // Cancel any existing work
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()

        // For testing with short intervals (< 15 minutes), use repeating OneTimeWork
        if (intervalMinutes < 15) {
            val reminderRequest = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
                .setInitialDelay(intervalMinutes.toLong(), TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                reminderRequest
            )
        } else {
            // For longer intervals, use PeriodicWorkRequest
            val reminderRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
                intervalMinutes.toLong(),
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                reminderRequest
            )
        }
    }

    /**
     * Cancel all reminders
     */
    fun cancelReminders() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_NAME)
    }

    /**
     * Show notification immediately (for testing)
     */
    fun showNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

/**
 * Worker class for periodic hydration reminders
 */
class HydrationReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val reminderManager = HydrationReminderManager(applicationContext)
        reminderManager.showNotification()

        // For short intervals, reschedule the next reminder
        val prefs = applicationContext.getSharedPreferences("healthflow_prefs", Context.MODE_PRIVATE)
        val interval = prefs.getInt("reminder_interval", 60)
        val isEnabled = prefs.getBoolean("reminders_enabled", false)

        if (isEnabled && interval < 15) {
            reminderManager.scheduleReminders(interval)
        }

        return Result.success()
    }
}