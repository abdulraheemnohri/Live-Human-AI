package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.data.repository.AIRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/** Bounded Android-side JCL scheduler. Hardware producers feed semantic input here. */
@Singleton
class JalebiLiveController @Inject constructor(
    private val aiRepository: AIRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null
    private var loopId: Int? = null
    private var running = false

    fun start(goal: String, maxIterations: Int = 8): Int? {
        stop()
        val id = aiRepository.createJalebiLoop(goal, maxIterations)
        if (id <= 0 || !aiRepository.startJalebiLoop(id)) return null
        loopId = id
        running = true
        return id
    }

    fun stop() {
        running = false
        job?.cancel()
        job = null
        loopId?.let(aiRepository::cancelJalebiLoop)
        loopId = null
    }

    fun pause() { loopId?.let(aiRepository::pauseJalebiLoop) }
    fun resume() { loopId?.let(aiRepository::resumeJalebiLoop) }
    fun currentLoopId(): Int? = loopId
    fun isRunning(): Boolean = running

    fun submitPerception(input: String, evaluate: suspend (String) -> Evaluation) {
        val id = loopId ?: return
        if (!running || input.isBlank()) return
        job?.cancel()
        job = scope.launch {
            if (aiRepository.shouldPauseJalebiForResources()) {
                aiRepository.pauseJalebiLoop(id)
                return@launch
            }
            aiRepository.executeJalebiIteration(id, input)
            val result = evaluate(input)
            aiRepository.evaluateJalebiLoop(id, result.confidence.coerceIn(0f, 1f), result.completed,
                result.evidence, result.nextAction, result.memoryUpdates)
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
