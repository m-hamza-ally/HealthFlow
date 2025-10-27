package com.example.healthflow.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.healthflow.MainActivity
import com.example.healthflow.R
import com.example.healthflow.data.PreferencesManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Widget showing today's habit completion progress
 */
class HealthProgressWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Called when first widget is created
    }

    override fun onDisabled(context: Context) {
        // Called when last widget is removed
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefsManager = PreferencesManager.getInstance(context)
            val habits = prefsManager.getHabits()

            // Calculate progress
            val totalHabits = habits.size
            val completedHabits = habits.count { it.isCompletedToday() }
            val percentage = if (totalHabits > 0) {
                (completedHabits.toFloat() / totalHabits.toFloat() * 100).toInt()
            } else {
                0
            }

            // Create RemoteViews
            val views = RemoteViews(context.packageName, R.layout.widget_health_progress)

            // Update percentage
            views.setTextViewText(R.id.tvProgressPercentage, "$percentage%")

            // Update completion text
            views.setTextViewText(
                R.id.tvCompletionText,
                "$completedHabits of $totalHabits completed"
            )

            // Update motivational message
            val motivationMessage = when {
                percentage == 100 -> "Perfect! 🎉"
                percentage >= 75 -> "Almost there! 💪"
                percentage >= 50 -> "Keep going! 🚀"
                percentage >= 25 -> "Good start! 👍"
                else -> "You got this! 💙"
            }
            views.setTextViewText(R.id.tvMotivation, motivationMessage)

            // Update last updated time
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            views.setTextViewText(R.id.tvLastUpdated, "Updated at $currentTime")

            // Set click intent to open app
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.tvProgressPercentage, pendingIntent)

            // Update widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        /**
         * Manually trigger widget update from app
         */
        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, HealthProgressWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(
                    android.content.ComponentName(
                        context,
                        HealthProgressWidget::class.java
                    )
                )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}