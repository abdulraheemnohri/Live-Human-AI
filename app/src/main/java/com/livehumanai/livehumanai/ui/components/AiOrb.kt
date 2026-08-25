package com.livehumanai.livehumanai.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AiOrbState { IDLE, LISTENING, THINKING, SPEAKING, VISION, ERROR, PAUSED }

@Composable
fun AiOrb(
    state: AiOrbState = AiOrbState.IDLE,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    val transition = rememberInfiniteTransition(label = "ai_orb_anim")
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = when (state) {
                AiOrbState.THINKING -> 500
                AiOrbState.LISTENING -> 700
                AiOrbState.SPEAKING -> 600
                AiOrbState.VISION -> 900
                else -> 1200
            }),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val primaryColor = when (state) {
        AiOrbState.IDLE -> MaterialTheme.colorScheme.primary
        AiOrbState.LISTENING -> Color(0xFF00E676)
        AiOrbState.THINKING -> Color(0xFF29B6F6)
        AiOrbState.SPEAKING -> Color(0xFFAB47BC)
        AiOrbState.VISION -> Color(0xFFFFCA28)
        AiOrbState.ERROR -> MaterialTheme.colorScheme.error
        AiOrbState.PAUSED -> Color.Gray
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val baseRadius = (size.toPx() / 3f) * pulse

            // Outer ring
            drawCircle(
                color = primaryColor.copy(alpha = 0.25f),
                radius = baseRadius * 1.3f,
                center = center
            )

            // Inner solid core
            drawCircle(
                color = primaryColor,
                radius = baseRadius * 0.75f,
                center = center
            )

            // State specific accents
            when (state) {
                AiOrbState.THINKING -> {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.8f),
                        radius = baseRadius * 0.35f,
                        center = center
                    )
                }
                AiOrbState.VISION -> {
                    drawCircle(
                        color = primaryColor,
                        radius = baseRadius * 1.1f,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
                else -> {}
            }
        }
    }
}
