package com.livehumanai.livehumanai.jalebi

data class JalebiAudioSignal(
    val transcript: String,
    val confidence: Float,
    val isFinal: Boolean,
    val timestampMs: Long = System.currentTimeMillis()
)
