package com.livehumanai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.livehumanai.domain.model.AIState
import kotlinx.coroutines.delay

/**
 * Modern AI Orb Component with Glass Morphism
 * Displays real-time AI state with smooth animations
 */
@Composable
fun ModernAIOrb(
    state: AIState,
    size: Float = 120f,
    modifier: Modifier = Modifier,
    isListening: Boolean = false,
    isSpeaking: Boolean = false,
    confidence: Float = 0.9f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    
    // Animated scale for breathing effect
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // Rotation for active states
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Pulse ring animation
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // Wave amplitude for listening state
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )
    
    // Get colors based on state
    val (primaryColor, secondaryColor) = getOrbColors(state)
    
    Box(
        modifier = modifier
            .size(size.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow blur
        Canvas(
            modifier = Modifier
                .size((size * 1.4).dp)
                .blur(20.dp)
        ) {
            drawCircle(
                color = primaryColor.copy(alpha = 0.3f),
                radius = size.dp.toPx() * 0.7f * scale
            )
        }
        
        // Rotating gradient ring
        Canvas(
            modifier = Modifier
                .size((size * 1.2).dp)
                .rotate(rotation)
        ) {
            val sweepAngle = if (state == AIState.THINKING || state == AIState.PROCESSING) 270f else 360f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.8f),
                        secondaryColor.copy(alpha = 0.6f),
                        primaryColor.copy(alpha = 0.2f)
                    )
                ),
                startAngle = 0f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx())
            )
        }
        
        // Pulsing outer ring
        Canvas(
            modifier = Modifier.size(size.dp)
        ) {
            drawCircle(
                color = primaryColor.copy(alpha = pulseAlpha),
                radius = size.dp.toPx() * 0.45f,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        
        // Main orb with gradient
        Canvas(
            modifier = Modifier.size((size * 0.8).dp)
        ) {
            val center = Offset(size.dp.toPx() * 0.4f, size.dp.toPx() * 0.4f)
            val radius = size.dp.toPx() * 0.35f
            
            // Radial gradient fill
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.9f),
                        secondaryColor.copy(alpha = 0.7f),
                        primaryColor.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
            
            // Inner highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = radius * 0.3f,
                center = Offset(center.x - radius * 0.2f, center.y - radius * 0.2f)
            )
        }
        
        // Wave visualization for listening state
        if (isListening || state == AIState.LISTENING) {
            Canvas(
                modifier = Modifier.size((size * 1.3).dp)
            ) {
                val centerX = size.dp.toPx() * 0.65f
                val centerY = size.dp.toPx() * 0.65f
                val baseRadius = size.dp.toPx() * 0.5f
                
                // Draw multiple wave rings
                for (i in 0 until 3) {
                    val phase = wavePhase + (i * 0.5f)
                    val radius = baseRadius + (Math.sin(phase.toDouble()) * 10).toFloat()
                    val alpha = (0.5f - i * 0.15f).coerceAtLeast(0.1f)
                    
                    drawCircle(
                        color = primaryColor.copy(alpha = alpha),
                        radius = radius.toFloat(),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
        
        // Sound waves for speaking state
        if (isSpeaking || state == AIState.SPEAKING) {
            Canvas(
                modifier = Modifier.size((size * 1.1).dp)
            ) {
                val barCount = 5
                val barWidth = 4.dp.toPx()
                val maxBarHeight = size.dp.toPx() * 0.3f
                
                for (i in 0 until barCount) {
                    val phase = wavePhase + (i * 0.3f)
                    val barHeight = (Math.abs(Math.sin(phase.toDouble())) * maxBarHeight).toFloat()
                    val x = center.x + (i - 2) * (barWidth * 2)
                    
                    drawRoundRect(
                        color = secondaryColor.copy(alpha = 0.8f),
                        topLeft = Offset(x - barWidth / 2, center.y - barHeight / 2),
                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                    )
                }
            }
        }
        
        // Confidence indicator ring
        if (confidence < 1.0f && (state == AIState.THINKING || state == AIState.PROCESSING)) {
            Canvas(
                modifier = Modifier.size((size * 0.9).dp)
            ) {
                drawArc(
                    color = secondaryColor.copy(alpha = 0.6f),
                    startAngle = -90f,
                    sweepAngle = 360f * confidence,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

/**
 * Get orb colors based on AI state
 */
@Composable
private fun getOrbColors(state: AIState): Pair<Color, Color> {
    return when (state) {
        AIState.IDLE -> MaterialTheme.colorScheme.outline to MaterialTheme.colorScheme.outlineVariant
        AIState.LISTENING -> Color(0xFF00D4FF) to Color(0xFF64B5F6)  // Cyber Blue
        AIState.PROCESSING -> Color(0xFF6C5DD3) to Color(0xFF9C27B0)  // Nebula Purple
        AIState.THINKING -> Color(0xFF6C5DD3) to Color(0xFFCE93D8)  // Nebula Purple
        AIState.SPEAKING -> Color(0xFFFF6B6B) to Color(0xFFFF8A80)  // Coral
        AIState.VISION -> Color(0xFFFF8A65) to Color(0xFFFF5722)  // Deep Orange
        AIState.TOOL_EXECUTION -> Color(0xFFCE93D8) to Color(0xFF9C27B0)  // Lavender
        AIState.MEMORY -> Color(0xFF7986CB) to Color(0xFF3F51B5)  // Indigo
        AIState.PAUSED -> Color(0xFFBDBDBD) to Color(0xFF9E9E9E)  // Gray
        AIState.ERROR -> Color(0xFFFF4D4D) to Color(0xFFEF5350)  // Alert Red
    }
}

/**
 * Compact AI Status Indicator for top bar
 */
@Composable
fun AIStatusIndicator(
    state: AIState,
    modifier: Modifier = Modifier
) {
    val color = when (state) {
        AIState.IDLE -> MaterialTheme.colorScheme.outline
        AIState.LISTENING -> Color(0xFF00D4FF)
        AIState.PROCESSING -> Color(0xFF6C5DD3)
        AIState.THINKING -> Color(0xFF6C5DD3)
        AIState.SPEAKING -> Color(0xFFFF6B6B)
        AIState.VISION -> Color(0xFFFF8A65)
        AIState.TOOL_EXECUTION -> Color(0xFFCE93D8)
        AIState.MEMORY -> Color(0xFF7986CB)
        AIState.PAUSED -> Color(0xFFBDBDBD)
        AIState.ERROR -> Color(0xFFFF4D4D)
    }
    
    Surface(
        modifier = modifier
            .size(12.dp),
        shape = CircleShape,
        color = color
    ) {
        // Blinking animation for active states
        if (state != AIState.IDLE && state != AIState.ERROR) {
            val infiniteTransition = rememberInfiniteTransition(label = "status_blink")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color.copy(alpha = alpha))
            )
        }
    }
}
