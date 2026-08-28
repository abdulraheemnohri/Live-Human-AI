package com.livehumanai.livehumanai

import android.app.Application
import android.util.Log
import com.livehumanai.livehumanai.nativebridge.NativeBridge
import dagger.hilt.android.HiltAndroidApp

/**
 * LiveHumanAIApp is the main Application class for the Live Human AI app.
 * The native runtime is optional so the UI can still launch in degraded mode
 * when a device build does not include a compatible native backend.
 */
@HiltAndroidApp
class LiveHumanAIApp : Application() {

    companion object {
        private const val TAG = "LiveHumanAIApp"
    }

    val nativeBridge: NativeBridge by lazy { NativeBridge() }

    override fun onCreate() {
        super.onCreate()

        if (nativeBridge.initialize()) {
            Log.i(TAG, "Native runtime initialized")
        } else {
            Log.w(TAG, "Native runtime unavailable; continuing in degraded mode")
        }
    }

    override fun onTerminate() {
        nativeBridge.shutdown()
        super.onTerminate()
    }
}
