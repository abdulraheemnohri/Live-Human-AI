package com.livehumanai.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.livehumanai.ui.theme.ai_idle

enum class AIState {
    IDLE, LISTENING, PROCESSING, THINKING, SPEAKING, VISION, TOOL_EXECUTION, MEMORY, PAUSED, ERROR
}

@Composable
fun HomeScreen(
    onNavigateToChat: () -> Unit,
    onNavigateToVision: () -> Unit,
    onNavigateToMemory: () -> Unit,
    onNavigateToModels: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var aiState by remember { mutableStateOf(AIState.IDLE) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Human AI") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
                    label = { Text("Chat") },
                    selected = false,
                    onClick = onNavigateToChat
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Visibility, contentDescription = "Vision") },
                    label = { Text("Vision") },
                    selected = false,
                    onClick = onNavigateToVision
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Bookmarks, contentDescription = "Memory") },
                    label = { Text("Memory") },
                    selected = false,
                    onClick = onNavigateToMemory
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
                    label = { Text("More") },
                    selected = false,
                    onClick = onNavigateToModels
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AI Orb
            Spacer(modifier = Modifier.height(32.dp))
            
            AICoreIndicator(state = aiState)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = when (aiState) {
                    AIState.IDLE -> "READY"
                    AIState.LISTENING -> "LISTENING"
                    AIState.THINKING -> "THINKING"
                    AIState.SPEAKING -> "SPEAKING"
                    AIState.VISION -> "ANALYZING"
                    else -> aiState.name.replace("_", " ")
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "How can I help?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Search/Input field
            OutlinedTextField(
                value = "",
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = { Text("Ask anything...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice input")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Quick action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Mic,
                    label = "Voice",
                    onClick = { }
                )
                
                QuickActionButton(
                    icon = Icons.Default.AutoAwesome,
                    label = "AI Core",
                    onClick = onNavigateToChat
                )
                
                QuickActionButton(
                    icon = Icons.Default.PhotoCamera,
                    label = "Camera",
                    onClick = onNavigateToVision
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Device status card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Device Status",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DeviceStatItem(label = "RAM", value = "51%")
                        DeviceStatItem(label = "CPU", value = "34%")
                        DeviceStatItem(label = "Temp", value = "37°C")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .brush(Brush.radialGradient(listOf(ai_idle, Color.Transparent)))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Local AI Active",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun AICoreIndicator(state: AIState) {
    val orbColor = when (state) {
        AIState.IDLE -> ai_idle
        AIState.LISTENING -> Color(0xFF00BCD4)
        AIState.THINKING -> Color(0xFFFFC107)
        AIState.SPEAKING -> Color(0xFF4CAF50)
        AIState.VISION -> Color(0xFFFF5722)
        AIState.TOOL_EXECUTION -> Color(0xFF9C27B0)
        AIState.MEMORY -> Color(0xFF3F51B5)
        AIState.PAUSED -> Color(0xFF9E9E9E)
        AIState.ERROR -> Color(0xFFF44336)
        AIState.PROCESSING -> Color(0xFF6750A4)
    }
    
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow ring
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .brush(Brush.radialGradient(listOf(orbColor.copy(alpha = 0.3f), Color.Transparent)))
        )
        
        // Inner orb
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .brush(Brush.radialGradient(listOf(orbColor, orbColor.copy(alpha = 0.5f))))
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun DeviceStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
