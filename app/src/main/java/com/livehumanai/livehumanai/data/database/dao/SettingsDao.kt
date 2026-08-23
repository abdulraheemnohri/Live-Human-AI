package com.livehumanai.livehumanai.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.livehumanai.livehumanai.data.database.entity.SettingsEntity

/**
 * SettingsDao provides database operations for app settings.
 */
@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingsEntity)

    @Update
    suspend fun updateSetting(setting: SettingsEntity)

    @Query("DELETE FROM settings WHERE key = :key")
    suspend fun deleteSetting(key: String)

    @Query("SELECT * FROM settings WHERE key = :key")
    suspend fun getSettingByKey(key: String): SettingsEntity?

    @Query("SELECT * FROM settings")
    suspend fun getAllSettings(): List<SettingsEntity>

    @Query("SELECT COUNT(*) FROM settings")
    suspend fun getSettingCount(): Int

    // Convenience methods for common setting types

    @Query("SELECT value FROM settings WHERE key = :key AND type = 'BOOLEAN'")
    suspend fun getBooleanSetting(key: String): Boolean?

    @Query("SELECT value FROM settings WHERE key = :key AND type = 'INTEGER'")
    suspend fun getIntegerSetting(key: String): Int?

    @Query("SELECT value FROM settings WHERE key = :key AND type = 'FLOAT'")
    suspend fun getFloatSetting(key: String): Float?

    @Query("SELECT value FROM settings WHERE key = :key AND type = 'STRING'")
    suspend fun getStringSetting(key: String): String?

    // Batch operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSettings(settings: List<SettingsEntity>)

    @Query("DELETE FROM settings")
    suspend fun deleteAllSettings()

    // Default settings

    @Query("""
        INSERT OR IGNORE INTO settings (key, value, type, description)
        VALUES
        ('default_model', 'qwen3-1.7b-q4', 'STRING', 'Default AI model'),
        ('performance_mode', 'BALANCED', 'STRING', 'Performance mode'),
        ('temperature', '0.7', 'FLOAT', 'AI temperature'),
        ('top_p', '0.9', 'FLOAT', 'Top-p sampling'),
        ('max_tokens', '512', 'INTEGER', 'Maximum tokens'),
        ('wake_word_enabled', 'true', 'BOOLEAN', 'Wake word detection'),
        ('microphone_enabled', 'true', 'BOOLEAN', 'Microphone access'),
        ('stt_model', 'whisper-base', 'STRING', 'STT model'),
        ('tts_model', 'piper-en', 'STRING', 'TTS model'),
        ('voice', 'default', 'STRING', 'TTS voice'),
        ('speech_speed', '1.0', 'FLOAT', 'Speech speed'),
        ('speech_pitch', '1.0', 'FLOAT', 'Speech pitch'),
        ('camera_enabled', 'true', 'BOOLEAN', 'Camera access'),
        ('object_detection_enabled', 'true', 'BOOLEAN', 'Object detection'),
        ('ocr_enabled', 'true', 'BOOLEAN', 'OCR'),
        ('vision_model', 'yolo-nano', 'STRING', 'Vision model'),
        ('memory_enabled', 'true', 'BOOLEAN', 'Memory enabled'),
        ('memory_retention_days', '30', 'INTEGER', 'Memory retention days'),
        ('network_enabled', 'false', 'BOOLEAN', 'Network access'),
        ('analytics_enabled', 'false', 'BOOLEAN', 'Analytics enabled')
    """)
    suspend fun insertDefaultSettings()
}
