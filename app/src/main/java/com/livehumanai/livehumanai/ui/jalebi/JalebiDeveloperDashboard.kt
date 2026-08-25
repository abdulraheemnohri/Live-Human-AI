package com.livehumanai.livehumanai.ui.jalebi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livehumanai.livehumanai.jalebi.JalebiTelemetry
import kotlin.math.roundToInt

/** Developer-only, read-only JCL telemetry surface. */
@Composable
fun JalebiDeveloperDashboard(
    telemetry: JalebiTelemetry,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Jalebi Cognitive Loop", style = MaterialTheme.typography.headlineSmall)
        Text("Active stage: ${telemetry.activeStage}")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Iteration", telemetry.iteration.toString(), Modifier.weight(1f))
            MetricCard("Confidence", "${(telemetry.confidence * 100).roundToInt()}%", Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("RAM", "${telemetry.ramPercent.roundToInt()}%", Modifier.weight(1f))
            MetricCard("Temp", "${telemetry.temperatureC.roundToInt()}°C", Modifier.weight(1f))
        }
        MetricCard("Model", telemetry.model.ifBlank { "auto" }, Modifier.fillMaxWidth())
        MetricCard("Next action", telemetry.nextAction.ifBlank { "—" }, Modifier.fillMaxWidth())
        if (telemetry.goal.isNotBlank()) Text("Goal: ${telemetry.goal}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}
