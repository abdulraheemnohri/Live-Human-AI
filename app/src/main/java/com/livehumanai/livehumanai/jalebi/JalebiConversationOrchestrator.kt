package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.service.TTSService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * End-to-end bounded conversation coordinator:
 * STT semantic event -> JCL conversation -> LLM -> Android TTS.
 * Audio capture remains outside this class so permissions/lifecycle stay in AudioManager.
 */
class JalebiConversationOrchestrator(
    private val conversation: JalebiConversationLoopAdapter,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val tts: TTSService? = null,
    private val respond: suspend (String) -> String
) {
    private var job: Job? = null

    fun start() = conversation.start()

    fun stop() {
        job?.cancel()
        job = null
        tts?.stopSynthesis()
        conversation.stop()
    }

    fun onSpeech(signal: JalebiAudioSignal) {
        if (!signal.isFinal || signal.transcript.isBlank()) return
        if (signal.confidence < 0.35f) {
            conversation.onLowConfidenceSpeech(signal.confidence)
            return
        }
        job?.cancel()
        job = scope.launch {
            conversation.onTranscript(signal.transcript)
            val answer = runCatching { respond(signal.transcript).trim() }.getOrDefault("")
            if (answer.isBlank() || !isActive) return@launch
            conversation.onResponseReady()
            val spoken = speak(answer)
            if (spoken && isActive) conversation.onSpeechFinished()
            else if (isActive) conversation.onSpeechError()
        }
    }

    private suspend fun speak(text: String): Boolean {
        val service = tts ?: return false
        return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            service.startSynthesis(text) { bytes ->
                val ok = bytes.isNotEmpty()
                if (continuation.isActive) continuation.resume(ok) {}
            }
            continuation.invokeOnCancellation { service.stopSynthesis() }
        }
    }
}

interface JalebiConversationLoopAdapter {
    fun start()
    fun stop()
    fun onTranscript(text: String)
    fun onResponseReady()
    fun onSpeechFinished()
    fun onLowConfidenceSpeech(confidence: Float)
    fun onSpeechError()
}
