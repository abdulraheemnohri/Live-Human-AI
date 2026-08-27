package com.livehumanai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livehumanai.ui.navigation.BottomNavItem
import com.livehumanai.ui.navigation.Screen
import com.livehumanai.ui.navigation.bottomNavItems

@Composable
fun LiveHumanAIBottomBar(
    currentRoute: String,
    onNavigateToScreen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        for (item in bottomNavItems) {
            val selected = currentRoute == item.screen.route
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = item.contentDescription
                    )
                },
                label = {
                    Text(
                        text = item.screen.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selected,
                onClick = {
                    onNavigateToScreen(item.screen.route)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigateToScreen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        for (item in bottomNavItems) {
            val selected = currentRoute == item.screen.route
            
            NavigationBarItem(
                icon = {
                    // Using placeholder icons - will be replaced with actual icon resources
                    Icon(
                        imageVector = when (item.screen) {
                            Screen.Home -> Icons.Filled.Home
                            Screen.Chat -> Icons.Filled.Message
                            Screen.Vision -> Icons.Filled.Visibility
                            Screen.Memory -> Icons.Filled.Folder
                            Screen.More -> Icons.Filled.MoreHoriz
                            else -> Icons.Filled.Home
                        },
                        contentDescription = item.contentDescription
                    )
                },
                label = {
                    Text(
                        text = item.screen.title,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                },
                selected = selected,
                onClick = {
                    onNavigateToScreen(item.screen.route)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
