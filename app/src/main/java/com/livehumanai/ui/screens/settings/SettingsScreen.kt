package com.livehumanai.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var darkMode by remember { mutableStateOf("System") }
    var dynamicColor by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf("Auto") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Appearance Section
            SettingsSection(title = "Appearance") {
                SettingsDropdownItem(
                    title = "Theme",
                    value = darkMode,
                    options = listOf("System", "Light", "Dark"),
                    onValueSelected = { darkMode = it }
                )
                
                SettingsToggleItem(
                    title = "Dynamic Color",
                    description = "Use Material You colors from wallpaper",
                    checked = dynamicColor,
                    onCheckedChange = { dynamicColor = it }
                )
                
                SettingsDropdownItem(
                    title = "Language",
                    value = language,
                    options = listOf("Auto", "English", "Urdu", "Roman Urdu", "Hindi", "Arabic"),
                    onValueSelected = { language = it }
                )
            }
            
            // AI Section
            SettingsSection(title = "AI") {
                SettingsNavigationItem(
                    title = "Default LLM",
                    subtitle = "Qwen3 1.7B Q4",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "Default STT",
                    subtitle = "Whisper Base",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "Default TTS",
                    subtitle = "Local TTS Engine",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "Context Size",
                    subtitle = "4096 tokens",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "Thread Count",
                    subtitle = "4 threads",
                    onClick = { }
                )
            }
            
            // Voice Section
            SettingsSection(title = "Voice") {
                SettingsNavigationItem(
                    title = "Wake Word",
                    subtitle = "Hey AI",
                    onClick = { }
                )
                
                SettingsToggleItem(
                    title = "Continuous Listening",
                    description = "Keep microphone active for wake word",
                    checked = false,
                    onCheckedChange = { }
                )
                
                SettingsNavigationItem(
                    title = "Voice Speed",
                    subtitle = "Normal",
                    onClick = { }
                )
            }
            
            // Vision Section
            SettingsSection(title = "Vision") {
                SettingsNavigationItem(
                    title = "Camera Quality",
                    subtitle = "Balanced",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "AI FPS Limit",
                    subtitle = "10 FPS",
                    onClick = { }
                )
                
                SettingsToggleItem(
                    title = "Always-on Vision",
                    description = "Continuously analyze camera feed",
                    checked = false,
                    onCheckedChange = { }
                )
            }
            
            // Memory Section
            SettingsSection(title = "Memory") {
                SettingsToggleItem(
                    title = "Long-term Memory",
                    description = "Store and retrieve memories across sessions",
                    checked = true,
                    onCheckedChange = { }
                )
                
                SettingsToggleItem(
                    title = "Semantic Retrieval",
                    description = "Use embeddings for memory search (uses more RAM)",
                    checked = false,
                    onCheckedChange = { }
                )
                
                SettingsNavigationItem(
                    title = "Memory Budget",
                    subtitle = "512 MB",
                    onClick = { }
                )
            }
            
            // Performance Section
            SettingsSection(title = "Performance") {
                SettingsNavigationItem(
                    title = "Performance Mode",
                    subtitle = "Balanced",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "GPU Backend",
                    subtitle = "Vulkan",
                    onClick = { }
                )
                
                SettingsToggleItem(
                    title = "Model Caching",
                    description = "Keep frequently used models in memory",
                    checked = true,
                    onCheckedChange = { }
                )
            }
            
            // Privacy & Security Section
            SettingsSection(title = "Privacy & Security") {
                SettingsNavigationItem(
                    title = "Privacy Center",
                    subtitle = "Manage permissions and data access",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "Security",
                    subtitle = "Biometric lock, encryption settings",
                    onClick = { }
                )
                
                SettingsToggleItem(
                    title = "Offline Mode",
                    description = "Disable all network connections",
                    checked = false,
                    onCheckedChange = { }
                )
            }
            
            // Storage Section
            SettingsSection(title = "Storage") {
                SettingsNavigationItem(
                    title = "Model Storage",
                    subtitle = "2.4 GB used",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "Cache",
                    subtitle = "156 MB",
                    onClick = { }
                )
                
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Cache")
                }
            }
            
            // About Section
            SettingsSection(title = "About") {
                SettingsNavigationItem(
                    title = "Version",
                    subtitle = "1.0.0 (Alpha)",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "Licenses",
                    subtitle = "Open source licenses",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "Diagnostics",
                    subtitle = "Run system tests",
                    onClick = { }
                )
                
                SettingsNavigationItem(
                    title = "Developer Options",
                    subtitle = "Advanced debugging tools",
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
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
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsDropdownItem(
    title: String,
    value: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select",
                    modifier = Modifier.size(20.dp)
                )
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsNavigationItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
