package com.livehumanai.livehumanai.jalebi

/** Semantic result produced by the vision layer. Raw frames never enter JCL. */
data class JalebiVisionResult(
    val sceneId: String,
    val objects: List<String> = emptyList(),
    val text: List<String> = emptyList(),
    val sceneSummary: String = "",
    val confidence: Float = 0f,
    val timestampMs: Long = System.currentTimeMillis()
) {
    fun toPerceptionInput(): String = buildString {
        append("scene=").append(sceneId)
        if (objects.isNotEmpty()) append(";objects=").append(objects.joinToString(","))
        if (text.isNotEmpty()) append(";text=").append(text.joinToString(" | "))
        if (sceneSummary.isNotBlank()) append(";summary=").append(sceneSummary)
        append(";confidence=").append(confidence.coerceIn(0f, 1f))
    }
}
