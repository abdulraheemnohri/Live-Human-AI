package com.livehumanai.livehumanai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.livehumanai.livehumanai.ui.HomeScreen
import com.livehumanai.livehumanai.ui.about.AboutScreen
import com.livehumanai.livehumanai.ui.chat.ChatScreen
import com.livehumanai.livehumanai.ui.developer.DeveloperScreen
import com.livehumanai.livehumanai.ui.diagnostics.DiagnosticsScreen
import com.livehumanai.livehumanai.ui.downloads.DownloadsScreen
import com.livehumanai.livehumanai.ui.jalebi.JalebiDeveloperScreen
import com.livehumanai.livehumanai.ui.knowledge.KnowledgeScreen
import com.livehumanai.livehumanai.ui.memory.MemoryScreen
import com.livehumanai.livehumanai.ui.models.ModelManagerScreen
import com.livehumanai.livehumanai.ui.performance.PerformanceScreen
import com.livehumanai.livehumanai.ui.privacy.PrivacyScreen
import com.livehumanai.livehumanai.ui.security.SecurityScreen
import com.livehumanai.livehumanai.ui.settings.SettingsScreen
import com.livehumanai.livehumanai.ui.tasks.TasksScreen
import com.livehumanai.livehumanai.ui.vision.VisionScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController, Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToCamera = { navController.navigate(Screen.Vision.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToJalebi = { navController.navigate(Screen.Jalebi.route) }
            )
        }
        composable(Screen.Chat.route) { ChatScreen() }
        composable(Screen.Vision.route) { VisionScreen() }
        composable(Screen.Memory.route) { MemoryScreen() }
        composable(Screen.More.route) {
            MoreScreen(
                onNavigateToScreen = { route -> navController.navigate(route) }
            )
        }
        composable(Screen.Models.route) { ModelManagerScreen() }
        composable(Screen.Downloads.route) { DownloadsScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Screen.Performance.route) { PerformanceScreen() }
        composable(Screen.Tasks.route) { TasksScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Screen.Knowledge.route) { KnowledgeScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Screen.Privacy.route) { PrivacyScreen() }
        composable(Screen.Security.route) { SecurityScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Screen.Settings.route) { SettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Diagnostics.route) { DiagnosticsScreen() }
        composable(Screen.Developer.route) { DeveloperScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Screen.About.route) { AboutScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Screen.Jalebi.route) { JalebiDeveloperScreen() }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Chat : Screen("chat")
    object Vision : Screen("vision")
    object Memory : Screen("memory")
    object More : Screen("more")
    object Models : Screen("models")
    object Downloads : Screen("downloads")
    object Performance : Screen("performance")
    object Tasks : Screen("tasks")
    object Knowledge : Screen("knowledge")
    object Privacy : Screen("privacy")
    object Security : Screen("security")
    object Settings : Screen("settings")
    object Diagnostics : Screen("diagnostics")
    object Developer : Screen("developer")
    object About : Screen("about")
    object Jalebi : Screen("jalebi-developer")
}
