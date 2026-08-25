package com.livehumanai.livehumanai.ui.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.livehumanai.livehumanai.nativebridge.NativeBridge
import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicBoolean

/** 16 kHz mono PCM microphone capture with optional native Whisper/JCL routing. */
class AudioManager(private val context: Context) {
    companion object {
        const val SAMPLE_RATE = 16_000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val MIN_VAD_RMS = 0.008f
        private const val MAX_CAPTURE_SECONDS = 30
        val BUFFER_SIZE: Int = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
            .takeIf { it > 0 }?.coerceAtLeast(2048) ?: 4096
    }

    private val nativeBridge = NativeBridge.getInstance()
    private var audioRecord: AudioRecord? = null
    private val recording = AtomicBoolean(false)
    private var captureThread: Thread? = null
    private var audioData = ByteArrayOutputStream()
    private var speechLoopId: Int? = null
    private var speechFlagshipDevice = false

    var isMicrophoneAvailable by mutableStateOf(false); private set
    var availableInputDevices by mutableStateOf(listOf<AudioDeviceInfo>()); private set
    var isRecording by mutableStateOf(false); private set
    var lastTranscript by mutableStateOf(""); private set
    var lastSpeechConfidence by mutableStateOf(0f); private set
    var isSpeechModelLoaded by mutableStateOf(false); private set

    fun initialize() {
        if (!hasMicrophonePermission()) { isMicrophoneAvailable = false; return }
        try {
            val manager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
            availableInputDevices = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.getDevices(android.media.AudioManager.GET_DEVICES_INPUTS).filter { it.isSourceDevice() }
            } else emptyList()
            releaseRecorder()
            audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE)
            isMicrophoneAvailable = audioRecord?.state == AudioRecord.STATE_INITIALIZED
        } catch (_: Exception) { isMicrophoneAvailable = false }
    }

    fun attachSpeechLoop(loopId: Int, flagshipDevice: Boolean = false): Boolean {
        if (!nativeBridge.isInitialized || loopId <= 0) return false
        speechLoopId = loopId; speechFlagshipDevice = flagshipDevice; return true
    }
    fun detachSpeechLoop() { speechLoopId = null; speechFlagshipDevice = false }

    fun loadSpeechModel(modelPath: String): Boolean {
        if (!nativeBridge.isInitialized || modelPath.isBlank()) return false
        val loaded = nativeBridge.loadSpeechModel(modelPath); isSpeechModelLoaded = loaded; return loaded
    }

    fun startRecording() {
        if (recording.get() || !isMicrophoneAvailable || !hasMicrophonePermission()) return
        val recorder = audioRecord ?: run { initialize(); audioRecord } ?: return
        try {
            audioData = ByteArrayOutputStream()
            recorder.startRecording()
            if (recorder.recordingState != AudioRecord.RECORDSTATE_RECORDING) return
            recording.set(true); isRecording = true
            captureThread = Thread({ captureLoop(recorder) }, "lhai-audio-capture").apply { isDaemon = true; start() }
        } catch (_: Exception) { recording.set(false); isRecording = false }
    }

    fun stopRecording(): ByteArray { stopCapture(); return audioData.toByteArray() }

    fun stopAndTranscribe(): String {
        stopCapture()
        val pcm = audioData.toByteArray()
        if (pcm.isEmpty() || !nativeBridge.isInitialized || !nativeBridge.isSpeechModelLoaded()) return ""
        val samples = pcmToShortArray(pcm)
        if (samples.isEmpty()) return ""
        val transcript = nativeBridge.transcribePcm(samples, SAMPLE_RATE).trim()
        lastTranscript = transcript
        return transcript
    }

    fun stopAndSubmitToJalebi(): String {
        val transcript = stopAndTranscribe(); val loopId = speechLoopId
        if (transcript.isBlank() || loopId == null || !hasSpeechSignal()) return transcript
        val confidence = 0.85f
        lastSpeechConfidence = confidence
        nativeBridge.submitJalebiSpeech(loopId, transcript, confidence, true, speechFlagshipDevice)
        return transcript
    }

    fun stopSpeech() = nativeBridge.stopSpeech()
    fun unloadSpeechModel() { nativeBridge.unloadSpeechModel(); isSpeechModelLoaded = false }
    fun getAudioDataAsFloatArray(): FloatArray = pcmToFloatArray(audioData.toByteArray())

    fun hasMicrophonePermission(): Boolean = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    fun cleanup() {
        stopCapture(); detachSpeechLoop(); unloadSpeechModel(); releaseRecorder()
        availableInputDevices = emptyList(); isMicrophoneAvailable = false
    }

    private fun captureLoop(recorder: AudioRecord) {
        val buffer = ByteArray(BUFFER_SIZE); var capturedBytes = 0L
        val maxBytes = SAMPLE_RATE.toLong() * 2L * MAX_CAPTURE_SECONDS
        try {
            while (recording.get() && capturedBytes < maxBytes) {
                val read = recorder.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING)
                if (read > 0) { audioData.write(buffer, 0, read); capturedBytes += read }
                else if (read == AudioRecord.ERROR_DEAD_OBJECT || read == AudioRecord.ERROR_INVALID_OPERATION) break
            }
        } finally {
            recording.set(false); isRecording = false
            try { if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) recorder.stop() } catch (_: Exception) {}
        }
    }

    private fun stopCapture() {
        if (!recording.getAndSet(false)) { isRecording = false; return }
        try { audioRecord?.stop() } catch (_: Exception) {}
        captureThread?.let { thread -> if (thread !== Thread.currentThread()) try { thread.join(500) } catch (_: InterruptedException) { Thread.currentThread().interrupt() } }
        captureThread = null; isRecording = false
    }
    private fun releaseRecorder() { try { audioRecord?.stop() } catch (_: Exception) {}; try { audioRecord?.release() } catch (_: Exception) {}; audioRecord = null }

    private fun pcmToShortArray(bytes: ByteArray): ShortArray {
        val out = ShortArray(bytes.size / 2)
        for (i in out.indices) out[i] = (((bytes[i * 2 + 1].toInt()) shl 8) or (bytes[i * 2].toInt() and 0xff)).toShort()
        return out
    }
    private fun pcmToFloatArray(bytes: ByteArray): FloatArray { val s = pcmToShortArray(bytes); return FloatArray(s.size) { s[it] / 32768f } }

    fun calculateRms(): Float {
        val samples = pcmToShortArray(audioData.toByteArray()); if (samples.isEmpty()) return 0f
        var sum = 0.0
        for (sample in samples) { val n = sample / 32768.0; sum += n * n }
        return kotlin.math.sqrt(sum / samples.size).toFloat()
    }
    fun hasSpeechSignal(): Boolean = calculateRms() >= MIN_VAD_RMS

    private fun AudioDeviceInfo.isSourceDevice(): Boolean = when (type) {
        AudioDeviceInfo.TYPE_BUILTIN_MIC, AudioDeviceInfo.TYPE_USB_HEADSET,
        AudioDeviceInfo.TYPE_WIRED_HEADSET, AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> true
        else -> false
    }
    data class AudioDevice(val id: Int, val name: String, val type: Int)
}
