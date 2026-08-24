package com.livehumanai.livehumanai.nativebridge

/**
 * Kotlin interface to the Live Human AI native runtime.
 * JCL is intentionally exposed as a bounded, inspectable lifecycle rather than
 * an unrestricted autonomous agent.
 */
class NativeBridge {

    companion object {
        private var instance: NativeBridge? = null

        @JvmStatic
        fun getInstance(): NativeBridge {
            if (instance == null) instance = NativeBridge()
            return instance!!
        }

        init {
            try {
                System.loadLibrary("native-core")
            } catch (_: UnsatisfiedLinkError) {
                // Allows JVM/unit-test environments to load the class without NDK binaries.
            }
        }
    }

    val isInitialized: Boolean
        get() = nativeHandle != 0L

    private var nativeHandle: Long = 0

    fun initialize(): Boolean {
        if (nativeHandle != 0L) return true
        nativeHandle = nativeInitialize()
        return nativeHandle != 0L
    }

    fun shutdown() {
        if (nativeHandle != 0L) {
            nativeShutdown(nativeHandle)
            nativeHandle = 0
        }
    }

    fun getVersion(): String = nativeGetVersion(nativeHandle)
    fun getRuntimeStatus(): String = nativeGetRuntimeStatus(nativeHandle)
    fun getDeviceProfile(): String = nativeGetDeviceProfile(nativeHandle)

    fun loadModel(modelName: String): Boolean = nativeLoadModel(nativeHandle, modelName)
    fun unloadModel(modelName: String): Boolean = nativeUnloadModel(nativeHandle, modelName)

    fun generate(
        prompt: String,
        modelName: String = "",
        temperature: Float = 0.7f,
        maxTokens: Int = 512
    ): String = nativeGenerate(nativeHandle, prompt, modelName, temperature, maxTokens)

    fun stopGeneration() = nativeStopGeneration(nativeHandle)

    fun getTotalRAM(): Long = nativeGetTotalRAM(nativeHandle)
    fun getAvailableRAM(): Long = nativeGetAvailableRAM(nativeHandle)
    fun getRAMUsagePercentage(): Float = nativeGetRAMUsagePercentage(nativeHandle)
    fun getCPUUsage(): Float = nativeGetCPUUsage(nativeHandle)
    fun getTemperature(): Float = nativeGetTemperature(nativeHandle)
    fun getBatteryLevel(): Float = nativeGetBatteryLevel(nativeHandle)

    enum class PerformanceMode { BATTERY_SAVER, BALANCED, PERFORMANCE, MAXIMUM }

    fun setPerformanceMode(mode: PerformanceMode) = nativeSetPerformanceMode(nativeHandle, mode.ordinal)

    fun getPerformanceMode(): PerformanceMode {
        val ordinal = nativeGetPerformanceMode(nativeHandle)
        return PerformanceMode.values().getOrElse(ordinal) { PerformanceMode.BALANCED }
    }

    // -------------------------------------------------------------------------
    // Jalebi Cognitive Loop (JCL)
    // -------------------------------------------------------------------------

    fun createJalebiLoop(
        goal: String,
        maxIterations: Int = 8
    ): Int = nativeCreateJalebiLoop(goal, maxIterations)

    fun startJalebiLoop(loopId: Int): Boolean = nativeStartJalebiLoop(loopId)
    fun pauseJalebiLoop(loopId: Int): Boolean = nativePauseJalebiLoop(loopId)
    fun resumeJalebiLoop(loopId: Int): Boolean = nativeResumeJalebiLoop(loopId)
    fun cancelJalebiLoop(loopId: Int): Boolean = nativeCancelJalebiLoop(loopId)
    fun completeJalebiLoop(loopId: Int): Boolean = nativeCompleteJalebiLoop(loopId)
    fun failJalebiLoop(loopId: Int, reason: String): Boolean = nativeFailJalebiLoop(loopId, reason)

    /** Execute one bounded lifecycle iteration and return inspectable history JSON. */
    fun executeJalebiIteration(loopId: Int, input: String): String =
        nativeExecuteJalebiIteration(loopId, input)

    /** Record external evaluation/model evidence; JCL owns the next state decision. */
    fun evaluateJalebiLoop(
        loopId: Int,
        confidence: Float,
        goalCompleted: Boolean,
        evaluation: String,
        nextAction: String,
        memoryUpdates: String = ""
    ): Boolean = nativeEvaluateJalebiLoop(
        loopId, confidence, goalCompleted, evaluation, nextAction, memoryUpdates
    )

    fun getJalebiLoopState(loopId: Int): String = nativeGetJalebiLoopState(loopId)
    fun getJalebiConfidence(loopId: Int): Float = nativeGetJalebiConfidence(loopId)
    fun getJalebiIteration(loopId: Int): Int = nativeGetJalebiIteration(loopId)
    fun getJalebiHistory(loopId: Int): String = nativeGetJalebiHistory(loopId)

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
    private external fun nativeCancelJalebiLoop(loopId: Int): Boolean
    private external fun nativeExecuteJalebiIteration(loopId: Int, input: String): String
    private external fun nativeEvaluateJalebiLoop(loopId: Int, confidence: Float, goalCompleted: Boolean, evaluation: String, nextAction: String, memoryUpdates: String): Boolean
    private external fun nativeGetJalebiLoopState(loopId: Int): String
    private external fun nativeGetJalebiConfidence(loopId: Int): Float
    private external fun nativeGetJalebiIteration(loopId: Int): Int
    private external fun nativeGetJalebiHistory(loopId: Int): String
    private external fun nativeCompleteJalebiLoop(loopId: Int): Boolean
    private external fun nativeFailJalebiLoop(loopId: Int, reason: String): Boolean
}
