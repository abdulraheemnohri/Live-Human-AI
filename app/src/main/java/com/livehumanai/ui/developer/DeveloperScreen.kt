package com.livehumanai.ui.developer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nativeRuntimeStatus by remember { mutableStateOf("Running") }
    var jniStatus by remember { mutableStateOf("Connected") }
    var loadedModels by remember { mutableStateOf(listOf("Qwen3-1.7B-Q4", "Whisper-Base")) }
    var threadCount by remember { mutableStateOf(4) }
    var tokensPerSec by remember { mutableStateOf(28.5f) }
    var sttLatency by remember { mutableStateOf(145) }
    var visionFps by remember { mutableStateOf(12) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Developer") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Runtime Status Section
                DeveloperSection(title = "Runtime Status") {
                    DeveloperStatRow(label = "Native Runtime", value = nativeRuntimeStatus)
                    DeveloperStatRow(label = "JNI Bridge", value = jniStatus)
                    DeveloperStatRow(label = "Thread Count", value = threadCount.toString())
                    DeveloperStatRow(label = "Active Models", value = loadedModels.size.toString())
                }
                
                // Performance Metrics Section
                DeveloperSection(title = "Performance Metrics") {
                    DeveloperStatRow(label = "Tokens/sec", value = "%.1f".format(tokensPerSec))
                    DeveloperStatRow(label = "STT Latency", value = "${sttLatency}ms")
                    DeveloperStatRow(label = "Vision FPS", value = visionFps.toString())
                }
                
                // Loaded Models Section
                DeveloperSection(title = "Loaded Models") {
                    loadedModels.forEach { model ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = model,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                AssistChip(
                                    onClick = { },
                                    label = { Text("Active", style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Native Logs Section
                DeveloperSection(title = "Native Logs") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.9f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "[INFO] Engine initialized\n[INFO] Model loaded: Qwen3-1.7B-Q4\n[INFO] STT ready: Whisper-Base\n[DEBUG] Thread pool size: 4\n[INFO] Vision pipeline started\n[DEBUG] Memory usage: 2.4GB / 6GB\n[INFO] Jalebi loop engine ready",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Green,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { /* Clear logs */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear Logs")
                        }
                        
                        OutlinedButton(
                            onClick = { /* Export logs */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Export")
                        }
                    }
                }
                
                // Debug Actions Section
                DeveloperSection(title = "Debug Actions") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { /* Run diagnostics */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Run Full Diagnostic")
                        }
                        
                        OutlinedButton(
                            onClick = { /* Benchmark models */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Benchmark All Models")
                        }
                        
                        OutlinedButton(
                            onClick = { /* Toggle debug mode */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Toggle Debug Mode")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeveloperSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun DeveloperStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}
