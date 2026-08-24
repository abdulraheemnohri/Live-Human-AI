package com.livehumanai.livehumanai

import android.app.Application
import com.livehumanai.livehumanai.nativebridge.NativeBridge
import dagger.hilt.android.HiltAndroidApp

/**
 * LiveHumanAIApp is the main Application class for the Live Human AI app.
 * It initializes the native runtime and provides access to it throughout the app.
 */
@HiltAndroidApp
class LiveHumanAIApp : Application() {

    // Singleton instance of the native bridge
    val nativeBridge: NativeBridge by lazy {
        NativeBridge().apply {
            initialize()
        }
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize the native runtime when the app starts
        if (!nativeBridge.initialize()) {
            throw RuntimeException("Failed to initialize native runtime")
        }
    }

    override fun onTerminate() {
        // Shutdown the native runtime when the app terminates
        nativeBridge.shutdown()
        super.onTerminate()
    }
}
