package com.livehumanai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "downloads",
    indices = [Index(value = ["status"]), Index(value = ["createdAt"])]
)
data class DownloadEntity(
    @PrimaryKey
    val id: String, // Unique download ID
    val modelId: String,
    val fileName: String,
    val url: String,
    val totalBytes: Long,
    val downloadedBytes: Long = 0,
    val status: String = "pending", // "pending", "downloading", "paused", "completed", "failed", "cancelled"
    val createdAt: Date = Date(),
    val startedAt: Date? = null,
    val completedAt: Date? = null,
    val errorMessage: String? = null,
    val retryCount: Int = 0,
    val checksumSha256: String? = null,
    val tempFilePath: String? = null,
    val finalFilePath: String? = null
)
