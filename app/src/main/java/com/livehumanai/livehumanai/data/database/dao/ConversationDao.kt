package com.livehumanai.livehumanai.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.livehumanai.livehumanai.data.database.entity.ConversationEntity
import com.livehumanai.livehumanai.data.database.entity.MessageEntity

/**
 * ConversationDao provides database operations for conversations.
 */
@Dao
interface ConversationDao {

    // Conversation operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: Long)

    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    suspend fun getAllConversations(): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: Long): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE isPinned = 1 ORDER BY updatedAt DESC")
    suspend fun getPinnedConversations(): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE isArchived = 1 ORDER BY updatedAt DESC")
    suspend fun getArchivedConversations(): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE title LIKE :query ORDER BY updatedAt DESC")
    suspend fun searchConversations(query: String): List<ConversationEntity>

    // Message operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: Long)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    suspend fun getMessagesByConversation(conversationId: Long): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND id = :messageId")
    suspend fun getMessageById(conversationId: Long, messageId: Long): MessageEntity?

    // Combined operations

    @Transaction
    suspend fun insertConversationWithMessages(
        conversation: ConversationEntity,
        messages: List<MessageEntity>
    ): Pair<Long, List<Long>> {
        val conversationId = insertConversation(conversation)
        val messageIds = messages.map { message ->
            insertMessage(message.copy(conversationId = conversationId))
        }
        return Pair(conversationId, messageIds)
    }

    @Transaction
    suspend fun getConversationWithMessages(id: Long): Pair<ConversationEntity?, List<MessageEntity>> {
        val conversation = getConversationById(id)
        val messages = getMessagesByConversation(id)
        return Pair(conversation, messages)
    }

    @Transaction
    suspend fun deleteConversationWithMessages(id: Long) {
        deleteConversation(id)
        // Messages will be automatically deleted due to CASCADE
    }

    // Utility queries

    @Query("SELECT COUNT(*) FROM conversations")
    suspend fun getConversationCount(): Int

    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId")
    suspend fun getMessageCount(conversationId: Long): Int

    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC LIMIT :limit")
    suspend fun getRecentConversations(limit: Int): List<ConversationEntity>
}
