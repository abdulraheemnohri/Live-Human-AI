package com.livehumanai.ui.about

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.livehumanai.ui.components.AIOrb

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
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // App Logo / AI Orb
            AIOrb(
                state = com.livehumanai.ui.components.AIState.IDLE,
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App Name
            Text(
                text = "Live Human AI",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tagline
            Text(
                text = "See. Hear. Understand. Remember. Speak.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Version
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "Version 1.0.0 (Alpha)",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Description
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Live Human AI transforms your Android phone into a local multimodal AI assistant capable of natural voice conversation, real-time speech recognition, camera understanding, and much more - all running entirely on your device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Features Section
            Text(
                text = "Key Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FeatureItem(
                icon = Icons.Default.Psychology,
                title = "Local LLM Inference",
                description = "Run powerful language models directly on your device"
            )
            
            FeatureItem(
                icon = Icons.Default.RecordVoiceOver,
                title = "Voice Interaction",
                description = "Natural speech recognition and synthesis in multiple languages"
            )
            
            FeatureItem(
                icon = Icons.Default.Visibility,
                title = "Vision Understanding",
                description = "Object detection, OCR, and scene analysis from your camera"
            )
            
            FeatureItem(
                icon = Icons.Default.Folder,
                title = "Local Memory",
                description = "Semantic memory and document knowledge stored privately"
            )
            
            FeatureItem(
                icon = Icons.Default.Security,
                title = "Privacy First",
                description = "All processing happens on-device with no cloud dependency"
            )
            
            FeatureItem(
                icon = Icons.Default.Tune,
                title = "Hardware Aware",
                description = "Optimized for both 6GB and 16GB RAM devices"
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Technology Stack
            Text(
                text = "Technology Stack",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TechChip(label = "Kotlin")
            TechChip(label = "Jetpack Compose")
            TechChip(label = "Material 3")
            TechChip(label = "Android NDK")
            TechChip(label = "C++17/20")
            TechChip(label = "llama.cpp")
            TechChip(label = "whisper.cpp")
            TechChip(label = "CameraX")
            TechChip(label = "Room Database")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Credits
            Divider()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Built with ❤️ for the Android community",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "© 2024 Live Human AI Project",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Links
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(onClick = { /* Open GitHub */ }) {
                    Text("GitHub")
                }
                
                OutlinedButton(onClick = { /* Open Website */ }) {
                    Text("Website")
                }
                
                OutlinedButton(onClick = { /* Open License */ }) {
                    Text("License")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TechChip(label: String) {
    AssistChip(
        onClick = { },
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        modifier = Modifier.padding(4.dp)
    )
}
