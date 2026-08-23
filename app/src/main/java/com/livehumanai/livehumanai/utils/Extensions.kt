package com.livehumanai.livehumanai.utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extensions provides utility extension functions for common tasks.
 */

// Context extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

// Date extensions
fun Date.format(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

fun Date.formatRelative(): String {
    val now = Date()
    val diff = now.time - this.time

    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} minutes ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
        diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} days ago"
        else -> this.format("MMM dd, yyyy")
    }
}

// String extensions
fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

fun String.isValidUrl(): Boolean {
    return this.startsWith("http://") || this.startsWith("https://")
}

fun String.limit(length: Int): String {
    return if (this.length <= length) this else "${this.substring(0, length)}..."
}

// Number extensions
fun Int.formatWithCommas(): String {
    return "%,d".format(this)
}

fun Float.formatWithCommas(): String {
    return "%,.2f".format(this)
}

fun Long.formatBytes(): String {
    return when {
        this >= 1024 * 1024 * 1024 -> "%.2f GB".format(this / (1024f * 1024 * 1024))
        this >= 1024 * 1024 -> "%.2f MB".format(this / (1024f * 1024))
        this >= 1024 -> "%.2f KB".format(this / 1024f)
        else -> "$this B"
    }
}

fun Float.formatPercentage(): String {
    return "%.1f%%".format(this)
}

// Collection extensions
fun <T> List<T>.second(): T? {
    return if (this.size >= 2) this[1] else null
}

fun <T> List<T>.third(): T? {
    return if (this.size >= 3) this[2] else null
}

fun <T> List<T>.lastOrNull(): T? {
    return if (this.isNotEmpty()) this.last() else null
}

// Boolean extensions
fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

// Array extensions
fun ByteArray.toHexString(): String {
    return this.joinToString("") { "%02x".format(it) }
}

fun ShortArray.toFloatArray(): FloatArray {
    return this.map { it.toFloat() / Short.MAX_VALUE.toFloat() }.toFloatArray()
}

fun FloatArray.toShortArray(): ShortArray {
    return this.map { (it * Short.MAX_VALUE.toFloat()).toInt().toShort() }.toShortArray()
}
