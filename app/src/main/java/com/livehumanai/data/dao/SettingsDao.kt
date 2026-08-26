package com.livehumanai.data.dao

import androidx.room.*
import com.livehumanai.data.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings ORDER BY key")
    fun getAllSettings(): Flow<List<SettingsEntity>>

    @Query("SELECT * FROM settings WHERE key = :key LIMIT 1")
    suspend fun getSettingByKey(key: String): SettingsEntity?

    @Query("SELECT * FROM settings WHERE key = :key LIMIT 1")
    fun getSettingByKeyFlow(key: String): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingsEntity)

    @Update
    suspend fun updateSetting(setting: SettingsEntity)

    @Delete
    suspend fun deleteSetting(setting: SettingsEntity)

    @Query("DELETE FROM settings WHERE key = :key")
    suspend fun deleteSettingByKey(key: String)

    @Query("SELECT value FROM settings WHERE key = :key LIMIT 1")
    suspend fun getSettingValue(key: String): String?

    @Query("SELECT value FROM settings WHERE key = :key LIMIT 1")
    fun getSettingValueFlow(key: String): Flow<String?>

    suspend fun setSettingValue(key: String, value: String, type: String = "string") {
        insertSetting(SettingsEntity(key = key, value = value, type = type))
    }
}
