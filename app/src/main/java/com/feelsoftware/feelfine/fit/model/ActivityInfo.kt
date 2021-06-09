package com.feelsoftware.feelfine.fit.model

import java.util.*

data class ActivityInfo(
    val date: Date,
    val activityUnknown: Duration,
    val activityWalking: Duration,
    val activityRunning: Duration
)

val ActivityInfo.total: Duration
    get() = activityUnknown + activityWalking + activityRunning