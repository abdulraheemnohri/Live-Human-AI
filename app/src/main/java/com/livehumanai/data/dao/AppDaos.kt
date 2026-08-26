package com.livehumanai.data.dao

import androidx.room.*
import com.livehumanai.data.local.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: Long): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE isPinned = 1 ORDER BY updatedAt DESC")
    fun getPinnedConversations(): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: Long)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("UPDATE conversations SET wordCount = wordCount + :wordAddition, updatedAt = :timestamp WHERE id = :conversationId")
    suspend fun updateConversationStats(conversationId: Long, wordAddition: Int, timestamp: Long)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY lastAccessedAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE category = :category ORDER BY importanceScore DESC")
    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE content LIKE '%' || :query || '%' ORDER BY importanceScore DESC LIMIT 20")
    suspend fun searchMemories(query: String): List<MemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    @Query("UPDATE memories SET lastAccessedAt = :timestamp WHERE id = :id")
    suspend fun updateLastAccessed(id: Long, timestamp: Long)

    @Query("SELECT * FROM memories ORDER BY importanceScore DESC LIMIT :limit")
    suspend fun getTopMemories(limit: Int): List<MemoryEntity>
}

@Dao
interface ModelDao {
    @Query("SELECT * FROM models ORDER BY name ASC")
    fun getAllModels(): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models WHERE type = :type ORDER BY name ASC")
    fun getModelsByType(type: String): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models WHERE id = :id")
    suspend fun getModelById(id: String): ModelEntity?

    @Query("SELECT * FROM models WHERE isInstalled = 1 ORDER BY lastUsedAt DESC")
    fun getInstalledModels(): Flow<List<ModelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: ModelEntity)

    @Update
    suspend fun updateModel(model: ModelEntity)

    @Delete
    suspend fun deleteModel(model: ModelEntity)

    @Query("UPDATE models SET downloadProgress = :progress WHERE id = :id")
    suspend fun updateDownloadProgress(id: String, progress: Float)

    @Query("UPDATE models SET isInstalled = 1, installedAt = :timestamp WHERE id = :id")
    suspend fun markModelInstalled(id: String, timestamp: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status IN ('running', 'paused') ORDER BY priority DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET currentIteration = :iteration WHERE id = :id")
    suspend fun updateIteration(id: Long, iteration: Int)

    @Query("UPDATE tasks SET status = :status, completedAt = :timestamp WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: String, timestamp: Long?)
}

@Dao
interface DeviceProfileDao {
    @Query("SELECT * FROM device_profiles WHERE id = 'current_device'")
    suspend fun getCurrentProfile(): DeviceProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: DeviceProfileEntity)

    @Update
    suspend fun updateProfile(profile: DeviceProfileEntity)
}
