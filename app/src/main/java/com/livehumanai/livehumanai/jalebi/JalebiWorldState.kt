package com.livehumanai.livehumanai.jalebi

/** Ephemeral, privacy-safe world state. Raw camera/audio is never retained here. */
data class JalebiWorldState(
    val timestampMs: Long = System.currentTimeMillis(),
    val sceneId: String = "",
    val detectedObjects: List<String> = emptyList(),
    val detectedText: List<String> = emptyList(),
    val visionConfidence: Float = 0f,
    val changed: Boolean = false
)

class JalebiWorldStateTracker {
    @Volatile private var previous = JalebiWorldState()

    fun update(result: JalebiVisionResult): JalebiWorldState {
        val objects = result.objects.map(String::trim).filter(String::isNotEmpty).distinct().sorted()
        val text = result.text.map(String::trim).filter(String::isNotEmpty).distinct().sorted()
        val changed = previous.sceneId != result.sceneId || previous.detectedObjects != objects || previous.detectedText != text
        return JalebiWorldState(result.timestampMs, result.sceneId, objects, text, result.confidence, changed).also { previous = it }
    }

    fun snapshot(): JalebiWorldState = previous
    fun reset() { previous = JalebiWorldState() }
}
