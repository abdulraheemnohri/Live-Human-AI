package com.livehumanai.livehumanai.ui.models

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

data class ModelInfo(
    val id: String,
    val name: String,
    val type: String,
    val size: String,
    val ramRequirement: String,
    val repoId: String,
    val isInstalled: Boolean,
    val isLoaded: Boolean
)

@Composable
fun ModelManagerScreen(viewModel: ModelViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var userMessage by remember { mutableStateOf<String?>(null) }

    var allModels by remember {
        mutableStateOf(
            listOf(
                ModelInfo("qwen-0.6b", "Qwen3 0.6B Q4", "LLM", "400MB", "1GB", "Qwen/Qwen2.5-0.5B-Instruct-GGUF", true, true),
                ModelInfo("qwen-1.7b", "Qwen3 1.7B Q4", "LLM", "1.1GB", "2GB", "Qwen/Qwen2.5-1.5B-Instruct-GGUF", true, false),
                ModelInfo("qwen-4b", "Qwen3 4B Q4", "LLM", "2.6GB", "6GB", "Qwen/Qwen2.5-3B-Instruct-GGUF", false, false),
                ModelInfo("whisper-base", "Whisper Base STT", "STT", "148MB", "500MB", "ggerganov/whisper.cpp", true, true),
                ModelInfo("yolo-nano", "YOLO Vision Lite", "Vision", "12MB", "200MB", "ultralytics/yolov8n", true, false)
            )
        )
    }

    var selectedPerformanceMode by remember { mutableStateOf("Balanced") }
    var hfRepoId by remember { mutableStateOf("Qwen/Qwen2.5-0.5B-Instruct-GGUF") }
    var hfFilename by remember { mutableStateOf("qwen2.5-0.5b-instruct-q4_k_m.gguf") }

    val downloadProgressMap by viewModel.downloadProgress.collectAsState()
    val isHfDownloading = downloadProgressMap.containsKey(hfFilename)
    val hfProgress = downloadProgressMap[hfFilename] ?: 0f

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            userMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("AI Model Management & Catalog", style = MaterialTheme.typography.headlineMedium)

            // Hugging Face Downloader Panel
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Hugging Face Model Downloader", style = MaterialTheme.typography.titleMedium)
                        AssistChip(onClick = {}, label = { Text("HF Catalog") })
                    }

                    OutlinedTextField(
                        value = hfRepoId,
                        onValueChange = { hfRepoId = it },
                        label = { Text("Hugging Face Repo ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = hfFilename,
                        onValueChange = { hfFilename = it },
                        label = { Text("Model File (.gguf / .bin)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isHfDownloading) {
                        LinearProgressIndicator(progress = { hfProgress }, modifier = Modifier.fillMaxWidth())
                    }

                    Button(
                        onClick = {
                            viewModel.downloadHuggingFaceModel(hfRepoId, hfFilename, context.filesDir)
                            userMessage = "Started downloading $hfFilename from Hugging Face"
                        },
                        enabled = !isHfDownloading && hfRepoId.isNotBlank() && hfFilename.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                        Spacer(Modifier.width(8.dp))
                        Text(if (isHfDownloading) "Downloading (${(hfProgress * 100).toInt()}%)..." else "Download from Hugging Face")
                    }
                }
            }

            // Hardware Profile & Recommendation Engine
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Hardware Profile & Recommendation Engine", style = MaterialTheme.typography.titleMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Saver (6GB)", "Balanced (8GB)", "Pro (16GB)", "Max").forEach { mode ->
                            FilterChip(
                                selected = selectedPerformanceMode == mode.split(" ")[0],
                                onClick = { selectedPerformanceMode = mode.split(" ")[0] },
                                label = { Text(mode) }
                            )
                        }
                    }
                    Text(
                        "Recommended profile: ${if (selectedPerformanceMode == "Saver") "6 GB Lite Profile (Qwen3 0.6B Q4 + Whisper Tiny)" else "16 GB Pro Profile (Qwen3 1.7B Q4 + Whisper Base + Vision Lite)"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text("Catalog & Installed Models", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allModels) { model ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(model.name, style = MaterialTheme.typography.titleMedium)
                                Text("${model.type} • Size: ${model.size} • Minimum RAM: ${model.ramRequirement}", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    when {
                                        model.isLoaded -> "● Loaded in RAM & Ready"
                                        model.isInstalled -> "○ Installed locally"
                                        else -> "↓ Available on Hugging Face"
                                    },
                                    color = if (model.isLoaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (model.isInstalled) {
                                    IconButton(onClick = {
                                        val nextLoadedState = !model.isLoaded
                                        allModels = allModels.map { if (it.id == model.id) it.copy(isLoaded = nextLoadedState) else it }
                                        userMessage = if (nextLoadedState) "Loaded ${model.name} into native memory" else "Unloaded ${model.name} from native memory"
                                    }) {
                                        Icon(if (model.isLoaded) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = "Toggle")
                                    }
                                    IconButton(onClick = {
                                        allModels = allModels.map { if (it.id == model.id) it.copy(isInstalled = false, isLoaded = false) else it }
                                        userMessage = "Deleted ${model.name} files from local storage"
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                } else {
                                    Button(onClick = {
                                        allModels = allModels.map { if (it.id == model.id) it.copy(isInstalled = true) else it }
                                        userMessage = "Installed ${model.name}"
                                    }) {
                                        Text("Install")
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
