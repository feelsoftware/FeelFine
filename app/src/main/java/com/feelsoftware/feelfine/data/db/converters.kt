package com.feelsoftware.feelfine.data.db

import androidx.room.TypeConverter
import com.feelsoftware.feelfine.fit.model.Duration
import java.text.SimpleDateFormat
import java.util.*

class DateConverter {

    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

    @TypeConverter
    fun toRaw(date: Date?): String? = date?.let(format::format)

    @TypeConverter
    fun toDate(raw: String?): Date? = raw?.let(format::parse)
}

class DurationConverter {

    @TypeConverter
    fun toRaw(duration: Duration?): Int? = duration?.minutesTotal

    @TypeConverter
    fun toDuration(raw: Int?): Duration? = raw?.let(::Duration)
}