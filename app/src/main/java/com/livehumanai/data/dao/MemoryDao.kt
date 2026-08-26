package com.livehumanai.data.dao

import androidx.room.*
import com.livehumanai.data.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE category = :category ORDER BY createdAt DESC")
    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isApproved = 1 ORDER BY createdAt DESC")
    fun getApprovedMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE id = :id")
    suspend fun getMemoryById(id: Long): MemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("UPDATE memories SET isApproved = :isApproved, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateMemoryApproval(id: Long, isApproved: Boolean, updatedAt: java.util.Date)

    @Query("SELECT * FROM memories WHERE content LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchMemories(query: String): Flow<List<MemoryEntity>>

    @Query("SELECT COUNT(*) FROM memories")
    fun getMemoryCount(): Flow<Int>
}
