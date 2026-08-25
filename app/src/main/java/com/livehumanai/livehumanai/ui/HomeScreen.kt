package com.livehumanai.livehumanai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livehumanai.livehumanai.ui.theme.LiveHumanAITheme

@Composable
fun HomeScreen(onNavigateToChat:()->Unit={},onNavigateToCamera:()->Unit={},onNavigateToSettings:()->Unit={},onNavigateToJalebi:()->Unit={}){
 var runtimeStatus by remember{mutableStateOf("Ready")};var deviceProfile by remember{mutableStateOf("Balanced")}
 LaunchedEffect(Unit){val n=com.livehumanai.livehumanai.nativebridge.NativeBridge.getInstance();if(n.isInitialized){runtimeStatus=n.getRuntimeStatus();deviceProfile=n.getDeviceProfile()}}
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally){
  Text("Live Human AI",style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.primary);Spacer(Modifier.height(8.dp));Text("Status: $runtimeStatus");Text("Profile: $deviceProfile");Spacer(Modifier.height(28.dp));Text("● READY",style=MaterialTheme.typography.displaySmall,color=MaterialTheme.colorScheme.primary);Spacer(Modifier.height(28.dp));Text("How can I help?",style=MaterialTheme.typography.bodyLarge);Spacer(Modifier.height(20.dp))
  Row(horizontalArrangement=Arrangement.spacedBy(18.dp)){IconButton(onClick=onNavigateToChat,modifier=Modifier.size(64.dp)){Icon(Icons.Default.Mic,"Voice Input")};IconButton(onClick=onNavigateToCamera,modifier=Modifier.size(64.dp)){Icon(Icons.Default.Videocam,"Camera")}}
  Spacer(Modifier.height(20.dp));Button(onClick=onNavigateToJalebi){Text("Jalebi Cognitive Loop")};Spacer(Modifier.height(16.dp));Text("Bounded • Permission-aware • Resource-aware",style=MaterialTheme.typography.bodySmall)
  IconButton(onClick=onNavigateToSettings,modifier=Modifier.align(Alignment.End)){Icon(Icons.Default.Settings,"Settings")}
 }
}
@Preview(showBackground=true) @Composable fun HomeScreenPreview(){LiveHumanAITheme{HomeScreen()}}
