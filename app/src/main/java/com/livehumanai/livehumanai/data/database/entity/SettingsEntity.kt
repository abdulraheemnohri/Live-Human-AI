package com.livehumanai.livehumanai.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.livehumanai.livehumanai.data.database.Converters

/**
 * SettingsEntity represents app settings in the database.
 */
@Entity(tableName = "settings")
@TypeConverters(Converters::class)
data class SettingsEntity(
    @PrimaryKey val key: String, // Setting key is the primary key
    val value: String, // JSON string for complex values
    val type: SettingsType = SettingsType.STRING,
    val description: String? = null
) {
    enum class SettingsType {
        STRING,
        BOOLEAN,
        INTEGER,
        FLOAT,
        JSON
    }

    companion object {
        // AI Settings
        const val DEFAULT_MODEL = "default_model"
        const val PERFORMANCE_MODE = "performance_mode"
        const val TEMPERATURE = "temperature"
        const val TOP_P = "top_p"
        const val MAX_TOKENS = "max_tokens"

        // Voice Settings
        const val WAKE_WORD_ENABLED = "wake_word_enabled"
        const val MICROPHONE_ENABLED = "microphone_enabled"
        const val STT_MODEL = "stt_model"
        const val TTS_MODEL = "tts_model"
        const val VOICE = "voice"
        const val SPEECH_SPEED = "speech_speed"
        const val SPEECH_PITCH = "speech_pitch"

        // Vision Settings
        const val CAMERA_ENABLED = "camera_enabled"
        const val OBJECT_DETECTION_ENABLED = "object_detection_enabled"
        const val OCR_ENABLED = "ocr_enabled"
        const val VISION_MODEL = "vision_model"

        // Memory Settings
        const val MEMORY_ENABLED = "memory_enabled"
        const val MEMORY_RETENTION_DAYS = "memory_retention_days"

        // Network Settings
        const val NETWORK_ENABLED = "network_enabled"

        // Privacy Settings
        const val ANALYTICS_ENABLED = "analytics_enabled"
    }
}
