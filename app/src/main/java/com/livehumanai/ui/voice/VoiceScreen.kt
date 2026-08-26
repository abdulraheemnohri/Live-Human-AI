package com.livehumanai.ui.voice

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var audioLevel by remember { mutableStateOf(0f) }
    var transcription by remember { mutableStateOf("") }
    
    // Simulate audio level changes when listening
    LaunchedEffect(isListening) {
        if (isListening) {
            while (isListening) {
                audioLevel = (0..100).random() / 100f
                delay(100)
            }
            audioLevel = 0f
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voice") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Voice state indicator
            VoiceStateIndicator(
                isListening = isListening,
                isSpeaking = isSpeaking,
                audioLevel = audioLevel
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Current state text
            Text(
                text = when {
                    isSpeaking -> "AI is speaking..."
                    isListening -> "Listening..."
                    else -> "Tap to speak"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = when {
                    isSpeaking -> MaterialTheme.colorScheme.primary
                    isListening -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Transcription preview
            if (transcription.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = transcription,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Settings button
                FilledTonalIconButton(
                    onClick = { /* Open voice settings */ },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        androidx.compose.material.icons.Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Main listen/speak button
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isListening || isSpeaking) {
                        // Pulsing animation when active
                        repeat(3) { index ->
                            Canvas(
                                modifier = Modifier
                                    .size(72.dp + (index * 16).dp)
                                    .align(Alignment.Center)
                            ) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f - (index * 0.1f)),
                                            Color.Transparent
                                        )
                                    ),
                                    radius = size.minDimension / 2,
                                    alpha = 1f - (index * 0.3f)
                                )
                            }
                        }
                    }
                    
                    FloatingActionButton(
                        onClick = {
                            if (isListening) {
                                isListening = false
                                // Process speech and start speaking
                                isSpeaking = true
                                transcription = "This is a sample transcription of what you said."
                            } else if (isSpeaking) {
                                isSpeaking = false
                            } else {
                                isListening = true
                                transcription = ""
                            }
                        },
                        modifier = Modifier.size(72.dp),
                        containerColor = when {
                            isListening -> MaterialTheme.colorScheme.error
                            isSpeaking -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    ) {
                        Icon(
                            imageVector = when {
                                isListening -> androidx.compose.material.icons.Icons.Default.Close
                                isSpeaking -> androidx.compose.material.icons.Icons.Default.VolumeOff
                                else -> androidx.compose.material.icons.Icons.Default.Mic
                            },
                            contentDescription = if (isListening) "Stop listening" else "Start listening",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }
                
                // History button
                FilledTonalIconButton(
                    onClick = { /* Show voice history */ },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        androidx.compose.material.icons.Icons.Default.History,
                        contentDescription = "History",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Voice mode toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Continuous listening",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = false,
                    onCheckedChange = { /* Toggle continuous mode */ }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Press and hold to talk, release to send",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun VoiceStateIndicator(
    isListening: Boolean,
    isSpeaking: Boolean,
    audioLevel: Float
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer rings when listening
        if (isListening) {
            repeat(3) { index ->
                Canvas(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale)
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension / 2 * (1f - index * 0.2f),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
            }
        }
        
        // Audio waveform visualization
        Canvas(
            modifier = Modifier.size(160.dp)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxRadius = size.minDimension / 2
            
            // Draw circular waveform
            for (i in 0 until 36 step 10) {
                val angle = Math.toRadians(i.toDouble())
                val level = if (isListening) {
                    0.5f + (audioLevel * 0.5f * (kotlin.random.Random.nextFloat() * 0.5f + 0.5f))
                } else if (isSpeaking) {
                    0.7f + (kotlin.random.Random.nextFloat() * 0.3f)
                } else {
                    0.3f
                }
                
                val radius = maxRadius * level
                val x = centerX + kotlin.math.cos(angle).toFloat() * radius
                val y = centerY + kotlin.math.sin(angle).toFloat() * radius
                
                drawCircle(
                    color = when {
                        isListening -> MaterialTheme.colorScheme.secondary
                        isSpeaking -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline
                    },
                    radius = 8.dp.toPx(),
                    center = Offset(x, y),
                    alpha = level
                )
            }
        }
        
        // Center circle
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = when {
                isListening -> MaterialTheme.colorScheme.secondaryContainer
                isSpeaking -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when {
                        isListening -> androidx.compose.material.icons.Icons.Default.Mic
                        isSpeaking -> androidx.compose.material.icons.Icons.Default.VolumeUp
                        else -> androidx.compose.material.icons.Icons.Default.MicNone
                    },
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = when {
                        isListening -> MaterialTheme.colorScheme.onSecondaryContainer
                        isSpeaking -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
