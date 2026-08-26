package com.livehumanai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "jalebi_loops",
    indices = [Index(value = ["status"]), Index(value = ["createdAt"])]
)
data class JalebiLoopEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goal: String,
    val status: String = "queued", // "queued", "running", "paused", "completed", "failed", "cancelled"
    val createdAt: Date = Date(),
    val startedAt: Date? = null,
    val endedAt: Date? = null,
    val iterationCount: Int = 0,
    val maxIterations: Int = 8,
    val success: Boolean = false,
    val failureReason: String? = null,
    val modelUsed: String? = null,
    val confidence: Float = 0.0f,
    val memoryBudgetBytes: Long = 0,
    val tokenBudget: Int = 0,
    val toolCallLimit: Int = 0,
    val actualToolCalls: Int = 0,
    val metadata: String? = null // JSON metadata
)
