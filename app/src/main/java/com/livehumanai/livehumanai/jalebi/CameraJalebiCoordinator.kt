package com.livehumanai.livehumanai.jalebi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/** Connects the cheap CameraX change detector to semantic JCL vision and the native loop. */
class CameraJalebiCoordinator(
    private val bridge: JalebiVisionBridge,
    private val loopId: () -> Int,
    private val nativeSubmit: (loopId: Int, result: JalebiVisionResult) -> String,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val onResult: (JalebiVisionResult, String) -> Unit = { _, _ -> }
) {
    private var job: Job? = null
    private val running = AtomicBoolean(false)

    fun start() { running.set(true) }

    fun stop() {
        running.set(false)
        job?.cancel()
        job = null
    }

    fun submit(signal: JalebiCameraAnalyzer.FrameSignal) {
        if (!running.get() || !signal.changed) return
        val id = loopId()
        if (id <= 0) return
        job?.cancel()
        job = scope.launch {
            val result = runCatching { bridge.analyze(signal) }.getOrNull() ?: return@launch
            if (!running.get() || loopId() != id) return@launch
            val nextAction = runCatching { nativeSubmit(id, result) }.getOrDefault("")
            if (running.get()) onResult(result, nextAction)
        }
    }
}

data class JalebiVisionResult(
    val sceneId: String,
    val objects: List<String>,
    val text: List<String>,
    val confidence: Float,
    val timestampMs: Long = System.currentTimeMillis()
)
