package com.livehumanai.livehumanai.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.livehumanai.livehumanai.ui.components.AiOrb
import com.livehumanai.livehumanai.ui.components.AiOrbState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Live Human AI") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            AiOrb(state = AiOrbState.IDLE, size = 100.dp)

            Text("Live Human AI", style = MaterialTheme.typography.headlineSmall)
            Text("See. Hear. Understand. Remember. Speak.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Product Identity", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Live Human AI is an offline-first, local-first personal AI assistant built for Android. Powered by native C++17/C++20 NDK engines, Jalebi Cognitive Loop, and Hugging Face model distribution.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Core Principles", style = MaterialTheme.typography.titleMedium)
                    Text("1. Local-first (Private on-device inference)", style = MaterialTheme.typography.bodySmall)
                    Text("2. User-controlled (Explicit permissions & zero cloud forced routing)", style = MaterialTheme.typography.bodySmall)
                    Text("3. Model-agnostic (Replaceable GGUF / ONNX / Whisper models)", style = MaterialTheme.typography.bodySmall)
                    Text("4. Hardware-aware (6 GB Lite & 16 GB Pro device profiles)", style = MaterialTheme.typography.bodySmall)
                    Text("5. Safety-bounded autonomy (Jalebi loop iteration & token budget limits)", style = MaterialTheme.typography.bodySmall)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("System Details", style = MaterialTheme.typography.titleMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Version", style = MaterialTheme.typography.bodySmall)
                        Text("1.0.0 (Build 1)", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Target Architecture", style = MaterialTheme.typography.bodySmall)
                        Text("arm64-v8a / x86_64", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("License", style = MaterialTheme.typography.bodySmall)
                        Text("Apache-2.0 / MIT", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Button(
                onClick = {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://github.com/abdulraheemnohri/Live-Human-AI")
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Code, contentDescription = "Source")
                Spacer(Modifier.width(8.dp))
                Text("View Source Repository")
            }
        }
    }
}
