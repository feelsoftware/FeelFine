@file:Suppress("NAME_SHADOWING")

package com.feelsoftware.feelfine.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.feelsoftware.feelfine.data.model.ScoreInfo

interface ScoreCalculator {

    fun calculate(
        steps: LiveData<ScoreInfo>,
        sleep: LiveData<ScoreInfo>,
        activity: LiveData<ScoreInfo>
    ): LiveData<Float>
}

class ScoreCalculatorImpl : ScoreCalculator {

    override fun calculate(
        steps: LiveData<ScoreInfo>,
        sleep: LiveData<ScoreInfo>,
        activity: LiveData<ScoreInfo>
    ): LiveData<Float> =
        steps.switchMap { steps ->
            sleep.map { sleep ->
                steps to sleep
            }.switchMap { (steps, sleep) ->
                activity.map { activity ->
                    Triple(steps, sleep, activity)
                }
            }
        }.map { (steps, sleep, activity) ->
            (steps.score + sleep.score + activity.score) / 3
        }
}