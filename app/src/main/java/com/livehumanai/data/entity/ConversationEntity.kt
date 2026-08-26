package com.livehumanai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "conversations",
    indices = [Index(value = ["createdAt"])]
)
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val messageCount: Int = 0,
    val isArchived: Boolean = false,
    val metadata: String? = null // JSON metadata
)
