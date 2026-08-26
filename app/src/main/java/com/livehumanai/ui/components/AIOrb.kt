package com.livehumanai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.livehumanai.ui.theme.*

enum class AIState {
    IDLE,
    LISTENING,
    PROCESSING,
    THINKING,
    SPEAKING,
    VISION,
    TOOL_EXECUTION,
    MEMORY,
    PAUSED,
    ERROR
}

fun AIState.getColor(isDark: Boolean): Color {
    return if (isDark) {
        when (this) {
            AIState.IDLE -> DarkAIActive
            AIState.LISTENING -> DarkAIListening
            AIState.PROCESSING, AIState.THINKING -> DarkAIThinking
            AIState.SPEAKING -> DarkAISpeaking
            AIState.VISION -> DarkAIVision
            AIState.TOOL_EXECUTION -> DarkAIToolExecution
            AIState.MEMORY -> DarkAIMemory
            AIState.PAUSED -> DarkAIPaused
            AIState.ERROR -> DarkAIError
        }
    } else {
        when (this) {
            AIState.IDLE -> LightAIActive
            AIState.LISTENING -> LightAIListening
            AIState.PROCESSING, AIState.THINKING -> LightAIThinking
            AIState.SPEAKING -> LightAISpeaking
            AIState.VISION -> LightAIVision
            AIState.TOOL_EXECUTION -> LightAIToolExecution
            AIState.MEMORY -> LightAIMemory
            AIState.PAUSED -> LightAIPaused
            AIState.ERROR -> LightAIError
        }
    }
}

@Composable
fun AIOrb(
    state: AIState = AIState.IDLE,
    modifier: Modifier = Modifier,
    size: Float = 80f
) {
    val isDark = MaterialTheme.colorScheme.brightness < 0.5f
    val infiniteTransition = rememberInfiniteTransition(label = "orb_animation")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    AIState.LISTENING -> 800
                    AIState.THINKING, AIState.PROCESSING -> 1200
                    AIState.SPEAKING -> 600
                    AIState.VISION -> 900
                    else -> 1500
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    AIState.THINKING, AIState.PROCESSING -> 8000
                    AIState.VISION -> 5000
                    else -> 10000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val color = state.getColor(isDark)
    val glowColor = color.copy(alpha = 0.3f)
    
    Canvas(
        modifier = modifier
            .size((size * scale).dp)
            .padding(8.dp)
    ) {
        val center = Offset(size / 2, size / 2)
        val outerRadius = size * 0.45f
        
        // Outer glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    glowColor,
                    glowColor.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                center = center,
                radius = outerRadius * 1.5f
            ),
            radius = outerRadius * 1.5f,
            center = center
        )
        
        // Main orb with rotation effect
        rotate(rotation) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.8f),
                        color,
                        color.copy(alpha = 0.6f)
                    ),
                    center = center,
                    radius = outerRadius
                ),
                radius = outerRadius * 0.7f,
                center = center
            )
        }
        
        // Inner pulse for active states
        if (state == AIState.LISTENING || state == AIState.SPEAKING) {
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(300, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )
            drawCircle(
                color = color.copy(alpha = pulseAlpha),
                radius = outerRadius * 0.3f,
                center = center
            )
        }
    }
}

@Composable
fun AIStateIndicator(
    state: AIState,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.brightness < 0.5f
    val color = state.getColor(isDark)
    
    Row(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = color,
                    shape = MaterialTheme.shapes.small
                )
        )
        Text(
            text = state.name.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
