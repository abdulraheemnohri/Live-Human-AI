package com.livehumanai.livehumanai.ui.downloads

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class DownloadItem(
    val id: String,
    val modelName: String,
    val repoId: String,
    val fileName: String,
    val sizeBytes: Long,
    val downloadedBytes: Long,
    val speed: String,
    val status: String, // DOWNLOADING, PAUSED, COMPLETED, VERIFYING, ERROR
    val progress: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeDownloads by remember {
        mutableStateOf(
            listOf(
                DownloadItem(
                    id = "1",
                    modelName = "Qwen3 1.7B Q4",
                    repoId = "Qwen/Qwen2.5-1.5B-Instruct-GGUF",
                    fileName = "qwen2.5-1.5b-instruct-q4_k_m.gguf",
                    sizeBytes = 1100000000L,
                    downloadedBytes = 850000000L,
                    speed = "8.4 MB/s",
                    status = "DOWNLOADING",
                    progress = 0.77f
                ),
                DownloadItem(
                    id = "2",
                    modelName = "Whisper Base STT",
                    repoId = "ggerganov/whisper.cpp",
                    fileName = "ggml-base.en.bin",
                    sizeBytes = 148000000L,
                    downloadedBytes = 148000000L,
                    speed = "0 KB/s",
                    status = "COMPLETED",
                    progress = 1.0f
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Model Downloads") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Active Download Queue", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Hugging Face streaming model downloader with automatic SHA-256 integrity verification.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text("Download Tasks", style = MaterialTheme.typography.titleMedium)

            if (activeDownloads.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No active model downloads", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(activeDownloads) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.modelName, style = MaterialTheme.typography.titleSmall)
                                        Text(item.repoId, style = MaterialTheme.typography.labelSmall)
                                    }
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(item.status) }
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { item.progress },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "${(item.progress * 100).toInt()}% • ${item.speed}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "${item.downloadedBytes / (1024 * 1024)}MB / ${item.sizeBytes / (1024 * 1024)}MB",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (item.status == "DOWNLOADING") {
                                        IconButton(onClick = {
                                            activeDownloads = activeDownloads.map {
                                                if (it.id == item.id) it.copy(status = "PAUSED") else it
                                            }
                                        }) {
                                            Icon(Icons.Default.Pause, contentDescription = "Pause")
                                        }
                                    } else if (item.status == "PAUSED") {
                                        IconButton(onClick = {
                                            activeDownloads = activeDownloads.map {
                                                if (it.id == item.id) it.copy(status = "DOWNLOADING") else it
                                            }
                                        }) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = "Resume")
                                        }
                                    }
                                    IconButton(onClick = {
                                        activeDownloads = activeDownloads.filter { it.id != item.id }
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Cancel")
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
