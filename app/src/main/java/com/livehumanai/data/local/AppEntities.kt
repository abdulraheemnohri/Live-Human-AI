package com.livehumanai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val wordCount: Int = 0
)

@Entity(
    tableName = "messages",
    indices = [Index(value = ["conversationId"])]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val hasAudio: Boolean = false,
    val audioPath: String? = null,
    val hasImage: Boolean = false,
    val imagePath: String? = null,
    val modelUsed: String? = null,
    val tokensUsed: Int = 0,
    val latencyMs: Long = 0
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val category: String, // "fact", "preference", "project", "person"
    val importanceScore: Float = 0.5f,
    val createdAt: Long = System.currentTimeMillis(),
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = true,
    val sourceContext: String? = null // e.g., "From chat #42"
)

@Entity(tableName = "models")
data class ModelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // "llm", "stt", "tts", "vision"
    val path: String,
    val sizeBytes: Long,
    val isInstalled: Boolean = false,
    val isDownloaded: Boolean = false,
    val downloadProgress: Float = 0f,
    val checksum: String? = null,
    val installedAt: Long? = null,
    val lastUsedAt: Long? = null,
    val benchmarkScore: Float? = null
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goal: String,
    val status: String, // "queued", "running", "paused", "completed", "failed"
    val priority: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val maxIterations: Int = 10,
    val currentIteration: Int = 0,
    val result: String? = null,
    val errorMessage: String? = null
)

@Entity(tableName = "device_profiles")
data class DeviceProfileEntity(
    @PrimaryKey val id: String = "current_device",
    val totalRamGb: Float,
    val availableRamGb: Float,
    val cpuCores: Int,
    val hasGpu: Boolean,
    val hasVulkan: Boolean,
    val hasNeon: Boolean,
    val storageFreeGb: Float,
    val recommendedProfile: String // "lite", "standard", "pro"
)
