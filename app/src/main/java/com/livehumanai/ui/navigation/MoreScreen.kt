package com.livehumanai.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoreScreen(
    onNavigateToScreen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "More",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom bar
        ) {
            item {
                MoreNavItem(
                    title = "Models",
                    icon = Icons.Filled.Storage,
                    onClick = { onNavigateToScreen(Screen.Models.route) }
                )
            }
            item {
                MoreNavItem(
                    title = "Downloads",
                    icon = Icons.Filled.Download,
                    onClick = { onNavigateToScreen(Screen.Downloads.route) }
                )
            }
            item {
                MoreNavItem(
                    title = "Performance",
                    icon = Icons.Filled.Speed,
                    onClick = { onNavigateToScreen(Screen.Performance.route) }
                )
            }
            item {
                MoreNavItem(
                    title = "Tasks",
                    icon = Icons.Filled.TaskAlt,
                    onClick = { onNavigateToScreen(Screen.Tasks.route) }
                )
            }
            item {
                MoreNavItem(
                    title = "Knowledge",
                    icon = Icons.Filled.School,
                    onClick = { onNavigateToScreen(Screen.Knowledge.route) }
                )
            }
            item {
                MoreNavItem(
                    title = "Privacy",
                    icon = Icons.Filled.Lock,
                    onClick = { onNavigateToScreen(Screen.Privacy.route) }
                )
            }
            item {
                MoreNavItem(
                    title = "Security",
                    icon = Icons.Filled.Security,
                    onClick = { onNavigateToScreen(Screen.Security.route) }
                )
            }
            item {
                MoreNavItem(
                    title = "Settings",
                    icon = Icons.Filled.Settings,
                    onClick = { onNavigateToScreen(Screen.Settings.route) }
                )
            }
            item {
                MoreNavItem(
                    title = "Diagnostics",
                    icon = Icons.Filled.BugReport,
                    onClick = { onNavigateToScreen(Screen.Diagnostics.route) }
                )
            }
            item {
                MoreNavItem(
                    title = "Developer",
                    icon = Icons.Filled.Code,
                    onClick = { onNavigateToScreen(Screen.Developer.route) }
                )
            }
            item {
                MoreNavItem(
                    title = "About",
                    icon = Icons.Filled.Info,
                    onClick = { onNavigateToScreen(Screen.About.route) }
                )
            }
        }
    }
}

@Composable
private fun MoreNavItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
