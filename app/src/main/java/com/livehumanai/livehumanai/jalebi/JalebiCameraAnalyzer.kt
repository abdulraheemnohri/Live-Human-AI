package com.livehumanai.livehumanai.jalebi

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min

/** CameraX gate with runtime FPS/resolution policy and cheap scene-change detection. */
class JalebiCameraAnalyzer(
    private val governor: JalebiRuntimeGovernor = JalebiRuntimeGovernor(),
    private val onFrame: (FrameSignal) -> Unit
) : ImageAnalysis.Analyzer {
    private val lastTimestamp = AtomicLong(0L)
    private var lastChecksum = 0L

    override fun analyze(image: ImageProxy) {
        try {
            val limits = governor.limits()
            if (!limits.allowVision) return
            val now = System.currentTimeMillis()
            val interval = (1000L / limits.cameraFps.coerceAtLeast(1)).coerceAtLeast(1L)
            val previous = lastTimestamp.get()
            if (previous != 0L && now - previous < interval) return

            val checksum = sampleLuma(image)
            if (lastChecksum != 0L && checksum == lastChecksum) return
            lastTimestamp.set(now)
            lastChecksum = checksum

            val rgba = imageToRgba(image, limits.cameraWidth, limits.cameraHeight) ?: return
            onFrame(FrameSignal(now, image.imageInfo.timestamp, limits.cameraWidth, limits.cameraHeight,
                image.imageInfo.rotationDegrees, checksum, true, rgba))
        } finally { image.close() }
    }

    private fun sampleLuma(image: ImageProxy): Long {
        val plane = image.planes.firstOrNull() ?: return 0L
        val buffer = plane.buffer.duplicate(); val available = buffer.remaining()
        if (available <= 0) return 0L
        val samples = min(available, 2048); val step = maxOf(1, available / samples)
        var hash = 1469598103934665603L; var offset = 0
        repeat(samples) {
            if (offset >= available) return@repeat
            buffer.position(offset)
            hash = (hash xor (buffer.get().toLong() and 0xffL)) * 1099511628211L
            offset += step
        }
        return hash
    }

    private fun imageToRgba(image: ImageProxy, targetW: Int, targetH: Int): ByteArray? {
        if (image.format != android.graphics.ImageFormat.YUV_420_888) return null
        val w = image.width; val h = image.height
        val outW = min(w, targetW.coerceAtLeast(1)); val outH = min(h, targetH.coerceAtLeast(1))
        val out = ByteArray(outW * outH * 4)
        val y = image.planes[0]; val u = image.planes[1]; val v = image.planes[2]
        val yb = y.buffer.duplicate(); val ub = u.buffer.duplicate(); val vb = v.buffer.duplicate()
        var o = 0
        for (row in 0 until outH) {
            val sy = (row * h) / outH; val yRow = sy * y.rowStride; val uvRow = (sy shr 1) * u.rowStride
            for (col in 0 until outW) {
                val sx = (col * w) / outW
                val yy = yb.get(yRow + sx * y.pixelStride).toInt() and 255
                val uvCol = (sx shr 1) * u.pixelStride
                val uu = (ub.get(uvRow + uvCol).toInt() and 255) - 128
                val vv = (vb.get((sy shr 1) * v.rowStride + uvCol).toInt() and 255) - 128
                out[o++] = (yy + 1.402f * vv).toInt().coerceIn(0, 255).toByte()
                out[o++] = (yy - 0.344136f * uu - 0.714136f * vv).toInt().coerceIn(0, 255).toByte()
                out[o++] = (yy + 1.772f * uu).toInt().coerceIn(0, 255).toByte(); out[o++] = 255.toByte()
            }
        }
        return out
    }

    data class FrameSignal(val timestampMs: Long, val timestampNs: Long, val width: Int, val height: Int,
        val rotationDegrees: Int, val checksum: Long, val changed: Boolean, val rgba: ByteArray)
}