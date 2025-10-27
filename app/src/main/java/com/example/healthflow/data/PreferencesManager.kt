package com.example.healthflow.data

import android.content.Context
import android.content.SharedPreferences
import com.example.healthflow.models.Habit
import com.example.healthflow.models.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Manager class for handling SharedPreferences data persistence
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "healthflow_prefs"
        private const val KEY_HABITS = "habits"
        private const val KEY_MOODS = "moods"
        private const val KEY_REMINDERS_ENABLED = "reminders_enabled"
        private const val KEY_REMINDER_INTERVAL = "reminder_interval"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_TODAY_STEPS = "today_steps"
        private const val KEY_STEPS_DATE = "steps_date"
        private const val KEY_DARK_MODE = "dark_mode"

        @Volatile
        private var instance: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: PreferencesManager(context.applicationContext).also { instance = it }
            }
        }
    }

    // ==================== Habits ====================

    /**
     * Save habits list to SharedPreferences
     */
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, json).apply()
    }

    /**
     * Get all habits from SharedPreferences
     */
    fun getHabits(): List<Habit> {
        val json = prefs.getString(KEY_HABITS, null) ?: return emptyList()
        val type = object : TypeToken<List<Habit>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Add a new habit
     */
    fun addHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        habits.add(habit)
        saveHabits(habits)
    }

    /**
     * Update an existing habit
     */
    fun updateHabit(updatedHabit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            habits[index] = updatedHabit
            saveHabits(habits)
        }
    }

    /**
     * Delete a habit
     */
    fun deleteHabit(habitId: String) {
        val habits = getHabits().toMutableList()
        habits.removeAll { it.id == habitId }
        saveHabits(habits)
    }

    /**
     * Toggle habit completion for today
     */
    fun toggleHabitCompletion(habitId: String) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habitId }
        if (index != -1) {
            habits[index] = habits[index].toggleCompletion()
            saveHabits(habits)
        }
    }

    // ==================== Mood Entries ====================

    /**
     * Save mood entries list to SharedPreferences
     */
    fun saveMoodEntries(moods: List<MoodEntry>) {
        val json = gson.toJson(moods)
        prefs.edit().putString(KEY_MOODS, json).apply()
    }

    /**
     * Get all mood entries from SharedPreferences
     */
    fun getMoodEntries(): List<MoodEntry> {
        val json = prefs.getString(KEY_MOODS, null) ?: return emptyList()
        val type = object : TypeToken<List<MoodEntry>>() {}.type
        return try {
            gson.fromJson<List<MoodEntry>>(json, type).sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Add a new mood entry
     */
    fun addMoodEntry(moodEntry: MoodEntry) {
        val moods = getMoodEntries().toMutableList()
        moods.add(moodEntry)
        saveMoodEntries(moods)
    }

    /**
     * Delete a mood entry
     */
    fun deleteMoodEntry(moodId: String) {
        val moods = getMoodEntries().toMutableList()
        moods.removeAll { it.id == moodId }
        saveMoodEntries(moods)
    }

    // ==================== Settings ====================

    /**
     * Enable or disable hydration reminders
     */
    fun setRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDERS_ENABLED, enabled).apply()
    }

    /**
     * Check if reminders are enabled
     */
    fun areRemindersEnabled(): Boolean {
        return prefs.getBoolean(KEY_REMINDERS_ENABLED, false)
    }

    /**
     * Set reminder interval in minutes
     */
    fun setReminderInterval(minutes: Int) {
        prefs.edit().putInt(KEY_REMINDER_INTERVAL, minutes).apply()
    }

    /**
     * Get reminder interval in minutes (default 60 minutes)
     */
    fun getReminderInterval(): Int {
        return prefs.getInt(KEY_REMINDER_INTERVAL, 60)
    }

    /**
     * Check if this is the first launch
     */
    fun isFirstLaunch(): Boolean {
        val isFirst = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        if (isFirst) {
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
        }
        return isFirst
    }

    /**
     * Clear all data (for testing or reset)
     */
    fun clearAllData() {
        prefs.edit().clear().apply()
    }

    // ==================== Step Counter ====================

    /**
     * Save today's step count
     */
    fun saveTodaySteps(steps: Int) {
        val today = getCurrentDate()
        prefs.edit()
            .putInt(KEY_TODAY_STEPS, steps)
            .putString(KEY_STEPS_DATE, today)
            .apply()
    }

    /**
     * Get today's step count
     */
    fun getTodaySteps(): Int {
        val today = getCurrentDate()
        val savedDate = prefs.getString(KEY_STEPS_DATE, "")

        return if (savedDate == today) {
            prefs.getInt(KEY_TODAY_STEPS, 0)
        } else {
            // New day, reset steps
            0
        }
    }

    // ==================== Dark Mode ====================

    /**
     * Enable or disable dark mode
     */
    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    /**
     * Check if dark mode is enabled
     */
    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    /**
     * Get current date string
     */
    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}