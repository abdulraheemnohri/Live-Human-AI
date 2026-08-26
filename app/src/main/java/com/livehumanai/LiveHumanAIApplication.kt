package com.livehumanai

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import com.livehumanai.data.database.LiveHumanAIDatabase
import com.livehumanai.native.LiveHumanAINative

class LiveHumanAIApplication : Application(), Configuration.Provider {
    
    companion object {
        private const val TAG = "LiveHumanAIApp"
        
        @Volatile
        private var instance: LiveHumanAIApplication? = null
        
        fun getInstance(): LiveHumanAIApplication {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }
    
    val database: LiveHumanAIDatabase by lazy {
        LiveHumanAIDatabase.getDatabase(this)
    }
    
    private val nativeRuntime: LiveHumanAINative by lazy {
        LiveHumanAINative()
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        Log.i(TAG, "Live Human AI Application starting...")
        
        // Initialize WorkManager
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setMinimumLoggingLevel(Log.INFO)
                .build()
        )
        
        // Initialize native runtime
        try {
            val initialized = nativeRuntime.nativeInitialize(this)
            if (initialized) {
                Log.i(TAG, "Native runtime initialized successfully")
            } else {
                Log.w(TAG, "Native runtime initialization returned false")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize native runtime", e)
        }
        
        Log.i(TAG, "Live Human AI Application started")
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        Log.i(TAG, "Live Human AI Application terminating...")
        
        // Shutdown native runtime
        try {
            nativeRuntime.nativeShutdown()
            Log.i(TAG, "Native runtime shutdown complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error during native runtime shutdown", e)
        }
        
        instance = null
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()
    }
}
