package com.livehumanai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.livehumanai.ui.screens.home.HomeScreen
import com.livehumanai.ui.screens.chat.ChatScreen
import com.livehumanai.ui.screens.vision.VisionScreen
import com.livehumanai.ui.screens.memory.MemoryScreen
import com.livehumanai.ui.screens.models.ModelsScreen
import com.livehumanai.ui.screens.downloads.DownloadsScreen
import com.livehumanai.ui.screens.settings.SettingsScreen
import com.livehumanai.ui.screens.diagnostics.DiagnosticsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToVision = { navController.navigate(Screen.Vision.route) },
                onNavigateToMemory = { navController.navigate(Screen.Memory.route) },
                onNavigateToModels = { navController.navigate(Screen.Models.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.Chat.route) {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Vision.route) {
            VisionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Memory.route) {
            MemoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Models.route) {
            ModelsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDownloads = { navController.navigate(Screen.Downloads.route) }
            )
        }
        
        composable(Screen.Downloads.route) {
            DownloadsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDiagnostics = { navController.navigate(Screen.Diagnostics.route) },
                onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) },
                onNavigateToPerformance = { navController.navigate(Screen.Performance.route) }
            )
        }
        
        composable(Screen.Diagnostics.route) {
            DiagnosticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Placeholder screens for remaining routes
        composable(Screen.Developer.route) {
            DeveloperScreenPlaceholder(onNavigateBack = { navController.popBackStack() })
        }
        
        composable(Screen.Privacy.route) {
            PrivacyScreenPlaceholder(onNavigateBack = { navController.popBackStack() })
        }
        
        composable(Screen.Performance.route) {
            PerformanceScreenPlaceholder(onNavigateBack = { navController.popBackStack() })
        }
        
        composable(Screen.Tasks.route) {
            TasksScreenPlaceholder(onNavigateBack = { navController.popBackStack() })
        }
        
        composable(Screen.Knowledge.route) {
            KnowledgeScreenPlaceholder(onNavigateBack = { navController.popBackStack() })
        }
        
        composable(Screen.Security.route) {
            SecurityScreenPlaceholder(onNavigateBack = { navController.popBackStack() })
        }
        
        composable(Screen.About.route) {
            AboutScreenPlaceholder(onNavigateBack = { navController.popBackStack() })
        }
        
        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.LongType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: 0L
            ChatScreen(
                conversationId = conversationId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.ModelDetail.route,
            arguments = listOf(navArgument("modelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
            ModelDetailScreenPlaceholder(
                modelId = modelId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

// Placeholder screen functions - to be implemented in subsequent phases
@Composable
private fun DeveloperScreenPlaceholder(onNavigateBack: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Developer") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.material3.Surface(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("Developer Screen - Coming Soon")
            }
        }
    }
}

@Composable
private fun PrivacyScreenPlaceholder(onNavigateBack: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Privacy") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.material3.Surface(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("Privacy Center - Coming Soon")
            }
        }
    }
}

@Composable
private fun PerformanceScreenPlaceholder(onNavigateBack: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Performance") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.material3.Surface(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("Performance Dashboard - Coming Soon")
            }
        }
    }
}

@Composable
private fun TasksScreenPlaceholder(onNavigateBack: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Tasks") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.material3.Surface(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("Task Manager - Coming Soon")
            }
        }
    }
}

@Composable
private fun KnowledgeScreenPlaceholder(onNavigateBack: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Knowledge") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.material3.Surface(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("Knowledge Base - Coming Soon")
            }
        }
    }
}

@Composable
private fun SecurityScreenPlaceholder(onNavigateBack: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Security") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.material3.Surface(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("Security Settings - Coming Soon")
            }
        }
    }
}

@Composable
private fun AboutScreenPlaceholder(onNavigateBack: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("About") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.material3.Surface(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("About Live Human AI - Coming Soon")
            }
        }
    }
}

@Composable
private fun ModelDetailScreenPlaceholder(modelId: String, onNavigateBack: () -> Unit) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Model Details") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.material3.Surface(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("Model Detail: $modelId - Coming Soon")
            }
        }
    }
}
