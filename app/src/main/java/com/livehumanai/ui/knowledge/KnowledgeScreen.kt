package com.livehumanai.ui.knowledge

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.livehumanai.domain.model.Document

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var documents by remember { mutableStateOf(emptyList<Document>()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    // Sample documents - will be replaced with actual data from repository
    LaunchedEffect(Unit) {
        documents = listOf(
            Document(
                id = "1",
                title = "Project Requirements.pdf",
                category = "Documents",
                sizeBytes = 2456789L,
                createdAt = System.currentTimeMillis() - 86400000,
                chunkCount = 15
            ),
            Document(
                id = "2",
                title = "Meeting Notes - Q4 Planning",
                category = "Notes",
                sizeBytes = 12345L,
                createdAt = System.currentTimeMillis() - 172800000,
                chunkCount = 5
            ),
            Document(
                id = "3",
                title = "Research Paper - AI Ethics",
                category = "Documents",
                sizeBytes = 5678901L,
                createdAt = System.currentTimeMillis() - 604800000,
                chunkCount = 32
            ),
            Document(
                id = "4",
                title = "Personal Preferences",
                category = "Preferences",
                sizeBytes = 2048L,
                createdAt = System.currentTimeMillis() - 1209600000,
                chunkCount = 3
            )
        )
    }
    
    val filteredDocuments = documents.filter { doc ->
        val matchesSearch = searchQuery.isBlank() || 
            doc.title.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || 
            doc.category == selectedCategory
        matchesSearch && matchesCategory
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Knowledge") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Import document */ }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Document"
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
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search documents...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Category chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedCategory == "Documents",
                    onClick = { selectedCategory = "Documents" },
                    label = { Text("Documents") }
                )
                FilterChip(
                    selected = selectedCategory == "Notes",
                    onClick = { selectedCategory = "Notes" },
                    label = { Text("Notes") }
                )
                FilterChip(
                    selected = selectedCategory == "Preferences",
                    onClick = { selectedCategory = "Preferences" },
                    label = { Text("Preferences") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${filteredDocuments.size} document${if (filteredDocuments.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${documents.sumOf { it.chunkCount }} indexed chunks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Documents list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredDocuments, key = { it.id }) { document ->
                    DocumentItem(document = document)
                }
                
                if (filteredDocuments.isEmpty()) {
                    item {
                        EmptyKnowledgeView(hasFilter = searchQuery.isNotEmpty() || selectedCategory != null)
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentItem(document: Document) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Document icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (document.category) {
                        "Preferences" -> Icons.Default.Star
                        "Notes" -> Icons.Default.Edit
                        else -> Icons.Default.Description
                    },
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = document.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatFileSize(document.sizeBytes),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${document.chunkCount} chunks",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Added ${formatTimeAgo(document.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Actions
            PopupMenuButton {
                // TODO: Implement popup menu
            }
        }
    }
}

@Composable
private fun PopupMenuButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = "More options"
        )
    }
}

@Composable
private fun EmptyKnowledgeView(hasFilter: Boolean) {
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
                text = "📚",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (hasFilter) "No matching documents" else "No documents yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (hasFilter) 
                    "Try adjusting your search or filters" 
                else 
                    "Import documents to build your knowledge base",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!hasFilter) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* Import document */ }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import Document")
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60000 -> "just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        diff < 604800000 -> "${diff / 86400000}d ago"
        else -> "${diff / 604800000}w ago"
    }
}
