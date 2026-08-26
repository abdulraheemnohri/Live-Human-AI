package com.livehumanai.data.database

import androidx.room.TypeConverter
import java.util.Date

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
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString("|")
    }

    @TypeConverter
    fun toStringList(data: String?): List<String>? {
        return data?.split("|")?.filterNotNull()
    }
}
