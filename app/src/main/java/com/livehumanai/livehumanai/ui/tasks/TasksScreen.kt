package com.livehumanai.livehumanai.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class UiTaskItem(
    val id: String,
    val title: String,
    val goal: String,
    val status: String, // QUEUED, RUNNING, COMPLETED, FAILED
    val iterations: Int,
    val maxIterations: Int,
    val confidence: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var taskList by remember {
        mutableStateOf(
            listOf(
                UiTaskItem(
                    id = "task-1",
                    title = "Analyze Document & Extract Tables",
                    goal = "Extract technical specifications from PDF and summarize key specs",
                    status = "RUNNING",
                    iterations = 3,
                    maxIterations = 8,
                    confidence = 92
                ),
                UiTaskItem(
                    id = "task-2",
                    title = "Continuous Scene Detection",
                    goal = "Monitor camera stream for objects and change events",
                    status = "COMPLETED",
                    iterations = 5,
                    maxIterations = 5,
                    confidence = 98
                )
            )
        )
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskGoal by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Jalebi Autonomous Tasks", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Every complex AI prompt is dispatched as a task with strict memory, iteration, and token limits.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text("Active & Recent Tasks", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(taskList) { task ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(task.title, style = MaterialTheme.typography.titleSmall)
                                AssistChip(
                                    onClick = {},
                                    label = { Text(task.status) }
                                )
                            }

                            Text(task.goal, style = MaterialTheme.typography.bodySmall)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Iteration ${task.iterations} / ${task.maxIterations}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "Confidence: ${task.confidence}%",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            LinearProgressIndicator(
                                progress = { task.iterations.toFloat() / task.maxIterations.toFloat() },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (task.status == "RUNNING") {
                                    OutlinedButton(onClick = {
                                        taskList = taskList.map {
                                            if (it.id == task.id) it.copy(status = "CANCELLED") else it
                                        }
                                    }) {
                                        Text("Cancel")
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                IconButton(onClick = {
                                    taskList = taskList.filter { it.id != task.id }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Task")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create AI Task") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            label = { Text("Task Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newTaskGoal,
                            onValueChange = { newTaskGoal = it },
                            label = { Text("Goal Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                taskList = taskList + UiTaskItem(
                                    id = "task-${System.currentTimeMillis()}",
                                    title = newTaskTitle,
                                    goal = newTaskGoal,
                                    status = "QUEUED",
                                    iterations = 0,
                                    maxIterations = 5,
                                    confidence = 100
                                )
                                newTaskTitle = ""
                                newTaskGoal = ""
                                showCreateDialog = false
                            }
                        }
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
