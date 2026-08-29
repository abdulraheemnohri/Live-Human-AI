package com.livehumanai.livehumanai.ui.models

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.livehumanai.livehumanai.ui.theme.LiveHumanAITheme
import com.livehumanai.livehumanai.ui.viewmodel.ModelViewModel

@Composable
fun ModelManagerScreen(viewModel: ModelViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val models by viewModel.models.collectAsState()
    val downloadProgressMap by viewModel.downloadProgress.collectAsState()
    val operationMessage by viewModel.operationMessage.collectAsState()
    var hfRepoId by remember { mutableStateOf("Qwen/Qwen2.5-0.5B-Instruct-GGUF") }
    var hfFilename by remember { mutableStateOf("qwen2.5-0.5b-instruct-q4_k_m.gguf") }
    var selectedPerformanceMode by remember { mutableStateOf("Balanced") }
    val isHfDownloading = downloadProgressMap.containsKey(hfFilename)
    val hfProgress = downloadProgressMap[hfFilename] ?: 0f
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(operationMessage) {
        operationMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearOperationMessage()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("AI Model Management & Catalog", style = MaterialTheme.typography.headlineMedium)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Hugging Face Model Downloader", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = hfRepoId,
                        onValueChange = { hfRepoId = it },
                        label = { Text("Hugging Face Repo ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = hfFilename,
                        onValueChange = { hfFilename = it },
                        label = { Text("Model File (.gguf / .bin / .onnx)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    if (isHfDownloading) {
                        LinearProgressIndicator(progress = { hfProgress }, modifier = Modifier.fillMaxWidth())
                    }
                    Button(
                        onClick = { viewModel.downloadHuggingFaceModel(hfRepoId.trim(), hfFilename.trim(), context.filesDir) },
                        enabled = !isHfDownloading && hfRepoId.isNotBlank() && hfFilename.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                        Spacer(Modifier.width(8.dp))
                        Text(if (isHfDownloading) "Downloading ${(hfProgress * 100).toInt()}%" else "Download, verify, and register")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Hardware Profile", style = MaterialTheme.typography.titleMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Saver", "Balanced", "Pro", "Max").forEach { mode ->
                            FilterChip(selected = selectedPerformanceMode == mode, onClick = { selectedPerformanceMode = mode }, label = { Text(mode) })
                        }
                    }
                    Text(
                        "Selected: $selectedPerformanceMode • ${if (selectedPerformanceMode == "Saver") "Prefer smaller models and lower memory pressure" else "Allow larger models when device resources permit"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Text("Registered Models", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (models.isEmpty()) {
                    item {
                        Text("No models are registered yet. Download a model above; it appears here only after the file is fully downloaded and its checksum is calculated.")
                    }
                }
                items(models, key = { it.name }) { model ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(model.name, style = MaterialTheme.typography.titleMedium)
                                Text("${model.type} • ${model.format} • ${model.size / (1024 * 1024)} MB • RAM ${model.ramRequirement / (1024 * 1024)} MB", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    when {
                                        !model.isInstalled -> "Not installed"
                                        model.isLoaded -> "Loaded in native runtime"
                                        else -> "Installed locally"
                                    },
                                    color = if (model.isLoaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                            if (model.isInstalled) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { if (model.isLoaded) viewModel.unloadModel(model.name) else viewModel.loadModel(model.name) }) {
                                        Icon(if (model.isLoaded) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = if (model.isLoaded) "Unload" else "Load")
                                    }
                                    IconButton(onClick = { viewModel.deleteModel(model.name) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModelManagerScreenPreview() {
    LiveHumanAITheme { ModelManagerScreen() }
}
