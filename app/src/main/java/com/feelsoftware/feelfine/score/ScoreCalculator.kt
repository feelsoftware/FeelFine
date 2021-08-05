@file:Suppress("NAME_SHADOWING")

package com.feelsoftware.feelfine.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.feelsoftware.feelfine.data.model.ScoreInfo
import kotlin.math.roundToInt

interface ScoreCalculator {

    fun calculate(
        steps: LiveData<ScoreInfo>,
        sleep: LiveData<ScoreInfo>,
        activity: LiveData<ScoreInfo>,
        mood: LiveData<ScoreInfo>
    ): LiveData<Float>
}

class ScoreCalculatorImpl : ScoreCalculator {

    override fun calculate(
        steps: LiveData<ScoreInfo>,
        sleep: LiveData<ScoreInfo>,
        activity: LiveData<ScoreInfo>,
        mood: LiveData<ScoreInfo>
    ): LiveData<Float> =
        steps.switchMap { steps ->
            sleep.map { sleep ->
                steps to sleep
            }.switchMap { (steps, sleep) ->
                activity.map { activity ->
                    Triple(steps, sleep, activity)
                }.switchMap { (steps, sleep, activity) ->
                    mood.map { mood ->
                        ValuesHolder(steps, sleep, activity, mood)
                    }
                }
            }
        }.map { (steps, sleep, activity, mood) ->
            var sum = 0
            var denominator = 0

            fun ScoreInfo.calculate(multiplier: Int) {
                current?.let {
                    sum += score.roundToInt() * multiplier
                    denominator += multiplier
                }
            }

            steps.calculate(2)
            sleep.calculate(2)
            activity.calculate(2)
            mood.calculate(1)

            (sum / denominator.coerceAtLeast(1).toFloat()).coerceAtMost(100f)
        }
}

private data class ValuesHolder(
    val steps: ScoreInfo,
    val sleep: ScoreInfo,
    val activity: ScoreInfo,
    val mood: ScoreInfo
)