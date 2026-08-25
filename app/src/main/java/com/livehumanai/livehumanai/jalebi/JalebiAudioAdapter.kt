package com.livehumanai.livehumanai.jalebi

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Microphone/STT-facing semantic adapter. Audio capture remains outside JCL;
 * only a transcript and confidence enter the cognitive loop.
 */
@Singleton
class JalebiAudioAdapter @Inject constructor() {
    fun submitTranscript(
        transcript: String,
        confidence: Float,
        speakerId: String? = null,
        final: Boolean = true
    ): SpeechInput? {
        if (!final || transcript.isBlank()) return null
        return SpeechInput(transcript.trim(), confidence.coerceIn(0f, 1f), speakerId)
    }

    data class SpeechInput(
        val transcript: String,
        val confidence: Float,
        val speakerId: String?
    )
}
