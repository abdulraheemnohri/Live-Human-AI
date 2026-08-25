package com.livehumanai.livehumanai.jalebi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

data class JalebiTelemetryEvent(
    val timestampMs: Long = System.currentTimeMillis(),
    val loopId: Int?,
    val state: String,
    val iteration: Int,
    val confidence: Float,
    val model: String = "",
    val latencyMs: Long = 0,
    val message: String = ""
)

@Singleton
class JalebiTelemetryStore @Inject constructor() {
    private val sequence = AtomicLong(0)
    private val _events = MutableStateFlow<List<JalebiTelemetryEvent>>(emptyList())
    val events: StateFlow<List<JalebiTelemetryEvent>> = _events.asStateFlow()

    fun record(event: JalebiTelemetryEvent) {
        _events.value = (_events.value + event).takeLast(128)
    }

    fun nextIteration(): Int = sequence.incrementAndGet().toInt()

    fun clear() {
        sequence.set(0)
        _events.value = emptyList()
    }
}
