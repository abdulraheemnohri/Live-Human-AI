package com.livehumanai.livehumanai.jalebi

/** Immutable telemetry snapshot for the developer-mode Jalebi dashboard. */
data class JalebiTelemetry(
    val state: String = "IDLE",
    val iteration: Int = 0,
    val confidence: Float = 0f,
    val goal: String = "",
    val model: String = "",
    val latencyMs: Long = 0L,
    val ramPercent: Float = 0f,
    val cpuPercent: Float = 0f,
    val temperatureC: Float = 0f,
    val batteryPercent: Float = 0f,
    val nextAction: String = "",
    val loopId: Int? = null,
    val historyJson: String = "[]"
) {
    val activeStage: String
        get() = when (state) {
            "PERCEIVING" -> "PERCEIVE"
            "INTERPRETING" -> "INTERPRET"
            "REASONING" -> "REASON"
            "PLANNING" -> "PLAN"
            "ACTING" -> "ACT"
            "OBSERVING" -> "OBSERVE"
            "EVALUATING" -> "EVALUATE"
            "UPDATING_MEMORY" -> "UPDATE MEMORY"
            "REPLANNING" -> "REPLAN"
            else -> state
        }
}

/** Thread-safe holder used by developer UI; only metadata is retained. */
class JalebiTelemetryStore {
    @Volatile private var current = JalebiTelemetry()
    fun update(value: JalebiTelemetry) { current = value.copy(confidence = value.confidence.coerceIn(0f, 1f)) }
    fun snapshot(): JalebiTelemetry = current
    fun clear() { current = JalebiTelemetry() }
}
