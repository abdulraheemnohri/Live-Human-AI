package com.livehumanai.data.dao

import androidx.room.*
import com.livehumanai.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: Long): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getConversationByIdFlow(id: Long): Flow<ConversationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: Long)

    @Query("UPDATE conversations SET updatedAt = :updatedAt, messageCount = :messageCount WHERE id = :id")
    suspend fun updateConversationStats(id: Long, updatedAt: Date, messageCount: Int)

    @Query("SELECT COUNT(*) FROM conversations")
    fun getConversationCount(): Flow<Int>
}
