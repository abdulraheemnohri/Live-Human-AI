package com.livehumanai.livehumanai.ui.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
 * PrivacyScreen provides privacy controls and information for the app.
 */
@Composable
fun PrivacyScreen() {
    // State for privacy settings
    var microphoneAccess by remember { mutableStateOf(true) }
    var cameraAccess by remember { mutableStateOf(true) }
    var locationAccess by remember { mutableStateOf(false) }
    var bluetoothAccess by remember { mutableStateOf(false) }
    var fileAccess by remember { mutableStateOf(true) }
    var networkAccess by remember { mutableStateOf(false) }
    var analyticsEnabled by remember { mutableStateOf(false) }

    // State for current activity
    var isMicrophoneActive by remember { mutableStateOf(false) }
    var isCameraActive by remember { mutableStateOf(false) }
    var isNetworkActive by remember { mutableStateOf(false) }
    var isMemoryRecording by remember { mutableStateOf(false) }
    var currentModel by remember { mutableStateOf("None") }
    var currentTool by remember { mutableStateOf("None") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Privacy Center",
            style = MaterialTheme.typography.headlineMedium
        )

        // Current status section
        Text(
            text = "Current Status",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrivacyStatusItem(
                name = "Microphone",
                status = if (isMicrophoneActive) "Active" else "Inactive",
                color = if (isMicrophoneActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )

            PrivacyStatusItem(
                name = "Camera",
                status = if (isCameraActive) "Active" else "Inactive",
                color = if (isCameraActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )

            PrivacyStatusItem(
                name = "Network",
                status = if (isNetworkActive) "Active" else "Inactive",
                color = if (isNetworkActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )

            PrivacyStatusItem(
                name = "Memory",
                status = if (isMemoryRecording) "Recording" else "Not Recording",
                color = if (isMemoryRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )

            PrivacyStatusItem(
                name = "Current Model",
                status = currentModel,
                color = MaterialTheme.colorScheme.onSurface
            )

            PrivacyStatusItem(
                name = "Current Tool",
                status = currentTool,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Permission controls section
        Text(
            text = "Permission Controls",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrivacyToggle(
                name = "Microphone",
                description = "Allow microphone access for voice input",
                enabled = microphoneAccess,
                onToggle = { microphoneAccess = it }
            )

            PrivacyToggle(
                name = "Camera",
                description = "Allow camera access for vision tasks",
                enabled = cameraAccess,
                onToggle = { cameraAccess = it }
            )

            PrivacyToggle(
                name = "Location",
                description = "Allow location access for location-based services",
                enabled = locationAccess,
                onToggle = { locationAccess = it }
            )

            PrivacyToggle(
                name = "Bluetooth",
                description = "Allow Bluetooth access for device connections",
                enabled = bluetoothAccess,
                onToggle = { bluetoothAccess = it }
            )

            PrivacyToggle(
                name = "Files",
                description = "Allow file access for document processing",
                enabled = fileAccess,
                onToggle = { fileAccess = it }
            )

            PrivacyToggle(
                name = "Network",
                description = "Allow network access for model downloads",
                enabled = networkAccess,
                onToggle = { networkAccess = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data controls section
        Text(
            text = "Data Controls",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrivacyToggle(
                name = "Analytics",
                description = "Enable anonymous usage analytics",
                enabled = analyticsEnabled,
                onToggle = { analyticsEnabled = it }
            )

            // Additional data controls would go here
            Text(
                text = "Data Deletion",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delete All Conversations",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delete All Memories",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delete All Models",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy information section
        Text(
            text = "Privacy Information",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "• All AI processing happens locally on your device",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "• No audio or video is sent to the cloud without your permission",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "• Conversations and memories are stored locally and never shared",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "• You can delete your data at any time",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "• Model files are verified before loading to ensure integrity",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Displays a privacy status item.
 */
@Composable
fun PrivacyStatusItem(
    name: String,
    status: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = status,
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Displays a privacy toggle switch.
 */
@Composable
fun PrivacyToggle(
    name: String,
    description: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Switch(
            checked = enabled,
            onCheckedChange = onToggle
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyScreenPreview() {
    LiveHumanAITheme {
        PrivacyScreen()
    }
}
