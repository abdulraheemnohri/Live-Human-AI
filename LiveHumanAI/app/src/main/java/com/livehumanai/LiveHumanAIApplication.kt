package com.livehumanai

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import com.livehumanai.native.LiveHumanAINative
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Live Human AI Application Class
 *
 * Initializes core components:
 * - Native runtime (C++ engine via JNI)
 * - WorkManager for background tasks
 * - Global coroutine scope
 * - Hardware profiler on startup
 */
class LiveHumanAIApplication : Application(), Configuration.Provider {

    companion object {
        private const val TAG = "LiveHumanAIApp"

        @Volatile
        private lateinit var instance: LiveHumanAIApplication

        fun getInstance(): LiveHumanAIApplication {
            return instance
        }
    }

    // Application-wide coroutine scope
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Native runtime reference
    lateinit var nativeRuntime: LiveHumanAINative
        private set

    // Initialization state
    var isNativeInitialized: Boolean = false
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        Log.i(TAG, "Live Human AI Application starting...")

        // Initialize native runtime
        initializeNativeRuntime()

        // Initialize WorkManager
        WorkManager.initialize(this, workManagerConfiguration)

        Log.i(TAG, "Live Human AI Application initialized")
    }

    /**
     * Initialize the C++ native runtime via JNI
     */
    private fun initializeNativeRuntime() {
        try {
            // Load native library
            System.loadLibrary("livehumanai")

            // Create native runtime instance
            nativeRuntime = LiveHumanAINative()

            // Initialize native engine
            isNativeInitialized = nativeRuntime.initialize(applicationContext)

            if (isNativeInitialized) {
                Log.i(TAG, "Native runtime initialized successfully")
            } else {
                Log.w(TAG, "Native runtime initialization returned false")
            }
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load native library: ${e.message}")
            isNativeInitialized = false
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing native runtime: ${e.message}")
            isNativeInitialized = false
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()
    }

    /**
     * Get device files directory for models and data
     */
    fun getModelsDirectory(): String {
        return "${filesDir.absolutePath}/models"
    }

    fun getDownloadsDirectory(): String {
        return "${cacheDir.absolutePath}/downloads"
    }

    fun getTempDirectory(): String {
        return "${cacheDir.absolutePath}/temp"
    }
}
