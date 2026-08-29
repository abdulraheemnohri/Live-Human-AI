package com.livehumanai.livehumanai.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HuggingFaceDownloader @Inject constructor() {

    suspend fun downloadModel(
        repoId: String,
        filename: String,
        targetFile: File,
        onProgress: (bytesDownloaded: Long, totalBytes: Long, progressPercent: Float) -> Unit,
        revision: String = "main"
    ): Boolean = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        val parent = targetFile.parentFile ?: return@withContext false
        val partialFile = File(parent, "${targetFile.name}.part")

        try {
            parent.mkdirs()
            partialFile.delete()
            var currentUrl = "https://huggingface.co/$repoId/resolve/$revision/$filename"
            var redirects = 0

            while (redirects <= 5) {
                connection = URL(currentUrl).openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = false
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 30000

                when (val responseCode = connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> break
                    HttpURLConnection.HTTP_MOVED_PERM,
                    HttpURLConnection.HTTP_MOVED_TEMP,
                    HttpURLConnection.HTTP_SEE_OTHER,
                    307,
                    308 -> {
                        val location = connection.getHeaderField("Location")
                        connection.disconnect()
                        if (location.isNullOrBlank()) return@withContext false
                        currentUrl = URL(URL(currentUrl), location).toString()
                        redirects++
                    }
                    else -> {
                        connection.disconnect()
                        return@withContext false
                    }
                }
            }

            if (connection?.responseCode != HttpURLConnection.HTTP_OK) return@withContext false
            val totalBytes = connection?.contentLengthLong ?: -1L
            inputStream = connection?.inputStream ?: return@withContext false
            outputStream = FileOutputStream(partialFile)
            val buffer = ByteArray(1024 * 64)
            var downloaded = 0L

            while (true) {
                val bytesRead = inputStream.read(buffer)
                if (bytesRead == -1) break
                outputStream.write(buffer, 0, bytesRead)
                downloaded += bytesRead
                val progress = if (totalBytes > 0) downloaded.toFloat() / totalBytes else 0f
                onProgress(downloaded, totalBytes, progress.coerceIn(0f, 1f))
            }
            outputStream.flush()
            if (totalBytes > 0 && downloaded != totalBytes) return@withContext false

            if (targetFile.exists() && !targetFile.delete()) return@withContext false
            if (!partialFile.renameTo(targetFile)) {
                partialFile.copyTo(targetFile, overwrite = true)
                partialFile.delete()
            }
            onProgress(downloaded, downloaded, 1f)
            true
        } catch (_: Exception) {
            false
        } finally {
            outputStream?.close()
            inputStream?.close()
            connection?.disconnect()
            if (partialFile.exists()) partialFile.delete()
        }
    }
}
