package com.example.healthflow.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.healthflow.database.converters.Converters

@Entity(tableName = "habits")
@TypeConverters(Converters::class)
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val createdAt: Long,
    val completionDates: List<String> // Room will use TypeConverter for this
)