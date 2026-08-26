package com.livehumanai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.livehumanai.ui.chat.ChatScreen
import com.livehumanai.ui.home.HomeScreen
import com.livehumanai.ui.memory.MemoryScreen
import com.livehumanai.ui.vision.VisionScreen
import com.livehumanai.ui.models.ModelsScreen
import com.livehumanai.ui.downloads.DownloadsScreen
import com.livehumanai.ui.performance.PerformanceScreen
import com.livehumanai.ui.tasks.TasksScreen
import com.livehumanai.ui.knowledge.KnowledgeScreen
import com.livehumanai.ui.privacy.PrivacyScreen
import com.livehumanai.ui.security.SecurityScreen
import com.livehumanai.ui.settings.SettingsScreen
import com.livehumanai.ui.diagnostics.DiagnosticsScreen
import com.livehumanai.ui.developer.DeveloperScreen
import com.livehumanai.ui.about.AboutScreen

@Composable
fun LiveHumanAINavGraph(
    navController: NavHostController,
    startDestination: String = "home"
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDestination
    
    // Extract base route for bottom bar highlighting (handles detail screens)
    val baseRoute = currentRoute.substringBefore("/")
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Main tab screens
        composable(Screen.Home.route) {
            HomeScreen(
                onChatClick = { navController.navigate(Screen.Chat.route) },
                onVoiceClick = { /* Handle voice input */ },
                onCameraClick = { navController.navigate(Screen.Vision.route) },
                onSearchQuery = { query -> 
                    // Navigate to chat with pre-filled query
                    navController.navigate(Screen.Chat.route)
                }
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
        
        composable(Screen.More.route) {
            MoreScreen(
                onNavigateToScreen = { route ->
                    navController.navigate(route)
                }
            )
        }
        
        // More section sub-screens
        composable(Screen.Models.route) {
            ModelsScreen(
                onNavigateBack = { navController.popBackStack() },
                onModelClick = { modelId ->
                    navController.navigate(Screen.ModelDetail.createRoute(modelId))
                }
            )
        }
        
        composable(Screen.Downloads.route) {
            DownloadsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Performance.route) {
            PerformanceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Tasks.route) {
            TasksScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Knowledge.route) {
            KnowledgeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Privacy.route) {
            PrivacyScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Security.route) {
            SecurityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Diagnostics.route) {
            DiagnosticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Developer.route) {
            DeveloperScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Detail screens with arguments
        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(
                navArgument("conversationId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: 0L
            ChatScreen(
                conversationId = conversationId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.ModelDetail.route,
            arguments = listOf(
                navArgument("modelId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
            ModelDetailScreen(
                modelId = modelId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Returns the current screen route for bottom bar highlighting.
 * Handles both main routes and detail screens.
 */
fun getCurrentRouteForBottomBar(currentRoute: String): String {
    return when {
        currentRoute.startsWith("chat/") -> Screen.Chat.route
        currentRoute.startsWith("model/") -> Screen.Models.route
        else -> currentRoute
    }
}
