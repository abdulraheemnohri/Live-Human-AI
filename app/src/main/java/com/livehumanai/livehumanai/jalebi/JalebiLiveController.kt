package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.data.repository.AIRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android-side bounded JCL scheduler. Camera/mic producers feed semantic input
 * through submitPerception; this class never accesses hardware by itself.
 */
@Singleton
class JalebiLiveController @Inject constructor(
    private val aiRepository: AIRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var loopJob: Job? = null
    private var loopId: Int? = null
    private var running = false

    fun start(goal: String, maxIterations: Int = 8): Boolean {
        stop()
        val id = aiRepository.createJalebiLoop(goal, maxIterations)
        if (id <= 0 || !aiRepository.startJalebiLoop(id)) return false
        loopId = id
        running = true
        return true
    }

    fun stop() {
        running = false
        loopJob?.cancel()
        loopJob = null
        loopId?.let { aiRepository.cancelJalebiLoop(it) }
        loopId = null
    }

    fun pause() { loopId?.let { aiRepository.pauseJalebiLoop(it) } }
    fun resume() { loopId?.let { aiRepository.resumeJalebiLoop(it) } }

    fun submitPerception(input: String, evaluate: suspend (String) -> Evaluation) {
        val id = loopId ?: return
        if (!running || input.isBlank()) return
        loopJob?.cancel()
        loopJob = scope.launch {
            if (aiRepository.shouldPauseJalebiForResources()) {
                aiRepository.pauseJalebiLoop(id)
                return@launch
            }
            aiRepository.executeJalebiIteration(id, input)
            val result = evaluate(input)
            aiRepository.evaluateJalebiLoop(
                id,
                result.confidence.coerceIn(0f, 1f),
                result.completed,
                result.evidence,
                result.nextAction,
                result.memoryUpdates
            )
        }
    }

    fun startPolling(intervalMs: Long = 500L, producer: suspend () -> String) {
        loopJob?.cancel()
        loopJob = scope.launch {
            while (isActive && running) {
                val input = producer()
                if (input.isNotBlank()) submitPerception(input) { Evaluation(0f, false, "pending", "WAIT") }
                delay(intervalMs)
            }
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
