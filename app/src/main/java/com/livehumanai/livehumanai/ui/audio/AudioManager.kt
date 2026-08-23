package com.livehumanai.livehumanai.ui.audio

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import java.io.ByteArrayOutputStream

/**
 * AudioManager manages audio recording and playback for the app.
 * It handles microphone access, audio capture, and processing.
 */
class AudioManager(private val context: Context) {

    companion object {
        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
    }

    private var audioRecord: AudioRecord? = null
    private var isRecording by mutableStateOf(false)
    private var audioData = ByteArrayOutputStream()

    // State
    var isMicrophoneAvailable by mutableStateOf(false)
        private set

    var availableInputDevices by mutableStateOf(listOf<AudioDeviceInfo>())
        private set

    // Initialize
    fun initialize() {
        try {
            // Check if microphone is available
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
            isMicrophoneAvailable = audioManager.isMicrophoneMute == false

            // Get available input devices
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val audioDeviceManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                availableInputDevices = audioDeviceManager.getDevices(AudioDeviceInfo.GET_ALL)
                    .filter { it.type == AudioDeviceInfo.TYPE_BUILTIN_MIC || it.type == AudioDeviceInfo.TYPE_USB_HEADSET }
                    .toList()
            }

            // Create audio recorder
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                BUFFER_SIZE
            )

            isMicrophoneAvailable = audioRecord?.state == AudioRecord.STATE_INITIALIZED
        } catch (e: Exception) {
            isMicrophoneAvailable = false
        }
    }

    // Start recording
    fun startRecording() {
        if (isMicrophoneAvailable && !isRecording) {
            try {
                audioRecord?.startRecording()
                isRecording = true
                audioData = ByteArrayOutputStream()

                // Start a thread to read audio data
                Thread {
                    val buffer = ByteArray(BUFFER_SIZE)
                    while (isRecording) {
                        val bytesRead = audioRecord?.read(buffer, 0, BUFFER_SIZE) ?: 0
                        if (bytesRead > 0) {
                            audioData.write(buffer, 0, bytesRead)
                        }
                    }
                }.start()
            } catch (e: Exception) {
                isRecording = false
            }
        }
    }

    // Stop recording
    fun stopRecording(): ByteArray {
        if (isRecording) {
            try {
                isRecording = false
                audioRecord?.stop()
                audioRecord?.release()

                // Create a new audio record for next time
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    BUFFER_SIZE
                )

                return audioData.toByteArray()
            } catch (e: Exception) {
                return ByteArray(0)
            }
        }
        return ByteArray(0)
    }

    // Get audio data as float array (normalized to [-1, 1])
    fun getAudioDataAsFloatArray(): FloatArray {
        val byteData = audioData.toByteArray()
        val floatData = FloatArray(byteData.size / 2)

        for (i in 0 until byteData.size step 2) {
            val sample = byteData[i].toInt() and 0xFF or (byteData[i + 1].toInt() shl 8)
            floatData[i / 2] = sample.toShort().toFloat() / Short.MAX_VALUE.toFloat()
        }

        return floatData
    }

    // Check if microphone permission is granted
    fun hasMicrophonePermission(): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        return audioManager.isMicrophoneMute == false
    }

    // Cleanup
    fun cleanup() {
        stopRecording()
        audioRecord?.release()
        audioRecord = null
    }

    // Data classes

    data class AudioDevice(
        val id: Int,
        val name: String,
        val type: Int
    )
}
