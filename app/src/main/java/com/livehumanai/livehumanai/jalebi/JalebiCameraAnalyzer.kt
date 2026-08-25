package com.livehumanai.livehumanai.jalebi

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min

/**
 * Cheap CameraX gate. Raw frames are never retained. A sampled Y-plane
 * checksum suppresses duplicate frames before expensive semantic vision.
 */
class JalebiCameraAnalyzer(
    private val intervalMs: Long = 500L,
    private val onFrame: (FrameSignal) -> Unit
) : ImageAnalysis.Analyzer {
    private val lastTimestamp = AtomicLong(0L)
    private var lastChecksum = 0L

    override fun analyze(image: ImageProxy) {
        try {
            val now = System.currentTimeMillis()
            val previous = lastTimestamp.get()
            if (previous != 0L && now - previous < intervalMs) return

            val checksum = sampleLuma(image)
            val changed = lastChecksum == 0L || checksum != lastChecksum
            if (changed) {
                lastTimestamp.set(now)
                lastChecksum = checksum
                onFrame(FrameSignal(
                    timestampMs = now,
                    timestampNs = image.imageInfo.timestamp,
                    width = image.width,
                    height = image.height,
                    rotationDegrees = image.imageInfo.rotationDegrees,
                    checksum = checksum,
                    changed = true
                ))
            } else {
                lastTimestamp.set(now)
            }
        } finally {
            image.close()
        }
    }

    private fun sampleLuma(image: ImageProxy): Long {
        val plane = image.planes.firstOrNull() ?: return 0L
        val buffer = plane.buffer.duplicate()
        val available = buffer.remaining()
        if (available <= 0) return 0L
        val samples = min(available, 2048)
        val step = maxOf(1, available / samples)
        var hash = 1469598103934665603L
        var offset = 0
        repeat(samples) {
            if (offset >= available) return@repeat
            buffer.position(offset)
            hash = (hash xor (buffer.get().toLong() and 0xffL)) * 1099511628211L
            offset += step
        }
        return hash
    }

    data class FrameSignal(
        val timestampMs: Long,
        val timestampNs: Long,
        val width: Int,
        val height: Int,
        val rotationDegrees: Int,
        val checksum: Long,
        val changed: Boolean
    )
}
