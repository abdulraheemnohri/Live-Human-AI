package com.livehumanai.livehumanai.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.livehumanai.livehumanai.data.repository.AIRepository
import com.livehumanai.livehumanai.data.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** Real Android SpeechRecognizer bridge. The app owns the shared native runtime lifecycle. */
@AndroidEntryPoint
class STTService : Service() {
    @Inject lateinit var aiRepository: AIRepository
    @Inject lateinit var settingsRepository: SettingsRepository

    private val binder = STTServiceBinder()
    private var recognizer: SpeechRecognizer? = null
    private var recognitionCallback: ((String) -> Unit)? = null
    private var liveLoopId: Int? = null
    var isRecognizing by mutableStateOf(false)
        private set

    inner class STTServiceBinder : Binder() { fun getService(): STTService = this@STTService }
    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this).apply { setRecognitionListener(listener) }
        }
    }

    override fun onDestroy() {
        stopRecognition()
        recognizer?.destroy()
        recognizer = null
        super.onDestroy()
    }

    fun startRecognition(callback: (String) -> Unit) { startRecognitionInternal(null, callback) }
    fun startLiveRecognition(loopId: Int, callback: (String) -> Unit = {}) { startRecognitionInternal(loopId, callback) }

    private fun startRecognitionInternal(loopId: Int?, callback: (String) -> Unit) {
        if (isRecognizing) return
        val speechRecognizer = recognizer ?: run { callback("Error: speech recognition unavailable"); return }
        liveLoopId = loopId
        recognitionCallback = callback
        isRecognizing = true
        speechRecognizer.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        })
    }

    fun stopRecognition() {
        isRecognizing = false
        liveLoopId = null
        recognitionCallback = null
        recognizer?.cancel()
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) { isRecognizing = true }
        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() { isRecognizing = false }
        override fun onPartialResults(results: Bundle?) {
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
            if (text.isNotBlank()) recognitionCallback?.invoke(text)
        }
        override fun onResults(results: Bundle?) {
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
            val confidence = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)?.firstOrNull() ?: 0.0f
            } else 0.0f
            if (text.isNotBlank()) {
                recognitionCallback?.invoke(text)
                liveLoopId?.let { aiRepository.submitJalebiSpeech(it, text, confidence, true) }
            }
            isRecognizing = false
        }
        override fun onError(error: Int) {
            isRecognizing = false
            recognitionCallback?.invoke("Error: speech recognition code $error")
        }
        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }

    suspend fun setSTTModel(modelName: String) { settingsRepository.setSTTModel(modelName) }
    suspend fun getSTTModel(): String = settingsRepository.getSTTModel()
}
