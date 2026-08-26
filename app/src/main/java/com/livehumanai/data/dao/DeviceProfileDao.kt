package com.livehumanai.data.dao

import androidx.room.*
import com.livehumanai.data.entity.DeviceProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceProfileDao {
    @Query("SELECT * FROM device_profiles WHERE id = 'current' LIMIT 1")
    fun getCurrentProfile(): Flow<DeviceProfileEntity?>

    @Query("SELECT * FROM device_profiles WHERE id = 'current' LIMIT 1")
    suspend fun getCurrentProfileSync(): DeviceProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: DeviceProfileEntity)

    @Query("UPDATE device_profiles SET thermalStatus = :thermalStatus, batteryLevel = :batteryLevel, isCharging = :isCharging, updatedAt = :updatedAt WHERE id = 'current'")
    suspend fun updateHardwareStatus(thermalStatus: String, batteryLevel: Int, isCharging: Boolean, updatedAt: java.util.Date)

    @Query("UPDATE device_profiles SET profileCategory = :profileCategory, updatedAt = :updatedAt WHERE id = 'current'")
    suspend fun updateProfileCategory(profileCategory: String, updatedAt: java.util.Date)
}
