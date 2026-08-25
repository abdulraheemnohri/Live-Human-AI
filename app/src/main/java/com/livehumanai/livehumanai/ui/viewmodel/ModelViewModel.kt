package com.livehumanai.livehumanai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livehumanai.livehumanai.data.database.entity.ModelEntity
import com.livehumanai.livehumanai.data.repository.AIRepository
import com.livehumanai.livehumanai.data.repository.ModelRepository
import com.livehumanai.livehumanai.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ModelViewModel provides the business logic for model management operations.
 */
@HiltViewModel
class ModelViewModel @Inject constructor(
    private val modelRepository: ModelRepository,
    private val aiRepository: AIRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // State for models
    private val _models = MutableStateFlow<List<ModelState>>(emptyList())
    val models: StateFlow<List<ModelState>> = _models.asStateFlow()

    // State for installed models
    private val _installedModels = MutableStateFlow<List<ModelState>>(emptyList())
    val installedModels: StateFlow<List<ModelState>> = _installedModels.asStateFlow()

    // State for loaded models
    private val _loadedModels = MutableStateFlow<List<String>>(emptyList())
    val loadedModels: StateFlow<List<String>> = _loadedModels.asStateFlow()

    // State for download progress
    private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress.asStateFlow()

    // State for loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadModels()
        loadInstalledModels()
    }

    // Model operations

    fun loadModels() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val modelsList = modelRepository.getAllModels()
                _models.value = modelsList.map { model ->
                    ModelState(
                        name = model.name,
                        type = model.type,
                        version = model.version,
                        size = model.size,
                        format = model.format,
                        ramRequirement = model.ramRequirement,
                        supportedLanguages = model.supportedLanguages,
                        license = model.license,
                        source = model.source,
                        isInstalled = model.isInstalled,
                        isLoaded = model.isLoaded
                    )
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadInstalledModels() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val modelsList = modelRepository.getInstalledModels()
                _installedModels.value = modelsList.map { model ->
                    ModelState(
                        name = model.name,
                        type = model.type,
                        version = model.version,
                        size = model.size,
                        format = model.format,
                        ramRequirement = model.ramRequirement,
                        supportedLanguages = model.supportedLanguages,
                        license = model.license,
                        source = model.source,
                        isInstalled = model.isInstalled,
                        isLoaded = model.isLoaded
                    )
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadModel(modelName: String) {
        viewModelScope.launch {
            try {
                if (aiRepository.loadModel(modelName)) {
                    _loadedModels.value = _loadedModels.value + modelName
                    modelRepository.setModelLoaded(modelName, true)
                    updateModelState(modelName) { it.copy(isLoaded = true) }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun unloadModel(modelName: String) {
        viewModelScope.launch {
            try {
                if (aiRepository.unloadModel(modelName)) {
                    _loadedModels.value = _loadedModels.value - modelName
                    modelRepository.setModelLoaded(modelName, false)
                    updateModelState(modelName) { it.copy(isLoaded = false) }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun downloadHuggingFaceModel(repoId: String, filename: String, targetDir: java.io.File) {
        viewModelScope.launch {
            try {
                val targetFile = java.io.File(targetDir, filename)
                _downloadProgress.value = _downloadProgress.value + (filename to 0f)
                val success = modelRepository.downloadHuggingFaceModel(repoId, filename, targetFile) { _, _, progress ->
                    _downloadProgress.value = _downloadProgress.value + (filename to progress)
                }
                _downloadProgress.value = _downloadProgress.value - filename
                if (success) {
                    loadModels()
                    loadInstalledModels()
                }
            } catch (e: Exception) {
                _downloadProgress.value = _downloadProgress.value - filename
            }
        }
    }

    fun downloadModel(modelName: String) {
        viewModelScope.launch {
            try {
                // Simulate download
                _downloadProgress.value = _downloadProgress.value + (modelName to 0f)

                // In a real implementation, this would download the model file
                // and update the progress
                for (i in 1..10) {
                    kotlinx.coroutines.delay(500)
                    _downloadProgress.value = _downloadProgress.value + (modelName to (i * 0.1f))
                }

                // Mark as installed
                modelRepository.setModelInstalled(modelName, true)
                _downloadProgress.value = _downloadProgress.value - modelName
                loadInstalledModels()
            } catch (e: Exception) {
                _downloadProgress.value = _downloadProgress.value - modelName
                // Handle error
            }
        }
    }

    fun deleteModel(modelName: String) {
        viewModelScope.launch {
            try {
                modelRepository.deleteModel(modelName)
                modelRepository.setModelInstalled(modelName, false)
                modelRepository.setModelLoaded(modelName, false)
                _loadedModels.value = _loadedModels.value - modelName
                loadInstalledModels()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Settings operations

    suspend fun getDefaultModel(): String {
        return settingsRepository.getDefaultModel()
    }

    suspend fun setDefaultModel(modelName: String) {
        settingsRepository.setDefaultModel(modelName)
    }

    // Utility functions

    suspend fun getRecommendedModels(): List<ModelState> {
        val deviceProfile = aiRepository.getDeviceProfile()
        return modelRepository.getRecommendedModels(deviceProfile).map { model ->
            ModelState(
                name = model.name,
                type = model.type,
                version = model.version,
                size = model.size,
                format = model.format,
                ramRequirement = model.ramRequirement,
                supportedLanguages = model.supportedLanguages,
                license = model.license,
                source = model.source,
                isInstalled = model.isInstalled,
                isLoaded = model.isLoaded
            )
        }
    }

    fun getModelsByType(type: ModelEntity.ModelType): List<ModelState> {
        return _models.value.filter { it.type == type }
    }

    fun getInstalledModelsByType(type: ModelEntity.ModelType): List<ModelState> {
        return _installedModels.value.filter { it.type == type }
    }

    // Helper functions

    private fun updateModelState(modelName: String, update: (ModelState) -> ModelState) {
        _models.value = _models.value.map { model ->
            if (model.name == modelName) update(model) else model
        }
        _installedModels.value = _installedModels.value.map { model ->
            if (model.name == modelName) update(model) else model
        }
    }

    // State classes

    data class ModelState(
        val name: String,
        val type: ModelEntity.ModelType,
        val version: String,
        val size: Long,
        val format: String,
        val ramRequirement: Long,
        val supportedLanguages: List<String>,
        val license: String,
        val source: String,
        val isInstalled: Boolean,
        val isLoaded: Boolean
    )
}
