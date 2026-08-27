package com.livehumanai.livehumanai.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * 5-Section Bottom Navigation System (Home, Chat, Vision, Memory, More) with Floating Actions.
 */
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav(currentDestination)) {
                BottomNavigationBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        },
        floatingActionButton = {
            if (shouldShowBottomNav(currentDestination)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.Chat.route) },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Input")
                    }
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.Vision.route) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Camera Input")
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavGraph(navController = navController)
        }
    }
}

private fun shouldShowBottomNav(destination: androidx.navigation.NavDestination?): Boolean {
    return destination?.route in listOf(
        Screen.Home.route,
        Screen.Chat.route,
        Screen.Vision.route,
        Screen.Memory.route,
        Screen.More.route
    )
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    val items = listOf(
        BottomNavItem(
            route = Screen.Home.route,
            icon = Icons.Default.Home,
            label = "Home"
        ),
        BottomNavItem(
            route = Screen.Chat.route,
            icon = Icons.Default.Chat,
            label = "Chat"
        ),
        BottomNavItem(
            route = Screen.Vision.route,
            icon = Icons.Default.Videocam,
            label = "Vision"
        ),
        BottomNavItem(
            route = Screen.Memory.route,
            icon = Icons.Default.Psychology,
            label = "Memory"
        ),
        BottomNavItem(
            route = Screen.More.route,
            icon = Icons.Default.MoreHoriz,
            label = "More"
        )
    )

    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = actions
    )
}
