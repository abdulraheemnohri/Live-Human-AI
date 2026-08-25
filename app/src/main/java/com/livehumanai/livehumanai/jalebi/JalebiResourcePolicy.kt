package com.livehumanai.livehumanai.jalebi

/** Resource-aware policy for continuous camera/audio/LLM workloads. */
enum class JalebiDeviceTier { UNKNOWN, LOW_6GB, STANDARD_8GB, HIGH_16GB }
enum class JalebiResourceMode { NORMAL, WARM, HOT, CRITICAL, PAUSED }

data class JalebiResourceSnapshot(
    val ramPercent: Float,
    val cpuPercent: Float,
    val temperatureC: Float,
    val batteryPercent: Float,
    val tier: JalebiDeviceTier,
    val mode: JalebiResourceMode
)

data class JalebiWorkloadLimits(
    val cameraFps: Int,
    val cameraWidth: Int,
    val cameraHeight: Int,
    val maxAudioSeconds: Int,
    val maxTokens: Int,
    val allowVision: Boolean,
    val allowSpeech: Boolean,
    val allowLargeModel: Boolean
)

class JalebiResourcePolicy {
    fun tier(totalRamMb: Long): JalebiDeviceTier = when {
        totalRamMb <= 0 -> JalebiDeviceTier.UNKNOWN
        totalRamMb <= 6144 -> JalebiDeviceTier.LOW_6GB
        totalRamMb <= 12288 -> JalebiDeviceTier.STANDARD_8GB
        else -> JalebiDeviceTier.HIGH_16GB
    }

    fun mode(temperatureC: Float, ramPercent: Float, batteryPercent: Float): JalebiResourceMode = when {
        temperatureC >= 48f || ramPercent >= 95f -> JalebiResourceMode.CRITICAL
        temperatureC >= 43f || ramPercent >= 88f -> JalebiResourceMode.HOT
        temperatureC >= 39f || ramPercent >= 78f || batteryPercent in 0f..10f -> JalebiResourceMode.WARM
        else -> JalebiResourceMode.NORMAL
    }

    fun limits(snapshot: JalebiResourceSnapshot): JalebiWorkloadLimits {
        if (snapshot.mode == JalebiResourceMode.CRITICAL || snapshot.mode == JalebiResourceMode.PAUSED) {
            return JalebiWorkloadLimits(1, 320, 240, 5, 128, false, false, false)
        }
        val low = snapshot.tier == JalebiDeviceTier.LOW_6GB
        return when (snapshot.mode) {
            JalebiResourceMode.HOT -> JalebiWorkloadLimits(2, 640, 480, 10, 256, true, true, false)
            JalebiResourceMode.WARM -> JalebiWorkloadLimits(5, if (low) 640 else 960, if (low) 480 else 540, 15, if (low) 256 else 512, true, true, !low)
            else -> JalebiWorkloadLimits(if (low) 8 else 15, if (low) 960 else 1280, if (low) 540 else 720, if (low) 20 else 30, if (low) 512 else 1024, true, true, snapshot.tier == JalebiDeviceTier.HIGH_16GB)
        }
    }
}
