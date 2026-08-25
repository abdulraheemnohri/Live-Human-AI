package com.livehumanai.livehumanai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livehumanai.livehumanai.jalebi.JalebiTelemetry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JalebiActivityBottomSheet(
    telemetry: JalebiTelemetry,
    developerMode: Boolean,
    onDismiss: () -> Unit,
    onStop: () -> Unit,
    onToggleDeveloperMode: (Boolean) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "AI Activity",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Iteration ${telemetry.iteration} / 8",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            if (!developerMode) {
                Text("● ${telemetry.activeStage}", style = MaterialTheme.typography.titleMedium)
                Text("Confidence: ${(telemetry.confidence * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
                LinearProgressIndicator(
                    progress = { telemetry.confidence.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("State: ${telemetry.state}", style = MaterialTheme.typography.bodyMedium)
                Text("Model: ${telemetry.model.ifBlank { "Auto" }}", style = MaterialTheme.typography.bodyMedium)
                Text("Latency: ${telemetry.latencyMs} ms", style = MaterialTheme.typography.bodyMedium)
                Text("RAM: ${telemetry.ramPercent.toInt()}% | CPU: ${telemetry.cpuPercent.toInt()}% | Temp: ${telemetry.temperatureC.toInt()}°C", style = MaterialTheme.typography.bodySmall)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onToggleDeveloperMode(!developerMode) }) {
                    Text(if (developerMode) "Normal View" else "Developer View")
                }
                Button(onClick = onStop) {
                    Text("Stop AI")
                }
            }
        }
    }
}
