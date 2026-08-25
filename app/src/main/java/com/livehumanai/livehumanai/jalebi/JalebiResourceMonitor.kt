package com.livehumanai.livehumanai.jalebi

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** Raw Android device resource sample. Policy snapshots are kept separate. */
data class JalebiDeviceResourceSnapshot(
    val availableMemoryMb: Long,
    val memoryPressure: Boolean,
    val batteryPercent: Int,
    val charging: Boolean,
    val thermalStatus: Int,
    val lowPowerMode: Boolean
) {
    val safeForExpensiveInference: Boolean
        get() = !memoryPressure && batteryPercent >= 15 &&
            thermalStatus < if (Build.VERSION.SDK_INT >= 29) PowerManager.THERMAL_STATUS_SEVERE else Int.MAX_VALUE
}

@Singleton
class JalebiResourceMonitor @Inject constructor(private val context: Context) {
    suspend fun snapshot(): JalebiDeviceResourceSnapshot = withContext(Dispatchers.Default) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memory = ActivityManager.MemoryInfo().also(activityManager::getMemoryInfo)
        val battery = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryPercent = battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(0, 100)
        val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val thermal = if (Build.VERSION.SDK_INT >= 29) power.currentThermalStatus else 0
        JalebiDeviceResourceSnapshot(
            availableMemoryMb = memory.availMem / (1024L * 1024L),
            memoryPressure = memory.low,
            batteryPercent = batteryPercent,
            charging = battery.isCharging,
            thermalStatus = thermal,
            lowPowerMode = power.isPowerSaveMode
        )
    }
}
