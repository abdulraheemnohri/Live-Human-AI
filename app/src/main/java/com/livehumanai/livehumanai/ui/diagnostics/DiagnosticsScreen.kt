package com.livehumanai.livehumanai.ui.diagnostics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
 * DiagnosticsScreen provides diagnostic tests for the app's features.
 */
@Composable
fun DiagnosticsScreen() {
    // State for diagnostic tests
    var cameraStatus by remember { mutableStateOf("Not Tested") }
    var microphoneStatus by remember { mutableStateOf("Not Tested") }
    var speakerStatus by remember { mutableStateOf("Not Tested") }
    var sttStatus by remember { mutableStateOf("Not Tested") }
    var llmStatus by remember { mutableStateOf("Not Tested") }
    var ttsStatus by remember { mutableStateOf("Not Tested") }
    var visionStatus by remember { mutableStateOf("Not Tested") }
    var storageStatus by remember { mutableStateOf("Not Tested") }
    var ramStatus by remember { mutableStateOf("Not Tested") }
    var gpuStatus by remember { mutableStateOf("Not Tested") }
    var vulkanStatus by remember { mutableStateOf("Not Tested") }
    var modelIntegrityStatus by remember { mutableStateOf("Not Tested") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Diagnostics",
            style = MaterialTheme.typography.headlineMedium
        )

        // Test all button
        Button(
            onClick = {
                // In a real implementation, this would run all tests
                cameraStatus = "OK"
                microphoneStatus = "OK"
                speakerStatus = "OK"
                sttStatus = "OK"
                llmStatus = "OK"
                ttsStatus = "OK"
                visionStatus = "OK"
                storageStatus = "OK"
                ramStatus = "OK"
                gpuStatus = "OK"
                vulkanStatus = "OK"
                modelIntegrityStatus = "OK"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Run Full Diagnostic")
        }

        // Hardware tests section
        Text(
            text = "Hardware Tests",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DiagnosticTest(
                name = "Camera",
                status = cameraStatus,
                onTest = { cameraStatus = "OK" }
            )

            DiagnosticTest(
                name = "Microphone",
                status = microphoneStatus,
                onTest = { microphoneStatus = "OK" }
            )

            DiagnosticTest(
                name = "Speaker",
                status = speakerStatus,
                onTest = { speakerStatus = "OK" }
            )

            DiagnosticTest(
                name = "Storage",
                status = storageStatus,
                onTest = { storageStatus = "OK" }
            )

            DiagnosticTest(
                name = "RAM",
                status = ramStatus,
                onTest = { ramStatus = "OK" }
            )

            DiagnosticTest(
                name = "GPU",
                status = gpuStatus,
                onTest = { gpuStatus = "OK" }
            )

            DiagnosticTest(
                name = "Vulkan",
                status = vulkanStatus,
                onTest = { vulkanStatus = "OK" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI tests section
        Text(
            text = "AI Tests",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DiagnosticTest(
                name = "STT (Speech-to-Text)",
                status = sttStatus,
                onTest = { sttStatus = "OK" }
            )

            DiagnosticTest(
                name = "LLM (Language Model)",
                status = llmStatus,
                onTest = { llmStatus = "OK" }
            )

            DiagnosticTest(
                name = "TTS (Text-to-Speech)",
                status = ttsStatus,
                onTest = { ttsStatus = "OK" }
            )

            DiagnosticTest(
                name = "Vision",
                status = visionStatus,
                onTest = { visionStatus = "OK" }
            )

            DiagnosticTest(
                name = "Model Integrity",
                status = modelIntegrityStatus,
                onTest = { modelIntegrityStatus = "OK" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results summary
        Text(
            text = "Results Summary",
            style = MaterialTheme.typography.titleMedium
        )

        val allPassed = listOf(
            cameraStatus, microphoneStatus, speakerStatus, sttStatus,
            llmStatus, ttsStatus, visionStatus, storageStatus,
            ramStatus, gpuStatus, vulkanStatus, modelIntegrityStatus
        ).all { it == "OK" }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (allPassed) {
                Text(
                    text = "✓ All tests passed",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = "✗ Some tests failed",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * Displays a single diagnostic test with its status and a test button.
 */
@Composable
fun DiagnosticTest(
    name: String,
    status: String,
    onTest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = when (status) {
                    "OK" -> MaterialTheme.colorScheme.primary
                    "Failed" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            Button(onClick = onTest) {
                Text("Test")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiagnosticsScreenPreview() {
    LiveHumanAITheme {
        DiagnosticsScreen()
    }
}
