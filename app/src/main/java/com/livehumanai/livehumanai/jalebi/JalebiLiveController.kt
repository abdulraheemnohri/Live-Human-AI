package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.data.repository.AIRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/** Android-side owner of a single bounded JCL session. */
@Singleton
class JalebiLiveController @Inject constructor(
    private val aiRepository: AIRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val lock = Any()
    private var job: Job? = null
    private var loopId: Int? = null
    private var running = false

    private val _state = MutableStateFlow(JalebiSessionState())
    val state: StateFlow<JalebiSessionState> = _state.asStateFlow()

    suspend fun startAsync(goal: String, maxIterations: Int = 8): Int? = synchronized(lock) {
        stopLocked()
        if (goal.isBlank()) return@synchronized null
        val boundedIterations = maxIterations.coerceIn(1, 32)
        val id = aiRepository.createJalebiLoop(goal.trim(), boundedIterations)
        if (id <= 0 || !aiRepository.startJalebiLoop(id)) {
            _state.value = JalebiSessionState(status = JalebiSessionStatus.FAILED, message = "Unable to start JCL")
            return@synchronized null
        }
        loopId = id
        running = true
        _state.value = JalebiSessionState(id, JalebiSessionStatus.RUNNING, 0f, goal.trim())
        id
    }

    fun start(goal: String, maxIterations: Int = 8): Int? {
        if (goal.isBlank()) return null
        return synchronized(lock) {
            stopLocked()
            val id = aiRepository.createJalebiLoop(goal.trim(), maxIterations.coerceIn(1, 32))
            if (id <= 0 || !aiRepository.startJalebiLoop(id)) {
                _state.value = JalebiSessionState(status = JalebiSessionStatus.FAILED, message = "Unable to start JCL")
                null
            } else {
                loopId = id
                running = true
                _state.value = JalebiSessionState(id, JalebiSessionStatus.RUNNING, 0f, goal.trim())
                id
            }
        }
    }

    fun stop() = synchronized(lock) { stopLocked() }

    private fun stopLocked() {
        running = false
        job?.cancel()
        job = null
        loopId?.let { aiRepository.cancelJalebiLoop(it) }
        loopId = null
        _state.value = _state.value.copy(loopId = null, status = JalebiSessionStatus.CANCELLED)
    }

    fun pause() = synchronized(lock) {
        loopId?.let { if (aiRepository.pauseJalebiLoop(it)) _state.value = _state.value.copy(status = JalebiSessionStatus.PAUSED) }
    }

    fun resume() = synchronized(lock) {
        loopId?.let { if (aiRepository.resumeJalebiLoop(it)) { running = true; _state.value = _state.value.copy(status = JalebiSessionStatus.RUNNING) } }
    }

    fun currentLoopId(): Int? = synchronized(lock) { loopId }
    fun isRunning(): Boolean = synchronized(lock) { running }

    fun submitPerception(input: String, evaluate: suspend (String) -> Evaluation) {
        val id = synchronized(lock) { if (running) loopId else null } ?: return
        if (input.isBlank()) return
        job?.cancel()
        job = scope.launch {
            if (aiRepository.shouldPauseJalebiForResources()) {
                aiRepository.pauseJalebiLoop(id)
                _state.value = _state.value.copy(status = JalebiSessionStatus.RESOURCE_LIMIT)
                return@launch
            }
            _state.value = _state.value.copy(status = JalebiSessionStatus.PROCESSING)
            if (!aiRepository.executeJalebiIteration(id, input)) return@launch
            val result = evaluate(input)
            val confidence = result.confidence.coerceIn(0f, 1f)
            aiRepository.evaluateJalebiLoop(id, confidence, result.completed, result.evidence, result.nextAction, result.memoryUpdates)
            _state.value = _state.value.copy(
                status = if (result.completed) JalebiSessionStatus.COMPLETED else JalebiSessionStatus.RUNNING,
                confidence = confidence,
                nextAction = result.nextAction
            )
            if (result.completed) synchronized(lock) { running = false }
        }
    }

    data class Evaluation(
        val confidence: Float,
        val completed: Boolean,
        val evidence: String,
        val nextAction: String,
        val memoryUpdates: String = ""
    )
}

enum class JalebiSessionStatus { IDLE, RUNNING, PROCESSING, PAUSED, RESOURCE_LIMIT, COMPLETED, FAILED, CANCELLED }

data class JalebiSessionState(
    val loopId: Int? = null,
    val status: JalebiSessionStatus = JalebiSessionStatus.IDLE,
    val confidence: Float = 0f,
    val goal: String = "",
    val nextAction: String = "",
    val message: String = ""
)
