package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.data.repository.AIRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** Android-side owner of exactly one bounded JCL session. */
@Singleton
class JalebiLiveController @Inject constructor(
    private val aiRepository: AIRepository,
    private val resources: JalebiResourceMonitor,
    private val telemetry: JalebiTelemetryStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val lock = Any()
    private var job: Job? = null
    private var loopId: Int? = null
    private var running = false

    private val _state = kotlinx.coroutines.flow.MutableStateFlow(JalebiSessionState())
    val state: kotlinx.coroutines.flow.StateFlow<JalebiSessionState> = _state

    suspend fun startAsync(goal: String, maxIterations: Int = 8): Int? = withContext(Dispatchers.Default) {
        synchronized(lock) { startLocked(goal, maxIterations) }
    }

    fun start(goal: String, maxIterations: Int = 8): Int? = synchronized(lock) {
        startLocked(goal, maxIterations)
    }

    private fun startLocked(goal: String, maxIterations: Int): Int? {
        stopLocked(updateState = false)
        val cleanGoal = goal.trim()
        if (cleanGoal.isEmpty()) {
            _state.value = JalebiSessionState(status = JalebiSessionStatus.FAILED, message = "Goal is empty")
            return null
        }

        val iterations = maxIterations.coerceIn(1, 32)
        val id = aiRepository.createJalebiLoop(cleanGoal, iterations)
        if (id <= 0 || !aiRepository.startJalebiLoop(id)) {
            _state.value = JalebiSessionState(status = JalebiSessionStatus.FAILED, message = "Unable to start JCL")
            return null
        }

        loopId = id
        running = true
        _state.value = JalebiSessionState(id, JalebiSessionStatus.RUNNING, 0f, cleanGoal)
        telemetry.record(JalebiTelemetryEvent(loopId = id, state = "RUNNING", iteration = 0, confidence = 0f, message = cleanGoal))
        return id
    }

    fun stop() = synchronized(lock) { stopLocked() }

    private fun stopLocked(updateState: Boolean = true) {
        val id = loopId
        running = false
        job?.cancel()
        job = null
        if (id != null) aiRepository.cancelJalebiLoop(id)
        loopId = null
        if (updateState) {
            _state.value = _state.value.copy(loopId = null, status = JalebiSessionStatus.CANCELLED)
            telemetry.record(JalebiTelemetryEvent(loopId = id, state = "CANCELLED", iteration = 0, confidence = _state.value.confidence))
        }
    }

    fun pause() = synchronized(lock) {
        loopId?.let { id ->
            if (aiRepository.pauseJalebiLoop(id)) {
                running = false
                _state.value = _state.value.copy(status = JalebiSessionStatus.PAUSED)
                telemetry.record(JalebiTelemetryEvent(loopId = id, state = "PAUSED", iteration = 0, confidence = _state.value.confidence))
            }
        }
    }

    fun resume() = synchronized(lock) {
        loopId?.let { id ->
            if (aiRepository.resumeJalebiLoop(id)) {
                running = true
                _state.value = _state.value.copy(status = JalebiSessionStatus.RUNNING)
                telemetry.record(JalebiTelemetryEvent(loopId = id, state = "RUNNING", iteration = 0, confidence = _state.value.confidence, message = "resumed"))
            }
        }
    }

    fun currentLoopId(): Int? = synchronized(lock) { loopId }
    fun isRunning(): Boolean = synchronized(lock) { running }

    /** Submits one semantic observation. Newer observations supersede an unfinished one. */
    fun submitPerception(input: String, evaluate: suspend (String) -> Evaluation) {
        val cleanInput = input.trim()
        if (cleanInput.isEmpty()) return

        val id: Int
        synchronized(lock) {
            id = loopId ?: return
            if (!running) return
            job?.cancel()
            _state.value = _state.value.copy(status = JalebiSessionStatus.PROCESSING)
            job = scope.launch { processObservation(id, cleanInput, evaluate) }
        }
    }

    private suspend fun processObservation(id: Int, input: String, evaluate: suspend (String) -> Evaluation) {
        val started = System.currentTimeMillis()
        try {
            val resourceSnapshot = resources.snapshot()
            if (!resourceSnapshot.safeForExpensiveInference) {
                aiRepository.pauseJalebiLoop(id)
                synchronized(lock) {
                    if (loopId == id) {
                        running = false
                        _state.value = _state.value.copy(status = JalebiSessionStatus.RESOURCE_LIMIT, message = "Device resources are constrained")
                    }
                }
                telemetry.record(JalebiTelemetryEvent(id, "RESOURCE_LIMIT", 0, _state.value.confidence, latencyMs = System.currentTimeMillis() - started))
                return
            }

            if (!aiRepository.executeJalebiIteration(id, input)) {
                markFailedIfCurrent(id, "JCL iteration rejected")
                return
            }

            val result = evaluate(input)
            val confidence = result.confidence.coerceIn(0f, 1f)
            val accepted = aiRepository.evaluateJalebiLoop(
                id, confidence, result.completed, result.evidence, result.nextAction, result.memoryUpdates
            )
            if (!accepted) {
                markFailedIfCurrent(id, "JCL evaluation rejected")
                return
            }

            synchronized(lock) {
                if (loopId != id) return
                if (result.completed) {
                    running = false
                    _state.value = _state.value.copy(status = JalebiSessionStatus.COMPLETED, confidence = confidence, nextAction = result.nextAction)
                } else {
                    _state.value = _state.value.copy(status = JalebiSessionStatus.RUNNING, confidence = confidence, nextAction = result.nextAction)
                }
            }
            telemetry.record(JalebiTelemetryEvent(
                loopId = id,
                state = if (result.completed) "COMPLETED" else "EVALUATING",
                iteration = 0,
                confidence = confidence,
                latencyMs = System.currentTimeMillis() - started,
                message = result.nextAction
            ))
        } catch (_: CancellationException) {
            // Superseded by a newer observation; do not turn cancellation into failure.
        } catch (t: Throwable) {
            markFailedIfCurrent(id, t.message ?: "Unexpected JCL error")
        }
    }

    private fun markFailedIfCurrent(id: Int, message: String) = synchronized(lock) {
        if (loopId != id) return
        running = false
        _state.value = _state.value.copy(status = JalebiSessionStatus.FAILED, message = message)
        telemetry.record(JalebiTelemetryEvent(id, "FAILED", 0, _state.value.confidence, message = message))
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
