package com.livehumanai.livehumanai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livehumanai.livehumanai.data.repository.AIRepository
import com.livehumanai.livehumanai.data.repository.ConversationRepository
import com.livehumanai.livehumanai.data.repository.MemoryRepository
import com.livehumanai.livehumanai.data.repository.ModelRepository
import com.livehumanai.livehumanai.data.repository.SettingsRepository
import com.livehumanai.livehumanai.jalebi.JalebiLiveController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val conversationRepository: ConversationRepository,
    private val memoryRepository: MemoryRepository,
    private val modelRepository: ModelRepository,
    private val settingsRepository: SettingsRepository,
    private val jalebiLiveController: JalebiLiveController
) : ViewModel() {
    private val _aiState = MutableStateFlow<AIState>(AIState.Idle)
    val aiState: StateFlow<AIState> = _aiState.asStateFlow()
    private val _currentConversationId = MutableStateFlow<Long?>(null)
    val currentConversationId: StateFlow<Long?> = _currentConversationId.asStateFlow()
    private val _performanceMetrics = MutableStateFlow(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()
    private val _loadedModels = MutableStateFlow<List<String>>(emptyList())
    val loadedModels: StateFlow<List<String>> = _loadedModels.asStateFlow()
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
        if (!aiRepository.initialize()) _aiState.value = AIState.Error("Failed to initialize AI runtime")
        viewModelScope.launch { settingsRepository.initializeDefaultSettings() }
        startPerformanceMonitoring()
    }

    override fun onCleared() { jalebiLiveController.stop(); aiRepository.shutdown(); super.onCleared() }

    fun generateResponse(prompt: String, conversationId: Long? = null, modelName: String = "") {
        viewModelScope.launch {
            try {
                _aiState.value = AIState.Thinking
                val response = aiRepository.generate(prompt, modelName, settingsRepository.getTemperature(), settingsRepository.getMaxTokens())
                conversationId?.let { conversationRepository.addMessageToConversation(it, response, false) }
                _aiState.value = AIState.Response(response)
            } catch (e: Exception) { _aiState.value = AIState.Error(e.message ?: "Unknown error") }
        }
    }

    fun stopGeneration() { aiRepository.stopGeneration(); _aiState.value = AIState.Idle }

    fun startJalebiLoop(goal: String, maxIterations: Int = 8) {
        viewModelScope.launch {
            if (jalebiLiveController.start(goal, maxIterations)) {
                _jclLoopId.value = aiRepository.createJalebiLoop(goal, maxIterations).takeIf { it > 0 }
                _jclLoopId.value?.let { refreshJalebiLoop(it) }
            } else _jclState.value = "FAILED"
        }
    }

    fun startLiveJalebi(goal: String = "Continuously understand the current user context", maxIterations: Int = 8): Boolean {
        val started = jalebiLiveController.start(goal, maxIterations)
        if (started) viewModelScope.launch { delay(10); refreshJalebiLoop(_jclLoopId.value) }
        return started
    }

    fun submitLivePerception(input: String, evaluation: suspend (String) -> JalebiLiveController.Evaluation) {
        jalebiLiveController.submitPerception(input, evaluation)
        viewModelScope.launch { delay(25); refreshJalebiLoop(_jclLoopId.value) }
    }

    fun stopLiveJalebi() { jalebiLiveController.stop(); _jclState.value = "CANCELLED" }
    fun pauseJalebiLoop() { jalebiLiveController.pause(); refreshJalebiLoop() }
    fun resumeJalebiLoop() { jalebiLiveController.resume(); refreshJalebiLoop() }

    fun executeJalebiIteration(input: String) { _jclLoopId.value?.let { id -> aiRepository.executeJalebiIteration(id, input); refreshJalebiLoop(id) } }
    fun evaluateJalebiLoop(confidence: Float, goalCompleted: Boolean, evaluation: String, nextAction: String, memoryUpdates: String = "") {
        _jclLoopId.value?.let { id -> aiRepository.evaluateJalebiLoop(id, confidence, goalCompleted, evaluation, nextAction, memoryUpdates); refreshJalebiLoop(id) }
    }
    fun refreshJalebiLoop(loopId: Int? = _jclLoopId.value) { loopId?.let { _jclLoopId.value = it; _jclState.value = aiRepository.getJalebiLoopState(it); _jclIteration.value = aiRepository.getJalebiIteration(it); _jclConfidence.value = aiRepository.getJalebiConfidence(it); _jclHistoryJson.value = aiRepository.getJalebiHistory(it) } }

    suspend fun createNewConversation(title: String = "New Conversation"): Long = conversationRepository.createConversation(title).also { _currentConversationId.value = it }
    suspend fun getConversation(conversationId: Long) { _currentConversationId.value = conversationRepository.getConversationById(conversationId).first?.id }
    suspend fun addMessageToConversation(content: String, isUser: Boolean) { _currentConversationId.value?.let { conversationRepository.addMessageToConversation(it, content, isUser) } }
    fun loadModel(modelName: String) { viewModelScope.launch { if (aiRepository.loadModel(modelName)) _loadedModels.value += modelName } }
    fun unloadModel(modelName: String) { viewModelScope.launch { if (aiRepository.unloadModel(modelName)) _loadedModels.value -= modelName } }
    suspend fun remember(content: String, title: String? = null) { memoryRepository.createMemory(content = content, title = title, isImportant = true) }
    suspend fun searchMemories(query: String): List<String> = memoryRepository.searchMemories(query).map { it.content }
    suspend fun getPerformanceMode(): String = settingsRepository.getPerformanceMode()
    suspend fun setPerformanceMode(mode: String) { settingsRepository.setPerformanceMode(mode); aiRepository.setPerformanceMode(com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.values().getOrElse(if (mode == "Maximum") 3 else if (mode == "Performance") 2 else if (mode == "Battery Saver") 0 else 1) { com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.BALANCED }) }

    private fun startPerformanceMonitoring() = viewModelScope.launch {
        while (true) { _performanceMetrics.value = PerformanceMetrics(aiRepository.getCPUUsage(), aiRepository.getRAMUsagePercentage(), aiRepository.getTemperature(), aiRepository.getBatteryLevel(), aiRepository.getTotalRAM(), aiRepository.getAvailableRAM()); delay(1000) }
    }

    sealed class AIState { data object Idle : AIState(); data object Thinking : AIState(); data class Response(val text: String) : AIState(); data class Error(val message: String) : AIState() }
    data class PerformanceMetrics(val cpuUsage: Float = 0f, val ramUsage: Float = 0f, val temperature: Float = 0f, val batteryLevel: Float = 0f, val totalRAM: Long = 0, val availableRAM: Long = 0) {
        val isThermalCritical get() = temperature >= 60
        val isThermalHot get() = temperature >= 50
        val isBatteryLow get() = batteryLevel <= 20
        val isRAMLow get() = ramUsage >= 90
    }
}
