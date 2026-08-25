package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.nativebridge.NativeBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Android/native boundary for semantic vision. */
interface JalebiVisionBridge {
    suspend fun analyze(signal: JalebiCameraAnalyzer.FrameSignal): JalebiVisionResult?
}

/** Real bridge: changed CameraX RGBA frame -> JNI/OpenCV -> VisionManager. */
class NativeJalebiVisionBridge(
    private val native: NativeBridge = NativeBridge.getInstance(),
    private val modelName: String = ""
) : JalebiVisionBridge {
    override suspend fun analyze(signal: JalebiCameraAnalyzer.FrameSignal): JalebiVisionResult? = withContext(Dispatchers.Default) {
        if (!native.isInitialized || signal.rgba.isEmpty()) return@withContext null
        val json = native.analyzeVisionRgba(signal.rgba, signal.width, signal.height, modelName)
        parseResult(json, signal)
    }

    private fun parseResult(json: String, signal: JalebiCameraAnalyzer.FrameSignal): JalebiVisionResult? {
        if (json.isBlank()) return null
        fun string(key: String): String = Regex("\\\"$key\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").find(json)?.groupValues?.get(1) ?: ""
        fun number(key: String): Float = Regex("\\\"$key\\\"\\s*:\\s*([0-9.]+)").find(json)?.groupValues?.get(1)?.toFloatOrNull() ?: 0f
        fun array(key: String): List<String> = Regex("\\\"$key\\\"\\s*:\\s*\\[([^]]*)]").find(json)?.groupValues?.get(1)?.split(',')?.mapNotNull {
            it.trim().removePrefix("\\\"").removeSuffix("\\\"").takeIf(String::isNotBlank)
        } ?: emptyList()
        return JalebiVisionResult(string("sceneId").ifBlank { signal.timestampNs.toString() }, array("objects"), array("text"), number("confidence").coerceIn(0f, 1f), signal.timestampMs)
    }
}

class NoOpJalebiVisionBridge : JalebiVisionBridge {
    override suspend fun analyze(signal: JalebiCameraAnalyzer.FrameSignal): JalebiVisionResult? = null
}
