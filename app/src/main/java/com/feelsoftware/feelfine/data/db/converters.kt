package com.feelsoftware.feelfine.data.db

import androidx.room.TypeConverter
import com.feelsoftware.feelfine.fit.model.Duration
import java.text.SimpleDateFormat
import java.util.*

class DateConverter {

    private val format = SimpleDateFormat("yyyy-mm-dd", Locale.ROOT)

    @TypeConverter
    fun toRaw(date: Date?): String? = date?.let(format::format)

    @TypeConverter
    fun toDate(raw: String?): Date? = raw?.let(format::parse)
}

class DurationConverter {

    @TypeConverter
    fun toRaw(duration: Duration?): Int? = duration?.let { it.hours * 60 + it.minutes }

    @TypeConverter
    fun toDuration(raw: Int?): Duration? = raw?.let(::Duration)
}