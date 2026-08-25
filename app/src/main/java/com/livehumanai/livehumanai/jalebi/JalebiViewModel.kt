package com.livehumanai.livehumanai.jalebi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JalebiViewModel @Inject constructor(
    private val controller: JalebiLiveController,
    private val telemetry: JalebiTelemetryStore,
    private val resources: JalebiResourceMonitor
) : ViewModel() {
    val state: StateFlow<JalebiSessionState> = controller.state
    val events = telemetry.events

    fun start(goal: String, maxIterations: Int = 8) {
        viewModelScope.launch { controller.startAsync(goal, maxIterations) }
    }

    fun pause() = controller.pause()
    fun resume() = controller.resume()
    fun stop() = controller.stop()

    fun refreshResources(onResult: (JalebiDeviceResourceSnapshot) -> Unit) {
        viewModelScope.launch { onResult(resources.snapshot()) }
    }

    override fun onCleared() {
        controller.stop()
        super.onCleared()
    }
}
