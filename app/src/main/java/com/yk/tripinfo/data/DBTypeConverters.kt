package com.yk.tripinfo.data

import androidx.room.TypeConverter
import java.util.*

class DBTypeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.getTime()
    }
}