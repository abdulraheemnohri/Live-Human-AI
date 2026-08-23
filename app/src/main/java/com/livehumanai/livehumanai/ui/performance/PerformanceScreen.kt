package com.livehumanai.livehumanai.ui.performance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
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
 * PerformanceScreen displays real-time performance metrics for the AI runtime.
 */
@Composable
fun PerformanceScreen() {
    // State for performance metrics
    var cpuUsage by remember { mutableStateOf(15f) }
    var ramUsage by remember { mutableStateOf(35f) }
    var gpuUsage by remember { mutableStateOf(0f) }
    var temperature by remember { mutableStateOf(32f) }
    var batteryLevel by remember { mutableStateOf(90f) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val nativeBridge = com.livehumanai.livehumanai.native.NativeBridge.getInstance()
        if (nativeBridge.isInitialized) {
            val cpu = nativeBridge.getCPUUsage()
            val ramPct = nativeBridge.getRAMUsagePercentage()
            val temp = nativeBridge.getTemperature()
            val batt = nativeBridge.getBatteryLevel()

            if (cpu > 0f) cpuUsage = cpu
            if (ramPct > 0f) ramUsage = ramPct
            if (temp > 0f) temperature = temp
            if (batt > 0f) batteryLevel = batt
        }
    }
    var fps by remember { mutableStateOf(15f) }
    var tokensPerSecond by remember { mutableStateOf(10f) }
    var sttLatency by remember { mutableStateOf(120f) }
    var ttsLatency by remember { mutableStateOf(80f) }
    var modelLoadTime by remember { mutableStateOf(2500f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Performance Monitor",
            style = MaterialTheme.typography.headlineMedium
        )

        // Hardware metrics section
        Text(
            text = "Hardware Metrics",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // CPU Usage
            PerformanceMetric(
                name = "CPU Usage",
                value = "$cpuUsage%",
                progress = cpuUsage / 100f,
                unit = "%"
            )

            // RAM Usage
            PerformanceMetric(
                name = "RAM Usage",
                value = "$ramUsage%",
                progress = ramUsage / 100f,
                unit = "%"
            )

            // GPU Usage
            PerformanceMetric(
                name = "GPU Usage",
                value = if (gpuUsage > 0) "$gpuUsage%" else "N/A",
                progress = gpuUsage / 100f,
                unit = "%"
            )

            // Temperature
            PerformanceMetric(
                name = "Temperature",
                value = "$temperature°C",
                progress = temperature / 100f, // Assuming max 100°C
                unit = "°C"
            )

            // Battery Level
            PerformanceMetric(
                name = "Battery Level",
                value = "$batteryLevel%",
                progress = batteryLevel / 100f,
                unit = "%"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Performance metrics section
        Text(
            text = "AI Performance",
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // FPS
            PerformanceMetric(
                name = "Vision FPS",
                value = "$fps",
                progress = fps / 30f, // Assuming max 30 FPS
                unit = "FPS"
            )

            // Tokens per second
            PerformanceMetric(
                name = "Tokens/Second",
                value = "$tokensPerSecond",
                progress = tokensPerSecond / 50f, // Assuming max 50 tokens/sec
                unit = "t/s"
            )

            // STT Latency
            PerformanceMetric(
                name = "STT Latency",
                value = "$sttLatency ms",
                progress = sttLatency / 1000f, // Assuming max 1000ms
                unit = "ms"
            )

            // TTS Latency
            PerformanceMetric(
                name = "TTS Latency",
                value = "$ttsLatency ms",
                progress = ttsLatency / 1000f, // Assuming max 1000ms
                unit = "ms"
            )

            // Model Load Time
            PerformanceMetric(
                name = "Model Load Time",
                value = "$modelLoadTime ms",
                progress = modelLoadTime / 10000f, // Assuming max 10000ms
                unit = "ms"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Thermal state indicator
        Text(
            text = "Thermal State",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val thermalState = when {
                temperature >= 60 -> "CRITICAL"
                temperature >= 50 -> "HOT"
                temperature >= 40 -> "WARM"
                else -> "NORMAL"
            }

            val thermalColor = when (thermalState) {
                "CRITICAL" -> MaterialTheme.colorScheme.error
                "HOT" -> MaterialTheme.colorScheme.errorContainer
                "WARM" -> MaterialTheme.colorScheme.warning
                else -> MaterialTheme.colorScheme.primary
            }

            Text(
                text = thermalState,
                color = thermalColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Performance advice
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Performance Advice",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = when {
                temperature >= 60 -> "Device temperature is high. AI performance has been temporarily reduced."
                batteryLevel <= 20 -> "Battery is low. Consider connecting to a charger for optimal performance."
                ramUsage >= 90 -> "RAM usage is high. Some models may be unloaded to free up memory."
                else -> "Device is running optimally."
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Displays a single performance metric with a progress bar.
 */
@Composable
fun PerformanceMetric(
    name: String,
    value: String,
    progress: Float,
    unit: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PerformanceScreenPreview() {
    LiveHumanAITheme {
        PerformanceScreen()
    }
}
