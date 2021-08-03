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

fun Duration.toHoursMinutes() =
    this.hours.toString() + " h " + this.minutes + " m"

fun Duration.toHours() =
    this.hours.toString() + " h"

operator fun Duration.plus(duration: Duration): Duration =
    Duration(hours * 60 + minutes + duration.hours * 60 + duration.minutes)