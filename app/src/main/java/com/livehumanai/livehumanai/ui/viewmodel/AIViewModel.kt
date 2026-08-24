package com.livehumanai.livehumanai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livehumanai.livehumanai.data.repository.AIRepository
import com.livehumanai.livehumanai.data.repository.ConversationRepository
import com.livehumanai.livehumanai.data.repository.MemoryRepository
import com.livehumanai.livehumanai.data.repository.ModelRepository
import com.livehumanai.livehumanai.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AIViewModel provides the business logic for AI operations and the bounded
 * Jalebi Cognitive Loop orchestration state used by the UI.
 */
@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val conversationRepository: ConversationRepository,
    private val memoryRepository: MemoryRepository,
    private val modelRepository: ModelRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _aiState = MutableStateFlow<AIState>(AIState.Idle)
    val aiState: StateFlow<AIState> = _aiState.asStateFlow()

    private val _currentConversationId = MutableStateFlow<Long?>(null)
    val currentConversationId: StateFlow<Long?> = _currentConversationId.asStateFlow()

    private val _performanceMetrics = MutableStateFlow<PerformanceMetrics>(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()

    private val _loadedModels = MutableStateFlow<List<String>>(emptyList())
    val loadedModels: StateFlow<List<String>> = _loadedModels.asStateFlow()

    // JCL developer/diagnostics state.
    private val _jclLoopId = MutableStateFlow<Int?>(null)
    val jclLoopId: StateFlow<Int?> = _jclLoopId.asStateFlow()

    private val _jclState = MutableStateFlow("IDLE")
    val jclState: StateFlow<String> = _jclState.asStateFlow()

    private val _jclIteration = MutableStateFlow(0)
    val jclIteration: StateFlow<Int> = _jclIteration.asStateFlow()

    private val _jclConfidence = MutableStateFlow(0f)
    val jclConfidence: StateFlow<Float> = _jclConfidence.asStateFlow()

    private val _jclHistoryJson = MutableStateFlow("[]")
    val jclHistoryJson: StateFlow<String> = _jclHistoryJson.asStateFlow()

    init {
        if (!aiRepository.initialize()) {
            _aiState.value = AIState.Error("Failed to initialize AI runtime")
        }

        viewModelScope.launch { settingsRepository.initializeDefaultSettings() }
        startPerformanceMonitoring()
    }

    override fun onCleared() {
        aiRepository.shutdown()
        super.onCleared()
    }

    fun generateResponse(prompt: String, conversationId: Long? = null, modelName: String = "") {
        viewModelScope.launch {
            try {
                _aiState.value = AIState.Thinking
                val response = aiRepository.generate(
                    prompt,
                    modelName,
                    settingsRepository.getTemperature(),
                    settingsRepository.getMaxTokens()
                )
                conversationId?.let { id ->
                    conversationRepository.addMessageToConversation(id, response, false)
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

    // -------------------------------------------------------------------------
    // Jalebi Cognitive Loop operations
    // -------------------------------------------------------------------------

    fun startJalebiLoop(goal: String, maxIterations: Int = 8) {
        viewModelScope.launch {
            if (aiRepository.getRuntimeStatus().isEmpty()) return@launch
            val loopId = aiRepository.createJalebiLoop(goal, maxIterations)
            if (loopId <= 0 || !aiRepository.startJalebiLoop(loopId)) {
                _jclState.value = "FAILED"
                return@launch
            }
            _jclLoopId.value = loopId
            refreshJalebiLoop(loopId)
        }
    }

    fun pauseJalebiLoop() = updateJclControl { aiRepository.pauseJalebiLoop(it) }
    fun resumeJalebiLoop() = updateJclControl { aiRepository.resumeJalebiLoop(it) }
    fun cancelJalebiLoop() = updateJclControl { aiRepository.cancelJalebiLoop(it) }

    /** Execute one perception/action cycle; actual model/tool work remains external. */
    fun executeJalebiIteration(input: String) {
        viewModelScope.launch {
            _jclLoopId.value?.let { id ->
                aiRepository.executeJalebiIteration(id, input)
                refreshJalebiLoop(id)
            }
        }
    }

    fun evaluateJalebiLoop(
        confidence: Float,
        goalCompleted: Boolean,
        evaluation: String,
        nextAction: String,
        memoryUpdates: String = ""
    ) {
        viewModelScope.launch {
            _jclLoopId.value?.let { id ->
                aiRepository.evaluateJalebiLoop(
                    id, confidence, goalCompleted, evaluation, nextAction, memoryUpdates
                )
                refreshJalebiLoop(id)
            }
        }
    }

    fun refreshJalebiLoop(loopId: Int? = _jclLoopId.value) {
        val id = loopId ?: return
        _jclLoopId.value = id
        _jclState.value = aiRepository.getJalebiLoopState(id)
        _jclIteration.value = aiRepository.getJalebiIteration(id)
        _jclConfidence.value = aiRepository.getJalebiConfidence(id)
        _jclHistoryJson.value = aiRepository.getJalebiHistory(id)
    }

    private fun updateJclControl(action: (Int) -> Boolean) {
        viewModelScope.launch {
            _jclLoopId.value?.let { id ->
                action(id)
                refreshJalebiLoop(id)
            }
        }
    }

    // Conversation operations
    suspend fun createNewConversation(title: String = "New Conversation"): Long =
        conversationRepository.createConversation(title).also { _currentConversationId.value = it }

    suspend fun getConversation(conversationId: Long) {
        val (conversation, _) = conversationRepository.getConversationById(conversationId)
        _currentConversationId.value = conversation?.id
    }

    suspend fun addMessageToConversation(content: String, isUser: Boolean) {
        _currentConversationId.value?.let {
            conversationRepository.addMessageToConversation(it, content, isUser)
        }
    }

    // Model operations
    fun loadModel(modelName: String) {
        viewModelScope.launch {
            if (aiRepository.loadModel(modelName)) _loadedModels.value = _loadedModels.value + modelName
        }
    }

    fun unloadModel(modelName: String) {
        viewModelScope.launch {
            if (aiRepository.unloadModel(modelName)) _loadedModels.value = _loadedModels.value - modelName
        }
    }

    // Memory operations
    suspend fun remember(content: String, title: String? = null) {
        memoryRepository.createMemory(content = content, title = title, isImportant = true)
    }

    suspend fun searchMemories(query: String): List<String> =
        memoryRepository.searchMemories(query).map { it.content }

    // Settings operations
    suspend fun getPerformanceMode(): String = settingsRepository.getPerformanceMode()

    suspend fun setPerformanceMode(mode: String) {
        settingsRepository.setPerformanceMode(mode)
        val nativeMode = when (mode) {
            "Battery Saver" -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.BATTERY_SAVER
            "Performance" -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.PERFORMANCE
            "Maximum" -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.MAXIMUM
            else -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.BALANCED
        }
        aiRepository.setPerformanceMode(nativeMode)
    }

    private fun startPerformanceMonitoring() {
        viewModelScope.launch {
            while (true) {
                _performanceMetrics.value = PerformanceMetrics(
                    cpuUsage = aiRepository.getCPUUsage(),
                    ramUsage = aiRepository.getRAMUsagePercentage(),
                    temperature = aiRepository.getTemperature(),
                    batteryLevel = aiRepository.getBatteryLevel(),
                    totalRAM = aiRepository.getTotalRAM(),
                    availableRAM = aiRepository.getAvailableRAM()
                )
                delay(1000)
            }
        }
    }

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
