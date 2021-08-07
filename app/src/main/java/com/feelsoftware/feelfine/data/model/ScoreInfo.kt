package com.feelsoftware.feelfine.data.model

import androidx.annotation.FloatRange

data class ScoreInfo(
    val current: Int?,
    val target: Int,
    @FloatRange(from = 0.0, to = 100.0)
    val score: Float
)