package com.livehumanai.livehumanai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livehumanai.livehumanai.data.repository.AIRepository
import com.livehumanai.livehumanai.data.repository.ConversationRepository
import com.livehumanai.livehumanai.data.repository.MemoryRepository
import com.livehumanai.livehumanai.data.repository.ModelRepository
import com.livehumanai.livehumanai.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AIViewModel provides the business logic for AI operations.
 */
@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val conversationRepository: ConversationRepository,
    private val memoryRepository: MemoryRepository,
    private val modelRepository: ModelRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // State for AI operations
    private val _aiState = MutableStateFlow<AIState>(AIState.Idle)
    val aiState: StateFlow<AIState> = _aiState.asStateFlow()

    // State for current conversation
    private val _currentConversationId = MutableStateFlow<Long?>(null)
    val currentConversationId: StateFlow<Long?> = _currentConversationId.asStateFlow()

    // State for performance metrics
    private val _performanceMetrics = MutableStateFlow<PerformanceMetrics>(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()

    // State for loaded models
    private val _loadedModels = MutableStateFlow<List<String>>(emptyList())
    val loadedModels: StateFlow<List<String>> = _loadedModels.asStateFlow()

    // State for Jalebi Cognitive Loop (JCL)
    private val _jclState = MutableStateFlow<String>("IDLE")
    val jclState: StateFlow<String> = _jclState.asStateFlow()

    init {
        // Initialize the native runtime
        if (!aiRepository.initialize()) {
            _aiState.value = AIState.Error("Failed to initialize AI runtime")
        }

        // Load default settings
        viewModelScope.launch {
            settingsRepository.initializeDefaultSettings()
        }

        // Start monitoring performance
        startPerformanceMonitoring()
    }

    override fun onCleared() {
        super.onCleared()
        aiRepository.shutdown()
    }

    // AI operations

    fun generateResponse(
        prompt: String,
        conversationId: Long? = null,
        modelName: String = ""
    ) {
        viewModelScope.launch {
            try {
                _aiState.value = AIState.Thinking

                val temp = settingsRepository.getTemperature()
                val tokens = settingsRepository.getMaxTokens()

                val response = aiRepository.generate(prompt, modelName, temp, tokens)

                // Save to conversation if provided
                conversationId?.let { id ->
                    conversationRepository.addMessageToConversation(
                        conversationId = id,
                        content = response,
                        isUser = false
                    )
                }

                _aiState.value = AIState.Response(response)
            } catch (e: Exception) {
                _aiState.value = AIState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun stopGeneration() {
        aiRepository.stopGeneration()
        _aiState.value = AIState.Idle
    }

    // Jalebi Cognitive Loop (JCL) operations

    fun startJalebiLoop(goal: String, maxIterations: Int = 8) {
        viewModelScope.launch {
            val bridge = com.livehumanai.livehumanai.nativebridge.NativeBridge.getInstance()
            if (bridge.isInitialized) {
                val loopId = bridge.createJalebiLoop(goal, maxIterations)
                _jclState.value = bridge.getJalebiLoopState(loopId)
            }
        }
    }

    // Conversation operations

    suspend fun createNewConversation(title: String = "New Conversation"): Long {
        return conversationRepository.createConversation(title).also { id ->
            _currentConversationId.value = id
        }
    }

    suspend fun getConversation(conversationId: Long) {
        viewModelScope.launch {
            val (conversation, messages) = conversationRepository.getConversationById(conversationId)
            _currentConversationId.value = conversation?.id
            // TODO: Update conversation state
        }
    }

    suspend fun addMessageToConversation(content: String, isUser: Boolean) {
        _currentConversationId.value?.let { conversationId ->
            conversationRepository.addMessageToConversation(
                conversationId = conversationId,
                content = content,
                isUser = isUser
            )
        }
    }

    // Model operations

    fun loadModel(modelName: String) {
        viewModelScope.launch {
            if (aiRepository.loadModel(modelName)) {
                _loadedModels.value = _loadedModels.value + modelName
                // TODO: Update model state in database
            }
        }
    }

    fun unloadModel(modelName: String) {
        viewModelScope.launch {
            if (aiRepository.unloadModel(modelName)) {
                _loadedModels.value = _loadedModels.value - modelName
                // TODO: Update model state in database
            }
        }
    }

    // Memory operations

    suspend fun remember(content: String, title: String? = null) {
        memoryRepository.createMemory(
            content = content,
            title = title,
            isImportant = true
        )
    }

    suspend fun searchMemories(query: String): List<String> {
        return memoryRepository.searchMemories(query).map { it.content }
    }

    // Settings operations

    suspend fun getPerformanceMode(): String {
        return settingsRepository.getPerformanceMode()
    }

    suspend fun setPerformanceMode(mode: String) {
        settingsRepository.setPerformanceMode(mode)
        // Update native performance mode
        val nativeMode = when (mode) {
            "Battery Saver" -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.BATTERY_SAVER
            "Performance" -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.PERFORMANCE
            "Maximum" -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.MAXIMUM
            else -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.BALANCED
        }
        aiRepository.setPerformanceMode(nativeMode)
    }

    // Performance monitoring

    private fun startPerformanceMonitoring() {
        viewModelScope.launch {
            while (true) {
                val metrics = PerformanceMetrics(
                    cpuUsage = aiRepository.getCPUUsage(),
                    ramUsage = aiRepository.getRAMUsagePercentage(),
                    temperature = aiRepository.getTemperature(),
                    batteryLevel = aiRepository.getBatteryLevel(),
                    totalRAM = aiRepository.getTotalRAM(),
                    availableRAM = aiRepository.getAvailableRAM()
                )
                _performanceMetrics.value = metrics
                // Update every second
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    // State classes

    sealed class AIState {
        object Idle : AIState()
        object Thinking : AIState()
        data class Response(val text: String) : AIState()
        data class Error(val message: String) : AIState()
    }

    data class PerformanceMetrics(
        val cpuUsage: Float = 0f,
        val ramUsage: Float = 0f,
        val temperature: Float = 0f,
        val batteryLevel: Float = 0f,
        val totalRAM: Long = 0,
        val availableRAM: Long = 0
    ) {
        val isThermalCritical: Boolean get() = temperature >= 60
        val isThermalHot: Boolean get() = temperature >= 50
        val isBatteryLow: Boolean get() = batteryLevel <= 20
        val isRAMLow: Boolean get() = ramUsage >= 90
    }
}
