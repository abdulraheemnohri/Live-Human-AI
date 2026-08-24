package com.livehumanai.livehumanai.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.livehumanai.livehumanai.data.database.Converters
import java.util.Date

/**
 * MemoryEntity represents a stored memory in the database.
 */
@Entity(tableName = "memories")
@TypeConverters(Converters::class)
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val title: String? = null,
    val type: MemoryType = MemoryType.GENERAL,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isImportant: Boolean = false,
    val tags: List<String> = emptyList(),
    val metadata: String? = null // JSON string for additional metadata
) {
    enum class MemoryType {
        GENERAL,       // General knowledge
        FACT,          // Specific facts
        PREFERENCE,    // User preferences
        PROJECT,       // Project-related information
        CONVERSATION   // Conversation context
    }
}
