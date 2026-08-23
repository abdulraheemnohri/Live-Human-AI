package com.livehumanai.livehumanai.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.livehumanai.livehumanai.data.database.entity.ModelEntity

/**
 * ModelDao provides database operations for AI models.
 */
@Dao
interface ModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: ModelEntity)

    @Update
    suspend fun updateModel(model: ModelEntity)

    @Query("DELETE FROM models WHERE name = :name")
    suspend fun deleteModel(name: String)

    @Query("SELECT * FROM models ORDER BY name ASC")
    suspend fun getAllModels(): List<ModelEntity>

    @Query("SELECT * FROM models WHERE name = :name")
    suspend fun getModelByName(name: String): ModelEntity?

    @Query("SELECT * FROM models WHERE type = :type ORDER BY name ASC")
    suspend fun getModelsByType(type: ModelEntity.ModelType): List<ModelEntity>

    @Query("SELECT * FROM models WHERE isInstalled = 1 ORDER BY name ASC")
    suspend fun getInstalledModels(): List<ModelEntity>

    @Query("SELECT * FROM models WHERE isLoaded = 1 ORDER BY name ASC")
    suspend fun getLoadedModels(): List<ModelEntity>

    @Query("SELECT * FROM models WHERE isInstalled = 1 AND type = :type ORDER BY name ASC")
    suspend fun getInstalledModelsByType(type: ModelEntity.ModelType): List<ModelEntity>

    @Query("SELECT * FROM models WHERE name LIKE :query ORDER BY name ASC")
    suspend fun searchModels(query: String): List<ModelEntity>

    @Query("SELECT COUNT(*) FROM models")
    suspend fun getModelCount(): Int

    @Query("SELECT COUNT(*) FROM models WHERE isInstalled = 1")
    suspend fun getInstalledModelCount(): Int

    @Query("SELECT COUNT(*) FROM models WHERE isLoaded = 1")
    suspend fun getLoadedModelCount(): Int

    @Query("UPDATE models SET isLoaded = :isLoaded WHERE name = :name")
    suspend fun setModelLoaded(name: String, isLoaded: Boolean)

    @Query("UPDATE models SET isInstalled = :isInstalled WHERE name = :name")
    suspend fun setModelInstalled(name: String, isInstalled: Boolean)
}
