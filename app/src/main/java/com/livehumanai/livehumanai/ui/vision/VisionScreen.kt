package com.livehumanai.livehumanai.ui.vision

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * VisionScreen provides a camera-based interface for vision AI tasks.
 * It allows users to see what the AI sees and ask questions about the visual content.
 */
@Composable
fun VisionScreen() {
    // State for the vision screen
    var detectedObjects by remember { mutableStateOf(listOf<String>()) }
    var detectedText by remember { mutableStateOf("") }
    var isCameraActive by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    // AI state
    var currentModel by remember { mutableStateOf("yolo-nano") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "AI Vision",
                style = MaterialTheme.typography.titleLarge
            )

            // Settings button
            IconButton(
                onClick = { /* TODO: Open vision settings */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Vision Settings"
                )
            }
        }

        // Model indicator
        Text(
            text = "Model: $currentModel",
            style = MaterialTheme.typography.bodySmall
        )

        // Camera preview
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isCameraActive) {
                // In a real implementation, this would show the camera preview
                // For now, just show a placeholder
                Text(
                    text = "CAMERA PREVIEW",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Show detected objects
                if (detectedObjects.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = detectedObjects.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Show detected text
                if (detectedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Text: $detectedText",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    text = "Camera Off",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Camera toggle button
        Button(
            onClick = { isCameraActive = !isCameraActive },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isCameraActive) "Stop Camera" else "Start Camera")
        }

        // Vision mode tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    currentModel = "yolo-nano"
                    detectedObjects = listOf("person", "bottle", "laptop")
                    detectedText = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Objects")
            }

            Button(
                onClick = {
                    currentModel = "ocr-lightweight"
                    detectedText = "LIVE HUMAN AI PRIVACY FIRST"
                    detectedObjects = emptyList()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Text")
            }

            Button(
                onClick = {
                    currentModel = "mobilenet-v3"
                    detectedObjects = listOf("workspace", "chair")
                    detectedText = "Indoor environment detected"
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Scene")
            }
        }

        // Input area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Voice input button
            IconButton(
                onClick = {
                    inputText = "What am I looking at?"
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input"
                )
            }

            // Text input
            androidx.compose.material3.TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about what you see...") },
                singleLine = true
            )

            // Send button
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        detectedText = "Analysis for '$inputText': Visual scene contains ${detectedObjects.joinToString()}."
                        inputText = ""
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VisionScreenPreview() {
    LiveHumanAITheme {
        VisionScreen()
    }
}
