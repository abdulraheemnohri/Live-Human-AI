package com.livehumanai.livehumanai.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.livehumanai.livehumanai.data.database.entity.MemoryEntity

/**
 * MemoryDao provides database operations for memories.
 */
@Dao
interface MemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("SELECT * FROM memories ORDER BY updatedAt DESC")
    suspend fun getAllMemories(): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE id = :id")
    suspend fun getMemoryById(id: Long): MemoryEntity?

    @Query("SELECT * FROM memories WHERE isImportant = 1 ORDER BY updatedAt DESC")
    suspend fun getImportantMemories(): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE type = :type ORDER BY updatedAt DESC")
    suspend fun getMemoriesByType(type: MemoryEntity.MemoryType): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE title LIKE :query OR content LIKE :query ORDER BY updatedAt DESC")
    suspend fun searchMemories(query: String): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE tags LIKE :tag ORDER BY updatedAt DESC")
    suspend fun getMemoriesByTag(tag: String): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE createdAt >= :timestamp ORDER BY updatedAt DESC")
    suspend fun getMemoriesSince(timestamp: Long): List<MemoryEntity>

    @Query("DELETE FROM memories WHERE createdAt < :timestamp")
    suspend fun deleteMemoriesBefore(timestamp: Long): Int

    @Query("SELECT COUNT(*) FROM memories")
    suspend fun getMemoryCount(): Int

    @Query("SELECT COUNT(*) FROM memories WHERE type = :type")
    suspend fun getMemoryCountByType(type: MemoryEntity.MemoryType): Int
}
