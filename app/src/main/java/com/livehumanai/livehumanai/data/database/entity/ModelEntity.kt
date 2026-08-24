package com.livehumanai.livehumanai.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.livehumanai.livehumanai.data.database.Converters
import java.util.Date

/**
 * ModelEntity represents an AI model in the database.
 */
@Entity(tableName = "models")
@TypeConverters(Converters::class)
data class ModelEntity(
    @PrimaryKey val name: String, // Model name is the primary key
    val version: String,
    val type: ModelType,
    val size: Long, // Size in bytes
    val format: String,
    val quantization: String? = null,
    val ramRequirement: Long, // RAM requirement in bytes
    val supportedLanguages: List<String> = emptyList(),
    val supportsVision: Boolean = false,
    val supportsAudio: Boolean = false,
    val license: String,
    val source: String,
    val checksum: String,
    val isInstalled: Boolean = false,
    val isLoaded: Boolean = false,
    val installedAt: Date? = null,
    val lastUsedAt: Date? = null,
    val downloadUrl: String? = null
) {
    enum class ModelType {
        LLM,      // Large Language Model
        STT,      // Speech-to-Text
        TTS,      // Text-to-Speech
        VISION,   // Vision model (object detection, OCR, etc.)
        EMBEDDING // Embedding model for semantic search
    }
}
