package com.feelsoftware.feelfine.fit.model

import java.util.*

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