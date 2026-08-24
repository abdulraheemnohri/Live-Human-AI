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

/**
 * CameraManager manages the camera functionality for the app.
 * It handles camera initialization, switching, and frame processing.
 */
class CameraManager(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraController: LifecycleCameraController? = null
    private var previewView: PreviewView? = null

    // State
    var isCameraAvailable by mutableStateOf(false)
        private set

    var isCameraActive by mutableStateOf(false)
        private set

    var currentCameraSelector by mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
        private set

    var isFlashEnabled by mutableStateOf(false)
        private set

    var availableCameras by mutableStateOf(listOf<CameraInfo>())
        private set

    // Initialize camera
    fun initialize(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        this.previewView = previewView

        try {
            // Get available cameras
            val cameraIds = cameraManager.cameraIdList
            availableCameras = cameraIds.map { cameraId ->
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                CameraInfo(
                    id = cameraId,
                    isBackCamera = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK,
                    isFrontCamera = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
                )
            }

            isCameraAvailable = availableCameras.isNotEmpty()

            // Create camera controller
            cameraController = LifecycleCameraController(context).apply {
                cameraSelector = currentCameraSelector
                bindToLifecycle(lifecycleOwner)
            }

            // Set up preview
            cameraController?.let { controller ->
                previewView.controller = controller
            }

            isCameraActive = true
        } catch (e: CameraAccessException) {
            isCameraAvailable = false
            isCameraActive = false
        }
    }

    // Start camera
    fun startCamera() {
        if (isCameraAvailable && !isCameraActive) {
            val owner = previewView?.context as? LifecycleOwner
            if (owner != null) {
                cameraController?.bindToLifecycle(owner)
                isCameraActive = true
            }
        }
    }

    // Stop camera
    fun stopCamera() {
        if (isCameraActive) {
            cameraController?.unbind()
            isCameraActive = false
        }
    }

    // Switch camera
    fun switchCamera() {
        currentCameraSelector = if (currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        cameraController?.cameraSelector = currentCameraSelector
    }

    // Toggle flash
    fun toggleFlash() {
        isFlashEnabled = !isFlashEnabled
        cameraController?.enableTorch(isFlashEnabled)
    }

    // Capture image
    fun captureImage() {
        // In a real implementation, this would capture an image
    }

    // Set up frame analysis
    fun setupFrameAnalysis() {
        // In a real implementation, this would set up a frame analyzer
    }

    // Cleanup
    fun cleanup() {
        stopCamera()
        cameraController = null
        previewView = null
    }

    // Data classes

    data class CameraInfo(
        val id: String,
        val isBackCamera: Boolean,
        val isFrontCamera: Boolean
    )
}
