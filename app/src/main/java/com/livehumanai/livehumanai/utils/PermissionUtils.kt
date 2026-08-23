package com.livehumanai.livehumanai.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * PermissionUtils provides utility functions for handling runtime permissions.
 */
object PermissionUtils {

    // Permission constants
    val CAMERA_PERMISSION = Manifest.permission.CAMERA
    val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    val READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    val READ_CONTACTS_PERMISSION = Manifest.permission.READ_CONTACTS
    val ACCESS_FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    val ACCESS_COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    val BLUETOOTH_PERMISSION = Manifest.permission.BLUETOOTH
    val BLUETOOTH_ADMIN_PERMISSION = Manifest.permission.BLUETOOTH_ADMIN
    val FOREGROUND_SERVICE_PERMISSION = Manifest.permission.FOREGROUND_SERVICE

    // Permission request codes
    const val REQUEST_CODE_CAMERA = 1001
    const val REQUEST_CODE_RECORD_AUDIO = 1002
    const val REQUEST_CODE_STORAGE = 1003
    const val REQUEST_CODE_LOCATION = 1004
    const val REQUEST_CODE_BLUETOOTH = 1005
    const val REQUEST_CODE_MULTIPLE = 1006

    // Check if a permission is granted
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    // Check if multiple permissions are granted
    fun arePermissionsGranted(context: Context, permissions: List<String>): Boolean {
        return permissions.all { isPermissionGranted(context, it) }
    }

    // Request a single permission
    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    // Request multiple permissions
    fun requestPermissions(activity: Activity, permissions: List<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), requestCode)
    }

    // Check if should show rationale for a permission
    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    // Get all permissions that are not granted
    fun getDeniedPermissions(context: Context, permissions: List<String>): List<String> {
        return permissions.filter { !isPermissionGranted(context, it) }
    }

    // Get all permissions that are granted
    fun getGrantedPermissions(context: Context, permissions: List<String>): List<String> {
        return permissions.filter { isPermissionGranted(context, it) }
    }

    // Common permission groups
    val CAMERA_PERMISSIONS = listOf(CAMERA_PERMISSION)
    val MICROPHONE_PERMISSIONS = listOf(RECORD_AUDIO_PERMISSION)
    val STORAGE_PERMISSIONS = listOf(
        READ_EXTERNAL_STORAGE_PERMISSION,
        WRITE_EXTERNAL_STORAGE_PERMISSION
    )
    val LOCATION_PERMISSIONS = listOf(
        ACCESS_FINE_LOCATION_PERMISSION,
        ACCESS_COARSE_LOCATION_PERMISSION
    )
    val BLUETOOTH_PERMISSIONS = listOf(
        BLUETOOTH_PERMISSION,
        BLUETOOTH_ADMIN_PERMISSION
    )

    // All permissions needed for the app
    val ALL_PERMISSIONS = listOf(
        CAMERA_PERMISSION,
        RECORD_AUDIO_PERMISSION,
        READ_EXTERNAL_STORAGE_PERMISSION,
        WRITE_EXTERNAL_STORAGE_PERMISSION,
        ACCESS_FINE_LOCATION_PERMISSION,
        ACCESS_COARSE_LOCATION_PERMISSION,
        BLUETOOTH_PERMISSION,
        BLUETOOTH_ADMIN_PERMISSION,
        FOREGROUND_SERVICE_PERMISSION
    )

    // Check if all required permissions are granted
    fun areAllPermissionsGranted(context: Context): Boolean {
        return arePermissionsGranted(context, ALL_PERMISSIONS)
    }

    // Request all required permissions
    fun requestAllPermissions(activity: Activity) {
        requestPermissions(activity, ALL_PERMISSIONS, REQUEST_CODE_MULTIPLE)
    }

    // Check if camera permission is granted
    fun isCameraPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, CAMERA_PERMISSION)
    }

    // Check if microphone permission is granted
    fun isMicrophonePermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, RECORD_AUDIO_PERMISSION)
    }

    // Check if storage permission is granted
    fun isStoragePermissionGranted(context: Context): Boolean {
        return arePermissionsGranted(context, STORAGE_PERMISSIONS)
    }

    // Check if location permission is granted
    fun isLocationPermissionGranted(context: Context): Boolean {
        return arePermissionsGranted(context, LOCATION_PERMISSIONS)
    }

    // Check if Bluetooth permission is granted
    fun isBluetoothPermissionGranted(context: Context): Boolean {
        return arePermissionsGranted(context, BLUETOOTH_PERMISSIONS)
    }

    // Permission result handling
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onAllGranted: () -> Unit = {},
        onSomeDenied: (List<String>) -> Unit = {},
        onAllDenied: () -> Unit = {}
    ) {
        when (requestCode) {
            REQUEST_CODE_CAMERA, REQUEST_CODE_RECORD_AUDIO, REQUEST_CODE_STORAGE,
            REQUEST_CODE_LOCATION, REQUEST_CODE_BLUETOOTH, REQUEST_CODE_MULTIPLE -> {
                val deniedPermissions = mutableListOf<String>()

                for (i in permissions.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permissions[i])
                    }
                }

                when {
                    deniedPermissions.isEmpty() -> onAllGranted()
                    deniedPermissions.size == permissions.size -> onAllDenied()
                    else -> onSomeDenied(deniedPermissions)
                }
            }
        }
    }

    // Permission explanation messages
    fun getPermissionExplanation(permission: String): String {
        return when (permission) {
            CAMERA_PERMISSION -> "Camera permission is required to use the camera for vision tasks"
            RECORD_AUDIO_PERMISSION -> "Microphone permission is required to use voice input"
            READ_EXTERNAL_STORAGE_PERMISSION,
            WRITE_EXTERNAL_STORAGE_PERMISSION -> "Storage permission is required to download and store models"
            ACCESS_FINE_LOCATION_PERMISSION,
            ACCESS_COARSE_LOCATION_PERMISSION -> "Location permission is required for location-based services"
            BLUETOOTH_PERMISSION,
            BLUETOOTH_ADMIN_PERMISSION -> "Bluetooth permission is required to connect to Bluetooth devices"
            else -> "This permission is required for the app to function properly"
        }
    }
}
