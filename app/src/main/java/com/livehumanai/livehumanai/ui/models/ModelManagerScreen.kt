package com.livehumanai.livehumanai.ui.models

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
 * ModelManagerScreen allows users to view, download, and manage AI models.
 */
@Composable
fun ModelManagerScreen() {
    // State for the model manager
    var availableModels by remember { mutableStateOf(listOf<ModelInfo>()) }
    var installedModels by remember { mutableStateOf(listOf<String>()) }
    var downloadingModels by remember { mutableStateOf(mapOf<String, Float>()) }

    // Initialize with some sample data
    // In a real implementation, this would be fetched from the NativeBridge
    if (availableModels.isEmpty()) {
        availableModels = listOf(
            ModelInfo(
                name = "Qwen3 0.6B Q4",
                type = "LLM",
                size = "400MB",
                ramRequirement = "1GB",
                isInstalled = true,
                isLoaded = true
            ),
            ModelInfo(
                name = "Qwen3 1.7B Q4",
                type = "LLM",
                size = "1GB",
                ramRequirement = "2GB",
                isInstalled = true,
                isLoaded = false
            ),
            ModelInfo(
                name = "Qwen3 4B Q4",
                type = "LLM",
                size = "2GB",
                ramRequirement = "4GB",
                isInstalled = false,
                isLoaded = false
            ),
            ModelInfo(
                name = "Whisper Base",
                type = "STT",
                size = "100MB",
                ramRequirement = "500MB",
                isInstalled = true,
                isLoaded = true
            ),
            ModelInfo(
                name = "YOLO Nano",
                type = "Vision",
                size = "5MB",
                ramRequirement = "200MB",
                isInstalled = true,
                isLoaded = false
            )
        )

        installedModels = availableModels.filter { it.isInstalled }.map { it.name }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Model Manager",
            style = MaterialTheme.typography.headlineMedium
        )

        // Tabs for different model types
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { /* TODO: Show all models */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("All")
            }

            Button(
                onClick = { /* TODO: Show LLM models */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("LLM")
            }

            Button(
                onClick = { /* TODO: Show STT models */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("STT")
            }

            Button(
                onClick = { /* TODO: Show Vision models */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Vision")
            }
        }

        // Installed models section
        Text(
            text = "Installed Models",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableModels.filter { it.isInstalled }) { model ->
                ModelCard(
                    model = model,
                    isDownloading = downloadingModels.containsKey(model.name),
                    downloadProgress = downloadingModels[model.name] ?: 0f,
                    onLoad = { /* TODO: Load model */ },
                    onUnload = { /* TODO: Unload model */ },
                    onDelete = { /* TODO: Delete model */ },
                    onDownload = { /* TODO: Download model */ }
                )
            }
        }

        // Available models section
        Text(
            text = "Available Models",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableModels.filter { !it.isInstalled }) { model ->
                ModelCard(
                    model = model,
                    isDownloading = downloadingModels.containsKey(model.name),
                    downloadProgress = downloadingModels[model.name] ?: 0f,
                    onLoad = { /* TODO: Load model */ },
                    onUnload = { /* TODO: Unload model */ },
                    onDelete = { /* TODO: Delete model */ },
                    onDownload = { /* TODO: Download model */ }
                )
            }
        }

        // Download all button
        Button(
            onClick = { /* TODO: Download all models */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Download Recommended Models")
        }
    }
}

/**
 * Represents information about an AI model.
 */
data class ModelInfo(
    val name: String,
    val type: String,
    val size: String,
    val ramRequirement: String,
    val isInstalled: Boolean,
    val isLoaded: Boolean
)

/**
 * Displays a card for a single model with actions.
 */
@Composable
fun ModelCard(
    model: ModelInfo,
    isDownloading: Boolean,
    downloadProgress: Float,
    onLoad: () -> Unit,
    onUnload: () -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${model.type} • ${model.size} • RAM: ${model.ramRequirement}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (model.isInstalled) {
                    if (model.isLoaded) {
                        IconButton(onClick = onUnload) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Unload"
                            )
                        }
                    } else {
                        IconButton(onClick = onLoad) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Load"
                            )
                        }
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = onDownload) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download"
                            )
                        }
                    }
                }

                IconButton(onClick = { /* TODO: Show model details */ }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Details"
                    )
                }
            }
        }

        if (isDownloading) {
            LinearProgressIndicator(
                progress = { downloadProgress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModelManagerScreenPreview() {
    LiveHumanAITheme {
        ModelManagerScreen()
    }
}
