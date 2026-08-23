package com.livehumanai.livehumanai.ui.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * WakeWordDetector listens for a wake word in the audio stream.
 * It uses a simple energy-based detection for demonstration.
 * In a real implementation, this would use a proper wake word detection model.
 */
class WakeWordDetector(
    private val context: Context,
    private val wakeWord: String = "Hey Human AI",
    private val onWakeWordDetected: () -> Unit
) {

    companion object {
        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )

        // Threshold for wake word detection (energy-based)
        const val ENERGY_THRESHOLD = 0.1f
        const val WAKE_WORD_DURATION_MS = 1500 // Duration of wake word in ms
    }

    private var audioRecord: AudioRecord? = null
    private var isListening by mutableStateOf(false)
    private var detectionJob: Job? = null

    // State
    var isWakeWordDetectionEnabled by mutableStateOf(false)
        private set

    // Initialize
    fun initialize() {
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                BUFFER_SIZE
            )

            if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                isWakeWordDetectionEnabled = true
            }
        } catch (e: Exception) {
            isWakeWordDetectionEnabled = false
        }
    }

    // Start listening for wake word
    fun startListening() {
        if (isWakeWordDetectionEnabled && !isListening) {
            try {
                audioRecord?.startRecording()
                isListening = true

                detectionJob = CoroutineScope(Dispatchers.Default).launch {
                    val buffer = ByteArray(BUFFER_SIZE)

                    while (isActive && isListening) {
                        val bytesRead = audioRecord?.read(buffer, 0, BUFFER_SIZE) ?: 0

                        if (bytesRead > 0) {
                            // Convert to float array
                            val samples = ShortArray(bytesRead / 2)
                            for (i in 0 until bytesRead / 2) {
                                samples[i] = (buffer[i * 2].toInt() and 0xFF or (buffer[i * 2 + 1].toInt() shl 8)).toShort()
                            }

                            // Calculate energy (simple approach)
                            val energy = calculateEnergy(samples)

                            // Simple wake word detection based on energy
                            // In a real implementation, this would use a proper model
                            if (energy > ENERGY_THRESHOLD) {
                                // Simulate wake word detection
                                delay(WAKE_WORD_DURATION_MS.toLong())
                                if (isActive && isListening) {
                                    onWakeWordDetected()
                                }
                            }
                        }

                        // Small delay to prevent CPU overload
                        delay(10)
                    }
                }
            } catch (e: Exception) {
                isListening = false
            }
        }
    }

    // Stop listening
    fun stopListening() {
        if (isListening) {
            isListening = false
            detectionJob?.cancel()
            detectionJob = null
            audioRecord?.stop()
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

    // Set wake word
    fun setWakeWord(newWakeWord: String) {
        // In a real implementation, this would update the wake word model
        // For now, just update the wake word text
    }

    // Cleanup
    fun cleanup() {
        stopListening()
        audioRecord?.release()
        audioRecord = null
    }
}
