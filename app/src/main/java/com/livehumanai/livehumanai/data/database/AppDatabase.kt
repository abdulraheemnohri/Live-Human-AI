package com.livehumanai.livehumanai.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.livehumanai.livehumanai.data.database.dao.ConversationDao
import com.livehumanai.livehumanai.data.database.dao.MemoryDao
import com.livehumanai.livehumanai.data.database.dao.MessageDao
import com.livehumanai.livehumanai.data.database.dao.ModelDao
import com.livehumanai.livehumanai.data.database.dao.SettingsDao
import com.livehumanai.livehumanai.data.database.entity.ConversationEntity
import com.livehumanai.livehumanai.data.database.entity.MemoryEntity
import com.livehumanai.livehumanai.data.database.entity.MessageEntity
import com.livehumanai.livehumanai.data.database.entity.ModelEntity
import com.livehumanai.livehumanai.data.database.entity.SettingsEntity

/**
 * AppDatabase is the Room database for the Live Human AI app.
 * It stores conversations, messages, memories, models, and settings.
 */
@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        MemoryEntity::class,
        ModelEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun memoryDao(): MemoryDao
    abstract fun modelDao(): ModelDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        const val DATABASE_NAME = "live_human_ai_db"
    }
}
