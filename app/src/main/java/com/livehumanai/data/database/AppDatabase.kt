package com.livehumanai.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.livehumanai.data.dao.*
import com.livehumanai.data.local.*

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        MemoryEntity::class,
        ModelEntity::class,
        TaskEntity::class,
        DeviceProfileEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun memoryDao(): MemoryDao
    abstract fun modelDao(): ModelDao
    abstract fun taskDao(): TaskDao
    abstract fun deviceProfileDao(): DeviceProfileDao

    companion object {
        const val DATABASE_NAME = "livehumanai_database"
    }
}
