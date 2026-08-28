package com.livehumanai.livehumanai.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.livehumanai.livehumanai.ui.theme.LiveHumanAITheme
import com.livehumanai.livehumanai.ui.viewmodel.SettingsViewModel

/**
 * SettingsScreen provides configuration options for the Live Human AI app.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settingsState by viewModel.settings.collectAsState()

    // Observe values from ViewModel
    val performanceMode = (settingsState["performanceMode"] as? String) ?: "Balanced"
    val enableWakeWord = (settingsState["wakeWordEnabled"] as? Boolean) ?: true
    val enableMemory = (settingsState["memoryEnabled"] as? Boolean) ?: true
    val enableCamera = (settingsState["cameraEnabled"] as? Boolean) ?: true
    val enableMicrophone = (settingsState["microphoneEnabled"] as? Boolean) ?: true
    val enableNetwork = (settingsState["networkEnabled"] as? Boolean) ?: false
    val enableObjectDetection = (settingsState["objectDetectionEnabled"] as? Boolean) ?: true
    val enableOCR = (settingsState["ocrEnabled"] as? Boolean) ?: true

    val selectedModel = (settingsState["defaultModel"] as? String) ?: "qwen3-1.7b-q4"
    var showModelDialog by remember { mutableStateOf(false) }
    var showRetentionDialog by remember { mutableStateOf(false) }
    val retentionPeriodDays = (settingsState["memoryRetentionDays"] as? Int) ?: 30

    // Scroll state for settings screen
    val scrollState = rememberScrollState()

    // AI & Jalebi Loop state from ViewModel
    val temperature = (settingsState["temperature"] as? Float) ?: 0.7f
    val topP = (settingsState["topP"] as? Float) ?: 0.9f
    val topK = ((settingsState["topK"] as? Number)?.toFloat()) ?: 40f
    val jalebiMaxIterations = ((settingsState["jalebiMaxIterations"] as? Number)?.toFloat()) ?: 8f
    val jalebiConfidenceThreshold = (settingsState["jalebiConfidenceThreshold"] as? Float) ?: 0.85f

    // Voice & Vision state from ViewModel
    val speechSpeed = (settingsState["speechSpeed"] as? Float) ?: 1.0f
    val speechPitch = (settingsState["speechPitch"] as? Float) ?: 1.0f

    // Theme & Accessibility state from ViewModel
    val themeMode = (settingsState["themeMode"] as? String) ?: "Dark"
    val enableHighContrast = (settingsState["highContrastEnabled"] as? Boolean) ?: false
    val enableReducedMotion = (settingsState["reducedMotionEnabled"] as? Boolean) ?: false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                    listOf("Battery Saver", "Balanced", "Performance", "Maximum").forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.selectable(
                                selected = (performanceMode == mode),
                                onClick = { viewModel.saveSetting("performanceMode", mode) },
                                role = Role.RadioButton
                            )
                        ) {
                            RadioButton(
                                selected = (performanceMode == mode),
                                onClick = null
                            )
                            Text(mode)
                        }
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

            // Temperature Slider
            SettingItem(
                title = "Temperature: ${"%.2f".format(temperature)}",
                description = "Controls output randomness (0.0 = deterministic, 1.0 = creative)"
            ) {
                Slider(
                    value = temperature,
                    onValueChange = { viewModel.saveSetting("temperature", it) },
                    valueRange = 0.0f..1.0f,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }

            // Top-P Slider
            SettingItem(
                title = "Top-P: ${"%.2f".format(topP)}",
                description = "Nucleus sampling probability threshold"
            ) {
                Slider(
                    value = topP,
                    onValueChange = { viewModel.saveSetting("topP", it) },
                    valueRange = 0.1f..1.0f,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }

            // Top-K Slider
            SettingItem(
                title = "Top-K: ${topK.toInt()}",
                description = "Limits top token candidate pool"
            ) {
                Slider(
                    value = topK,
                    onValueChange = { viewModel.saveSetting("topK", it.toInt()) },
                    valueRange = 1f..100f,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Jalebi Cognitive Loop / Autonomy Settings section
        Text(
            text = "Jalebi Cognitive Loop Settings",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingItem(
                title = "Max Iterations: ${jalebiMaxIterations.toInt()}",
                description = "Bounded iteration limit for autonomous loops"
            ) {
                Slider(
                    value = jalebiMaxIterations,
                    onValueChange = { viewModel.saveSetting("jalebiMaxIterations", it.toInt()) },
                    valueRange = 1f..20f,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }

            SettingItem(
                title = "Confidence Threshold: ${"%.2f".format(jalebiConfidenceThreshold)}",
                description = "Minimum evaluation confidence before completing task"
            ) {
                Slider(
                    value = jalebiConfidenceThreshold,
                    onValueChange = { viewModel.saveSetting("jalebiConfidenceThreshold", it) },
                    valueRange = 0.50f..0.99f,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
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
                    onCheckedChange = { viewModel.saveSetting("wakeWordEnabled", it) }
                )
            }

            // Microphone
            SettingItem(
                title = "Microphone",
                description = "Allow microphone access"
            ) {
                Switch(
                    checked = enableMicrophone,
                    onCheckedChange = { viewModel.saveSetting("microphoneEnabled", it) }
                )
            }

            // Speech Speed
            SettingItem(
                title = "Speech Speed: ${"%.1f".format(speechSpeed)}x",
                description = "Playback rate for text-to-speech engine"
            ) {
                Slider(
                    value = speechSpeed,
                    onValueChange = { viewModel.saveSetting("speechSpeed", it) },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }

            // Speech Pitch
            SettingItem(
                title = "Speech Pitch: ${"%.1f".format(speechPitch)}x",
                description = "Voice tone pitch for synthesis"
            ) {
                Slider(
                    value = speechPitch,
                    onValueChange = { viewModel.saveSetting("speechPitch", it) },
                    valueRange = 0.5f..1.5f,
                    modifier = Modifier.fillMaxWidth(0.6f)
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
                    onCheckedChange = { viewModel.saveSetting("cameraEnabled", it) }
                )
            }

            // Object detection
            SettingItem(
                title = "Object Detection",
                description = "Enable object detection in camera"
            ) {
                Switch(
                    checked = enableObjectDetection,
                    onCheckedChange = { viewModel.saveSetting("objectDetectionEnabled", it) }
                )
            }

            // OCR
            SettingItem(
                title = "Text Recognition (OCR)",
                description = "Enable text recognition in camera"
            ) {
                Switch(
                    checked = enableOCR,
                    onCheckedChange = { viewModel.saveSetting("ocrEnabled", it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Appearance & Accessibility section
        Text(
            text = "Appearance & Accessibility",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingItem(
                title = "Theme Mode ($themeMode)",
                description = "Choose app color theme"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Dark", "Light", "System").forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.selectable(
                                selected = (themeMode == mode),
                                onClick = { viewModel.saveSetting("themeMode", mode) },
                                role = Role.RadioButton
                            )
                        ) {
                            RadioButton(
                                selected = (themeMode == mode),
                                onClick = null
                            )
                            Text(mode)
                        }
                    }
                }
            }

            SettingItem(
                title = "High Contrast Mode",
                description = "Increase contrast ratio for UI elements"
            ) {
                Switch(
                    checked = enableHighContrast,
                    onCheckedChange = { viewModel.saveSetting("highContrastEnabled", it) }
                )
            }

            SettingItem(
                title = "Reduced Motion",
                description = "Minimize scale and dynamic animations"
            ) {
                Switch(
                    checked = enableReducedMotion,
                    onCheckedChange = { viewModel.saveSetting("reducedMotionEnabled", it) }
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
                    onCheckedChange = { viewModel.saveSetting("memoryEnabled", it) }
                )
            }

            // Memory retention
            SettingItem(
                title = "Memory Retention ($retentionPeriodDays days)",
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
                    onCheckedChange = { viewModel.saveSetting("networkEnabled", it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Activity & Diagnostic Logs section
        Text(
            text = "Diagnostic Activity Logs",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingItem(
                title = "Clear Activity Logs",
                description = "Reset in-memory activity logs and error history"
            ) {
                Button(onClick = { viewModel.saveSetting("activityLogsCleared", true) }) {
                    Text("Clear")
                }
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
                        listOf("qwen3-0.6b-q4", "qwen3-1.7b-q4", "qwen3-4b-q4", "whisper-base", "yolo-nano").forEach { model ->
                            Button(
                                onClick = {
                                    viewModel.saveSetting("defaultModel", model)
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
                        mapOf("7 Days" to 7, "30 Days" to 30, "1 Year" to 365, "Indefinite" to 3650).forEach { (label, days) ->
                            Button(
                                onClick = {
                                    viewModel.saveSetting("memoryRetentionDays", days)
                                    showRetentionDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(label)
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
            modifier = Modifier.weight(1f),
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
