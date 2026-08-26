package com.livehumanai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "jalebi_iterations",
    indices = [Index(value = ["loopId"])]
)
data class JalebiIterationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val loopId: Long,
    val iterationNumber: Int,
    val phase: String, // "perceive", "interpret", "reason", "plan", "act", "observe", "evaluate", "update", "replan"
    val action: String? = null,
    val observation: String? = null,
    val confidence: Float = 0.0f,
    val createdAt: Date = Date(),
    val durationMs: Long = 0,
    val toolCalls: String? = null, // JSON array of tool calls
    val metadata: String? = null // JSON metadata
)
