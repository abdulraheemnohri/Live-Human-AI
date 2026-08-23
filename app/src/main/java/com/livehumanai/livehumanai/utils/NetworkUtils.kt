package com.livehumanai.livehumanai.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * NetworkUtils provides utility functions for network operations.
 */
object NetworkUtils {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(Constants.NETWORK_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(Constants.NETWORK_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(Constants.NETWORK_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .build()
    }

    // Check if network is available
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Check if connected to WiFi
    fun isConnectedToWifi(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    // Check if connected to mobile data
    fun isConnectedToMobileData(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    // Check if connected to Ethernet
    fun isConnectedToEthernet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    // Get network type as string
    fun getNetworkType(context: Context): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return "None"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "None"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Unknown"
        }
    }

    // Download a file from a URL
    suspend fun downloadFile(url: String, destination: java.io.File): Boolean {
        return try {
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return false

                response.body?.use { body ->
                    destination.parentFile?.mkdirs()
                    java.io.FileOutputStream(destination).use { output ->
                        body.byteStream().copyTo(output)
                    }
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    // Download a file with progress callback
    suspend fun downloadFileWithProgress(
        url: String,
        destination: java.io.File,
        progressCallback: (Long, Long) -> Unit
    ): Boolean {
        return try {
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return false

                val contentLength = response.body?.contentLength() ?: 0L
                var bytesRead = 0L

                response.body?.use { body ->
                    destination.parentFile?.mkdirs()
                    java.io.FileOutputStream(destination).use { output ->
                        val buffer = ByteArray(8192)
                        var bytes: Int
                        while (body.byteStream().read(buffer).also { bytes = it } != -1) {
                            output.write(buffer, 0, bytes)
                            bytesRead += bytes
                            progressCallback(bytesRead, contentLength)
                        }
                    }
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    // Get file size from URL
    fun getFileSize(url: String): Long? {
        return try {
            val request = Request.Builder()
                .url(url)
                .head()
                .build()

            client.newCall(request).execute().use { response ->
                response.body?.contentLength()
            }
        } catch (e: IOException) {
            null
        }
    }

    // Check if URL is reachable
    fun isUrlReachable(url: String): Boolean {
        return try {
            val request = Request.Builder()
                .url(url)
                .head()
                .build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: IOException) {
            false
        }
    }

    // Extract file name from URL
    fun getFileNameFromUrl(url: String): String {
        return Uri.parse(url).lastPathSegment ?: "download"
    }

    // Get MIME type from file extension
    fun getMimeType(fileName: String): String {
        val extension = fileName.substringAfterLast(".", "").lowercase()
        return when (extension) {
            "gguf" -> "application/octet-stream"
            "onnx" -> "application/octet-stream"
            "bin" -> "application/octet-stream"
            "json" -> "application/json"
            "txt" -> "text/plain"
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            else -> "application/octet-stream"
        }
    }
}
