package com.livehumanai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.livehumanai.ui.screens.chat.ChatScreen
import com.livehumanai.ui.screens.home.HomeScreen
import com.livehumanai.ui.screens.memory.MemoryScreen
import com.livehumanai.ui.screens.vision.VisionScreen
import com.livehumanai.ui.screens.models.ModelsScreen
import com.livehumanai.ui.screens.downloads.DownloadsScreen
import com.livehumanai.ui.screens.settings.SettingsScreen
import com.livehumanai.ui.screens.diagnostics.DiagnosticsScreen
import com.livehumanai.ui.tasks.TasksScreen
import com.livehumanai.ui.knowledge.KnowledgeScreen
import com.livehumanai.ui.security.SecurityScreen
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
                onNavigateToDownloads = {
                    navController.navigate(Screen.Downloads.route)
                }
            )
        }
        
        composable(Screen.Downloads.route) {
            DownloadsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Performance.route) {
            // Placeholder for Performance
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
            // Placeholder for Privacy
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
            // Placeholder for ModelDetail
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
