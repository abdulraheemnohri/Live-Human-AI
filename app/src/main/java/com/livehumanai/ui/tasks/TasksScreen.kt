package com.livehumanai.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.livehumanai.domain.model.Task
import com.livehumanai.domain.model.TaskStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    var selectedFilter by remember { mutableStateOf<TaskStatus?>(null) }
    
    // Sample tasks - will be replaced with actual data from repository
    LaunchedEffect(Unit) {
        tasks = listOf(
            Task(
                id = "1",
                goal = "Analyze document and summarize key points",
                status = TaskStatus.COMPLETED,
                createdAt = System.currentTimeMillis(),
                iterationCount = 3,
                maxIterations = 5
            ),
            Task(
                id = "2",
                goal = "Identify objects in camera view",
                status = TaskStatus.RUNNING,
                createdAt = System.currentTimeMillis() - 60000,
                iterationCount = 2,
                maxIterations = 8
            ),
            Task(
                id = "3",
                goal = "Translate text to Urdu",
                status = TaskStatus.QUEUED,
                createdAt = System.currentTimeMillis() - 120000,
                iterationCount = 0,
                maxIterations = 3
            ),
            Task(
                id = "4",
                goal = "Extract information from PDF",
                status = TaskStatus.FAILED,
                createdAt = System.currentTimeMillis() - 300000,
                iterationCount = 5,
                maxIterations = 5,
                failureReason = "Document format not supported"
            )
        )
    }
    
    val filteredTasks = if (selectedFilter != null) {
        tasks.filter { it.status == selectedFilter }
    } else {
        tasks
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Clear completed tasks */ }) {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.Delete,
                            contentDescription = "Clear Completed"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedFilter == TaskStatus.QUEUED,
                    onClick = { selectedFilter = TaskStatus.QUEUED },
                    label = { Text("Queued") }
                )
                FilterChip(
                    selected = selectedFilter == TaskStatus.RUNNING,
                    onClick = { selectedFilter = TaskStatus.RUNNING },
                    label = { Text("Running") }
                )
                FilterChip(
                    selected = selectedFilter == TaskStatus.COMPLETED,
                    onClick = { selectedFilter = TaskStatus.COMPLETED },
                    label = { Text("Completed") }
                )
                FilterChip(
                    selected = selectedFilter == TaskStatus.FAILED,
                    onClick = { selectedFilter = TaskStatus.FAILED },
                    label = { Text("Failed") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Task summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredTasks.size} task${if (filteredTasks.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${tasks.count { it.status == TaskStatus.RUNNING }} running",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tasks list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskItem(task = task)
                }
                
                if (filteredTasks.isEmpty()) {
                    item {
                        EmptyTasksView(filter = selectedFilter)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (task.status) {
                TaskStatus.RUNNING -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                TaskStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                TaskStatus.FAILED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.goal,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                StatusBadge(status = task.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress indicator for running/queued tasks
            if (task.status == TaskStatus.RUNNING || task.status == TaskStatus.QUEUED) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Iteration ${task.iterationCount}/${task.maxIterations}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(task.iterationCount.toFloat() / task.maxIterations * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    LinearProgressIndicator(
                        progress = task.iterationCount.toFloat() / task.maxIterations,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Failure reason
            if (task.status == TaskStatus.FAILED && !task.failureReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ ${task.failureReason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Timestamp
            Text(
                text = "Created ${formatTimeAgo(task.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusBadge(status: TaskStatus) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = when (status) {
                    TaskStatus.QUEUED -> "Queued"
                    TaskStatus.RUNNING -> "Running"
                    TaskStatus.PAUSED -> "Paused"
                    TaskStatus.WAITING -> "Waiting"
                    TaskStatus.COMPLETED -> "Completed"
                    TaskStatus.FAILED -> "Failed"
                    TaskStatus.CANCELLED -> "Cancelled"
                },
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = when (status) {
                TaskStatus.QUEUED -> MaterialTheme.colorScheme.surfaceVariant
                TaskStatus.RUNNING -> MaterialTheme.colorScheme.primaryContainer
                TaskStatus.PAUSED -> MaterialTheme.colorScheme.tertiaryContainer
                TaskStatus.WAITING -> MaterialTheme.colorScheme.secondaryContainer
                TaskStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer
                TaskStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
                TaskStatus.CANCELLED -> MaterialTheme.colorScheme.outlineVariant
            }
        )
    )
}

@Composable
private fun EmptyTasksView(filter: TaskStatus?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📋",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (filter != null) "No $filter tasks" else "No tasks yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tasks will appear here when you ask the AI to perform complex operations",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60000 -> "just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> "${diff / 86400000}d ago"
    }
}
