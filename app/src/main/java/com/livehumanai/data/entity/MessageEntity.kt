package com.livehumanai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["conversationId"]), Index(value = ["createdAt"])]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: Long,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val contentType: String = "text", // "text", "image", "audio"
    val attachments: String? = null, // JSON array of attachment paths
    val createdAt: Date = Date(),
    val isFromMemory: Boolean = false,
    val confidence: Float = 1.0f,
    val modelUsed: String? = null,
    val metadata: String? = null // JSON metadata
)
