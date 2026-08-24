package com.livehumanai.livehumanai.utils

import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * AppLogger provides centralized logging with in-memory buffering and optional log retention.
 */
object AppLogger {

    private const val TAG = "LiveHumanAI"
    private const val MAX_MEMORY_LOGS = 200

    private val logQueue = ConcurrentLinkedQueue<String>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    fun d(tag: String = TAG, message: String) {
        Log.d(tag, message)
        appendLog("DEBUG", tag, message)
    }

    fun i(tag: String = TAG, message: String) {
        Log.i(tag, message)
        appendLog("INFO", tag, message)
    }

    fun w(tag: String = TAG, message: String, throwable: Throwable? = null) {
        Log.w(tag, message, throwable)
        appendLog("WARN", tag, "$message ${throwable?.message ?: ""}")
    }

    fun e(tag: String = TAG, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
        appendLog("ERROR", tag, "$message ${throwable?.message ?: ""}")
    }

    private fun appendLog(level: String, tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val formattedEntry = "[$timestamp] [$level] [$tag]: $message"

        logQueue.add(formattedEntry)
        while (logQueue.size > MAX_MEMORY_LOGS) {
            logQueue.poll()
        }
    }

    fun getRecentLogs(): List<String> {
        return logQueue.toList()
    }

    fun clearLogs() {
        logQueue.clear()
    }
}
