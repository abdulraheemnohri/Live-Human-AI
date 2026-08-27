package com.livehumanai.livehumanai.ui.knowledge

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

data class UiDocumentItem(
    val id: String,
    val name: String,
    val fileType: String,
    val size: String,
    val chunks: Int,
    val isIndexed: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var documents by remember {
        mutableStateOf(
            listOf(
                UiDocumentItem(
                    id = "doc-1",
                    name = "Live_Human_AI_Architecture_Spec.pdf",
                    fileType = "PDF",
                    size = "2.4 MB",
                    chunks = 48,
                    isIndexed = true
                ),
                UiDocumentItem(
                    id = "doc-2",
                    name = "Project_Notes_Urdu_Translate.txt",
                    fileType = "TXT",
                    size = "120 KB",
                    chunks = 6,
                    isIndexed = true
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Document AI & Knowledge Base") },
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
            FloatingActionButton(onClick = {
                documents = documents + UiDocumentItem(
                    id = "doc-${System.currentTimeMillis()}",
                    name = "Imported_Document_${documents.size + 1}.pdf",
                    fileType = "PDF",
                    size = "1.1 MB",
                    chunks = 18,
                    isIndexed = true
                )
            }) {
                Icon(Icons.Default.Add, contentDescription = "Import Document")
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search knowledge base & documents...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Semantic Knowledge Index", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Documents are chunked, embedded, and stored locally for offline RAG context retrieval.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text("Indexed Documents (${documents.size})", style = MaterialTheme.typography.titleMedium)

            val filteredDocs = documents.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredDocs) { doc ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(doc.name, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${doc.fileType} • ${doc.size} • ${doc.chunks} semantic chunks",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    if (doc.isIndexed) "● Indexed for AI retrieval" else "○ Pending indexing",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            IconButton(onClick = {
                                documents = documents.filter { it.id != doc.id }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }
        }
    }
}
