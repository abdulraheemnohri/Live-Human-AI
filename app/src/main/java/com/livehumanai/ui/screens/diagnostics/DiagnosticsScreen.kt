package com.livehumanai.ui.screens.diagnostics

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DiagnosticsScreen(
    onNavigateBack: () -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnostics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "System Diagnostics",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { isRunning = true },
                enabled = !isRunning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Run Full Diagnostic")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isRunning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Running diagnostics...")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            DiagnosticCategoryCard(title = "Android Runtime", status = "Ready")
            DiagnosticCategoryCard(title = "JNI Bridge", status = "Ready")
            DiagnosticCategoryCard(title = "C++ Engine", status = "Not Tested")
            DiagnosticCategoryCard(title = "CPU / NEON", status = "Not Tested")
            DiagnosticCategoryCard(title = "GPU / Vulkan", status = "Not Tested")
            DiagnosticCategoryCard(title = "RAM", status = "Not Tested")
            DiagnosticCategoryCard(title = "Storage", status = "Not Tested")
            DiagnosticCategoryCard(title = "Camera", status = "Not Tested")
            DiagnosticCategoryCard(title = "Microphone", status = "Not Tested")
            DiagnosticCategoryCard(title = "Speaker", status = "Not Tested")
            DiagnosticCategoryCard(title = "Database", status = "Ready")
            DiagnosticCategoryCard(title = "Model Downloader", status = "Ready")
        }
    }
}

@Composable
private fun DiagnosticCategoryCard(title: String, status: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title)
            Text(
                text = status,
                color = when (status) {
                    "Ready" -> MaterialTheme.colorScheme.primary
                    "Not Tested" -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}
