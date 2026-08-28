package com.livehumanai.livehumanai.nativebridge

/** Kotlin/JNI boundary for the Live Human AI native runtime and bounded JCL. */
class NativeBridge {
    companion object {
        private var instance: NativeBridge? = null
        private var nativeLibraryLoaded: Boolean = false

        @JvmStatic
        fun getInstance(): NativeBridge = instance ?: NativeBridge().also { instance = it }

        init {
            nativeLibraryLoaded = runCatching {
                System.loadLibrary("native-core")
                true
            }.getOrDefault(false)
        }
    }

    private var nativeHandle: Long = 0
    val isInitialized: Boolean get() = nativeHandle != 0L

    /** Initializes the optional native runtime without taking down the Android process. */
    fun initialize(): Boolean {
        if (nativeHandle != 0L) return true
        if (!nativeLibraryLoaded) return false

        nativeHandle = runCatching { nativeInitialize() }.getOrDefault(0L)
        return nativeHandle != 0L
    }
    fun shutdown() { if (nativeHandle != 0L) { nativeShutdown(nativeHandle); nativeHandle = 0L } }
    fun getVersion(): String = nativeGetVersion(nativeHandle)
    fun getRuntimeStatus(): String = nativeGetRuntimeStatus(nativeHandle)
    fun getDeviceProfile(): String = nativeGetDeviceProfile(nativeHandle)
    fun loadModel(modelName: String): Boolean = nativeLoadModel(nativeHandle, modelName)
    fun unloadModel(modelName: String): Boolean = nativeUnloadModel(nativeHandle, modelName)
    fun generate(prompt: String, modelName: String = "", temperature: Float = .7f, maxTokens: Int = 512): String = nativeGenerate(nativeHandle, prompt, modelName, temperature, maxTokens)
    fun stopGeneration() = nativeStopGeneration(nativeHandle)
    fun resetContext() = stopGeneration()
    fun getTotalRAM(): Long = nativeGetTotalRAM(nativeHandle)
    fun getAvailableRAM(): Long = nativeGetAvailableRAM(nativeHandle)
    fun getRAMUsagePercentage(): Float = nativeGetRAMUsagePercentage(nativeHandle)
    fun getCPUUsage(): Float = nativeGetCPUUsage(nativeHandle)
    fun getTemperature(): Float = nativeGetTemperature(nativeHandle)
    fun getBatteryLevel(): Float = nativeGetBatteryLevel(nativeHandle)
    enum class PerformanceMode { BATTERY_SAVER, BALANCED, PERFORMANCE, MAXIMUM }
    fun setPerformanceMode(mode: PerformanceMode) = nativeSetPerformanceMode(nativeHandle, mode.ordinal)
    fun getPerformanceMode(): PerformanceMode = PerformanceMode.values().getOrElse(nativeGetPerformanceMode(nativeHandle)) { PerformanceMode.BALANCED }
    fun createJalebiLoop(goal: String, maxIterations: Int = 8): Int = nativeCreateJalebiLoop(goal, maxIterations)
    fun startJalebiLoop(loopId: Int): Boolean = nativeStartJalebiLoop(loopId)
    fun pauseJalebiLoop(loopId: Int): Boolean = nativePauseJalebiLoop(loopId)
    fun resumeJalebiLoop(loopId: Int): Boolean = nativeResumeJalebiLoop(loopId)
    fun replanJalebiLoop(loopId: Int, reason: String = "low_confidence"): Boolean = nativeReplanJalebiLoop(loopId, reason)
    fun cancelJalebiLoop(loopId: Int): Boolean = nativeCancelJalebiLoop(loopId)
    fun completeJalebiLoop(loopId: Int): Boolean = nativeCompleteJalebiLoop(loopId)
    fun failJalebiLoop(loopId: Int, reason: String): Boolean = nativeFailJalebiLoop(loopId, reason)
    fun executeJalebiIteration(loopId: Int, input: String): String = nativeExecuteJalebiIteration(loopId, input)
    fun evaluateJalebiLoop(loopId: Int, confidence: Float, goalCompleted: Boolean, evaluation: String, nextAction: String, memoryUpdates: String = ""): Boolean = nativeEvaluateJalebiLoop(loopId, confidence, goalCompleted, evaluation, nextAction, memoryUpdates)
    fun submitJalebiVision(loopId: Int, sceneId: String, objects: List<String>, text: List<String>, confidence: Float, flagshipDevice: Boolean = false): String = nativeSubmitJalebiVision(loopId, sceneId, objects.filter(String::isNotBlank).joinToString("|"), text.filter(String::isNotBlank).joinToString("|"), confidence.coerceIn(0f,1f), getRAMUsagePercentage(), getCPUUsage(), getTemperature(), getBatteryLevel(), flagshipDevice)
    fun submitJalebiSpeech(loopId: Int, transcript: String, confidence: Float, isFinal: Boolean, flagshipDevice: Boolean = false): String = nativeSubmitJalebiSpeech(loopId, transcript, confidence.coerceIn(0f,1f), isFinal, getRAMUsagePercentage(), getCPUUsage(), getTemperature(), getBatteryLevel(), flagshipDevice)
    fun getJalebiLoopState(loopId: Int): String = nativeGetJalebiLoopState(loopId)
    fun getJalebiConfidence(loopId: Int): Float = nativeGetJalebiConfidence(loopId)
    fun getJalebiIteration(loopId: Int): Int = nativeGetJalebiIteration(loopId)
    fun getJalebiHistory(loopId: Int): String = nativeGetJalebiHistory(loopId)
    fun loadSpeechModel(modelPath: String): Boolean = nativeLoadSpeechModel(nativeHandle, modelPath)
    fun unloadSpeechModel() = nativeUnloadSpeechModel(nativeHandle)
    fun isSpeechModelLoaded(): Boolean = nativeIsSpeechModelLoaded(nativeHandle)
    fun transcribePcm(samples: ShortArray, sampleRate: Int = 16_000, offset: Int = 0): String = if (!isInitialized || samples.isEmpty()) "" else nativeTranscribePcm(nativeHandle, samples, sampleRate, offset)
    fun stopSpeech() = nativeStopSpeech(nativeHandle)
    fun analyzeVisionRgba(rgba: ByteArray, width: Int, height: Int, modelName: String = ""): String = if (!isInitialized || rgba.isEmpty() || width <= 0 || height <= 0) "" else nativeAnalyzeVisionRgba(nativeHandle, rgba, width, height, modelName)
    private external fun nativeInitialize(): Long
    private external fun nativeShutdown(nativeHandle: Long)
    private external fun nativeGetVersion(nativeHandle: Long): String
    private external fun nativeGetRuntimeStatus(nativeHandle: Long): String
    private external fun nativeGetDeviceProfile(nativeHandle: Long): String
    private external fun nativeLoadModel(nativeHandle: Long, modelName: String): Boolean
    private external fun nativeUnloadModel(nativeHandle: Long, modelName: String): Boolean
    private external fun nativeGenerate(nativeHandle: Long, prompt: String, modelName: String, temperature: Float, maxTokens: Int): String
    private external fun nativeStopGeneration(nativeHandle: Long)
    private external fun nativeGetTotalRAM(nativeHandle: Long): Long
    private external fun nativeGetAvailableRAM(nativeHandle: Long): Long
    private external fun nativeGetRAMUsagePercentage(nativeHandle: Long): Float
    private external fun nativeGetCPUUsage(nativeHandle: Long): Float
    private external fun nativeGetTemperature(nativeHandle: Long): Float
    private external fun nativeGetBatteryLevel(nativeHandle: Long): Float
    private external fun nativeSetPerformanceMode(nativeHandle: Long, mode: Int)
    private external fun nativeGetPerformanceMode(nativeHandle: Long): Int
    private external fun nativeCreateJalebiLoop(goal: String, maxIterations: Int): Int
    private external fun nativeStartJalebiLoop(loopId: Int): Boolean
    private external fun nativePauseJalebiLoop(loopId: Int): Boolean
    private external fun nativeResumeJalebiLoop(loopId: Int): Boolean
    private external fun nativeReplanJalebiLoop(loopId: Int, reason: String): Boolean
    private external fun nativeCancelJalebiLoop(loopId: Int): Boolean
    private external fun nativeSubmitJalebiVision(loopId: Int, sceneId: String, objects: String, text: String, confidence: Float, ram: Float, cpu: Float, temperature: Float, battery: Float, flagship: Boolean): String
    private external fun nativeSubmitJalebiSpeech(loopId: Int, transcript: String, confidence: Float, isFinal: Boolean, ram: Float, cpu: Float, temperature: Float, battery: Float, flagship: Boolean): String
    private external fun nativeExecuteJalebiIteration(loopId: Int, input: String): String
    private external fun nativeEvaluateJalebiLoop(loopId: Int, confidence: Float, goalCompleted: Boolean, evaluation: String, nextAction: String, memoryUpdates: String): Boolean
    private external fun nativeGetJalebiLoopState(loopId: Int): String
    private external fun nativeGetJalebiConfidence(loopId: Int): Float
    private external fun nativeGetJalebiIteration(loopId: Int): Int
    private external fun nativeGetJalebiHistory(loopId: Int): String
    private external fun nativeCompleteJalebiLoop(loopId: Int): Boolean
    private external fun nativeFailJalebiLoop(loopId: Int, reason: String): Boolean
    private external fun nativeLoadSpeechModel(nativeHandle: Long, modelPath: String): Boolean
    private external fun nativeUnloadSpeechModel(nativeHandle: Long)
    private external fun nativeIsSpeechModelLoaded(nativeHandle: Long): Boolean
    private external fun nativeTranscribePcm(nativeHandle: Long, samples: ShortArray, sampleRate: Int, offset: Int): String
    private external fun nativeStopSpeech(nativeHandle: Long)
    private external fun nativeAnalyzeVisionRgba(nativeHandle: Long, rgba: ByteArray, width: Int, height: Int, modelName: String): String
}