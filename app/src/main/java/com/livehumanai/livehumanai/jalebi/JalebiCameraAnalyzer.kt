package com.livehumanai.livehumanai.jalebi

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.concurrent.atomic.AtomicLong

/**
 * Lightweight CameraX analyzer. It throttles frames and forwards only a
 * semantic frame token to the JCL boundary. Heavy vision remains in the
 * existing VisionManager/native layer.
 */
class JalebiCameraAnalyzer(
    private val intervalMs: Long = 500L,
    private val onFrame: (FrameSignal) -> Unit
) : ImageAnalysis.Analyzer {
    private val lastTimestamp = AtomicLong(0L)

    override fun analyze(image: ImageProxy) {
        try {
            val now = System.currentTimeMillis()
            val previous = lastTimestamp.get()
            if (previous == 0L || now - previous >= intervalMs) {
                lastTimestamp.set(now)
                onFrame(FrameSignal(now, image.width, image.height, image.imageInfo.rotationDegrees))
            }
        } finally {
            image.close()
        }
    }

    data class FrameSignal(
        val timestampMs: Long,
        val width: Int,
        val height: Int,
        val rotationDegrees: Int
    )
}
