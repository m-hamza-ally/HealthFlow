package com.example.healthflow.data

import android.content.Context
import com.example.healthflow.database.HealthFlowDatabase
import com.example.healthflow.database.repository.HealthFlowRepository
import com.example.healthflow.models.Habit
import com.example.healthflow.models.MoodEntry
import kotlinx.coroutines.runBlocking

/**
 * Manager class bridging old SharedPreferences API with new Room Database
 */
class PreferencesManager private constructor(context: Context) {

    private val database = HealthFlowDatabase.getDatabase(context)
    private val repository = HealthFlowRepository(database)

    companion object {
        @Volatile
        private var instance: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: PreferencesManager(context.applicationContext).also { instance = it }
            }
        }
    }

    // ==================== Habits ====================

    fun getHabits(): List<Habit> = runBlocking {
        repository.getAllHabits()
    }

    fun addHabit(habit: Habit) = runBlocking {
        repository.insertHabit(habit)
    }

    fun updateHabit(updatedHabit: Habit) = runBlocking {
        repository.updateHabit(updatedHabit)
    }

    fun deleteHabit(habitId: String) = runBlocking {
        repository.deleteHabit(habitId)
    }

    fun toggleHabitCompletion(habitId: String) {
        val habits = getHabits()
        val habit = habits.find { it.id == habitId }
        habit?.let {
            updateHabit(it.toggleCompletion())
        }
    }

    // ==================== Mood Entries ====================

    fun getMoodEntries(): List<MoodEntry> = runBlocking {
        repository.getAllMoodEntries()
    }

    fun addMoodEntry(moodEntry: MoodEntry) = runBlocking {
        repository.insertMoodEntry(moodEntry)
    }

    fun deleteMoodEntry(moodId: String) = runBlocking {
        repository.deleteMoodEntry(moodId)
    }

    // ==================== Settings ====================

    fun setRemindersEnabled(enabled: Boolean) = runBlocking {
        repository.saveSetting("reminders_enabled", enabled.toString())
    }

    fun areRemindersEnabled(): Boolean = runBlocking {
        repository.getSetting("reminders_enabled")?.toBoolean() ?: false
    }

    fun setReminderInterval(minutes: Int) = runBlocking {
        repository.saveSetting("reminder_interval", minutes.toString())
    }

    fun getReminderInterval(): Int = runBlocking {
        repository.getSetting("reminder_interval")?.toInt() ?: 60
    }

    fun setDarkMode(enabled: Boolean) = runBlocking {
        repository.saveSetting("dark_mode", enabled.toString())
    }

    fun isDarkModeEnabled(): Boolean = runBlocking {
        repository.getSetting("dark_mode")?.toBoolean() ?: false
    }

    fun isFirstLaunch(): Boolean = runBlocking {
        val isFirst = repository.getSetting("first_launch")?.toBoolean() ?: true
        if (isFirst) {
            repository.saveSetting("first_launch", "false")
        }
        isFirst
    }

    // ==================== Step Counter ====================

    fun saveTodaySteps(steps: Int) = runBlocking {
        val today = getCurrentDate()
        repository.saveStepRecord(today, steps)
    }

    fun getTodaySteps(): Int = runBlocking {
        val today = getCurrentDate()
        repository.getStepRecordByDate(today)
    }

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}