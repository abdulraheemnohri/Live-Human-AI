package com.livehumanai.data.repository

import com.livehumanai.data.dao.*
import com.livehumanai.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConversationRepository(
    private val conversationDao: ConversationDao
) {
    val allConversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()

    fun getConversationById(id: Long): Flow<ConversationEntity?> =
        conversationDao.getMessagesForConversation(id) // Simplified - would need separate query

    suspend fun createConversation(title: String): Long = withContext(Dispatchers.IO) {
        conversationDao.insertConversation(ConversationEntity(title = title))
    }

    suspend fun updateConversation(conversation: ConversationEntity) = withContext(Dispatchers.IO) {
        conversationDao.updateConversation(conversation)
    }

    suspend fun deleteConversation(id: Long) = withContext(Dispatchers.IO) {
        conversationDao.deleteConversationById(id)
    }

    suspend fun addMessage(
        conversationId: Long,
        role: String,
        content: String,
        modelUsed: String? = null,
        tokensUsed: Int = 0,
        latencyMs: Long = 0
    ): Long = withContext(Dispatchers.IO) {
        val messageId = conversationDao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                role = role,
                content = content,
                modelUsed = modelUsed,
                tokensUsed = tokensUsed,
                latencyMs = latencyMs
            )
        )
        conversationDao.updateConversationStats(
            conversationId = conversationId,
            wordAddition = content.split(" ").size,
            timestamp = System.currentTimeMillis()
        )
        messageId
    }

    fun getMessages(conversationId: Long): Flow<List<MessageEntity>> =
        conversationDao.getMessagesForConversation(conversationId)
}

class MemoryRepository(
    private val memoryDao: MemoryDao
) {
    val allMemories: Flow<List<MemoryEntity>> = memoryDao.getAllMemories()

    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>> =
        memoryDao.getMemoriesByCategory(category)

    suspend fun searchMemories(query: String): List<MemoryEntity> = withContext(Dispatchers.IO) {
        memoryDao.searchMemories(query)
    }

    suspend fun addMemory(
        content: String,
        category: String,
        importanceScore: Float = 0.5f,
        sourceContext: String? = null
    ): Long = withContext(Dispatchers.IO) {
        memoryDao.insertMemory(
            MemoryEntity(
                content = content,
                category = category,
                importanceScore = importanceScore,
                sourceContext = sourceContext
            )
        )
    }

    suspend fun updateMemory(memory: MemoryEntity) = withContext(Dispatchers.IO) {
        memoryDao.updateMemory(memory)
    }

    suspend fun deleteMemory(memory: MemoryEntity) = withContext(Dispatchers.IO) {
        memoryDao.deleteMemory(memory)
    }

    suspend fun getTopMemories(limit: Int = 10): List<MemoryEntity> = withContext(Dispatchers.IO) {
        memoryDao.getTopMemories(limit)
    }
}

class ModelRepository(
    private val modelDao: ModelDao
) {
    val allModels: Flow<List<ModelEntity>> = modelDao.getAllModels()
    val installedModels: Flow<List<ModelEntity>> = modelDao.getInstalledModels()

    fun getModelsByType(type: String): Flow<List<ModelEntity>> =
        modelDao.getModelsByType(type)

    suspend fun getModelById(id: String): ModelEntity? = withContext(Dispatchers.IO) {
        modelDao.getModelById(id)
    }

    suspend fun addModel(model: ModelEntity) = withContext(Dispatchers.IO) {
        modelDao.insertModel(model)
    }

    suspend fun updateModel(model: ModelEntity) = withContext(Dispatchers.IO) {
        modelDao.updateModel(model)
    }

    suspend fun deleteModel(model: ModelEntity) = withContext(Dispatchers.IO) {
        modelDao.deleteModel(model)
    }

    suspend fun updateDownloadProgress(id: String, progress: Float) = withContext(Dispatchers.IO) {
        modelDao.updateDownloadProgress(id, progress)
    }

    suspend fun markModelInstalled(id: String) = withContext(Dispatchers.IO) {
        modelDao.markModelInstalled(id, System.currentTimeMillis())
    }
}

class TaskRepository(
    private val taskDao: TaskDao
) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val activeTasks: Flow<List<TaskEntity>> = taskDao.getActiveTasks()

    suspend fun createTask(
        goal: String,
        priority: Int = 0,
        maxIterations: Int = 10
    ): Long = withContext(Dispatchers.IO) {
        taskDao.insertTask(
            TaskEntity(
                goal = goal,
                status = "queued",
                priority = priority,
                maxIterations = maxIterations
            )
        )
    }

    suspend fun updateTaskStatus(id: Long, status: String) = withContext(Dispatchers.IO) {
        taskDao.updateTaskStatus(id, status, if (status == "completed" || status == "failed") System.currentTimeMillis() else null)
    }

    suspend fun updateIteration(id: Long, iteration: Int) = withContext(Dispatchers.IO) {
        taskDao.updateIteration(id, iteration)
    }

    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
    }
}

class DeviceProfileRepository(
    private val deviceProfileDao: DeviceProfileDao
) {
    suspend fun getCurrentProfile(): DeviceProfileEntity? = withContext(Dispatchers.IO) {
        deviceProfileDao.getCurrentProfile()
    }

    suspend fun saveProfile(profile: DeviceProfileEntity) = withContext(Dispatchers.IO) {
        deviceProfileDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: DeviceProfileEntity) = withContext(Dispatchers.IO) {
        deviceProfileDao.updateProfile(profile)
    }
}
