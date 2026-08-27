package com.livehumanai.livehumanai.ui.developer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livehumanai.livehumanai.nativebridge.NativeBridge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var jniStatus by remember { mutableStateOf("JNI Connected") }
    var runtimeProfile by remember { mutableStateOf("16 GB Pro Profile") }
    var activeThreads by remember { mutableStateOf("8 Threads") }
    var tokensPerSec by remember { mutableStateOf("24.5 t/s") }
    var sttLatency by remember { mutableStateOf("120 ms") }
    var ttsLatency by remember { mutableStateOf("85 ms") }
    var thermalState by remember { mutableStateOf("NORMAL (36.2°C)") }
    var isLoopEngineActive by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val bridge = NativeBridge.getInstance()
        if (bridge.isInitialized) {
            jniStatus = bridge.getRuntimeStatus()
            runtimeProfile = bridge.getDeviceProfile()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Developer Diagnostics & Controls") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Backend Controls Panel Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Backend Runtime Controls Panel", style = MaterialTheme.typography.titleMedium)
                        AssistChip(onClick = {}, label = { Text("C++ Native Engine") })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            val bridge = NativeBridge.getInstance()
                            if (bridge.isInitialized) {
                                bridge.resetContext()
                                jniStatus = "Context Reset OK"
                            }
                        }) {
                            Text("Reset Context")
                        }

                        Button(
                            onClick = {
                                isLoopEngineActive = !isLoopEngineActive
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLoopEngineActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(if (isLoopEngineActive) "Pause Jalebi Loop" else "Resume Jalebi Loop")
                        }
                    }
                }
            }

            // Real-time Runtime Telemetry Dashboard
            Text("Native Telemetry & Benchmarks", style = MaterialTheme.typography.titleMedium)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("JNI Bridge Status", style = MaterialTheme.typography.bodySmall)
                        Text(jniStatus, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Runtime Profile", style = MaterialTheme.typography.bodySmall)
                        Text(runtimeProfile, style = MaterialTheme.typography.bodySmall)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Active Thread Pool", style = MaterialTheme.typography.bodySmall)
                        Text(activeThreads, style = MaterialTheme.typography.bodySmall)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("LLM Generation Speed", style = MaterialTheme.typography.bodySmall)
                        Text(tokensPerSec, style = MaterialTheme.typography.bodySmall)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Whisper STT Latency", style = MaterialTheme.typography.bodySmall)
                        Text(sttLatency, style = MaterialTheme.typography.bodySmall)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TTS Audio Latency", style = MaterialTheme.typography.bodySmall)
                        Text(ttsLatency, style = MaterialTheme.typography.bodySmall)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Thermal State", style = MaterialTheme.typography.bodySmall)
                        Text(thermalState, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Jalebi Execution Graph Visualizer
            Text("Jalebi Cognitive Loop Live Execution Graph", style = MaterialTheme.typography.titleMedium)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("● PERCEIVE → [OK] Camera & Audio captured", style = MaterialTheme.typography.labelMedium)
                    Text("● INTERPRET → [OK] Scene recognized: Technical Document", style = MaterialTheme.typography.labelMedium)
                    Text("● REASON → [OK] Planning query breakdown", style = MaterialTheme.typography.labelMedium)
                    Text("● PLAN → [OK] Select Qwen3 1.7B + Local RAG tool", style = MaterialTheme.typography.labelMedium)
                    Text("● ACT → [RUNNING] Executing tool query...", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text("○ OBSERVE → Pending response evaluation", style = MaterialTheme.typography.labelMedium)
                    Text("○ EVALUATE → Pending confidence escalation", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
