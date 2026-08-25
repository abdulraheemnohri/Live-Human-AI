package com.livehumanai.livehumanai.ui.memory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livehumanai.livehumanai.ui.theme.LiveHumanAITheme

data class MemoryState(
    val id: Long,
    val title: String,
    val content: String,
    val type: String,
    val isImportant: Boolean,
    val source: String = "User Verified",
    val confidence: Float = 0.98f,
    val createdAt: String
)

@Composable
fun MemoryScreen() {
    var memories by remember { mutableStateOf(listOf<MemoryState>()) }
    var searchQuery by remember { mutableStateOf("") }
    var showImportantOnly by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }

    if (memories.isEmpty()) {
        memories = listOf(
            MemoryState(1, "Preference", "Urdu + English concise responses", "Preference", true, "Conversation", 0.99f, "Today"),
            MemoryState(2, "Project", "Live Human AI JCL Loop architecture", "Project", true, "Camera OCR", 0.95f, "Yesterday"),
            MemoryState(3, "Important Note", "Approved user-specific local memory policy", "Important", true, "Manual Input", 1.0f, "3 days ago")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Memory Console", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search memory...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("User-Approved / Important Only")
            Switch(checked = showImportantOnly, onCheckedChange = { showImportantOnly = it })
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val filtered = memories.filter {
                (!showImportantOnly || it.isImportant) &&
                (it.title.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true))
            }

            items(filtered) { memory ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(memory.title, style = MaterialTheme.typography.titleMedium)
                            Text("${(memory.confidence * 100).toInt()}% conf", style = MaterialTheme.typography.labelSmall)
                        }
                        Text(memory.content, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Category: ${memory.type} • Source: ${memory.source} • ${memory.createdAt}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Button(
            onClick = {
                newTitle = ""
                newContent = ""
                showAddDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Memory")
            Spacer(modifier = Modifier.weight(1f))
            Text("Add Memory")
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Memory") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Title") })
                        OutlinedTextField(value = newContent, onValueChange = { newContent = it }, label = { Text("Content") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTitle.isNotBlank() && newContent.isNotBlank()) {
                                memories = memories + MemoryState(
                                    id = System.currentTimeMillis(),
                                    title = newTitle,
                                    content = newContent,
                                    type = "User Note",
                                    isImportant = true,
                                    createdAt = "Just now"
                                )
                            }
                            showAddDialog = false
                        }
                    ) { Text("Remember") }
                },
                dismissButton = {
                    Button(onClick = { showAddDialog = false }) { Text("Don't Remember") }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemoryScreenPreview() {
    LiveHumanAITheme { MemoryScreen() }
}
