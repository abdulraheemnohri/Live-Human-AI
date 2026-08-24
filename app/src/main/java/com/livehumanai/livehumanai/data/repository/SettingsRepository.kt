package com.livehumanai.livehumanai.data.repository

import com.livehumanai.livehumanai.data.database.dao.SettingsDao
import com.livehumanai.livehumanai.data.database.entity.SettingsEntity
import javax.inject.Inject

/**
 * SettingsRepository provides data access for app settings.
 */
class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) {

    suspend fun setSetting(key: String, value: String, type: SettingsEntity.SettingsType) {
        val setting = SettingsEntity(
            key = key,
            value = value,
            type = type
        )
        settingsDao.insertSetting(setting)
    }

    suspend fun getSetting(key: String): SettingsEntity? {
        return settingsDao.getSettingByKey(key)
    }

    suspend fun getAllSettings(): List<SettingsEntity> {
        return settingsDao.getAllSettings()
    }

    suspend fun deleteSetting(key: String) {
        settingsDao.deleteSetting(key)
    }

    suspend fun deleteAllSettings() {
        settingsDao.deleteAllSettings()
    }

    // Convenience methods for common setting types

    suspend fun getBooleanSetting(key: String, defaultValue: Boolean = false): Boolean {
        return settingsDao.getStringSetting(key)?.toBooleanStrictOrNull() ?: defaultValue
    }

    suspend fun getIntegerSetting(key: String, defaultValue: Int = 0): Int {
        return settingsDao.getStringSetting(key)?.toIntOrNull() ?: defaultValue
    }

    suspend fun getFloatSetting(key: String, defaultValue: Float = 0f): Float {
        return settingsDao.getStringSetting(key)?.toFloatOrNull() ?: defaultValue
    }

    suspend fun getStringSetting(key: String, defaultValue: String = ""): String {
        return settingsDao.getStringSetting(key) ?: defaultValue
    }

    suspend fun setBooleanSetting(key: String, value: Boolean) {
        setSetting(key, value.toString(), SettingsEntity.SettingsType.BOOLEAN)
    }

    suspend fun setIntegerSetting(key: String, value: Int) {
        setSetting(key, value.toString(), SettingsEntity.SettingsType.INTEGER)
    }

    suspend fun setFloatSetting(key: String, value: Float) {
        setSetting(key, value.toString(), SettingsEntity.SettingsType.FLOAT)
    }

    suspend fun setStringSetting(key: String, value: String) {
        setSetting(key, value, SettingsEntity.SettingsType.STRING)
    }

    // Initialize default settings
    suspend fun initializeDefaultSettings() {
        settingsDao.insertDefaultSettings()
    }

    // Specific setting getters and setters

    // AI Settings
    suspend fun getDefaultModel(): String {
        return getStringSetting(SettingsEntity.DEFAULT_MODEL, "qwen3-1.7b-q4")
    }

    suspend fun setDefaultModel(modelName: String) {
        setStringSetting(SettingsEntity.DEFAULT_MODEL, modelName)
    }

    suspend fun getPerformanceMode(): String {
        return getStringSetting(SettingsEntity.PERFORMANCE_MODE, "BALANCED")
    }

    suspend fun setPerformanceMode(mode: String) {
        setStringSetting(SettingsEntity.PERFORMANCE_MODE, mode)
    }

    suspend fun getTemperature(): Float {
        return getFloatSetting(SettingsEntity.TEMPERATURE, 0.7f)
    }

    suspend fun setTemperature(temperature: Float) {
        setFloatSetting(SettingsEntity.TEMPERATURE, temperature)
    }

    suspend fun getTopP(): Float {
        return getFloatSetting(SettingsEntity.TOP_P, 0.9f)
    }

    suspend fun setTopP(topP: Float) {
        setFloatSetting(SettingsEntity.TOP_P, topP)
    }

    suspend fun getMaxTokens(): Int {
        return getIntegerSetting(SettingsEntity.MAX_TOKENS, 512)
    }

    suspend fun setMaxTokens(maxTokens: Int) {
        setIntegerSetting(SettingsEntity.MAX_TOKENS, maxTokens)
    }

    // Voice Settings
    suspend fun isWakeWordEnabled(): Boolean {
        return getBooleanSetting(SettingsEntity.WAKE_WORD_ENABLED, true)
    }

