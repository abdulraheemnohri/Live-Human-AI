package com.livehumanai.livehumanai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livehumanai.livehumanai.ui.components.AiOrb
import com.livehumanai.livehumanai.ui.components.AiOrbState
import com.livehumanai.livehumanai.ui.theme.LiveHumanAITheme

@Composable
fun HomeScreen(
    onNavigateToChat: () -> Unit = {},
    onNavigateToCamera: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToJalebi: () -> Unit = {}
) {
    var runtimeStatus by remember { mutableStateOf("Ready") }
    var deviceProfile by remember { mutableStateOf("Balanced") }
    var promptInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val n = com.livehumanai.livehumanai.nativebridge.NativeBridge.getInstance()
        if (n.isInitialized) {
            runtimeStatus = n.getRuntimeStatus()
            deviceProfile = n.getDeviceProfile()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Live Human AI", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }

        // Center AI Console
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AiOrb(state = AiOrbState.IDLE, size = 120.dp)

            Text("AI READY", style = MaterialTheme.typography.titleLarge)
            Text("\"How can I help?\"", style = MaterialTheme.typography.bodyLarge)

            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = { Text("Ask anything...") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = onNavigateToChat) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Prompt")
                    }
                }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                IconButton(onClick = onNavigateToChat, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice Input")
                }
                IconButton(onClick = onNavigateToCamera, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.Videocam, contentDescription = "Camera Input")
                }
            }
        }

        // Today's AI Summary Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Today's AI Console", style = MaterialTheme.typography.titleMedium)
                Text("Status: $runtimeStatus • Profile: $deviceProfile", style = MaterialTheme.typography.bodyMedium)
                Text("Offline Mode ✓", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Button(onClick = onNavigateToJalebi, modifier = Modifier.fillMaxWidth()) {
                    Text("Jalebi Cognitive Loop Console")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LiveHumanAITheme { HomeScreen() }
}
