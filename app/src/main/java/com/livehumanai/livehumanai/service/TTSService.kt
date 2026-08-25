package com.livehumanai.livehumanai.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.livehumanai.livehumanai.data.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executors
import javax.inject.Inject

/** Android system TTS fallback for JCL. Native TTS remains optional and fail-closed. */
@AndroidEntryPoint
class TTSService : Service(), TextToSpeech.OnInitListener {
    @Inject lateinit var settingsRepository: SettingsRepository

    private val binder = TTSServiceBinder()
    private val executor = Executors.newSingleThreadExecutor()
    private var tts: TextToSpeech? = null
    private var pending: PendingSynthesis? = null

    var isSynthesizing by mutableStateOf(false)
        private set
    var isReady by mutableStateOf(false)
        private set

    private data class PendingSynthesis(val file: File, val callback: (ByteArray) -> Unit, val utteranceId: String)

    inner class TTSServiceBinder : Binder() { fun getService(): TTSService = this@TTSService }
    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) { isReady = false; return }
        isReady = true
        tts?.language = Locale.getDefault()
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) { isSynthesizing = true }
            override fun onDone(utteranceId: String) { finishSynthesis(utteranceId, true) }
            override fun onError(utteranceId: String) { finishSynthesis(utteranceId, false) }
        })
    }

    private fun finishSynthesis(utteranceId: String, success: Boolean) {
        val current = pending ?: return
        if (current.utteranceId != utteranceId) return
        pending = null
        isSynthesizing = false
        executor.execute {
            val bytes = if (success) runCatching { current.file.readBytes() }.getOrDefault(ByteArray(0)) else ByteArray(0)
            current.file.delete()
            current.callback(bytes)
        }
    }

    @Synchronized
    fun startSynthesis(text: String, callback: (ByteArray) -> Unit) {
        if (text.isBlank() || !isReady || isSynthesizing) return
        val engine = tts ?: return
        val file = runCatching { File.createTempFile("jcl_tts_", ".wav", cacheDir) }.getOrNull() ?: return
        val id = "jcl-${UUID.randomUUID()}"
        pending?.file?.delete()
        pending = PendingSynthesis(file, callback, id)
        isSynthesizing = true
        val result = engine.synthesizeToFile(text, Bundle(), file, id)
        if (result != TextToSpeech.SUCCESS) {
            pending = null
            isSynthesizing = false
            file.delete()
            callback(ByteArray(0))
        }
    }

    fun speak(text: String): Boolean {
        if (text.isBlank() || !isReady) return false
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, Bundle(), "live-${UUID.randomUUID()}")
        return result == TextToSpeech.SUCCESS
    }

    @Synchronized
    fun stopSynthesis() {
        tts?.stop()
        pending?.file?.delete()
        pending = null
        isSynthesizing = false
    }

    override fun onDestroy() {
        stopSynthesis()
        tts?.shutdown()
        tts = null
        isReady = false
        executor.shutdownNow()
        super.onDestroy()
    }

    suspend fun setTTSModel(modelName: String) { settingsRepository.setTTSModel(modelName) }
    suspend fun getTTSModel(): String = settingsRepository.getTTSModel()
    suspend fun setVoice(voice: String) { settingsRepository.setVoice(voice) }
    suspend fun getVoice(): String = settingsRepository.getVoice()
    suspend fun setSpeechSpeed(speed: Float) { settingsRepository.setSpeechSpeed(speed) }
    suspend fun getSpeechSpeed(): Float = settingsRepository.getSpeechSpeed()
    suspend fun setSpeechPitch(pitch: Float) { settingsRepository.setSpeechPitch(pitch) }
    suspend fun getSpeechPitch(): Float = settingsRepository.getSpeechPitch()
}
