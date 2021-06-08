package com.feelsoftware.feelfine.fit

import java.util.*

data class Duration(
    private val raw: Int
) {
    val hours: Int = raw / 60

    val minutes: Int = raw % 60

    override fun toString(): String {
        return "Duration(hours=$hours, minutes=$minutes)"
    }
}

data class StepsInfo(
    val date: Date,
    val count: Int,
)

data class SleepInfo(
    val date: Date,
    val lightSleep: Duration,
    val deepSleep: Duration,
    val awake: Duration,
    val outOfBed: Duration,
)

data class ActivityInfo(
    val date: Date,
    val duration: Duration,
    val type: ActivityType
) {

    enum class ActivityType(val raw: Int) {
        UNKNOWN(0),
        WALKING(7),
        RUNNING(8)
    }
}

// region Extensions
operator fun Duration.plus(duration: Duration): Duration =
    Duration(hours * 60 + minutes + duration.hours * 60 + duration.minutes)

val SleepInfo.total: Duration
    get() = lightSleep + deepSleep + awake + outOfBed
// endregion
