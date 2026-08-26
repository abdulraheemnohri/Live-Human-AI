package com.livehumanai.data.dao

import androidx.room.*
import com.livehumanai.data.entity.DownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status IN ('downloading', 'paused', 'pending') ORDER BY createdAt DESC")
    fun getActiveDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownloadById(id: String): DownloadEntity?

    @Query("SELECT * FROM downloads WHERE id = :id")
    fun getDownloadByIdFlow(id: String): Flow<DownloadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Update
    suspend fun updateDownload(download: DownloadEntity)

    @Delete
    suspend fun deleteDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownloadById(id: String)

    @Query("UPDATE downloads SET status = :status, downloadedBytes = :downloadedBytes WHERE id = :id")
    suspend fun updateDownloadProgress(id: String, status: String, downloadedBytes: Long)

    @Query("UPDATE downloads SET status = :status, errorMessage = :errorMessage, completedAt = :completedAt WHERE id = :id")
    suspend fun markDownloadAsFailed(id: String, status: String, errorMessage: String?, completedAt: java.util.Date)

    @Query("UPDATE downloads SET status = :status, completedAt = :completedAt, finalFilePath = :finalFilePath WHERE id = :id")
    suspend fun markDownloadAsCompleted(id: String, status: String, completedAt: java.util.Date, finalFilePath: String?)

    @Query("UPDATE downloads SET status = :status, startedAt = :startedAt WHERE id = :id AND status = 'pending'")
    suspend fun startDownload(id: String, status: String, startedAt: java.util.Date)

    @Query("SELECT COUNT(*) FROM downloads WHERE status = 'downloading'")
    fun getActiveDownloadCount(): Flow<Int>

    @Query("SELECT SUM(downloadedBytes) FROM downloads WHERE status IN ('downloading', 'paused')")
    suspend fun getTotalDownloadedBytesForActiveDownloads(): Long?
}
