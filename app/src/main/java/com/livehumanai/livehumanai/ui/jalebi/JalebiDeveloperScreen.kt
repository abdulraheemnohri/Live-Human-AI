package com.livehumanai.livehumanai.ui.jalebi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.livehumanai.livehumanai.ui.viewmodel.AIViewModel

/** Developer-mode screen for observing and controlling the bounded JCL runtime. */
@Composable
fun JalebiDeveloperScreen(
    viewModel: AIViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val telemetry by viewModel.jclTelemetry.collectAsState()
    val running = telemetry.loopId != null && telemetry.state != "CANCELLED" && telemetry.state != "COMPLETED" && telemetry.state != "FAILED"
    val paused = telemetry.state == "PAUSED"

    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        JalebiDeveloperDashboard(telemetry = telemetry, modifier = Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (paused) {
                Button(onClick = viewModel::resumeJalebiLoop, modifier = Modifier.weight(1f)) { Text("Resume") }
            } else {
                Button(onClick = viewModel::pauseJalebiLoop, enabled = running, modifier = Modifier.weight(1f)) { Text("Pause") }
            }
            OutlinedButton(onClick = { viewModel.replanJalebiLoop("developer_manual_replan") }, enabled = running, modifier = Modifier.weight(1f)) { Text("Replan") }
            OutlinedButton(onClick = viewModel::stopLiveJalebi, enabled = running, modifier = Modifier.weight(1f)) { Text("Stop") }
        }
    }
}
