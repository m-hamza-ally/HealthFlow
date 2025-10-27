package com.example.healthflow.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.healthflow.database.entities.MoodEntryEntity

@Dao
interface MoodDao {

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoodEntries(): LiveData<List<MoodEntryEntity>>

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    suspend fun getAllMoodEntriesSync(): List<MoodEntryEntity>

    @Query("SELECT * FROM mood_entries WHERE id = :moodId")
    suspend fun getMoodEntryById(moodId: String): MoodEntryEntity?

    @Query("SELECT * FROM mood_entries WHERE timestamp >= :startDate AND timestamp <= :endDate ORDER BY timestamp DESC")
    suspend fun getMoodEntriesByDateRange(startDate: Long, endDate: Long): List<MoodEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(moodEntry: MoodEntryEntity)

    @Delete
    suspend fun deleteMoodEntry(moodEntry: MoodEntryEntity)

    @Query("DELETE FROM mood_entries WHERE id = :moodId")
    suspend fun deleteMoodEntryById(moodId: String)

    @Query("DELETE FROM mood_entries")
    suspend fun deleteAllMoodEntries()
}