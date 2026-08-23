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
    // State for settings
    var performanceMode by remember { mutableStateOf("Balanced") }
    var enableWakeWord by remember { mutableStateOf(true) }
    var enableMemory by remember { mutableStateOf(true) }
    var enableCamera by remember { mutableStateOf(true) }
    var enableMicrophone by remember { mutableStateOf(true) }
    var enableNetwork by remember { mutableStateOf(false) }

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
                title = "Default Model",
                description = "Select the default AI model"
            ) {
                Button(onClick = { /* TODO: Open model selection */ }) {
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
                    checked = true,
                    onCheckedChange = { /* TODO: Toggle object detection */ }
                )
            }

            // OCR
            SettingItem(
                title = "Text Recognition (OCR)",
                description = "Enable text recognition in camera"
            ) {
                Switch(
                    checked = true,
                    onCheckedChange = { /* TODO: Toggle OCR */ }
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
                title = "Memory Retention",
                description = "Set how long to keep memories"
            ) {
                Button(onClick = { /* TODO: Open retention settings */ }) {
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
                Button(onClick = { /* TODO: Open GitHub */ }) {
                    Text("Open")
                }
            }
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
