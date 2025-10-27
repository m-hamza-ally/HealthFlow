package com.example.healthflow.database.dao

import androidx.room.*
import com.example.healthflow.database.entities.SettingsEntity

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings WHERE `key` = :key")
    suspend fun getSetting(key: String): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingsEntity)

    @Query("DELETE FROM settings WHERE `key` = :key")
    suspend fun deleteSetting(key: String)

    @Query("DELETE FROM settings")
    suspend fun deleteAllSettings()
}