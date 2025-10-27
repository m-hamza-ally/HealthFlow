package com.example.healthflow.models

import java.util.UUID

/**
 * Data class representing a daily habit
 */
data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: String, // Emoji or icon identifier
    val color: String, // Hex color code
    val createdAt: Long = System.currentTimeMillis(),
    val completionDates: MutableList<String> = mutableListOf() // Format: "yyyy-MM-dd"
) {
    /**
     * Check if habit is completed today
     */
    fun isCompletedToday(): Boolean {
        val today = getCurrentDate()
        return completionDates.contains(today)
    }

    /**
     * Toggle completion status for today
     */
    fun toggleCompletion(): Habit {
        val today = getCurrentDate()
        return if (completionDates.contains(today)) {
            this.copy(completionDates = completionDates.apply { remove(today) })
        } else {
            this.copy(completionDates = completionDates.apply { add(today) })
        }
    }

    /**
     * Get completion streak (consecutive days)
     */
    fun getStreak(): Int {
        var streak = 0
        var currentDate = getCurrentDateAsLong()
        val sortedDates = completionDates.map { dateStringToLong(it) }.sorted().reversed()

        for (date in sortedDates) {
            if (date == currentDate || date == currentDate - 86400000) { // 1 day in milliseconds
                streak++
                currentDate = date
            } else {
                break
            }
        }
        return streak
    }

    companion object {
        private fun getCurrentDate(): String {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            return sdf.format(java.util.Date())
        }

        private fun getCurrentDateAsLong(): Long {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val today = sdf.format(java.util.Date())
            return sdf.parse(today)?.time ?: System.currentTimeMillis()
        }

        private fun dateStringToLong(dateString: String): Long {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            return sdf.parse(dateString)?.time ?: 0L
        }
    }
}