package com.livehumanai.livehumanai.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livehumanai.livehumanai.ui.theme.LiveHumanAITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isUser: Boolean,
    val confidence: Float = 0.94f,
    val source: String = "Local LLM",
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen() {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }
    var selectedMessage by remember { mutableStateOf<ChatMessage?>(null) }

    var currentModel by remember { mutableStateOf("qwen3-1.7b-q4") }
    var latency by remember { mutableStateOf("0ms") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Live Conversation", style = MaterialTheme.typography.titleLarge)
            Text(text = "$currentModel • $latency", style = MaterialTheme.typography.bodySmall)
        }

        // Messages list
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { selectedMessage = message }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(if (message.isUser) "YOU" else "AI", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(message.text, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        Text("${message.source} • ${(message.confidence * 100).toInt()}% confidence", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Input Composer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { isListening = !isListening },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Voice"
                )
            }

            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                decorationBox = { inner ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                        if (inputText.isEmpty()) Text("Ask anything or show camera...", color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                        inner()
                    }
                }
            )

            Button(
                onClick = {
                    if (inputText.isNotBlank() && !isGenerating) {
                        val prompt = inputText
                        messages = messages + ChatMessage(text = prompt, isUser = true)
                        inputText = ""
                        isGenerating = true

                        scope.launch {
                            val start = System.currentTimeMillis()
                            val response = withContext(Dispatchers.IO) {
                                val bridge = com.livehumanai.livehumanai.nativebridge.NativeBridge.getInstance()
                                if (bridge.isInitialized) bridge.generate(prompt, currentModel, 0.7f, 512)
                                else "I understand: $prompt"
                            }
                            latency = "${System.currentTimeMillis() - start}ms"
                            messages = messages + ChatMessage(text = response, isUser = false)
                            isGenerating = false
                        }
                    }
                },
                enabled = inputText.isNotBlank() && !isGenerating
            ) {
                if (isGenerating) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }

        // Long Press Action Sheet
        selectedMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = { selectedMessage = null },
                title = { Text("Message Options") },
                text = { Text(msg.text) },
                confirmButton = {
                    Button(onClick = { selectedMessage = null }) { Text("Save to Memory") }
                },
                dismissButton = {
                    TextButton(onClick = { selectedMessage = null }) { Text("Close") }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    LiveHumanAITheme { ChatScreen() }
}
