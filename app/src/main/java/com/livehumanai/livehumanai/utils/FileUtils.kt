package com.livehumanai.livehumanai.utils

import android.content.Context
import android.os.Environment
import android.os.StatFs
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * FileUtils provides utility functions for file operations.
 */
object FileUtils {

    // Get the models directory
    fun getModelsDirectory(context: Context): File {
        return File(context.getExternalFilesDir(null), Constants.MODEL_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    // Get the LLM models directory
    fun getLLMModelsDirectory(context: Context): File {
        return File(getModelsDirectory(context), Constants.LLM_MODEL_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    // Get the STT models directory
    fun getSTTModelsDirectory(context: Context): File {
        return File(getModelsDirectory(context), Constants.STT_MODEL_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    // Get the TTS models directory
    fun getTTSModelsDirectory(context: Context): File {
        return File(getModelsDirectory(context), Constants.TTS_MODEL_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    // Get the vision models directory
    fun getVisionModelsDirectory(context: Context): File {
        return File(getModelsDirectory(context), Constants.VISION_MODEL_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    // Check if a file exists
    fun fileExists(file: File): Boolean {
        return file.exists() && file.isFile
    }

    // Get file size in bytes
    fun getFileSize(file: File): Long {
        return if (fileExists(file)) file.length() else 0
    }

    // Get file size in human-readable format
    fun getFileSizeString(file: File): String {
        return getFileSize(file).formatBytes()
    }

    // Calculate SHA-256 checksum of a file
    fun calculateSHA256(file: File): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(file).use { input ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            null
        }
    }

    // Verify file checksum
    fun verifyChecksum(file: File, expectedChecksum: String): Boolean {
        return calculateSHA256(file) == expectedChecksum
    }

    // Copy a file
    fun copyFile(source: File, destination: File): Boolean {
        return try {
            source.inputStream().use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // Delete a file
    fun deleteFile(file: File): Boolean {
        return try {
            if (fileExists(file)) file.delete()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Delete a directory recursively
    fun deleteDirectory(dir: File): Boolean {
        return try {
            if (dir.exists()) {
                dir.listFiles()?.forEach { file ->
                    if (file.isDirectory) {
                        deleteDirectory(file)
                    } else {
                        deleteFile(file)
                    }
                }
                dir.delete()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // Get available storage space
    fun getAvailableStorageSpace(): Long {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            stat.availableBytes
        } catch (e: Exception) {
            0
        }
    }

    // Get total storage space
    fun getTotalStorageSpace(): Long {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            stat.totalBytes
        } catch (e: Exception) {
            0
        }
    }

    // Check if there's enough storage space
    fun hasEnoughStorageSpace(requiredBytes: Long): Boolean {
        return getAvailableStorageSpace() >= requiredBytes
    }

    // Get file extension
    fun getFileExtension(file: File): String {
        return file.name.substringAfterLast(".", "")
    }

    // Check if file has a specific extension
    fun hasExtension(file: File, extension: String): Boolean {
        return getFileExtension(file).equals(extension, ignoreCase = true)
    }

    // Create a temporary file
    fun createTempFile(context: Context, prefix: String = "temp", suffix: String = ".tmp"): File {
        return File.createTempFile(prefix, suffix, context.cacheDir)
    }

    // Write text to a file
    fun writeTextToFile(file: File, text: String): Boolean {
        return try {
            file.parentFile?.mkdirs()
            FileOutputStream(file).use { output ->
                output.write(text.toByteArray())
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // Read text from a file
    fun readTextFromFile(file: File): String? {
        return try {
            FileInputStream(file).use { input ->
                input.bufferedReader().use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    // List files in a directory
    fun listFiles(dir: File): List<File> {
        return dir.listFiles()?.toList() ?: emptyList()
    }

    // List files with a specific extension
    fun listFilesWithExtension(dir: File, extension: String): List<File> {
        return listFiles(dir).filter { hasExtension(it, extension) }
    }

    // Get file name without extension
    fun getFileNameWithoutExtension(file: File): String {
        return file.name.substringBeforeLast(".", file.name)
    }
}
