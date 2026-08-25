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
    val name: String,
    val type: String,
    val size: String,
    val ramRequirement: String,
    val isInstalled: Boolean,
    val isLoaded: Boolean
)

@Composable
fun ModelManagerScreen(viewModel: ModelViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var allModels by remember {
        mutableStateOf(
            listOf(
                ModelInfo("Qwen3 0.6B Q4", "LLM", "400MB", "1GB", true, true),
                ModelInfo("Qwen3 1.7B Q4", "LLM", "1GB", "2GB", true, false),
                ModelInfo("Whisper Base", "STT", "100MB", "500MB", true, true),
                ModelInfo("YOLO Nano", "Vision", "5MB", "200MB", true, false)
            )
        )
    }
    var selectedPerformanceMode by remember { mutableStateOf("Balanced") }

    var hfRepoId by remember { mutableStateOf("Qwen/Qwen2.5-0.5B-Instruct-GGUF") }
    var hfFilename by remember { mutableStateOf("qwen2.5-0.5b-instruct-q4_k_m.gguf") }

    val downloadProgressMap by viewModel.downloadProgress.collectAsState()
    val isHfDownloading = downloadProgressMap.containsKey(hfFilename)
    val hfProgress = downloadProgressMap[hfFilename] ?: 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("AI Model Manager", style = MaterialTheme.typography.headlineMedium)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Hugging Face Model Downloader", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = hfRepoId,
                    onValueChange = { hfRepoId = it },
                    label = { Text("Repo ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hfFilename,
                    onValueChange = { hfFilename = it },
                    label = { Text("Filename (.gguf / .onnx)") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (isHfDownloading) {
                    LinearProgressIndicator(progress = { hfProgress }, modifier = Modifier.fillMaxWidth())
                }
                Button(
                    onClick = {
                        viewModel.downloadHuggingFaceModel(hfRepoId, hfFilename, context.filesDir)
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

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("AI Performance Profile", style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Saver", "Balanced", "Perf", "Max").forEach { mode ->
                        FilterChip(
                            selected = selectedPerformanceMode.startsWith(mode),
                            onClick = { selectedPerformanceMode = mode },
                            label = { Text(mode) }
                        )
                    }
                }
                Text("Recommended for this device: Lite AI Profile (Qwen3 1.7B Q4)", style = MaterialTheme.typography.bodySmall)
            }
        }

        Text("Installed Models", style = MaterialTheme.typography.titleMedium)

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
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(model.name, style = MaterialTheme.typography.titleMedium)
                            Text("${model.type} • Size: ${model.size} • RAM: ~${model.ramRequirement}", style = MaterialTheme.typography.bodySmall)
                            Text(if (model.isLoaded) "● Ready" else "○ Installed", color = if (model.isLoaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        }
                        IconButton(onClick = {
                            allModels = allModels.map { if (it.name == model.name) it.copy(isLoaded = !it.isLoaded) else it }
                        }) {
                            Icon(if (model.isLoaded) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = "Toggle")
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
