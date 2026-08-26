package com.livehumanai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livehumanai.ui.navigation.BottomNavItem
import com.livehumanai.ui.navigation.Screen

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
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Home,
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
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            
            NavigationBarItem(
                icon = {
                    // Using placeholder icons - will be replaced with actual icon resources
                    Icon(
                        imageVector = when (item.screen) {
                            Screen.Home -> androidx.compose.material.icons.Icons.Filled.Home
                            Screen.Chat -> androidx.compose.material.icons.Icons.Filled.Message
                            Screen.Vision -> androidx.compose.material.icons.Icons.Filled.Visibility
                            Screen.Memory -> androidx.compose.material.icons.Icons.Filled.Folder
                            Screen.More -> androidx.compose.material.icons.Icons.Filled.MoreHoriz
                            else -> androidx.compose.material.icons.Icons.Filled.Home
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
