package com.example.healthflow.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.healthflow.database.entities.StepRecordEntity

@Dao
interface StepDao {

    @Query("SELECT * FROM step_records WHERE date = :date")
    suspend fun getStepRecordByDate(date: String): StepRecordEntity?

    @Query("SELECT * FROM step_records ORDER BY date DESC LIMIT 30")
    fun getLast30DaysSteps(): LiveData<List<StepRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepRecord(stepRecord: StepRecordEntity)

    @Update
    suspend fun updateStepRecord(stepRecord: StepRecordEntity)

    @Query("DELETE FROM step_records")
    suspend fun deleteAllStepRecords()
}