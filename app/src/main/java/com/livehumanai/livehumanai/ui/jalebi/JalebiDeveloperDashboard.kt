package com.livehumanai.livehumanai.ui.jalebi

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livehumanai.livehumanai.jalebi.JalebiTelemetry
import kotlin.math.roundToInt

/** Developer-mode JCL dashboard with live stage animation and explicit autonomy controls. */
@Composable
fun JalebiDeveloperDashboard(
    telemetry: JalebiTelemetry,
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onStop: () -> Unit = {},
    onReplan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val active = telemetry.state in setOf("PERCEIVING", "INTERPRETING", "REASONING", "PLANNING", "ACTING", "OBSERVING", "EVALUATING", "UPDATING_MEMORY", "REPLANNING")
    val transition = rememberInfiniteTransition(label = "jcl_stage")
    val pulse by transition.animateFloat(initialValue = 0.55f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "stage_pulse")

    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Jalebi Cognitive Loop", style = MaterialTheme.typography.headlineSmall)
        Text(if (active) "● ${telemetry.activeStage}" else telemetry.activeStage, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (active) pulse else 1f))
        LinearProgressIndicator(progress = { telemetry.confidence.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Iteration", telemetry.iteration.toString(), Modifier.weight(1f))
            MetricCard("Confidence", "${(telemetry.confidence * 100).roundToInt()}%", Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("RAM", "${telemetry.ramPercent.roundToInt()}%", Modifier.weight(1f))
            MetricCard("CPU", "${telemetry.cpuPercent.roundToInt()}%", Modifier.weight(1f))
            MetricCard("Temp", "${telemetry.temperatureC.roundToInt()}°C", Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Model", telemetry.model.ifBlank { "auto" }, Modifier.weight(1f))
            MetricCard("Next", telemetry.nextAction.ifBlank { "—" }, Modifier.weight(1f))
        }
        if (telemetry.goal.isNotBlank()) Text("Goal: ${telemetry.goal}", style = MaterialTheme.typography.bodyMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (telemetry.state == "PAUSED" || telemetry.state == "RESOURCE_LIMIT") Button(onClick = onResume, modifier = Modifier.weight(1f)) { Text("Resume") }
            else OutlinedButton(onClick = onPause, modifier = Modifier.weight(1f), enabled = active) { Text("Pause") }
            OutlinedButton(onClick = onReplan, modifier = Modifier.weight(1f), enabled = telemetry.loopId != null) { Text("Replan") }
            Button(onClick = onStop, modifier = Modifier.weight(1f), enabled = telemetry.loopId != null) { Text("Stop") }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) { Column(Modifier.padding(12.dp)) { Text(title, style = MaterialTheme.typography.labelMedium); Text(value, style = MaterialTheme.typography.titleMedium) } }
}
