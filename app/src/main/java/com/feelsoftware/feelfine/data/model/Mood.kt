package com.feelsoftware.feelfine.data.model

import java.util.*

enum class Mood(val intensity: Int) {
    APATHY(1),
    BLAME(2),
    ANGRY(3),
    ANXIETY(4),
    SED(5),
    NEUTRALITY(6),
    CALM(7),
    HAPPY(8),
    HARMONY(9)
}

data class MoodWithDate(
    val mood: Mood,
    val date: Date
)
