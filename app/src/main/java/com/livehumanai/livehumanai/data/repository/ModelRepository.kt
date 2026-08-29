package com.livehumanai.livehumanai.data.repository

import com.livehumanai.livehumanai.data.database.dao.ModelDao
import com.livehumanai.livehumanai.data.database.entity.ModelEntity
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * ModelRepository provides data access for AI models.
 */
import com.livehumanai.livehumanai.utils.HuggingFaceDownloader
import java.io.File

class ModelRepository @Inject constructor(
    private val modelDao: ModelDao,
    private val hfDownloader: HuggingFaceDownloader,
    @ApplicationContext private val appContext: Context
) {

    suspend fun downloadHuggingFaceModel(
        repoId: String,
        filename: String,
        targetFile: File,
        onProgress: (Long, Long, Float) -> Unit,
        revision: String = "main"
    ): Boolean {
        val success = hfDownloader.downloadModel(repoId, filename, targetFile, onProgress, revision)
        if (!success) return false

        val modelName = filename.substringBeforeLast(".")
        val type = when {
            filename.endsWith(".onnx", ignoreCase = true) -> ModelEntity.ModelType.VISION
            filename.contains("whisper", ignoreCase = true) -> ModelEntity.ModelType.STT
            else -> ModelEntity.ModelType.LLM
        }
        modelDao.insertModel(
            ModelEntity(
                name = modelName,
                version = revision,
                type = type,
                size = targetFile.length(),
                format = filename.substringAfterLast(".").uppercase(),
                quantization = if (filename.contains("q4", true)) "Q4" else "FP16",
                ramRequirement = (targetFile.length() * 2).coerceAtLeast(512L * 1024L * 1024L),
                supportsAudio = type == ModelEntity.ModelType.STT,
                license = "Unknown - verify model license",
                source = repoId,
                checksum = sha256(targetFile),
                isInstalled = true,
                installedAt = java.util.Date(),
                downloadUrl = "https://huggingface.co/$repoId/resolve/$revision/$filename"
            )
        )
        return true
    }
    private fun sha256(file: File): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(1024 * 64)
            while (true) {
                val read = input.read(buffer)
                if (read == -1) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
    suspend fun addModel(model: ModelEntity) {
        modelDao.insertModel(model)
    }

    suspend fun updateModel(model: ModelEntity) {
        modelDao.updateModel(model)
    }

    suspend fun deleteModel(name: String) {
        val model = modelDao.getModelByName(name)
        model?.let {
            val extension = it.format.lowercase()
            File(appContext.filesDir, "$name.$extension").delete()
            File(appContext.filesDir, "$name.$extension.part").delete()
        }
        modelDao.deleteModel(name)
    }

    suspend fun getModelByName(name: String): ModelEntity? {
        return modelDao.getModelByName(name)
    }

    suspend fun getAllModels(): List<ModelEntity> {
        return modelDao.getAllModels()
    }

    suspend fun getModelsByType(type: ModelEntity.ModelType): List<ModelEntity> {
        return modelDao.getModelsByType(type)
    }

    suspend fun getInstalledModels(): List<ModelEntity> {
        return modelDao.getInstalledModels()
    }

    suspend fun getLoadedModels(): List<ModelEntity> {
        return modelDao.getLoadedModels()
    }

    suspend fun getInstalledModelsByType(type: ModelEntity.ModelType): List<ModelEntity> {
        return modelDao.getInstalledModelsByType(type)
    }

    suspend fun searchModels(query: String): List<ModelEntity> {
        return modelDao.searchModels("%$query%")
    }

    suspend fun setModelLoaded(name: String, isLoaded: Boolean) {
        modelDao.setModelLoaded(name, isLoaded)
    }

    suspend fun setModelInstalled(name: String, isInstalled: Boolean) {
        modelDao.setModelInstalled(name, isInstalled)
    }

    suspend fun getModelCount(): Int {
        return modelDao.getModelCount()
    }

    suspend fun getInstalledModelCount(): Int {
        return modelDao.getInstalledModelCount()
    }

    suspend fun getLoadedModelCount(): Int {
        return modelDao.getLoadedModelCount()
    }

    // Utility functions

    suspend fun getRecommendedModels(deviceProfile: String): List<ModelEntity> {
        // In a real implementation, this would return models
        // appropriate for the device profile
        return when (deviceProfile) {
            "6GB Profile" -> {
                listOf(
                    ModelEntity(
                        name = "qwen3-0.6b-q4",
                        version = "1.0",
                        type = ModelEntity.ModelType.LLM,
                        size = 400000000,
                        format = "GGUF",
                        quantization = "Q4",
                        ramRequirement = 1000000000,
                        supportedLanguages = listOf("en", "ur", "hi", "ar"),
                        license = "Apache 2.0",
                        source = "Qwen",
                        checksum = "abc123",
                        isInstalled = true
                    ),
                    ModelEntity(
                        name = "whisper-tiny",
                        version = "1.0",
                        type = ModelEntity.ModelType.STT,
                        size = 50000000,
                        format = "GGUF",
                        ramRequirement = 500000000,
                        supportedLanguages = listOf("en", "ur", "hi", "ar"),
                        license = "MIT",
                        source = "OpenAI",
                        checksum = "def456",
                        isInstalled = true
                    ),
                    ModelEntity(
                        name = "yolo-nano",
                        version = "1.0",
                        type = ModelEntity.ModelType.VISION,
                        size = 5000000,
                        format = "ONNX",
                        ramRequirement = 200000000,
                        license = "Apache 2.0",
                        source = "Ultralytics",
                        checksum = "ghi789",
                        isInstalled = true
                    )
                )
            }
            "16GB Profile" -> {
                listOf(
                    ModelEntity(
                        name = "qwen3-4b-q4",
                        version = "1.0",
                        type = ModelEntity.ModelType.LLM,
                        size = 2000000000,
                        format = "GGUF",
                        quantization = "Q4",
                        ramRequirement = 4000000000,
                        supportedLanguages = listOf("en", "ur", "hi", "ar"),
                        license = "Apache 2.0",
                        source = "Qwen",
                        checksum = "jkl012",
                        isInstalled = true
                    ),
                    ModelEntity(
                        name = "whisper-base",
                        version = "1.0",
                        type = ModelEntity.ModelType.STT,
                        size = 100000000,
                        format = "GGUF",
                        ramRequirement = 500000000,
                        supportedLanguages = listOf("en", "ur", "hi", "ar"),
                        license = "MIT",
                        source = "OpenAI",
                        checksum = "mno345",
                        isInstalled = true
                    ),
                    ModelEntity(
                        name = "mobilenet-v3",
                        version = "1.0",
                        type = ModelEntity.ModelType.VISION,
                        size = 10000000,
                        format = "ONNX",
                        ramRequirement = 500000000,
                        license = "Apache 2.0",
                        source = "TensorFlow",
                        checksum = "pqr678",
                        isInstalled = true
                    )
                )
            }
            else -> {
                getAllModels().filter { it.isInstalled }
            }
        }
    }
}
