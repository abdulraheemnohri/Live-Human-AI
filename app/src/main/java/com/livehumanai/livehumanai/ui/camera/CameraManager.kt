package com.livehumanai.livehumanai.ui.camera

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import com.livehumanai.livehumanai.jalebi.JalebiCameraAnalyzer

/** Camera lifecycle owner. Raw frames are transient and are never retained by this class. */
class CameraManager(private val context: Context) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraController: LifecycleCameraController? = null
    private var previewView: PreviewView? = null
    private var lifecycleOwner: LifecycleOwner? = null
    var isCameraAvailable by mutableStateOf(false); private set
    var isCameraActive by mutableStateOf(false); private set
    var currentCameraSelector by mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA); private set
    var isFlashEnabled by mutableStateOf(false); private set
    var availableCameras by mutableStateOf(listOf<CameraInfo>()); private set

    fun initialize(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        this.previewView = previewView; this.lifecycleOwner = lifecycleOwner
        try {
            availableCameras = cameraManager.cameraIdList.map { id ->
                val facing = cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING)
                CameraInfo(id, facing == CameraCharacteristics.LENS_FACING_BACK, facing == CameraCharacteristics.LENS_FACING_FRONT)
            }
            isCameraAvailable = availableCameras.isNotEmpty()
            cameraController = LifecycleCameraController(context).apply { cameraSelector = currentCameraSelector; bindToLifecycle(lifecycleOwner) }
            previewView.controller = cameraController
            isCameraActive = true
        } catch (_: CameraAccessException) { isCameraAvailable = false; isCameraActive = false }
        catch (_: SecurityException) { isCameraAvailable = false; isCameraActive = false }
    }

    fun setupFrameAnalysis(onFrame: (JalebiCameraAnalyzer.FrameSignal) -> Unit) {
        cameraController?.setImageAnalysisAnalyzer(context.mainExecutor, JalebiCameraAnalyzer(onFrame = onFrame))
    }

    fun clearFrameAnalysis() { cameraController?.clearImageAnalysisAnalyzer() }
    fun startCamera() { lifecycleOwner?.let { if (isCameraAvailable && !isCameraActive) { cameraController?.bindToLifecycle(it); isCameraActive = true } } }
    fun stopCamera() { if (isCameraActive) { cameraController?.unbind(); isCameraActive = false } }
    fun switchCamera() { currentCameraSelector = if (currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA; cameraController?.cameraSelector = currentCameraSelector }
    fun toggleFlash() { isFlashEnabled = !isFlashEnabled; cameraController?.enableTorch(isFlashEnabled) }
    fun cleanup() { stopCamera(); cameraController?.clearImageAnalysisAnalyzer(); cameraController = null; previewView = null; lifecycleOwner = null; isCameraAvailable = false }

    data class CameraInfo(val id: String, val isBackCamera: Boolean, val isFrontCamera: Boolean)
}
