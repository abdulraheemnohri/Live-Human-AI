package com.livehumanai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "memories",
    indices = [
        Index(value = ["category"]),
        Index(value = ["createdAt"]),
        Index(value = ["isApproved"])
    ]
)
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val category: String = "general", // "preference", "fact", "project", "important", "custom"
    val source: String = "user", // "user", "conversation", "document", "vision"
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isApproved: Boolean = true,
    val confidence: Float = 1.0f,
    val embeddingId: Long? = null, // Reference to embedding if using semantic memory
    val metadata: String? = null // JSON metadata
)
