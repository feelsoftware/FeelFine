package com.feelsoftware.feelfine.fit.model

import java.util.*

data class SleepInfo(
    val date: Date,
    val lightSleep: Duration,
    val deepSleep: Duration,
    val awake: Duration,
    val outOfBed: Duration,
)

val SleepInfo.total: Duration
    get() = lightSleep + deepSleep + awake + outOfBed