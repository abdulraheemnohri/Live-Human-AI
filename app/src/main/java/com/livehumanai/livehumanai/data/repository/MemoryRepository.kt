package com.livehumanai.livehumanai.data.repository

import com.livehumanai.livehumanai.data.database.dao.MemoryDao
import com.livehumanai.livehumanai.data.database.entity.MemoryEntity
import javax.inject.Inject

/**
 * MemoryRepository provides data access for AI memories.
 */
class MemoryRepository @Inject constructor(
    private val memoryDao: MemoryDao
) {

    suspend fun createMemory(
        content: String,
        title: String? = null,
        type: MemoryEntity.MemoryType = MemoryEntity.MemoryType.GENERAL,
        isImportant: Boolean = false,
        tags: List<String> = emptyList()
    ): Long {
        val memory = MemoryEntity(
            content = content,
            title = title,
            type = type,
            isImportant = isImportant,
            tags = tags
        )
        return memoryDao.insertMemory(memory)
    }

    suspend fun updateMemory(memory: MemoryEntity) {
        memoryDao.updateMemory(memory)
    }

    suspend fun deleteMemory(id: Long) {
        memoryDao.deleteMemory(id)
    }

    suspend fun getMemoryById(id: Long): MemoryEntity? {
        return memoryDao.getMemoryById(id)
    }

    suspend fun getAllMemories(): List<MemoryEntity> {
        return memoryDao.getAllMemories()
    }

    suspend fun getImportantMemories(): List<MemoryEntity> {
        return memoryDao.getImportantMemories()
    }

    suspend fun getMemoriesByType(type: MemoryEntity.MemoryType): List<MemoryEntity> {
        return memoryDao.getMemoriesByType(type)
    }

    suspend fun searchMemories(query: String): List<MemoryEntity> {
        return memoryDao.searchMemories("%$query%")
    }

    suspend fun getMemoriesByTag(tag: String): List<MemoryEntity> {
        return memoryDao.getMemoriesByTag("%$tag%")
    }

    suspend fun getRecentMemories(limit: Int = 20): List<MemoryEntity> {
        return memoryDao.getAllMemories().take(limit)
    }

    suspend fun toggleMemoryImportance(id: Long): Boolean {
        val memory = memoryDao.getMemoryById(id) ?: return false
        memoryDao.updateMemory(
            memory.copy(isImportant = !memory.isImportant)
        )
        return true
    }

    suspend fun updateMemoryTags(id: Long, newTags: List<String>): Boolean {
        val memory = memoryDao.getMemoryById(id) ?: return false
        memoryDao.updateMemory(
            memory.copy(tags = newTags)
        )
        return true
    }

    suspend fun getMemoryCount(): Int {
        return memoryDao.getMemoryCount()
    }

    suspend fun cleanupOldMemories(days: Int): Int {
        val timestamp = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000)
        return memoryDao.deleteMemoriesBefore(timestamp)
    }
}
