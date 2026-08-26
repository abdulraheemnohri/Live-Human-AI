package com.livehumanai.native

import android.content.Context

/**
 * JNI bridge to the native C++ Live Human AI runtime.
 * This class provides the interface between Kotlin/Android and the native engine.
 */
class LiveHumanAINative {

    companion object {
        init {
            System.loadLibrary("livehumanai")
        }
    }

    // Native method declarations
    external fun nativeInitialize(context: Context): Boolean
    external fun nativeShutdown(): Boolean
    external fun nativeIsInitialized(): Boolean

    // Engine control
    external fun nativeStartEngine(): Boolean
    external fun nativeStopEngine(): Boolean
    external fun nativeGetEngineStatus(): String

    // Hardware profiling
    external fun nativeProfileHardware(): String // JSON result
    external fun nativeGetDeviceProfile(): String // JSON result

    // Model management
    external fun nativeLoadModel(modelId: String, modelPath: String): Boolean
    external fun nativeUnloadModel(modelId: String): Boolean
    external fun nativeIsModelLoaded(modelId: String): Boolean
    external fun nativeGetLoadedModels(): String // JSON array

    // AI inference (placeholder - actual implementation in Phase 2)
    external fun nativeGenerate(prompt: String, maxTokens: Int): String
    external fun nativeGenerateStreaming(prompt: String, callback: StreamingCallback): Boolean
    external fun nativeStopGeneration(): Boolean

    // Jalebi Loop
    external fun nativeCreateLoop(goal: String, maxIterations: Int): Long
    external fun nativeStartLoop(loopId: Long): Boolean
    external fun nativePauseLoop(loopId: Long): Boolean
    external fun nativeResumeLoop(loopId: Long): Boolean
    external fun nativeCancelLoop(loopId: Long): Boolean
    external fun nativeGetLoopState(loopId: Long): String // JSON result
    external fun nativeDestroyLoop(loopId: Long): Boolean

    // Diagnostics
    external fun nativeRunDiagnostics(): String // JSON result
    external fun nativeGetPerformanceMetrics(): String // JSON result

    // Memory management
    external fun nativeGetMemoryUsage(): Long // bytes
    external fun nativeGarbageCollect(): Boolean

    /**
     * Callback interface for streaming responses from native code
     */
    interface StreamingCallback {
        fun onToken(token: String)
        fun onComplete()
        fun onError(error: String)
    }
}
