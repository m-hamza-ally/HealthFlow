package com.example.healthflow.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Data class representing a mood journal entry
 */
data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val mood: MoodType,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Get formatted date string
     */
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Get formatted time string
     */
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Get date key for grouping (yyyy-MM-dd)
     */
    fun getDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

/**
 * Enum representing different mood types
 */
enum class MoodType(val emoji: String, val label: String, val color: String) {
    AMAZING("😄", "Amazing", "#FFD93D"),
    HAPPY("😊", "Happy", "#7ED957"),
    OKAY("😐", "Okay", "#6BB6FF"),
    SAD("😔", "Sad", "#B19EFF"),
    STRESSED("😰", "Stressed", "#FFB054"),
    ANGRY("😠", "Angry", "#FF6B6B");

    companion object {
        fun fromEmoji(emoji: String): MoodType? {
            return values().find { it.emoji == emoji }
        }
    }
}