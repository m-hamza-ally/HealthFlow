package com.example.healthflow.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey
    val id: String,
    val mood: String, // Store as String: "HAPPY", "SAD", etc.
    val note: String,
    val timestamp: Long
)