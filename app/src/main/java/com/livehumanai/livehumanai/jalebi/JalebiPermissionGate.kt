package com.livehumanai.livehumanai.jalebi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import javax.inject.Inject
import javax.inject.Singleton

enum class JalebiPermission { CAMERA, MICROPHONE }

@Singleton
class JalebiPermissionGate @Inject constructor(
    private val context: Context
) {
    fun has(permission: JalebiPermission): Boolean = ContextCompat.checkSelfPermission(
        context,
        when (permission) {
            JalebiPermission.CAMERA -> Manifest.permission.CAMERA
            JalebiPermission.MICROPHONE -> Manifest.permission.RECORD_AUDIO
        }
    ) == PackageManager.PERMISSION_GRANTED

    fun canPerceiveVision(): Boolean = has(JalebiPermission.CAMERA)
    fun canPerceiveSpeech(): Boolean = has(JalebiPermission.MICROPHONE)
}
