package com.example.healthflow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.healthflow.database.converters.Converters
import com.example.healthflow.database.dao.*
import com.example.healthflow.database.entities.*

@Database(
    entities = [
        HabitEntity::class,
        MoodEntryEntity::class,
        SettingsEntity::class,
        StepRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HealthFlowDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun moodDao(): MoodDao
    abstract fun settingsDao(): SettingsDao
    abstract fun stepDao(): StepDao

    companion object {
        @Volatile
        private var INSTANCE: HealthFlowDatabase? = null

        fun getDatabase(context: Context): HealthFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthFlowDatabase::class.java,
                    "healthflow_database"
                )
                    .fallbackToDestructiveMigration() // For development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}