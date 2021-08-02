package com.feelsoftware.feelfine.score

import com.feelsoftware.feelfine.fit.model.Duration
import io.reactivex.rxjava3.core.Single

interface ScoreTargetProvider {

    fun getSteps(): Single<Int>

    fun getActivityDuration(): Single<Duration>

    fun getSleepDuration(): Single<Duration>
}

class ScoreTargetProviderImpl : ScoreTargetProvider {

    override fun getSteps(): Single<Int> = Single.just(6_000)

    override fun getActivityDuration(): Single<Duration> =
        Single.just(Duration.of(hours = 3, minutes = 30))

    override fun getSleepDuration(): Single<Duration> =
        Single.just(Duration.of(hours = 7, minutes = 30))
}