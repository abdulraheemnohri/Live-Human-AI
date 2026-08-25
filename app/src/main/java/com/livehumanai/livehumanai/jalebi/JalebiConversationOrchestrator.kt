package com.livehumanai.livehumanai.jalebi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Binds semantic STT events to the bounded conversation loop. LLM/TTS remain
 * injectable so the orchestrator never owns microphone permissions or audio IO.
 */
class JalebiConversationOrchestrator(
    private val conversation: JalebiConversationLoopAdapter,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private var job: Job? = null

    fun start() = conversation.start()
    fun stop() { job?.cancel(); job = null; conversation.stop() }

    fun onSpeech(signal: JalebiAudioSignal, respond: suspend (String) -> String, speak: suspend (String) -> Unit) {
        if (!signal.isFinal || signal.transcript.isBlank()) return
        job?.cancel()
        job = scope.launch {
            conversation.onTranscript(signal.transcript)
            val answer = respond(signal.transcript)
            if (answer.isNotBlank()) {
                conversation.onResponseReady()
                speak(answer)
                conversation.onSpeechFinished()
            }
        }
    }
}

interface JalebiConversationLoopAdapter {
    fun start()
    fun stop()
    fun onTranscript(text: String)
    fun onResponseReady()
    fun onSpeechFinished()
}
