package com.livehumanai.livehumanai.data.repository

import com.livehumanai.livehumanai.jalebi.JalebiConfidenceEscalator
import com.livehumanai.livehumanai.jalebi.JalebiModelRouter
import com.livehumanai.livehumanai.jalebi.JalebiRuntimeGovernor
import com.livehumanai.livehumanai.nativebridge.NativeBridge
import javax.inject.Inject

class AIRepository @Inject constructor(private val nativeBridge: NativeBridge) {
    private val governor = JalebiRuntimeGovernor(nativeBridge)
    private val modelRouter = JalebiModelRouter(nativeBridge, governor)
    private val confidenceEscalator = JalebiConfidenceEscalator(nativeBridge, governor)
    fun initialize() = nativeBridge.initialize()
    fun shutdown() = nativeBridge.shutdown()
    fun getVersion() = nativeBridge.getVersion()
    fun getRuntimeStatus() = nativeBridge.getRuntimeStatus()
    fun getDeviceProfile() = nativeBridge.getDeviceProfile()
    fun loadModel(modelName: String) = nativeBridge.loadModel(modelName)
    fun unloadModel(modelName: String) = nativeBridge.unloadModel(modelName)
    fun generate(prompt: String, modelName: String = "", temperature: Float = .7f, maxTokens: Int = 512): String { val route = modelRouter.route(if (modelName.isBlank()) getRecommendedLLMModel() else modelName, maxTokens); return if (route.allowed) nativeBridge.generate(prompt, route.modelName, temperature, route.maxTokens) else "" }
    fun generateWithConfidenceEscalation(prompt: String, modelName: String = getRecommendedLLMModel(), threshold: Float = .90f, maxEscalations: Int = 2, maxTokens: Int = 512, confidence: (String, String) -> Float) = confidenceEscalator.run(prompt, modelName, threshold, maxEscalations, maxTokens, confidence)
    fun routeModel(modelName: String, maxTokens: Int = 512) = modelRouter.route(modelName, maxTokens)
    fun stopGeneration() = nativeBridge.stopGeneration()
    fun getTotalRAM() = nativeBridge.getTotalRAM(); fun getAvailableRAM() = nativeBridge.getAvailableRAM(); fun getRAMUsagePercentage() = nativeBridge.getRAMUsagePercentage(); fun getCPUUsage() = nativeBridge.getCPUUsage(); fun getTemperature() = nativeBridge.getTemperature(); fun getBatteryLevel() = nativeBridge.getBatteryLevel()
    fun setPerformanceMode(mode: NativeBridge.PerformanceMode) = nativeBridge.setPerformanceMode(mode)
    fun getPerformanceMode() = nativeBridge.getPerformanceMode()
    fun createJalebiLoop(goal: String, maxIterations: Int = 8) = nativeBridge.createJalebiLoop(goal, maxIterations)
    fun startJalebiLoop(loopId: Int) = nativeBridge.startJalebiLoop(loopId); fun pauseJalebiLoop(loopId: Int) = nativeBridge.pauseJalebiLoop(loopId); fun resumeJalebiLoop(loopId: Int) = nativeBridge.resumeJalebiLoop(loopId); fun replanJalebiLoop(loopId: Int, reason: String = "low_confidence") = nativeBridge.replanJalebiLoop(loopId, reason); fun cancelJalebiLoop(loopId: Int) = nativeBridge.cancelJalebiLoop(loopId)
    fun completeJalebiLoop(loopId: Int) = nativeBridge.completeJalebiLoop(loopId); fun failJalebiLoop(loopId: Int, reason: String) = nativeBridge.failJalebiLoop(loopId, reason)
    fun executeJalebiIteration(loopId: Int, input: String) = nativeBridge.executeJalebiIteration(loopId, input)
    fun evaluateJalebiLoop(loopId: Int, confidence: Float, goalCompleted: Boolean, evaluation: String, nextAction: String, memoryUpdates: String = "") = nativeBridge.evaluateJalebiLoop(loopId, confidence, goalCompleted, evaluation, nextAction, memoryUpdates)
    fun getJalebiLoopState(loopId: Int) = nativeBridge.getJalebiLoopState(loopId); fun getJalebiConfidence(loopId: Int) = nativeBridge.getJalebiConfidence(loopId); fun getJalebiIteration(loopId: Int) = nativeBridge.getJalebiIteration(loopId); fun getJalebiHistory(loopId: Int) = nativeBridge.getJalebiHistory(loopId)
    fun evaluateAndReplanIfNeeded(loopId: Int, confidence: Float, goalCompleted: Boolean, evaluation: String, nextAction: String, memoryUpdates: String = ""): Boolean { if (!evaluateJalebiLoop(loopId, confidence, goalCompleted, evaluation, nextAction, memoryUpdates)) return false; return goalCompleted || confidence >= .90f || replanJalebiLoop(loopId, "confidence_below_threshold") }
    fun submitJalebiVision(loopId: Int, sceneId: String, objects: List<String>, text: List<String>, confidence: Float, flagshipDevice: Boolean = isHighEndDevice()) = nativeBridge.submitJalebiVision(loopId, sceneId, objects, text, confidence, flagshipDevice)
    fun submitJalebiSpeech(loopId: Int, transcript: String, confidence: Float, isFinal: Boolean, flagshipDevice: Boolean = isHighEndDevice()) = nativeBridge.submitJalebiSpeech(loopId, transcript, confidence, isFinal, flagshipDevice)
    fun analyzeVisionRgba(rgba: ByteArray, width: Int, height: Int, modelName: String = getRecommendedVisionModel()) = nativeBridge.analyzeVisionRgba(rgba, width, height, modelName)
    fun shouldPauseJalebiForResources() = governor.snapshot().mode.name == "CRITICAL"
    fun shouldReduceJalebiWorkload() = governor.snapshot().mode.name != "NORMAL"
    fun formatBytes(bytes: Long) = when { bytes >= 1024*1024*1024 -> "%.2f GB".format(bytes/(1024f*1024*1024)); bytes >= 1024*1024 -> "%.2f MB".format(bytes/(1024f*1024)); bytes >= 1024 -> "%.2f KB".format(bytes/1024f); else -> "$bytes B" }
    fun formatPercentage(value: Float) = "%.1f%%".format(value); fun formatTemperature(celsius: Float) = "%.1f°C".format(celsius)
    fun isLowEndDevice() = governor.snapshot().tier.name == "LOW_6GB"; fun isHighEndDevice() = governor.snapshot().tier.name == "HIGH_16GB"
    fun getRecommendedLLMModel() = when { isLowEndDevice() -> "qwen3-0.6b-q4"; isHighEndDevice() -> "qwen3-4b-q4"; else -> "qwen3-1.7b-q4" }
    fun getRecommendedSTTModel() = "whisper-base"; fun getRecommendedTTSModel() = "piper-en"; fun getRecommendedVisionModel() = if (isHighEndDevice()) "mobilenet-v3" else "yolo-nano"
}
