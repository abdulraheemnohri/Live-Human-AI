package com.livehumanai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "device_profiles",
    indices = []
)
data class DeviceProfileEntity(
    @PrimaryKey
    val id: String = "current",
    val totalRamBytes: Long,
    val availableRamBytes: Long,
    val cpuArchitecture: String,
    val cpuCoreCount: Int,
    val hasNeon: Boolean,
    val hasGpu: Boolean,
    val gpuVendor: String?,
    val hasVulkan: Boolean,
    val availableStorageBytes: Long,
    val androidVersion: Int,
    val abi: String,
    val thermalStatus: String = "normal", // "normal", "warm", "hot", "critical"
    val batteryLevel: Int = 100,
    val isCharging: Boolean = false,
    val profileCategory: String = "unknown", // "lite", "standard", "pro"
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
