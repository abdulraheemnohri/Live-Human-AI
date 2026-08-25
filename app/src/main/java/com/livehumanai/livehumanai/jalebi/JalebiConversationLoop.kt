package com.livehumanai.livehumanai.jalebi

/** Bounded state machine for LISTEN -> UNDERSTAND -> THINK -> RESPOND -> LISTEN. */
class JalebiConversationLoop {
    enum class State { IDLE, LISTENING, UNDERSTANDING, THINKING, RESPONDING, ERROR, STOPPED }

    private var current = State.IDLE

    @Synchronized fun start() { current = State.LISTENING }
    @Synchronized fun stop() { current = State.STOPPED }
    @Synchronized fun state(): State = current

    @Synchronized fun onSpeech(transcript: String) {
        current = if (transcript.isBlank()) State.ERROR else State.UNDERSTANDING
    }

    @Synchronized fun onIntentResolved() {
        if (current == State.UNDERSTANDING) current = State.THINKING
    }

    @Synchronized fun onTranscript(text: String) {
        onSpeech(text)
        onIntentResolved()
    }

    @Synchronized fun onResponseReady() {
        if (current == State.THINKING || current == State.UNDERSTANDING) current = State.RESPONDING
    }

    @Synchronized fun onSpeechFinished() { current = State.LISTENING }
    @Synchronized fun onLowConfidenceSpeech(@Suppress("UNUSED_PARAMETER") confidence: Float) { current = State.LISTENING }
    @Synchronized fun onSpeechError() { current = State.ERROR }
}

/** Compatibility semantic signal used by the conversation orchestrator. */
data class JalebiAudioSignal(
    val transcript: String,
    val confidence: Float,
    val isFinal: Boolean
)
