package com.livehumanai.livehumanai.native

/**
 * NativeBridge provides the Kotlin interface to the native C++ code.
 * This class loads the native library and provides methods to interact with the native runtime.
 */
class NativeBridge {

    companion object {
        // Load the native library
        init {
            System.loadLibrary("native-core")
        }
    }

    // Native handle to the NativeCore instance
    private var nativeHandle: Long = 0

    /**
     * Initialize the native runtime.
     * @return true if initialization was successful, false otherwise
     */
    fun initialize(): Boolean {
        if (nativeHandle != 0L) {
            return true
        }
        nativeHandle = nativeInitialize()
        return nativeHandle != 0L
    }

    /**
     * Shutdown the native runtime.
     */
    fun shutdown() {
        if (nativeHandle != 0L) {
            nativeShutdown(nativeHandle)
            nativeHandle = 0
        }
    }

    /**
     * Get the version of the native runtime.
     * @return Version string
     */
    fun getVersion(): String {
        return nativeGetVersion(nativeHandle)
    }

    /**
     * Get the runtime status.
     * @return Status string
     */
    fun getRuntimeStatus(): String {
        return nativeGetRuntimeStatus(nativeHandle)
    }

    /**
     * Get the device profile based on hardware capabilities.
     * @return Device profile string
     */
    fun getDeviceProfile(): String {
        return nativeGetDeviceProfile(nativeHandle)
    }

    // AI Engine functions

    /**
     * Load an AI model.
     * @param modelName Name of the model to load
     * @return true if loading was successful, false otherwise
     */
    fun loadModel(modelName: String): Boolean {
        return nativeLoadModel(nativeHandle, modelName)
    }

    /**
     * Unload an AI model.
     * @param modelName Name of the model to unload
     * @return true if unloading was successful, false otherwise
     */
    fun unloadModel(modelName: String): Boolean {
        return nativeUnloadModel(nativeHandle, modelName)
    }

    /**
     * Generate text from a prompt.
     * @param prompt Input prompt
     * @param modelName Name of the model to use (empty for default)
     * @param temperature Temperature for generation (0.0 to 1.0)
     * @param maxTokens Maximum number of tokens to generate
     * @return Generated text
     */
    fun generate(
        prompt: String,
        modelName: String = "",
        temperature: Float = 0.7f,
        maxTokens: Int = 512
    ): String {
        return nativeGenerate(nativeHandle, prompt, modelName, temperature, maxTokens)
    }

    /**
     * Stop the current generation.
     */
    fun stopGeneration() {
        nativeStopGeneration(nativeHandle)
    }

    // Hardware monitoring functions

    /**
     * Get the total RAM available on the device.
     * @return Total RAM in bytes
     */
    fun getTotalRAM(): Long {
        return nativeGetTotalRAM(nativeHandle)
    }

    /**
     * Get the available RAM on the device.
     * @return Available RAM in bytes
     */
    fun getAvailableRAM(): Long {
        return nativeGetAvailableRAM(nativeHandle)
    }

    /**
     * Get the percentage of RAM currently in use.
     * @return RAM usage percentage (0.0 to 100.0)
     */
    fun getRAMUsagePercentage(): Float {
        return nativeGetRAMUsagePercentage(nativeHandle)
    }

    /**
     * Get the current CPU usage.
     * @return CPU usage percentage (0.0 to 100.0)
     */
    fun getCPUUsage(): Float {
        return nativeGetCPUUsage(nativeHandle)
    }

    /**
     * Get the current device temperature.
     * @return Temperature in Celsius
     */
    fun getTemperature(): Float {
        return nativeGetTemperature(nativeHandle)
    }

    /**
     * Get the current battery level.
     * @return Battery level percentage (0.0 to 100.0)
     */
    fun getBatteryLevel(): Float {
        return nativeGetBatteryLevel(nativeHandle)
    }

    // Performance mode functions

    /**
     * Performance modes for the AI runtime.
     */
    enum class PerformanceMode {
        BATTERY_SAVER,
        BALANCED,
        PERFORMANCE,
        MAXIMUM
    }

    /**
     * Set the performance mode.
     * @param mode Performance mode to set
     */
    fun setPerformanceMode(mode: PerformanceMode) {
        nativeSetPerformanceMode(nativeHandle, mode.ordinal)
    }

    /**
     * Get the current performance mode.
     * @return Current performance mode
     */
    fun getPerformanceMode(): PerformanceMode {
        return PerformanceMode.values()[nativeGetPerformanceMode(nativeHandle)]
    }

    // Native methods (declared in JNIBridge.h and implemented in JNIBridge.cpp)
    private external fun nativeInitialize(): Long
    private external fun nativeShutdown(nativeHandle: Long)
    private external fun nativeGetVersion(nativeHandle: Long): String
    private external fun nativeGetRuntimeStatus(nativeHandle: Long): String
    private external fun nativeGetDeviceProfile(nativeHandle: Long): String

    private external fun nativeLoadModel(
        nativeHandle: Long,
        modelName: String
    ): Boolean

    private external fun nativeUnloadModel(
        nativeHandle: Long,
        modelName: String
    ): Boolean

    private external fun nativeGenerate(
        nativeHandle: Long,
        prompt: String,
        modelName: String,
        temperature: Float,
        maxTokens: Int
    ): String

    private external fun nativeStopGeneration(nativeHandle: Long)

    private external fun nativeGetTotalRAM(nativeHandle: Long): Long
    private external fun nativeGetAvailableRAM(nativeHandle: Long): Long
    private external fun nativeGetRAMUsagePercentage(nativeHandle: Long): Float
    private external fun nativeGetCPUUsage(nativeHandle: Long): Float
    private external fun nativeGetTemperature(nativeHandle: Long): Float
    private external fun nativeGetBatteryLevel(nativeHandle: Long): Float

    private external fun nativeSetPerformanceMode(
        nativeHandle: Long,
        mode: Int
    )

    private external fun nativeGetPerformanceMode(nativeHandle: Long): Int
}
