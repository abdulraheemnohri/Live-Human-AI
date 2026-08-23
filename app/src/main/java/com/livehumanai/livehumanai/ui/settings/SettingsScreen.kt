package com.livehumanai.livehumanai.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * SettingsScreen provides configuration options for the Live Human AI app.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // State for settings
    var performanceMode by remember { mutableStateOf("Balanced") }
    var enableWakeWord by remember { mutableStateOf(true) }
    var enableMemory by remember { mutableStateOf(true) }
    var enableCamera by remember { mutableStateOf(true) }
    var enableMicrophone by remember { mutableStateOf(true) }
    var enableNetwork by remember { mutableStateOf(false) }
    var enableObjectDetection by remember { mutableStateOf(true) }
    var enableOCR by remember { mutableStateOf(true) }

    var selectedModel by remember { mutableStateOf("Qwen3 1.7B Q4") }
    var showModelDialog by remember { mutableStateOf(false) }
    var showRetentionDialog by remember { mutableStateOf(false) }
    var retentionPeriod by remember { mutableStateOf("Indefinite") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // AI Settings section
        Text(
            text = "AI Settings",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Performance mode
            SettingItem(
                title = "Performance Mode",
                description = "Adjust AI performance based on your needs"
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectable(
                            selected = performanceMode == "Battery Saver",
                            role = Role.RadioButton
                        )
                    ) {
                        RadioButton(
                            selected = performanceMode == "Battery Saver",
                            onClick = { performanceMode = "Battery Saver" }
                        )
                        Text("Battery Saver")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectable(
                            selected = performanceMode == "Balanced",
                            role = Role.RadioButton
                        )
                    ) {
                        RadioButton(
                            selected = performanceMode == "Balanced",
                            onClick = { performanceMode = "Balanced" }
                        )
                        Text("Balanced")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectable(
                            selected = performanceMode == "Performance",
                            role = Role.RadioButton
                        )
                    ) {
                        RadioButton(
                            selected = performanceMode == "Performance",
                            onClick = { performanceMode = "Performance" }
                        )
                        Text("Performance")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectable(
                            selected = performanceMode == "Maximum",
                            role = Role.RadioButton
                        )
                    ) {
                        RadioButton(
                            selected = performanceMode == "Maximum",
                            onClick = { performanceMode = "Maximum" }
                        )
                        Text("Maximum")
                    }
                }
            }

            // Default model
            SettingItem(
                title = "Default Model ($selectedModel)",
                description = "Select the default AI model"
            ) {
                Button(onClick = { showModelDialog = true }) {
                    Text("Select Model")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Voice Settings section
        Text(
            text = "Voice Settings",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Wake word
            SettingItem(
                title = "Wake Word",
                description = "Enable wake word detection"
            ) {
                Switch(
                    checked = enableWakeWord,
                    onCheckedChange = { enableWakeWord = it }
                )
            }

            // Microphone
            SettingItem(
                title = "Microphone",
                description = "Allow microphone access"
            ) {
                Switch(
                    checked = enableMicrophone,
                    onCheckedChange = { enableMicrophone = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vision Settings section
        Text(
            text = "Vision Settings",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Camera
            SettingItem(
                title = "Camera",
                description = "Allow camera access"
            ) {
                Switch(
                    checked = enableCamera,
                    onCheckedChange = { enableCamera = it }
                )
            }

            // Object detection
            SettingItem(
                title = "Object Detection",
                description = "Enable object detection in camera"
            ) {
                Switch(
                    checked = enableObjectDetection,
                    onCheckedChange = { enableObjectDetection = it }
                )
            }

            // OCR
            SettingItem(
                title = "Text Recognition (OCR)",
                description = "Enable text recognition in camera"
            ) {
                Switch(
                    checked = enableOCR,
                    onCheckedChange = { enableOCR = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Memory Settings section
        Text(
            text = "Memory Settings",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Memory
            SettingItem(
                title = "Memory",
                description = "Enable AI memory features"
            ) {
                Switch(
                    checked = enableMemory,
                    onCheckedChange = { enableMemory = it }
                )
            }

            // Memory retention
            SettingItem(
                title = "Memory Retention ($retentionPeriod)",
                description = "Set how long to keep memories"
            ) {
                Button(onClick = { showRetentionDialog = true }) {
                    Text("Configure")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Network Settings section
        Text(
            text = "Network Settings",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Network access
            SettingItem(
                title = "Network Access",
                description = "Allow network access for model downloads"
            ) {
                Switch(
                    checked = enableNetwork,
                    onCheckedChange = { enableNetwork = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About section
        Text(
            text = "About",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingItem(
                title = "Version",
                description = "1.0.0"
            )

            SettingItem(
                title = "License",
                description = "Apache 2.0"
            )

            SettingItem(
                title = "Source Code",
                description = "View on GitHub"
            ) {
                Button(onClick = {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://github.com/abdulraheemnohri/Live-Human-AI")
                    )
                    context.startActivity(intent)
                }) {
                    Text("Open")
                }
            }
        }

        // Model Dialog
        if (showModelDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showModelDialog = false },
                title = { Text("Select Default Model") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Qwen3 0.6B Q4", "Qwen3 1.7B Q4", "Qwen3 4B Q4", "Whisper Base", "YOLO Nano").forEach { model ->
                            Button(
                                onClick = {
                                    selectedModel = model
                                    showModelDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(model)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showModelDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Retention Dialog
        if (showRetentionDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showRetentionDialog = false },
                title = { Text("Memory Retention") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("7 Days", "30 Days", "1 Year", "Indefinite").forEach { option ->
                            Button(
                                onClick = {
                                    retentionPeriod = option
                                    showRetentionDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(option)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showRetentionDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

/**
 * Displays a single setting item with a title, description, and optional actions.
 */
@Composable
fun SettingItem(
    title: String,
    description: String,
    action: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }

        action()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    LiveHumanAITheme {
        SettingsScreen()
    }
}
