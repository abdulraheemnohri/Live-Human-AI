package com.livehumanai.livehumanai.jalebi

import javax.inject.Inject
import javax.inject.Singleton

/**
 * CameraX-facing semantic adapter. ImageAnalysis owns capture; it supplies
 * already-computed lightweight perception so JCL never retains raw frames.
 */
@Singleton
class JalebiCameraAdapter @Inject constructor() {
    private var lastTimestamp: Long = 0L
    private var lastSceneSignature: String = ""

    fun submit(
        timestampMs: Long,
        sceneSignature: String,
        objects: List<String> = emptyList(),
        text: List<String> = emptyList(),
        taskChanged: Boolean = false,
        permissionAvailable: Boolean = true
    ): Perception {
        val changed = sceneSignature != lastSceneSignature
        lastTimestamp = timestampMs
        lastSceneSignature = sceneSignature
        return Perception(timestampMs, sceneSignature, objects, text, changed, taskChanged, permissionAvailable)
    }

    data class Perception(
        val timestampMs: Long,
        val sceneSignature: String,
        val objects: List<String>,
        val text: List<String>,
        val sceneChanged: Boolean,
        val taskChanged: Boolean,
        val permissionAvailable: Boolean
    )
}
