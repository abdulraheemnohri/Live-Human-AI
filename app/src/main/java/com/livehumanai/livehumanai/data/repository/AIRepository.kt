package com.livehumanai.livehumanai.data.repository

import com.livehumanai.livehumanai.jalebi.JalebiModelRouter
import com.livehumanai.livehumanai.jalebi.JalebiRuntimeGovernor
import com.livehumanai.livehumanai.nativebridge.NativeBridge
import javax.inject.Inject

/** Repository boundary for native AI and the bounded Jalebi Cognitive Loop. */
class AIRepository @Inject constructor(
    private val nativeBridge: NativeBridge
) {
    private val governor = JalebiRuntimeGovernor(nativeBridge)
    private val modelRouter = JalebiModelRouter(nativeBridge, governor)

    fun initialize(): Boolean = nativeBridge.initialize()
    fun shutdown() = nativeBridge.shutdown()
    fun getVersion(): String = nativeBridge.getVersion()
    fun getRuntimeStatus(): String = nativeBridge.getRuntimeStatus()
    fun getDeviceProfile(): String = nativeBridge.getDeviceProfile()
    fun loadModel(modelName: String): Boolean = nativeBridge.loadModel(modelName)
    fun unloadModel(modelName: String): Boolean = nativeBridge.unloadModel(modelName)

    /** Resource-governed generation; model escalation/fallback is handled by the router. */
    fun generate(prompt: String, modelName: String = "", temperature: Float = 0.7f, maxTokens: Int = 512): String {
        val route = modelRouter.route(if (modelName.isBlank()) getRecommendedLLMModel() else modelName, maxTokens)
        if (!route.allowed) return ""
        return nativeBridge.generate(prompt, route.modelName, temperature, route.maxTokens)
    }
    fun routeModel(modelName: String, maxTokens: Int = 512): JalebiModelRouter.Route = modelRouter.route(modelName, maxTokens)
    fun stopGeneration() = nativeBridge.stopGeneration()
    fun getTotalRAM(): Long = nativeBridge.getTotalRAM()
    fun getAvailableRAM(): Long = nativeBridge.getAvailableRAM()
    fun getRAMUsagePercentage(): Float = nativeBridge.getRAMUsagePercentage()
    fun getCPUUsage(): Float = nativeBridge.getCPUUsage()
    fun getTemperature(): Float = nativeBridge.getTemperature()
    fun getBatteryLevel(): Float = nativeBridge.getBatteryLevel()
    fun setPerformanceMode(mode: NativeBridge.PerformanceMode) = nativeBridge.setPerformanceMode(mode)
    fun getPerformanceMode(): NativeBridge.PerformanceMode = nativeBridge.getPerformanceMode()

    fun createJalebiLoop(goal: String, maxIterations: Int = 8): Int = nativeBridge.createJalebiLoop(goal, maxIterations)
    fun startJalebiLoop(loopId: Int): Boolean = nativeBridge.startJalebiLoop(loopId)
    fun pauseJalebiLoop(loopId: Int): Boolean = nativeBridge.pauseJalebiLoop(loopId)
    fun resumeJalebiLoop(loopId: Int): Boolean = nativeBridge.resumeJalebiLoop(loopId)
    fun cancelJalebiLoop(loopId: Int): Boolean = nativeBridge.cancelJalebiLoop(loopId)
    fun completeJalebiLoop(loopId: Int): Boolean = nativeBridge.completeJalebiLoop(loopId)
    fun failJalebiLoop(loopId: Int, reason: String): Boolean = nativeBridge.failJalebiLoop(loopId, reason)
    fun executeJalebiIteration(loopId: Int, input: String): String = nativeBridge.executeJalebiIteration(loopId, input)
    fun evaluateJalebiLoop(loopId: Int, confidence: Float, goalCompleted: Boolean, evaluation: String, nextAction: String, memoryUpdates: String = ""): Boolean = nativeBridge.evaluateJalebiLoop(loopId, confidence, goalCompleted, evaluation, nextAction, memoryUpdates)
    fun getJalebiLoopState(loopId: Int): String = nativeBridge.getJalebiLoopState(loopId)
    fun getJalebiConfidence(loopId: Int): Float = nativeBridge.getJalebiConfidence(loopId)
    fun getJalebiIteration(loopId: Int): Int = nativeBridge.getJalebiIteration(loopId)
    fun getJalebiHistory(loopId: Int): String = nativeBridge.getJalebiHistory(loopId)

    fun submitJalebiVision(loopId: Int, sceneId: String, objects: List<String>, text: List<String>, confidence: Float, flagshipDevice: Boolean = isHighEndDevice()): String = nativeBridge.submitJalebiVision(loopId, sceneId, objects, text, confidence, flagshipDevice)
    fun submitJalebiSpeech(loopId: Int, transcript: String, confidence: Float, isFinal: Boolean, flagshipDevice: Boolean = isHighEndDevice()): String = nativeBridge.submitJalebiSpeech(loopId, transcript, confidence, isFinal, flagshipDevice)

    fun shouldPauseJalebiForResources(): Boolean = governor.snapshot().mode.name == "CRITICAL"
    fun shouldReduceJalebiWorkload(): Boolean = governor.snapshot().mode.name != "NORMAL"
    fun formatBytes(bytes: Long): String = when { bytes >= 1024 * 1024 * 1024 -> "%.2f GB".format(bytes / (1024f * 1024 * 1024)); bytes >= 1024 * 1024 -> "%.2f MB".format(bytes / (1024f * 1024)); bytes >= 1024 -> "%.2f KB".format(bytes / 1024f); else -> "$bytes B" }
    fun formatPercentage(value: Float): String = "%.1f%%".format(value)
    fun formatTemperature(celsius: Float): String = "%.1f°C".format(celsius)
    fun isLowEndDevice(): Boolean = governor.snapshot().tier.name == "LOW_6GB"
    fun isHighEndDevice(): Boolean = governor.snapshot().tier.name == "HIGH_16GB"
    fun getRecommendedLLMModel(): String = when { isLowEndDevice() -> "qwen3-0.6b-q4"; isHighEndDevice() -> "qwen3-4b-q4"; else -> "qwen3-1.7b-q4" }
    fun getRecommendedSTTModel(): String = "whisper-base"
    fun getRecommendedTTSModel(): String = "piper-en"
    fun getRecommendedVisionModel(): String = if (isHighEndDevice()) "mobilenet-v3" else "yolo-nano"
}