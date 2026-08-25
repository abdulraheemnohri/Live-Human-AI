package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.nativebridge.NativeBridge

/** Applies resource policy to the real native runtime before expensive work. */
class JalebiRuntimeGovernor(
    private val nativeBridge: NativeBridge = NativeBridge.getInstance(),
    private val policy: JalebiResourcePolicy = JalebiResourcePolicy()
) {
    fun snapshot(): JalebiResourceSnapshot {
        val total = nativeBridge.getTotalRAM()
        val ram = nativeBridge.getRAMUsagePercentage().coerceIn(0f, 100f)
        val cpu = nativeBridge.getCPUUsage().coerceIn(0f, 100f)
        val temp = nativeBridge.getTemperature()
        val battery = nativeBridge.getBatteryLevel().coerceIn(0f, 100f)
        val tier = policy.tier(total)
        return JalebiResourceSnapshot(ram, cpu, temp, battery, tier, policy.mode(temp, ram, battery))
    }

    fun limits(): JalebiWorkloadLimits = policy.limits(snapshot())

    fun canRunVision(): Boolean = limits().allowVision
    fun canRunSpeech(): Boolean = limits().allowSpeech
    fun canRunLargeModel(): Boolean = limits().allowLargeModel

    fun prepareForExpensiveWork(): JalebiWorkloadLimits {
        val limits = limits()
        nativeBridge.setPerformanceMode(
            when {
                limits.allowLargeModel -> NativeBridge.PerformanceMode.PERFORMANCE
                limits.cameraFps <= 2 -> NativeBridge.PerformanceMode.BATTERY_SAVER
                else -> NativeBridge.PerformanceMode.BALANCED
            }
        )
        return limits
    }
}
