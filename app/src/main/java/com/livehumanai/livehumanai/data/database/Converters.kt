package com.livehumanai.livehumanai.data.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Converters for Room database to handle custom data types.
 */
class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun stringListToString(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun fromBooleanList(value: String?): List<Boolean>? {
        return value?.split(",")?.map { it.toBoolean() }
    }

    @TypeConverter
    fun booleanListToString(list: List<Boolean>?): String? {
        return list?.joinToString(",")
    }
}
