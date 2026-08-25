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
        onProgress: (bytesDownloaded: Long, totalBytes: Long, progressPercent: Float) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            var currentUrl = "https://huggingface.co/$repoId/resolve/main/$filename"
            var redirects = 0
            val maxRedirects = 5

            while (redirects < maxRedirects) {
                val url = URL(currentUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = true
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 30000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER ||
                    responseCode == 307 || responseCode == 308) {
                    val newUrl = connection.getHeaderField("Location")
                    connection.disconnect()
                    if (newUrl.isNull_or_blank()) break
                    currentUrl = newUrl
                    redirects++
                } else if (responseCode == HttpURLConnection.HTTP_OK) {
                    break
                } else {
                    connection.disconnect()
                    return@withContext false
                }
            }

            if (connection?.responseCode != HttpURLConnection.HTTP_OK) {
                connection?.disconnect()
                return@withContext false
            }

            val totalBytes = connection.contentLengthLong
            inputStream = connection.inputStream
            outputStream = FileOutputStream(targetFile)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesDownloaded = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesDownloaded += bytesRead
                val progress = if (totalBytes > 0) (totalBytesDownloaded.toFloat() / totalBytes) else 0f
                onProgress(totalBytesDownloaded, totalBytes, progress)
            }

            outputStream.flush()
            true
        } catch (e: Exception) {
            if (targetFile.exists()) {
                targetFile.delete()
            }
            false
        } finally {
            outputStream?.close()
            inputStream?.close()
            connection?.disconnect()
        }
    }

    private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
}
