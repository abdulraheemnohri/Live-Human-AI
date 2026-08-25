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
import com.livehumanai.livehumanai.data.repository.AIRepository
import com.livehumanai.livehumanai.data.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executors
import javax.inject.Inject

/** Real Android TextToSpeech bridge. The app owns the shared native runtime lifecycle. */
@AndroidEntryPoint
class TTSService : Service(), TextToSpeech.OnInitListener {
    @Inject lateinit var aiRepository: AIRepository
    @Inject lateinit var settingsRepository: SettingsRepository

    private val binder = TTSServiceBinder()
    private val executor = Executors.newSingleThreadExecutor()
    private var tts: TextToSpeech? = null
    private var pendingSynthesisFile: File? = null
    private var pendingSynthesisCallback: ((ByteArray) -> Unit)? = null

    var isSynthesizing by mutableStateOf(false)
        private set
    var isReady by mutableStateOf(false)
        private set

    inner class TTSServiceBinder : Binder() { fun getService(): TTSService = this@TTSService }
    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        isReady = true
        tts?.language = Locale.getDefault()
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) { isSynthesizing = true }
            override fun onDone(utteranceId: String) {
                val file = pendingSynthesisFile
                val callback = pendingSynthesisCallback
                pendingSynthesisFile = null
                pendingSynthesisCallback = null
                isSynthesizing = false
                if (file != null && callback != null) executor.execute {
                    val bytes = runCatching { file.readBytes() }.getOrDefault(ByteArray(0))
                    file.delete()
                    callback(bytes)
                }
            }
            override fun onError(utteranceId: String) {
                pendingSynthesisFile?.delete()
                pendingSynthesisFile = null
                pendingSynthesisCallback?.invoke(ByteArray(0))
                pendingSynthesisCallback = null
                isSynthesizing = false
            }
        })
    }

    fun startSynthesis(text: String, callback: (ByteArray) -> Unit) {
        if (text.isBlank() || !isReady || isSynthesizing) return
        val engine = tts ?: return
        val file = File.createTempFile("jcl_tts_", ".wav", cacheDir)
        pendingSynthesisFile?.delete()
        pendingSynthesisFile = file
        pendingSynthesisCallback = callback
        isSynthesizing = true
        val id = "jcl-${UUID.randomUUID()}"
        val result = engine.synthesizeToFile(text, Bundle(), file, id)
        if (result != TextToSpeech.SUCCESS) {
            file.delete()
            pendingSynthesisFile = null
            pendingSynthesisCallback = null
            isSynthesizing = false
            callback(ByteArray(0))
        }
    }

    fun speak(text: String): Boolean {
        if (text.isBlank() || !isReady) return false
        return tts?.speak(text, TextToSpeech.QUEUE_FLUSH, Bundle(), "live-${UUID.randomUUID()}") == TextToSpeech.SUCCESS
    }

    fun stopSynthesis() {
        tts?.stop()
        pendingSynthesisFile?.delete()
        pendingSynthesisFile = null
        pendingSynthesisCallback = null
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
