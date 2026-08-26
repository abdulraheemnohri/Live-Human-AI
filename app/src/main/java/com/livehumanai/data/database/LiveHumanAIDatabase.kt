package com.livehumanai.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.livehumanai.data.dao.*
import com.livehumanai.data.entity.*

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        MemoryEntity::class,
        ModelEntity::class,
        DownloadEntity::class,
        JalebiLoopEntity::class,
        JalebiIterationEntity::class,
        DeviceProfileEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class LiveHumanAIDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun memoryDao(): MemoryDao
    abstract fun modelDao(): ModelDao
    abstract fun downloadDao(): DownloadDao
    abstract fun jalebiLoopDao(): JalebiLoopDao
    abstract fun deviceProfileDao(): DeviceProfileDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: LiveHumanAIDatabase? = null

        fun getDatabase(context: Context): LiveHumanAIDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LiveHumanAIDatabase::class.java,
                    "livehumanai_database"
                )
                    .fallbackToDestructiveMigration() // For development; use proper migrations in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
