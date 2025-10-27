package com.example.healthflow.database.repository

import androidx.lifecycle.LiveData
import com.example.healthflow.database.HealthFlowDatabase
import com.example.healthflow.database.entities.*
import com.example.healthflow.models.Habit
import com.example.healthflow.models.MoodEntry
import com.example.healthflow.models.MoodType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HealthFlowRepository(private val database: HealthFlowDatabase) {

    private val habitDao = database.habitDao()
    private val moodDao = database.moodDao()
    private val settingsDao = database.settingsDao()
    private val stepDao = database.stepDao()

    // ==================== Habits ====================

    suspend fun insertHabit(habit: Habit) = withContext(Dispatchers.IO) {
        val entity = HabitEntity(
            id = habit.id,
            name = habit.name,
            icon = habit.icon,
            color = habit.color,
            createdAt = habit.createdAt,
            completionDates = habit.completionDates
        )
        habitDao.insertHabit(entity)
    }

    suspend fun getAllHabits(): List<Habit> = withContext(Dispatchers.IO) {
        habitDao.getAllHabitsSync().map { entity ->
            Habit(
                id = entity.id,
                name = entity.name,
                icon = entity.icon,
                color = entity.color,
                createdAt = entity.createdAt,
                completionDates = entity.completionDates.toMutableList()
            )
        }
    }

    suspend fun updateHabit(habit: Habit) = withContext(Dispatchers.IO) {
        val entity = HabitEntity(
            id = habit.id,
            name = habit.name,
            icon = habit.icon,
            color = habit.color,
            createdAt = habit.createdAt,
            completionDates = habit.completionDates
        )
        habitDao.updateHabit(entity)
    }

    suspend fun deleteHabit(habitId: String) = withContext(Dispatchers.IO) {
        habitDao.deleteHabitById(habitId)
    }

    // ==================== Mood Entries ====================

    suspend fun insertMoodEntry(moodEntry: MoodEntry) = withContext(Dispatchers.IO) {
        val entity = MoodEntryEntity(
            id = moodEntry.id,
            mood = moodEntry.mood.name,
            note = moodEntry.note,
            timestamp = moodEntry.timestamp
        )
        moodDao.insertMoodEntry(entity)
    }

    suspend fun getAllMoodEntries(): List<MoodEntry> = withContext(Dispatchers.IO) {
        moodDao.getAllMoodEntriesSync().map { entity ->
            MoodEntry(
                id = entity.id,
                mood = MoodType.valueOf(entity.mood),
                note = entity.note,
                timestamp = entity.timestamp
            )
        }
    }

    suspend fun deleteMoodEntry(moodId: String) = withContext(Dispatchers.IO) {
        moodDao.deleteMoodEntryById(moodId)
    }

    // ==================== Settings ====================

    suspend fun saveSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        val entity = SettingsEntity(key = key, value = value)
        settingsDao.insertSetting(entity)
    }

    suspend fun getSetting(key: String): String? = withContext(Dispatchers.IO) {
        settingsDao.getSetting(key)?.value
    }

    // ==================== Steps ====================

    suspend fun saveStepRecord(date: String, steps: Int) = withContext(Dispatchers.IO) {
        val entity = StepRecordEntity(date = date, steps = steps)
        stepDao.insertStepRecord(entity)
    }

    suspend fun getStepRecordByDate(date: String): Int = withContext(Dispatchers.IO) {
        stepDao.getStepRecordByDate(date)?.steps ?: 0
    }
}