package com.livehumanai.livehumanai.di

import android.content.Context
import androidx.room.Room
import com.livehumanai.livehumanai.data.database.AppDatabase
import com.livehumanai.livehumanai.data.database.dao.ConversationDao
import com.livehumanai.livehumanai.data.database.dao.MemoryDao
import com.livehumanai.livehumanai.data.database.dao.ModelDao
import com.livehumanai.livehumanai.data.database.dao.SettingsDao
import com.livehumanai.livehumanai.data.repository.AIRepository
import com.livehumanai.livehumanai.data.repository.ConversationRepository
import com.livehumanai.livehumanai.data.repository.MemoryRepository
import com.livehumanai.livehumanai.data.repository.ModelRepository
import com.livehumanai.livehumanai.data.repository.SettingsRepository
import com.livehumanai.livehumanai.nativebridge.NativeBridge
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AppModule provides dependencies for the application using Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Database

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideConversationDao(database: AppDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    @Singleton
    fun provideMemoryDao(database: AppDatabase): MemoryDao {
        return database.memoryDao()
    }

    @Provides
    @Singleton
    fun provideModelDao(database: AppDatabase): ModelDao {
        return database.modelDao()
    }

    @Provides
    @Singleton
    fun provideSettingsDao(database: AppDatabase): SettingsDao {
        return database.settingsDao()
    }

    // Repositories

    @Provides
    @Singleton
    fun provideConversationRepository(conversationDao: ConversationDao): ConversationRepository {
        return ConversationRepository(conversationDao)
    }

    @Provides
    @Singleton
    fun provideMemoryRepository(memoryDao: MemoryDao): MemoryRepository {
        return MemoryRepository(memoryDao)
    }

    @Provides
    @Singleton
    fun provideHuggingFaceDownloader(): com.livehumanai.livehumanai.utils.HuggingFaceDownloader {
        return com.livehumanai.livehumanai.utils.HuggingFaceDownloader()
    }

    @Provides
    @Singleton
    fun provideModelRepository(
        modelDao: ModelDao,
        hfDownloader: com.livehumanai.livehumanai.utils.HuggingFaceDownloader
    ): ModelRepository {
        return ModelRepository(modelDao, hfDownloader)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsDao: SettingsDao): SettingsRepository {
        return SettingsRepository(settingsDao)
    }

    // Native Bridge

    @Provides
    @Singleton
    fun provideNativeBridge(): NativeBridge {
        return NativeBridge()
    }

    // AI Repository

    @Provides
    @Singleton
    fun provideAIRepository(nativeBridge: NativeBridge): AIRepository {
        return AIRepository(nativeBridge)
    }
}
