package com.livehumanai.livehumanai.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
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
 * HomeScreen is the main screen of the Live Human AI app.
 * It provides quick access to the main features: Voice, Camera, and Settings.
 */
@Composable
fun HomeScreen(
    onNavigateToChat: () -> Unit = {},
    onNavigateToCamera: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    var runtimeStatus by remember { mutableStateOf("Ready") }
    var deviceProfile by remember { mutableStateOf("Balanced (6GB Profile)") }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val nativeBridge = com.livehumanai.livehumanai.native.NativeBridge.getInstance()
        if (nativeBridge.isInitialized) {
            runtimeStatus = nativeBridge.getRuntimeStatus()
            deviceProfile = nativeBridge.getDeviceProfile()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Live Human AI",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status indicators
        Text(
            text = "Status: $runtimeStatus",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Profile: $deviceProfile",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Main AI status indicator
        Text(
            text = "● READY",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Camera preview placeholder
        Column(
            modifier = Modifier
                .height(200.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LIVE CAMERA",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "(optional)",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Helper text
        Text(
            text = "How can I help?",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action buttons
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Voice button
            IconButton(
                onClick = onNavigateToChat,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Camera button
            IconButton(
                onClick = onNavigateToCamera,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Camera",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // System status
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CPU: 32% | RAM: 48% | TEMP: OK",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Settings button (top-right)
        IconButton(
            onClick = onNavigateToSettings,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LiveHumanAITheme {
        HomeScreen()
    }
}
