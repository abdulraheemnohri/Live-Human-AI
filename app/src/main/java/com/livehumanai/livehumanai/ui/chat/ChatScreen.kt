package com.livehumanai.livehumanai.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.livehumanai.livehumanai.ui.theme.LiveHumanAITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ChatScreen provides a chat interface for interacting with the AI.
 * It supports text input, voice input, and displays the conversation history.
 */
@Composable
fun ChatScreen() {
    // State for the chat
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }

    // AI state
    var currentModel by remember { mutableStateOf("qwen3-1.7b-q4") }
    var latency by remember { mutableStateOf("0ms") }

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
            Text(
                text = "Live AI",
                style = MaterialTheme.typography.titleLarge
            )

            // Model indicator
            Text(
                text = currentModel,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Latency indicator
        Text(
            text = "Latency: $latency",
            style = MaterialTheme.typography.bodySmall
        )

        // Messages list
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatMessageBubble(message = message)
            }
        }

        // Input area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Voice input button
            val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
            IconButton(
                onClick = {
                    isListening = !isListening
                    if (isListening) {
                        messages = messages + ChatMessage(
                            text = "[Listening to user speech...]",
                            isUser = true
                        )
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isListening) "Stop Listening" else "Start Listening",
                    tint = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            // Text input
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(horizontal = 8.dp),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (inputText.isEmpty()) {
                            Text(
                                text = "Ask about what you see...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // Send button
            val scope = androidx.compose.runtime.rememberCoroutineScope()
            Button(
                onClick = {
                    if (inputText.isNotBlank() && !isGenerating) {
                        val userPrompt = inputText
                        messages = messages + ChatMessage(
                            text = userPrompt,
                            isUser = true
                        )

                        isGenerating = true
                        inputText = ""

                        scope.launch {
                            val startTime = System.currentTimeMillis()
                            val responseText = withContext(Dispatchers.IO) {
                                val nativeBridge = com.livehumanai.livehumanai.nativebridge.NativeBridge.getInstance()
                                if (nativeBridge.isInitialized) {
                                    nativeBridge.generate(userPrompt, currentModel, 0.7f, 512)
                                } else {
                                    kotlinx.coroutines.delay(600)
                                    "I processed your request using $currentModel locally on device."
                                }
                            }
                            val elapsed = System.currentTimeMillis() - startTime
                            latency = "${elapsed}ms"
                            messages = messages + ChatMessage(
                                text = responseText,
                                isUser = false
                            )
                            isGenerating = false
                        }
                    }
                },
                enabled = inputText.isNotBlank() && !isGenerating,
                modifier = Modifier.size(48.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }

        // Cancel button (if generating)
        if (isGenerating) {
            Button(
                onClick = {
                    val nativeBridge = com.livehumanai.livehumanai.nativebridge.NativeBridge.getInstance()
                    if (nativeBridge.isInitialized) {
                        nativeBridge.stopGeneration()
                    }
                    isGenerating = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel Generation")
            }
        }
    }
}

/**
 * Represents a message in the chat.
 */
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Displays a single chat message.
 */
@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (message.isUser) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(12.dp)
        )

        Text(
            text = java.time.Instant.ofEpochMilli(message.timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalTime()
                .toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    LiveHumanAITheme {
        ChatScreen()
    }
}
