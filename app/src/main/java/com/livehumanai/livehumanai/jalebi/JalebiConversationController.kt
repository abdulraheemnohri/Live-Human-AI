package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.data.repository.AIRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/** LISTEN -> UNDERSTAND -> THINK -> RESPOND -> LISTEN orchestration. */
@Singleton
class JalebiConversationController @Inject constructor(
    private val aiRepository: AIRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null
    private val loop = JalebiConversationLoop()

    fun start() { loop.start() }
    fun stop() { job?.cancel(); job = null; loop.stop() }
    fun state(): JalebiConversationLoop.State = loop.state()

    fun submit(
        input: JalebiAudioAdapter.SpeechInput,
        respond: suspend (String) -> String,
        onResult: (Result) -> Unit
    ) {
        if (loop.state() != JalebiConversationLoop.State.LISTENING) return
        loop.onSpeech(input.transcript)
        loop.onIntentResolved()
        job?.cancel()
        job = scope.launch {
            if (aiRepository.shouldPauseJalebiForResources()) {
                onResult(Result(input.transcript, "", input.confidence, false, "RESOURCE_LIMIT"))
                loop.onSpeechFinished()
                return@launch
            }
            val response = respond(input.transcript)
            loop.onResponseReady()
            onResult(Result(input.transcript, response, input.confidence, true, "RESPOND"))
            loop.onSpeechFinished()
        }
    }

    data class Result(
        val transcript: String,
        val response: String,
        val confidence: Float,
        val spoken: Boolean,
        val nextAction: String
    )
}
