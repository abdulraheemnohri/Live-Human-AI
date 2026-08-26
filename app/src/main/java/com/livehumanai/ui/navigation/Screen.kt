package com.livehumanai.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Chat : Screen("chat", "Chat")
    object Vision : Screen("vision", "Vision")
    object Memory : Screen("memory", "Memory")
    object More : Screen("more", "More")
    
    // More section sub-screens
    object Models : Screen("models", "Models")
    object Downloads : Screen("downloads", "Downloads")
    object Performance : Screen("performance", "Performance")
    object Tasks : Screen("tasks", "Tasks")
    object Knowledge : Screen("knowledge", "Knowledge")
    object Privacy : Screen("privacy", "Privacy")
    object Security : Screen("security", "Security")
    object Settings : Screen("settings", "Settings")
    object Diagnostics : Screen("diagnostics", "Diagnostics")
    object Developer : Screen("developer", "Developer")
    object About : Screen("about", "About")
    
    // Detail screens
    object ChatDetail : Screen("chat/{conversationId}", "Chat") {
        fun createRoute(conversationId: Long) = "chat/$conversationId"
    }
    object ModelDetail : Screen("model/{modelId}", "Model") {
        fun createRoute(modelId: String) = "model/$modelId"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val selectedIcon: String,
    val unselectedIcon: String,
    val contentDescription: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "home", "home_outline", "Home"),
    BottomNavItem(Screen.Chat, "chat", "chat_outline", "Chat"),
    BottomNavItem(Screen.Vision, "visibility", "visibility_outline", "Vision"),
    BottomNavItem(Screen.Memory, "folder", "folder_outline", "Memory"),
    BottomNavItem(Screen.More, "menu", "menu_outline", "More")
)
