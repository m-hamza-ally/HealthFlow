package com.example.healthflow.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_records")
data class StepRecordEntity(
    @PrimaryKey
    val date: String, // Format: "yyyy-MM-dd"
    val steps: Int
)