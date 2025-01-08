package com.feelsoftware.feelfine.fit.model

import java.util.Date

data class StepsInfo(
    val date: Date,
    val count: Int,
)

val StepsInfo.calories: String
    get() = "%.2f".format(count * 0.045)

val StepsInfo.distance: String
    get() = "%.2f".format(count * 0.762 / 1000)
