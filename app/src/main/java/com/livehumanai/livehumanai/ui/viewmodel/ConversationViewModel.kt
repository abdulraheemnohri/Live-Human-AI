package com.livehumanai.livehumanai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livehumanai.livehumanai.data.repository.ConversationRepository
import com.livehumanai.livehumanai.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ConversationViewModel provides the business logic for conversation operations.
 */
@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // State for conversations
    private val _conversations = MutableStateFlow<List<ConversationState>>(emptyList())
    val conversations: StateFlow<List<ConversationState>> = _conversations.asStateFlow()

    // State for current conversation
    private val _currentConversation = MutableStateFlow<ConversationState?>(null)
    val currentConversation: StateFlow<ConversationState?> = _currentConversation.asStateFlow()

    // State for loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadConversations()
    }

    // Conversation operations

    fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val conversations = conversationRepository.getAllConversations()
                _conversations.value = conversations.map { conversation ->
                    ConversationState(
                        id = conversation.id,
                        title = conversation.title,
                        createdAt = conversation.createdAt,
                        updatedAt = conversation.updatedAt,
                        isPinned = conversation.isPinned,
                        isArchived = conversation.isArchived,
                        messages = emptyList()
                    )
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createNewConversation(title: String = "New Conversation") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val conversationId = conversationRepository.createConversation(title)
                val conversation = conversationRepository.getConversationById(conversationId).first
                conversation?.let { conv ->
                    val state = ConversationState(
                        id = conv.id,
                        title = conv.title,
                        createdAt = conv.createdAt,
                        updatedAt = conv.updatedAt,
                        isPinned = conv.isPinned,
                        isArchived = conv.isArchived,
                        messages = emptyList()
                    )
                    _currentConversation.value = state
                    _conversations.value = listOf(state) + _conversations.value
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadConversation(conversationId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (conversation, messages) = conversationRepository.getConversationById(conversationId)
                conversation?.let { conv ->
                    val state = ConversationState(
                        id = conv.id,
                        title = conv.title,
                        createdAt = conv.createdAt,
                        updatedAt = conv.updatedAt,
                        isPinned = conv.isPinned,
                        isArchived = conv.isArchived,
                        messages = messages.map { message ->
                            MessageState(
                                id = message.id,
                                content = message.content,
                                isUser = message.isUser,
                                createdAt = message.createdAt
                            )
                        }
                    )
                    _currentConversation.value = state
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteConversation(conversationId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                conversationRepository.deleteConversation(conversationId)
                _conversations.value = _conversations.value.filter { it.id != conversationId }
                if (_currentConversation.value?.id == conversationId) {
                    _currentConversation.value = null
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addMessageToConversation(content: String, isUser: Boolean) {
        viewModelScope.launch {
            _currentConversation.value?.let { conversation ->
                try {
                    conversationRepository.addMessageToConversation(
                        conversationId = conversation.id,
                        content = content,
                        isUser = isUser
                    )

                    // Update current conversation with new message
                    val messages = conversation.messages + MessageState(
                        id = 0, // Temporary ID, will be updated
                        content = content,
                        isUser = isUser,
                        createdAt = java.util.Date()
                    )

                    _currentConversation.value = conversation.copy(
                        messages = messages,
                        updatedAt = java.util.Date()
                    )

                    // Update in conversations list
                    _conversations.value = _conversations.value.map { conv ->
                        if (conv.id == conversation.id) {
                            conv.copy(
                                messages = messages,
                                updatedAt = java.util.Date()
                            )
                        } else {
                            conv
                        }
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    fun toggleConversationPin(conversationId: Long) {
        viewModelScope.launch {
            try {
                conversationRepository.toggleConversationPin(conversationId)
                _conversations.value = _conversations.value.map { conversation ->
                    if (conversation.id == conversationId) {
                        conversation.copy(isPinned = !conversation.isPinned)
                    } else {
                        conversation
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleConversationArchive(conversationId: Long) {
        viewModelScope.launch {
            try {
                conversationRepository.toggleConversationArchive(conversationId)
                _conversations.value = _conversations.value.map { conversation ->
                    if (conversation.id == conversationId) {
                        conversation.copy(isArchived = !conversation.isArchived)
                    } else {
                        conversation
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun renameConversation(conversationId: Long, newTitle: String) {
        viewModelScope.launch {
            try {
                conversationRepository.renameConversation(conversationId, newTitle)
                _conversations.value = _conversations.value.map { conversation ->
                    if (conversation.id == conversationId) {
                        conversation.copy(title = newTitle)
                    } else {
                        conversation
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun searchConversations(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val conversations = conversationRepository.searchConversations(query)
                _conversations.value = conversations.map { conversation ->
                    ConversationState(
                        id = conversation.id,
                        title = conversation.title,
                        createdAt = conversation.createdAt,
                        updatedAt = conversation.updatedAt,
                        isPinned = conversation.isPinned,
                        isArchived = conversation.isArchived,
                        messages = emptyList()
                    )
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    // State classes

    data class ConversationState(
        val id: Long,
        val title: String,
        val createdAt: java.util.Date,
        val updatedAt: java.util.Date,
        val isPinned: Boolean,
        val isArchived: Boolean,
        val messages: List<MessageState>
    )

    data class MessageState(
        val id: Long,
        val content: String,
        val isUser: Boolean,
        val createdAt: java.util.Date
    )
}
