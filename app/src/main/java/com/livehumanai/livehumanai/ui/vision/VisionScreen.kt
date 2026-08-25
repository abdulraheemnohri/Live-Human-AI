package com.livehumanai.livehumanai.ui.vision

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.livehumanai.livehumanai.ui.camera.CameraManager
import com.livehumanai.livehumanai.ui.viewmodel.AIViewModel

@Composable
fun VisionScreen(viewModel: AIViewModel = hiltViewModel()) {
    val context=LocalContext.current;val lifecycleOwner=androidx.lifecycle.compose.LocalLifecycleOwner.current;val cameraManager=remember{CameraManager(context)}
    var permissionGranted by remember{mutableStateOf(ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)}
    val permissionLauncher=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){permissionGranted=it};var preview by remember{mutableStateOf<PreviewView?>(null)};val telemetry by viewModel.jclTelemetry.collectAsState()
    LaunchedEffect(Unit){if(!permissionGranted)permissionLauncher.launch(Manifest.permission.CAMERA)}
    DisposableEffect(permissionGranted,lifecycleOwner,preview){
        if(permissionGranted&&preview!=null){cameraManager.initialize(preview!!,lifecycleOwner);cameraManager.setupFrameAnalysis{viewModel.submitJalebiCameraFrame(it)};viewModel.startLiveJalebi("Continuously understand the current camera scene",8)}
        onDispose{cameraManager.clearFrameAnalysis();cameraManager.cleanup();viewModel.stopLiveJalebi()}
    }
    Column(Modifier.fillMaxSize().padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Live AI Vision",style=MaterialTheme.typography.titleLarge);Text(telemetry.activeStage,style=MaterialTheme.typography.labelLarge)}
        if(!permissionGranted){Text("Camera permission is required for live perception.");Button(onClick={permissionLauncher.launch(Manifest.permission.CAMERA)}){Text("Allow Camera")}}
        else{AndroidView(factory={ctx->PreviewView(ctx).also{preview=it}},modifier=Modifier.fillMaxWidth().weight(1f).aspectRatio(16f/9f));Text("JCL: ${telemetry.state} • Iteration ${telemetry.iteration} • Confidence ${(telemetry.confidence*100).toInt()}%");Text("Model: ${telemetry.model}");Text("Next: ${telemetry.nextAction.ifBlank{"Waiting for frame"}}");Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick=viewModel::pauseJalebiLoop,enabled=telemetry.loopId!=null){Text("Pause")};OutlinedButton(onClick={viewModel.replanJalebiLoop("camera_manual_replan")},enabled=telemetry.loopId!=null){Text("Replan")};OutlinedButton(onClick=viewModel::stopLiveJalebi,enabled=telemetry.loopId!=null){Text("Stop")}}}
    }
}
