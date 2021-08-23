package com.feelsoftware.feelfine.fit.model

data class Duration(
    val minutesTotal: Int
) {
    companion object {
        fun of(hours: Int, minutes: Int = 0) = Duration(60 * hours + minutes)
    }

    val hours: Int = minutesTotal / 60

    val minutes: Int = minutesTotal % 60

    override fun toString(): String {
        return "Duration(hours=$hours, minutes=$minutes)"
    }
}

fun Duration.toHoursMinutes(): String =
    when {
        minutesTotal == 0 || hours > 0 -> "$hours h $minutes m"
        else -> "$minutes m"
    }

fun Duration.toHours(): String =
    "$hours h"

operator fun Duration.plus(duration: Duration): Duration =
    Duration(hours * 60 + minutes + duration.hours * 60 + duration.minutes)