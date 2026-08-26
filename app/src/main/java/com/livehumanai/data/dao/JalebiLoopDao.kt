package com.livehumanai.data.dao

import androidx.room.*
import com.livehumanai.data.entity.JalebiLoopEntity
import com.livehumanai.data.entity.JalebiIterationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JalebiLoopDao {
    @Query("SELECT * FROM jalebi_loops ORDER BY createdAt DESC")
    fun getAllLoops(): Flow<List<JalebiLoopEntity>>

    @Query("SELECT * FROM jalebi_loops WHERE status IN ('running', 'paused', 'queued') ORDER BY createdAt DESC")
    fun getActiveLoops(): Flow<List<JalebiLoopEntity>>

    @Query("SELECT * FROM jalebi_loops WHERE id = :id")
    suspend fun getLoopById(id: Long): JalebiLoopEntity?

    @Query("SELECT * FROM jalebi_loops WHERE id = :id")
    fun getLoopByIdFlow(id: Long): Flow<JalebiLoopEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoop(loop: JalebiLoopEntity): Long

    @Update
    suspend fun updateLoop(loop: JalebiLoopEntity)

    @Delete
    suspend fun deleteLoop(loop: JalebiLoopEntity)

    @Query("DELETE FROM jalebi_loops WHERE id = :id")
    suspend fun deleteLoopById(id: Long)

    @Query("UPDATE jalebi_loops SET status = :status, startedAt = :startedAt WHERE id = :id AND status = 'queued'")
    suspend fun startLoop(id: Long, status: String, startedAt: java.util.Date)

    @Query("UPDATE jalebi_loops SET status = :status, endedAt = :endedAt, success = :success, failureReason = :failureReason WHERE id = :id")
    suspend fun completeLoop(id: Long, status: String, endedAt: java.util.Date, success: Boolean, failureReason: String?)

    @Query("UPDATE jalebi_loops SET iterationCount = :iterationCount WHERE id = :id")
    suspend fun updateLoopIterationCount(id: Long, iterationCount: Int)

    @Query("SELECT * FROM jalebi_iterations WHERE loopId = :loopId ORDER BY iterationNumber ASC")
    fun getIterationsForLoop(loopId: Long): Flow<List<JalebiIterationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIteration(iteration: JalebiIterationEntity): Long

    @Query("DELETE FROM jalebi_iterations WHERE loopId = :loopId")
    suspend fun deleteAllIterationsForLoop(loopId: Long)
}
