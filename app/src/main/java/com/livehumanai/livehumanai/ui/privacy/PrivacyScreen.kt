package com.livehumanai.livehumanai.ui.privacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livehumanai.livehumanai.ui.theme.LiveHumanAITheme

@Composable
fun PrivacyScreen() {
    var microphoneAccess by remember { mutableStateOf(true) }
    var cameraAccess by remember { mutableStateOf(true) }
    var locationAccess by remember { mutableStateOf(false) }
    var networkAccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Privacy Center", style = MaterialTheme.typography.headlineMedium)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Local AI Processing", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text("● All inference runs 100% locally on your phone", style = MaterialTheme.typography.bodyMedium)
                Text("● No video or audio is ever uploaded to external servers", style = MaterialTheme.typography.bodyMedium)
                Text("● Local Mode Active", style = MaterialTheme.typography.labelMedium)
            }
        }

        Text("Permission Controls", style = MaterialTheme.typography.titleMedium)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PrivacyRow("Microphone", "Voice conversation & STT", microphoneAccess) { microphoneAccess = it }
            PrivacyRow("Camera", "Vision scene understanding", cameraAccess) { cameraAccess = it }
            PrivacyRow("Location", "Location context (Disabled by default)", locationAccess) { locationAccess = it }
            PrivacyRow("Network", "Model downloads only (Offline for AI inference)", networkAccess) { networkAccess = it }
        }

        Spacer(Modifier.height(8.dp))
        Text("Data Management", style = MaterialTheme.typography.titleMedium)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Delete Conversations")
            }
            OutlinedButton(onClick = {}) {
                Text("Clear Memories")
            }
        }
    }
}

@Composable
private fun PrivacyRow(name: String, desc: String, enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyLarge)
            Text(desc, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = enabled, onCheckedChange = onToggle)
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyScreenPreview() {
    LiveHumanAITheme { PrivacyScreen() }
}
