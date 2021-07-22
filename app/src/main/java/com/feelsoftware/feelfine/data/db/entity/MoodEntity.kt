package com.feelsoftware.feelfine.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.feelsoftware.feelfine.data.model.Mood
import java.util.*

@Entity(tableName = "mood")
@TypeConverters(MoodConverter::class)
data class MoodEntity(
    val mood: Mood,
    @PrimaryKey
    val date: Date
)

class MoodConverter {

    @TypeConverter
    fun toRaw(mood: Mood): String = mood.name

    @TypeConverter
    fun toMood(raw: String): Mood = Mood.values().firstOrNull { it.name == raw } ?: Mood.NEUTRALITY
}