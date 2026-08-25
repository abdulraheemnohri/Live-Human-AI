package com.livehumanai.livehumanai.ui.jalebi

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.livehumanai.livehumanai.ui.viewmodel.AIViewModel

@Composable
fun JalebiDeveloperScreen(viewModel:AIViewModel=hiltViewModel(),modifier:Modifier=Modifier){
 val telemetry by viewModel.jclTelemetry.collectAsState()
 JalebiDeveloperDashboard(telemetry=telemetry,modifier=modifier,onPause=viewModel::pauseJalebiLoop,onResume=viewModel::resumeJalebiLoop,onStop=viewModel::stopLiveJalebi,onReplan={viewModel.replanJalebiLoop("developer_manual_replan")})
}
