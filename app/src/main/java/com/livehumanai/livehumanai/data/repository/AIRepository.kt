package com.livehumanai.livehumanai.data.repository

import com.livehumanai.livehumanai.nativebridge.NativeBridge
import javax.inject.Inject

/**
 * AIRepository provides access to AI functionality through the native bridge.
 */
class AIRepository @Inject constructor(
    private val nativeBridge: NativeBridge
) {

    // Initialization

    fun initialize(): Boolean {
        return nativeBridge.initialize()
    }

    fun shutdown() {
        nativeBridge.shutdown()
    }

    // Version and status

    fun getVersion(): String {
        return nativeBridge.getVersion()
    }

    fun getRuntimeStatus(): String {
        return nativeBridge.getRuntimeStatus()
    }

    fun getDeviceProfile(): String {
        return nativeBridge.getDeviceProfile()
    }

    // Model management

    fun loadModel(modelName: String): Boolean {
        return nativeBridge.loadModel(modelName)
    }

    fun unloadModel(modelName: String): Boolean {
        return nativeBridge.unloadModel(modelName)
    }

    // AI generation

    fun generate(
        prompt: String,
        modelName: String = "",
        temperature: Float = 0.7f,
        maxTokens: Int = 512
    ): String {
        return nativeBridge.generate(prompt, modelName, temperature, maxTokens)
    }

    fun stopGeneration() {
        nativeBridge.stopGeneration()
    }

    // Hardware monitoring

    fun getTotalRAM(): Long {
        return nativeBridge.getTotalRAM()
    }

    fun getAvailableRAM(): Long {
        return nativeBridge.getAvailableRAM()
    }

    fun getRAMUsagePercentage(): Float {
        return nativeBridge.getRAMUsagePercentage()
    }

    fun getCPUUsage(): Float {
        return nativeBridge.getCPUUsage()
    }

    fun getTemperature(): Float {
        return nativeBridge.getTemperature()
    }

    fun getBatteryLevel(): Float {
        return nativeBridge.getBatteryLevel()
    }

    // Performance mode

    fun setPerformanceMode(mode: NativeBridge.PerformanceMode) {
        nativeBridge.setPerformanceMode(mode)
    }

    fun getPerformanceMode(): NativeBridge.PerformanceMode {
        return nativeBridge.getPerformanceMode()
    }

    // Utility functions

    fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> "%.2f GB".format(bytes / (1024f * 1024 * 1024))
            bytes >= 1024 * 1024 -> "%.2f MB".format(bytes / (1024f * 1024))
            bytes >= 1024 -> "%.2f KB".format(bytes / 1024f)
            else -> "$bytes B"
        }
    }

    fun formatPercentage(value: Float): String {
        return "%.1f%%".format(value)
    }

    fun formatTemperature(celsius: Float): String {
        return "%.1f°C".format(celsius)
    }

    // Device profile utilities

    fun isLowEndDevice(): Boolean {
        val profile = getDeviceProfile()
        return profile.contains("6GB") || profile.contains("LOW")
    }

    fun isHighEndDevice(): Boolean {
        val profile = getDeviceProfile()
        return profile.contains("16GB") || profile.contains("FLAGSHIP")
    }

    // Model recommendations

    fun getRecommendedLLMModel(): String {
        return when {
            isLowEndDevice() -> "qwen3-0.6b-q4"
            isHighEndDevice() -> "qwen3-4b-q4"
            else -> "qwen3-1.7b-q4"
        }
    }

    fun getRecommendedSTTModel(): String {
        return "whisper-base"
    }

    fun getRecommendedTTSModel(): String {
        return "piper-en"
    }

    fun getRecommendedVisionModel(): String {
        return when {
            isLowEndDevice() -> "yolo-nano"
            isHighEndDevice() -> "mobilenet-v3"
            else -> "yolo-nano"
        }
    }
}
