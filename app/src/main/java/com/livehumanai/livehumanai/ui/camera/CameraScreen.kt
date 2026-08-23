package com.livehumanai.livehumanai.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

/**
 * CameraScreen provides a camera interface for capturing images and videos.
 */
@Composable
fun CameraScreen() {
    val context = LocalContext.current

    // State for camera
    var hasCameraPermission by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    )}

    var isCameraActive by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isUsingFrontCamera by remember { mutableStateOf(false) }
    var detectedObjects by remember { mutableStateOf(listOf<String>()) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            isCameraActive = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Camera",
            style = MaterialTheme.typography.headlineMedium
        )

        // Camera preview
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission && isCameraActive) {
                // In a real implementation, this would show the camera preview
                // using CameraX or similar API
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Camera Preview")

                    // Show detected objects
                    if (detectedObjects.isNotEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            detectedObjects.forEach { obj ->
                                Text(
                                    text = obj,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            } else if (!hasCameraPermission) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Camera permission required")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    ) {
                        Text("Request Permission")
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Camera is off")
                }
            }
        }

        // Camera controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Flash toggle
            IconButton(
                onClick = { isFlashOn = !isFlashOn },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Toggle Flash"
                )
            }

            // Camera switch
            IconButton(
                onClick = { isUsingFrontCamera = !isUsingFrontCamera },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Switch Camera"
                )
            }

            // Capture button
            IconButton(
                onClick = {
                    detectedObjects = listOf("Captured frame", "Object 1", "Text line 1")
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = "Capture",
                    modifier = Modifier.size(48.dp)
                )
            }

            // Camera toggle
            IconButton(
                onClick = {
                    if (hasCameraPermission) {
                        isCameraActive = !isCameraActive
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isCameraActive) Icons.Default.Stop else Icons.Default.CameraAlt,
                    contentDescription = if (isCameraActive) "Stop Camera" else "Start Camera"
                )
            }

            // Voice input button
            IconButton(
                onClick = {
                    detectedObjects = listOf("Voice query active", "Processing scene...")
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input"
                )
            }
        }

        // Vision mode selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { detectedObjects = listOf("Person", "Cup", "Keyboard") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Objects")
            }

            Button(
                onClick = { detectedObjects = listOf("Text: 'LIVE HUMAN AI'") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Text")
            }

            Button(
                onClick = { detectedObjects = listOf("Scene: Office Room") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Scene")
            }

            Button(
                onClick = { detectedObjects = listOf("Document: Page 1 Scanned") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Document")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    LiveHumanAITheme {
        CameraScreen()
    }
}
