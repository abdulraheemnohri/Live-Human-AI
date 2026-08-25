package com.livehumanai.livehumanai.jalebi

/**
 * Android/native boundary for semantic vision. A concrete CameraX-to-native
 * image conversion can be supplied by the existing VisionManager bridge;
 * this contract keeps JCL independent from Android ImageProxy internals.
 */
interface JalebiVisionBridge {
    suspend fun analyze(signal: JalebiCameraAnalyzer.FrameSignal): JalebiVisionResult?
}

class NoOpJalebiVisionBridge : JalebiVisionBridge {
    override suspend fun analyze(signal: JalebiCameraAnalyzer.FrameSignal): JalebiVisionResult? = null
}
