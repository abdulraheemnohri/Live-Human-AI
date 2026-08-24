package com.livehumanai.livehumanai.ui.audio

/**
 * VAD (Voice Activity Detection) detects speech in audio streams.
 * It uses energy-based detection to identify when speech is present.
 */
class VAD(
    private val sampleRate: Int = 16000,
    private val frameSize: Int = 512,
    private val energyThreshold: Float = 0.05f,
    private var maxSilenceFrames: Int = 10
) {

    private var speechFrames = 0
    private var currentSilenceFrames = 0
    private var isSpeaking = false

    // Process audio samples and detect speech
    fun process(samples: ShortArray): Boolean {
        val energy = calculateEnergy(samples)

        return if (energy > energyThreshold) {
            speechFrames++
            currentSilenceFrames = 0
            if (!isSpeaking && speechFrames >= 1) {
                isSpeaking = true
                true // Speech started
            } else {
                false
            }
        } else {
            currentSilenceFrames++
            speechFrames = 0
            if (isSpeaking && currentSilenceFrames >= maxSilenceFrames) {
                isSpeaking = false
                false // Speech ended
            } else {
                false
            }
        }
    }

    // Calculate energy of audio samples
    private fun calculateEnergy(samples: ShortArray): Float {
        var sum = 0L
        for (sample in samples) {
            sum += (sample.toLong() * sample.toLong())
        }
        return (sum.toFloat() / samples.size.toFloat()).coerceAtLeast(0f)
    }

    // Check if currently speaking
    fun isSpeaking(): Boolean {
        return isSpeaking
    }

    // Reset VAD state
    fun reset() {
        speechFrames = 0
        currentSilenceFrames = 0
        isSpeaking = false
    }

    // Set energy threshold
    fun setEnergyThreshold(threshold: Float) {
        // Ensure threshold is positive
    }

    // Set silence frames
    fun setSilenceFrames(frames: Int) {
        if (frames > 0) {
            maxSilenceFrames = frames
        }
    }
}