    suspend fun setWakeWordEnabled(enabled: Boolean) {
        setBooleanSetting(SettingsEntity.WAKE_WORD_ENABLED, enabled)
    }

    suspend fun isMicrophoneEnabled(): Boolean {
        return getBooleanSetting(SettingsEntity.MICROPHONE_ENABLED, true)
    }

    suspend fun setMicrophoneEnabled(enabled: Boolean) {
        setBooleanSetting(SettingsEntity.MICROPHONE_ENABLED, enabled)
    }

    suspend fun getSTTModel(): String {
        return getStringSetting(SettingsEntity.STT_MODEL, "whisper-base")
    }

    suspend fun setSTTModel(modelName: String) {
        setStringSetting(SettingsEntity.STT_MODEL, modelName)
    }

    suspend fun getTTSModel(): String {
        return getStringSetting(SettingsEntity.TTS_MODEL, "piper-en")
    }

    suspend fun setTTSModel(modelName: String) {
        setStringSetting(SettingsEntity.TTS_MODEL, modelName)
    }

    suspend fun getVoice(): String {
        return getStringSetting(SettingsEntity.VOICE, "default")
    }

    suspend fun setVoice(voice: String) {
        setStringSetting(SettingsEntity.VOICE, voice)
    }

    suspend fun getSpeechSpeed(): Float {
        return getFloatSetting(SettingsEntity.SPEECH_SPEED, 1.0f)
    }

    suspend fun setSpeechSpeed(speed: Float) {
        setFloatSetting(SettingsEntity.SPEECH_SPEED, speed)
    }

    suspend fun getSpeechPitch(): Float {
        return getFloatSetting(SettingsEntity.SPEECH_PITCH, 1.0f)
    }

    suspend fun setSpeechPitch(pitch: Float) {
        setFloatSetting(SettingsEntity.SPEECH_PITCH, pitch)
    }

    // Vision Settings
    suspend fun isCameraEnabled(): Boolean {
        return getBooleanSetting(SettingsEntity.CAMERA_ENABLED, true)
    }

    suspend fun setCameraEnabled(enabled: Boolean) {
        setBooleanSetting(SettingsEntity.CAMERA_ENABLED, enabled)
    }

    suspend fun isObjectDetectionEnabled(): Boolean {
        return getBooleanSetting(SettingsEntity.OBJECT_DETECTION_ENABLED, true)
    }

    suspend fun setObjectDetectionEnabled(enabled: Boolean) {
        setBooleanSetting(SettingsEntity.OBJECT_DETECTION_ENABLED, enabled)
    }

    suspend fun isOCREnabled(): Boolean {
        return getBooleanSetting(SettingsEntity.OCR_ENABLED, true)
    }

    suspend fun setOCREnabled(enabled: Boolean) {
        setBooleanSetting(SettingsEntity.OCR_ENABLED, enabled)
    }

    suspend fun getVisionModel(): String {
        return getStringSetting(SettingsEntity.VISION_MODEL, "yolo-nano")
    }

    suspend fun setVisionModel(modelName: String) {
        setStringSetting(SettingsEntity.VISION_MODEL, modelName)
    }

    // Memory Settings
    suspend fun isMemoryEnabled(): Boolean {
        return getBooleanSetting(SettingsEntity.MEMORY_ENABLED, true)
    }

    suspend fun setMemoryEnabled(enabled: Boolean) {
        setBooleanSetting(SettingsEntity.MEMORY_ENABLED, enabled)
    }

    suspend fun getMemoryRetentionDays(): Int {
        return getIntegerSetting(SettingsEntity.MEMORY_RETENTION_DAYS, 30)
    }

    suspend fun setMemoryRetentionDays(days: Int) {
        setIntegerSetting(SettingsEntity.MEMORY_RETENTION_DAYS, days)
    }

    // Network Settings
    suspend fun isNetworkEnabled(): Boolean {
        return getBooleanSetting(SettingsEntity.NETWORK_ENABLED, false)
    }

    suspend fun setNetworkEnabled(enabled: Boolean) {
        setBooleanSetting(SettingsEntity.NETWORK_ENABLED, enabled)
    }

    // Privacy Settings
    suspend fun isAnalyticsEnabled(): Boolean {
        return getBooleanSetting(SettingsEntity.ANALYTICS_ENABLED, false)
    }

    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        setBooleanSetting(SettingsEntity.ANALYTICS_ENABLED, enabled)
    }
}
