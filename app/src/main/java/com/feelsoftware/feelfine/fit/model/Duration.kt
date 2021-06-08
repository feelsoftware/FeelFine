package com.feelsoftware.feelfine.fit.model

data class Duration(
    private val raw: Int
) {
    val hours: Int = raw / 60

    val minutes: Int = raw % 60

    override fun toString(): String {
        return "Duration(hours=$hours, minutes=$minutes)"
    }
}

operator fun Duration.plus(duration: Duration): Duration =
    Duration(hours * 60 + minutes + duration.hours * 60 + duration.minutes)