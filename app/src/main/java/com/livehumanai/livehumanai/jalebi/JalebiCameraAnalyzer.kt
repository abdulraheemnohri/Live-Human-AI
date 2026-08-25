package com.livehumanai.livehumanai.jalebi

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min

/** Cheap CameraX gate. Raw frames are copied only for changed frames and released after delivery. */
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
            lastTimestamp.set(now)
            if (!changed) return
            lastChecksum = checksum

            val rgba = imageToRgba(image) ?: return
            onFrame(FrameSignal(now, image.imageInfo.timestamp, image.width, image.height,
                image.imageInfo.rotationDegrees, checksum, true, rgba))
        } finally { image.close() }
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

    /** Converts CameraX YUV_420_888 to tightly packed RGBA without retaining ImageProxy. */
    private fun imageToRgba(image: ImageProxy): ByteArray? {
        if (image.format != android.graphics.ImageFormat.YUV_420_888) return null
        val w = image.width; val h = image.height
        val out = ByteArray(w * h * 4)
        val y = image.planes[0]; val u = image.planes[1]; val v = image.planes[2]
        val yb = y.buffer.duplicate(); val ub = u.buffer.duplicate(); val vb = v.buffer.duplicate()
        var o = 0
        for (row in 0 until h) {
            val yRow = row * y.rowStride
            val uvRow = (row shr 1) * u.rowStride
            for (col in 0 until w) {
                val yy = yb.get(yRow + col * y.pixelStride).toInt() and 255
                val uvCol = (col shr 1) * u.pixelStride
                val uu = (ub.get(uvRow + uvCol).toInt() and 255) - 128
                val vv = (vb.get((row shr 1) * v.rowStride + uvCol).toInt() and 255) - 128
                val r = (yy + 1.402f * vv).toInt().coerceIn(0, 255)
                val g = (yy - 0.344136f * uu - 0.714136f * vv).toInt().coerceIn(0, 255)
                val b = (yy + 1.772f * uu).toInt().coerceIn(0, 255)
                out[o++] = r.toByte(); out[o++] = g.toByte(); out[o++] = b.toByte(); out[o++] = 255.toByte()
            }
        }
        return out
    }

    data class FrameSignal(
        val timestampMs: Long, val timestampNs: Long, val width: Int, val height: Int,
        val rotationDegrees: Int, val checksum: Long, val changed: Boolean,
        val rgba: ByteArray
    )
}
