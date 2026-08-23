package com.livehumanai.livehumanai.ui.memory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * MemoryScreen provides an interface for viewing and managing AI memories.
 */
@Composable
fun MemoryScreen() {
    // State for memories
    var memories by remember { mutableStateOf(listOf<MemoryState>()) }
    var searchQuery by remember { mutableStateOf("") }
    var showImportantOnly by remember { mutableStateOf(false) }

    // Initialize with some sample data
    if (memories.isEmpty()) {
        memories = listOf(
            MemoryState(
                id = 1,
                title = "My Preferences",
                content = "User prefers dark mode and Urdu language",
                type = "Preference",
                isImportant = true,
                createdAt = "2 days ago"
            ),
            MemoryState(
                id = 2,
                title = "Project Deadline",
                content = "The AI project is due on August 30, 2026",
                type = "Project",
                isImportant = true,
                createdAt = "1 week ago"
            ),
            MemoryState(
                id = 3,
                title = "Favorite Food",
                content = "User likes biryani and karahi",
                type = "General",
                isImportant = false,
                createdAt = "3 days ago"
            ),
            MemoryState(
                id = 4,
                title = "Contact Info",
                content = "User's phone number is +92-XXX-XXXXXXX",
                type = "Fact",
                isImportant = true,
                createdAt = "5 days ago"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Memory",
            style = MaterialTheme.typography.headlineMedium
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search memories...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        )

        // Filter toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Important Only")
            androidx.compose.material3.Switch(
                checked = showImportantOnly,
                onCheckedChange = { showImportantOnly = it }
            )
        }

        // Memories list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filteredMemories = memories.filter { memory ->
                (if (showImportantOnly) memory.isImportant else true) &&
                (memory.title.contains(searchQuery, ignoreCase = true) ||
                 memory.content.contains(searchQuery, ignoreCase = true))
            }

            items(filteredMemories) { memory ->
                MemoryCard(
                    memory = memory,
                    onToggleImportant = { memoryId ->
                        memories = memories.map { m ->
                            if (m.id == memoryId) m.copy(isImportant = !m.isImportant) else m
                        }
                    },
                    onEdit = { memoryId ->
                        // TODO: Open edit dialog
                    },
                    onDelete = { memoryId ->
                        memories = memories.filter { it.id != memoryId }
                    }
                )
            }
        }

        // Add memory button
        Button(
            onClick = { /* TODO: Open add memory dialog */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Memory"
            )
            Spacer(modifier = Modifier.weight(1f))
            Text("Add Memory")
        }
    }
}

/**
 * Represents a memory for display in the UI.
 */
data class MemoryState(
    val id: Long,
    val title: String,
    val content: String,
    val type: String,
    val isImportant: Boolean,
    val createdAt: String
)

/**
 * Displays a card for a single memory with actions.
 */
@Composable
fun MemoryCard(
    memory: MemoryState,
    onToggleImportant: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = memory.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = memory.content,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${memory.type} • ${memory.createdAt}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { onToggleImportant(memory.id) }) {
                    Icon(
                        imageVector = if (memory.isImportant) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = "Toggle Important",
                        tint = if (memory.isImportant) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                IconButton(onClick = { onEdit(memory.id) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }

                IconButton(onClick = { onDelete(memory.id) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemoryScreenPreview() {
    LiveHumanAITheme {
        MemoryScreen()
    }
}
