package com.livehumanai.livehumanai.ui.camera

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.livehumanai.livehumanai.jalebi.JalebiCameraAnalyzer

class CameraManager(private val context: Context) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraController: LifecycleCameraController? = null
    private var previewView: PreviewView? = null
    var isCameraAvailable by mutableStateOf(false); private set
    var isCameraActive by mutableStateOf(false); private set
    var currentCameraSelector by mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA); private set
    var isFlashEnabled by mutableStateOf(false); private set
    var availableCameras by mutableStateOf(listOf<CameraInfo>()); private set

    fun initialize(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        this.previewView = previewView
        try {
            availableCameras = cameraManager.cameraIdList.map { id ->
                val c = cameraManager.getCameraCharacteristics(id)
                CameraInfo(id, c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK, c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT)
            }
            isCameraAvailable = availableCameras.isNotEmpty()
            cameraController = LifecycleCameraController(context).apply {
                cameraSelector = currentCameraSelector
                bindToLifecycle(lifecycleOwner)
            }
            cameraController?.let { previewView.controller = it }
            isCameraActive = true
        } catch (_: CameraAccessException) { isCameraAvailable = false; isCameraActive = false }
    }

    fun setupFrameAnalysis(onFrame: (JalebiCameraAnalyzer.FrameSignal) -> Unit, intervalMs: Long = 500L) {
        cameraController?.setImageAnalysisAnalyzer(
            context.mainExecutor,
            JalebiCameraAnalyzer(intervalMs, onFrame)
        )
    }

    fun clearFrameAnalysis() { cameraController?.clearImageAnalysisAnalyzer() }

    fun startCamera() { if (isCameraAvailable && !isCameraActive) { previewView?.let { cameraController?.bindToLifecycle(it.context as LifecycleOwner); isCameraActive = true } } }
    fun stopCamera() { if (isCameraActive) { cameraController?.unbind(); isCameraActive = false } }
    fun switchCamera() { currentCameraSelector = if (currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA; cameraController?.cameraSelector = currentCameraSelector }
    fun toggleFlash() { isFlashEnabled = !isFlashEnabled; cameraController?.enableTorch(isFlashEnabled) }
    fun captureImage() { }
    fun cleanup() { stopCamera(); cameraController = null; previewView = null }

    data class CameraInfo(val id: String, val isBackCamera: Boolean, val isFrontCamera: Boolean)
}
