package com.livehumanai.livehumanai.data.repository

import com.livehumanai.livehumanai.data.database.dao.ConversationDao
import com.livehumanai.livehumanai.data.database.entity.ConversationEntity
import com.livehumanai.livehumanai.data.database.entity.MessageEntity
import javax.inject.Inject

/**
 * ConversationRepository provides data access for conversations.
 */
class ConversationRepository @Inject constructor(
    private val conversationDao: ConversationDao
) {

    // Conversation operations

    suspend fun createConversation(title: String = "New Conversation"): Long {
        val conversation = ConversationEntity(
            title = title
        )
        return conversationDao.insertConversation(conversation)
    }

    suspend fun updateConversation(conversation: ConversationEntity) {
        conversationDao.updateConversation(conversation)
    }

    suspend fun deleteConversation(id: Long) {
        conversationDao.deleteConversationWithMessages(id)
    }

    suspend fun getConversationById(id: Long): Pair<ConversationEntity?, List<MessageEntity>> {
        return conversationDao.getConversationWithMessages(id)
    }

    suspend fun getAllConversations(): List<ConversationEntity> {
        return conversationDao.getAllConversations()
    }

    suspend fun getPinnedConversations(): List<ConversationEntity> {
        return conversationDao.getPinnedConversations()
    }

    suspend fun getArchivedConversations(): List<ConversationEntity> {
        return conversationDao.getArchivedConversations()
    }

    suspend fun searchConversations(query: String): List<ConversationEntity> {
        return conversationDao.searchConversations("%$query%")
    }

    // Message operations

    suspend fun addMessageToConversation(
        conversationId: Long,
        content: String,
        isUser: Boolean
    ): Long {
        val message = MessageEntity(
            conversationId = conversationId,
            content = content,
            isUser = isUser
        )
        return conversationDao.insertMessage(message)
    }

    suspend fun updateMessage(message: MessageEntity) {
        conversationDao.updateMessage(message)
    }

    suspend fun deleteMessage(id: Long) {
        conversationDao.deleteMessage(id)
    }

    suspend fun getMessagesByConversation(conversationId: Long): List<MessageEntity> {
        return conversationDao.getMessagesByConversation(conversationId)
    }

    // Utility functions

    suspend fun getConversationCount(): Int {
        return conversationDao.getConversationCount()
    }

    suspend fun getRecentConversations(limit: Int = 10): List<ConversationEntity> {
        return conversationDao.getRecentConversations(limit)
    }

    suspend fun toggleConversationPin(id: Long): Boolean {
        val conversation = conversationDao.getConversationById(id) ?: return false
        conversationDao.updateConversation(
            conversation.copy(isPinned = !conversation.isPinned)
        )
        return true
    }

    suspend fun toggleConversationArchive(id: Long): Boolean {
        val conversation = conversationDao.getConversationById(id) ?: return false
        conversationDao.updateConversation(
            conversation.copy(isArchived = !conversation.isArchived)
        )
        return true
    }

    suspend fun renameConversation(id: Long, newTitle: String): Boolean {
        val conversation = conversationDao.getConversationById(id) ?: return false
        conversationDao.updateConversation(
            conversation.copy(title = newTitle)
        )
        return true
    }
}
