package com.livehumanai.livehumanai.jalebi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Connects throttled CameraX signals to semantic vision and then JCL. */
class JalebiLiveVisionPipeline(
    private val visionBridge: JalebiVisionBridge,
    private val controller: JalebiLiveController,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private var job: Job? = null

    fun onFrame(signal: JalebiCameraAnalyzer.FrameSignal) {
        job?.cancel()
        job = scope.launch {
            val result = visionBridge.analyze(signal) ?: return@launch
            controller.submitPerception(result.toPerceptionInput()) {
                JalebiLiveController.Evaluation(
                    confidence = result.confidence,
                    completed = false,
                    evidence = result.sceneSummary.ifBlank { result.toPerceptionInput() },
                    nextAction = "CONTINUE"
                )
            }
        }
    }

    fun stop() { job?.cancel(); job = null }
}
