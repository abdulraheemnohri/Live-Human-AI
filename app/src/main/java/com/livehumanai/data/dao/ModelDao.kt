package com.livehumanai.data.dao

import androidx.room.*
import com.livehumanai.data.entity.ModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Query("SELECT * FROM models ORDER BY type, name")
    fun getAllModels(): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models WHERE type = :type ORDER BY name")
    fun getModelsByType(type: String): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models WHERE status = 'ready' ORDER BY lastUsedAt DESC")
    fun getReadyModels(): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models WHERE id = :id")
    suspend fun getModelById(id: String): ModelEntity?

    @Query("SELECT * FROM models WHERE id = :id")
    fun getModelByIdFlow(id: String): Flow<ModelEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: ModelEntity)

    @Update
    suspend fun updateModel(model: ModelEntity)

    @Delete
    suspend fun deleteModel(model: ModelEntity)

    @Query("DELETE FROM models WHERE id = :id")
    suspend fun deleteModelById(id: String)

    @Query("UPDATE models SET status = :status, installPath = :installPath, installedAt = :installedAt WHERE id = :id")
    suspend fun markModelAsInstalled(id: String, status: String, installPath: String?, installedAt: java.util.Date)

    @Query("UPDATE models SET lastUsedAt = :lastUsedAt WHERE id = :id")
    suspend fun updateModelLastUsed(id: String, lastUsedAt: java.util.Date)

    @Query("SELECT * FROM models WHERE isDefault = 1 AND type = :type LIMIT 1")
    suspend fun getDefaultModelForType(type: String): ModelEntity?

    @Query("UPDATE models SET isDefault = CASE WHEN id = :id THEN 1 ELSE 0 END WHERE type = :modelType")
    suspend fun setDefaultModel(id: String, modelType: String)

    @Query("SELECT COUNT(*) FROM models WHERE status = 'ready'")
    fun getReadyModelCount(): Flow<Int>
}
