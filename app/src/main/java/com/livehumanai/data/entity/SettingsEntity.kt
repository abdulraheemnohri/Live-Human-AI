package com.livehumanai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val key: String,
    val value: String,
    val type: String = "string", // "string", "int", "boolean", "float", "long"
    val updatedAt: Date = Date()
)
