package com.example.healthflow.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.healthflow.database.entities.HabitEntity

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): LiveData<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    suspend fun getAllHabitsSync(): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: String): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: String)

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
}