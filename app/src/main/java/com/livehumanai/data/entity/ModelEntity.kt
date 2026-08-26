package com.livehumanai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "models",
    indices = [Index(value = ["type"]), Index(value = ["status"])]
)
data class ModelEntity(
    @PrimaryKey
    val id: String, // Internal model ID
    val name: String,
    val type: String, // "llm", "stt", "tts", "vision"
    val repository: String, // Hugging Face repo
    val revision: String,
    val format: String, // "gguf", etc.
    val quantization: String, // "Q4", "Q5", etc.
    val sizeBytes: Long,
    val minimumRamGb: Int,
    val recommendedRamGb: Int,
    val status: String = "not_installed", // "not_installed", "downloading", "verifying", "ready", "error"
    val installPath: String? = null,
    val installedAt: Date? = null,
    val lastUsedAt: Date? = null,
    val sha256: String? = null,
    val license: String? = null,
    val isDefault: Boolean = false,
    val metadata: String? = null // JSON metadata
)
