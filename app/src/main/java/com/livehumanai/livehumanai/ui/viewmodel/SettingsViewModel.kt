package com.livehumanai.livehumanai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livehumanai.livehumanai.data.repository.AIRepository
import com.livehumanai.livehumanai.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SettingsViewModel provides the business logic for app settings operations.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val aiRepository: AIRepository
) : ViewModel() {

    // State for settings
    private val _settings = MutableStateFlow<Map<String, Any>>(emptyMap())
    val settings: StateFlow<Map<String, Any>> = _settings.asStateFlow()

    // State for loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSettings()
    }

    // Settings operations

    fun loadSettings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val settingsMap = mutableMapOf<String, Any>()

                // AI & Model Settings
                settingsMap["defaultModel"] = settingsRepository.getDefaultModel()
                settingsMap["autoModelSelection"] = settingsRepository.isAutoModelSelectionEnabled()
                settingsMap["maxRamBudgetGb"] = settingsRepository.getMaxRamBudgetGb()
                settingsMap["contextSize"] = settingsRepository.getContextSize()
                settingsMap["threadCount"] = settingsRepository.getThreadCount()
                settingsMap["vulkanEnabled"] = settingsRepository.isVulkanEnabled()
                settingsMap["gpuBackend"] = settingsRepository.getGpuBackend()
                settingsMap["performanceMode"] = settingsRepository.getPerformanceMode()
                settingsMap["temperature"] = settingsRepository.getTemperature()
                settingsMap["topP"] = settingsRepository.getTopP()
                settingsMap["topK"] = settingsRepository.getTopK()
                settingsMap["maxTokens"] = settingsRepository.getMaxTokens()

                // Jalebi Loop / Autonomy Settings
                settingsMap["jalebiMaxIterations"] = settingsRepository.getJalebiMaxIterations()
                settingsMap["jalebiTokenBudget"] = settingsRepository.getJalebiTokenBudget()
                settingsMap["jalebiConfidenceThreshold"] = settingsRepository.getJalebiConfidenceThreshold()

                // Voice Settings
                settingsMap["wakeWordEnabled"] = settingsRepository.isWakeWordEnabled()
                settingsMap["microphoneEnabled"] = settingsRepository.isMicrophoneEnabled()
                settingsMap["sttModel"] = settingsRepository.getSTTModel()
                settingsMap["ttsModel"] = settingsRepository.getTTSModel()
                settingsMap["voice"] = settingsRepository.getVoice()
                settingsMap["speechSpeed"] = settingsRepository.getSpeechSpeed()
                settingsMap["speechPitch"] = settingsRepository.getSpeechPitch()

                // Vision Settings
                settingsMap["cameraEnabled"] = settingsRepository.isCameraEnabled()
                settingsMap["objectDetectionEnabled"] = settingsRepository.isObjectDetectionEnabled()
                settingsMap["ocrEnabled"] = settingsRepository.isOCREnabled()
                settingsMap["visionModel"] = settingsRepository.getVisionModel()

                // Memory Settings
                settingsMap["memoryEnabled"] = settingsRepository.isMemoryEnabled()
                settingsMap["memoryRetentionDays"] = settingsRepository.getMemoryRetentionDays()

                // Network Settings
                settingsMap["networkEnabled"] = settingsRepository.isNetworkEnabled()

                // Privacy Settings
                settingsMap["analyticsEnabled"] = settingsRepository.isAnalyticsEnabled()

                // Appearance & Accessibility Settings
                settingsMap["themeMode"] = settingsRepository.getThemeMode()
                settingsMap["highContrastEnabled"] = settingsRepository.isHighContrastEnabled()
                settingsMap["reducedMotionEnabled"] = settingsRepository.isReducedMotionEnabled()

                // Experimental Feature Settings
                settingsMap["expLiveVisionEnabled"] = settingsRepository.isExperimentalLiveVisionEnabled()
                settingsMap["expMultiModelResidencyEnabled"] = settingsRepository.isExperimentalMultiModelResidencyEnabled()
                settingsMap["expSpeculativeDecodingEnabled"] = settingsRepository.isExperimentalSpeculativeDecodingEnabled()
                settingsMap["expVulkanGpuEnabled"] = settingsRepository.isExperimentalVulkanGpuEnabled()

                _settings.value = settingsMap
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveSettings(settings: Map<String, Any>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                settings.forEach { (key, value) ->
                    when (key) {
                        // AI & Model Settings
                        "defaultModel" -> settingsRepository.setDefaultModel(value as String)
                        "autoModelSelection" -> settingsRepository.setAutoModelSelectionEnabled(value as Boolean)
                        "maxRamBudgetGb" -> settingsRepository.setMaxRamBudgetGb((value as Number).toInt())
                        "contextSize" -> settingsRepository.setContextSize((value as Number).toInt())
                        "threadCount" -> settingsRepository.setThreadCount((value as Number).toInt())
                        "vulkanEnabled" -> settingsRepository.setVulkanEnabled(value as Boolean)
                        "gpuBackend" -> settingsRepository.setGpuBackend(value as String)
                        "performanceMode" -> {
                            settingsRepository.setPerformanceMode(value as String)
                            updateNativePerformanceMode(value as String)
                        }
                        "temperature" -> settingsRepository.setTemperature((value as Number).toFloat())
                        "topP" -> settingsRepository.setTopP((value as Number).toFloat())
                        "topK" -> settingsRepository.setTopK((value as Number).toInt())
                        "maxTokens" -> settingsRepository.setMaxTokens((value as Number).toInt())

                        // Jalebi Loop / Autonomy Settings
                        "jalebiMaxIterations" -> settingsRepository.setJalebiMaxIterations((value as Number).toInt())
                        "jalebiTokenBudget" -> settingsRepository.setJalebiTokenBudget((value as Number).toInt())
                        "jalebiConfidenceThreshold" -> settingsRepository.setJalebiConfidenceThreshold((value as Number).toFloat())

                        // Voice Settings
                        "wakeWordEnabled" -> settingsRepository.setWakeWordEnabled(value as Boolean)
                        "microphoneEnabled" -> settingsRepository.setMicrophoneEnabled(value as Boolean)
                        "sttModel" -> settingsRepository.setSTTModel(value as String)
                        "ttsModel" -> settingsRepository.setTTSModel(value as String)
                        "voice" -> settingsRepository.setVoice(value as String)
                        "speechSpeed" -> settingsRepository.setSpeechSpeed(value as Float)
                        "speechPitch" -> settingsRepository.setSpeechPitch(value as Float)

                        // Vision Settings
                        "cameraEnabled" -> settingsRepository.setCameraEnabled(value as Boolean)
                        "objectDetectionEnabled" -> settingsRepository.setObjectDetectionEnabled(value as Boolean)
                        "ocrEnabled" -> settingsRepository.setOCREnabled(value as Boolean)
                        "visionModel" -> settingsRepository.setVisionModel(value as String)

                        // Memory Settings
                        "memoryEnabled" -> settingsRepository.setMemoryEnabled(value as Boolean)
                        "memoryRetentionDays" -> settingsRepository.setMemoryRetentionDays(value as Int)

                        // Network Settings
                        "networkEnabled" -> settingsRepository.setNetworkEnabled(value as Boolean)

                        // Privacy Settings
                        "analyticsEnabled" -> settingsRepository.setAnalyticsEnabled(value as Boolean)

                        // Appearance & Accessibility Settings
                        "themeMode" -> settingsRepository.setThemeMode(value as String)
                        "highContrastEnabled" -> settingsRepository.setHighContrastEnabled(value as Boolean)
                        "reducedMotionEnabled" -> settingsRepository.setReducedMotionEnabled(value as Boolean)

                        // Experimental Feature Settings
                        "expLiveVisionEnabled" -> settingsRepository.setExperimentalLiveVisionEnabled(value as Boolean)
                        "expMultiModelResidencyEnabled" -> settingsRepository.setExperimentalMultiModelResidencyEnabled(value as Boolean)
                        "expSpeculativeDecodingEnabled" -> settingsRepository.setExperimentalSpeculativeDecodingEnabled(value as Boolean)
                        "expVulkanGpuEnabled" -> settingsRepository.setExperimentalVulkanGpuEnabled(value as Boolean)

                        // Activity & Diagnostic Log actions
                        "activityLogsCleared" -> {
                            // Activity log clearing handled
                        }
                    }
                }

                // Reload settings to update state
                loadSettings()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveSetting(key: String, value: Any) {
        val currentSettings = _settings.value.toMutableMap()
        currentSettings[key] = value
        saveSettings(currentSettings)
    }

    // Performance mode

    private fun updateNativePerformanceMode(mode: String) {
        val nativeMode = when (mode) {
            "Battery Saver" -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.BATTERY_SAVER
            "Performance" -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.PERFORMANCE
            "Maximum" -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.MAXIMUM
            else -> com.livehumanai.livehumanai.nativebridge.NativeBridge.PerformanceMode.BALANCED
        }
        aiRepository.setPerformanceMode(nativeMode)
    }

    // Device information

    fun getDeviceProfile(): String {
        return aiRepository.getDeviceProfile()
    }

    fun getRuntimeStatus(): String {
        return aiRepository.getRuntimeStatus()
    }

    fun getVersion(): String {
        return aiRepository.getVersion()
    }

    // Utility functions

    fun getPerformanceModeOptions(): List<String> {
        return listOf("Battery Saver", "Balanced", "Performance", "Maximum")
    }

    fun getAvailableLLMModels(): List<String> {
        // In a real implementation, this would return available LLM models
        return listOf("qwen3-0.6b-q4", "qwen3-1.7b-q4", "qwen3-4b-q4", "qwen3-7b-q4")
    }

    fun getAvailableSTTModels(): List<String> {
        return listOf("whisper-tiny", "whisper-base", "whisper-small")
    }

    fun getAvailableTTSModels(): List<String> {
        return listOf("piper-en", "piper-ur", "coqui-tts")
    }

    fun getAvailableVisionModels(): List<String> {
        return listOf("yolo-nano", "mobilenet-v3", "ocr-lightweight")
    }

    fun getAvailableVoices(): List<String> {
        return listOf("default", "male", "female", "child")
    }
}
