package com.livehumanai.livehumanai.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

/**
 * MessageEntity represents a message in a conversation.
 */
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
    indices = [Index(value = ["conversationId"])]
)
@TypeConverters(Converters::class)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val content: String,
    val isUser: Boolean, // true if the message is from the user, false if from AI
    val createdAt: Date = Date(),
    val metadata: String? = null // JSON string for additional metadata
)
