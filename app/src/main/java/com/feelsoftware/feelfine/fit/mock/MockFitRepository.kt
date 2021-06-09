package com.feelsoftware.feelfine.fit.mock

import com.feelsoftware.feelfine.fit.model.ActivityInfo
import com.feelsoftware.feelfine.fit.FitRepository
import com.feelsoftware.feelfine.fit.model.Duration
import com.feelsoftware.feelfine.fit.model.SleepInfo
import com.feelsoftware.feelfine.fit.model.StepsInfo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MockFitRepository : FitRepository {

    override fun getSteps(startTime: Date, endTime: Date): Single<List<StepsInfo>> =
        mockData(startTime, endTime, ::mockSteps)

    override fun getSleep(startTime: Date, endTime: Date): Single<List<SleepInfo>> =
        mockData(startTime, endTime, ::mockSleep)

    override fun getActivity(startTime: Date, endTime: Date): Single<List<ActivityInfo>> =
        mockData(startTime, endTime, ::mockActivity)

    private fun <T> mockData(
        startTime: Date,
        endTime: Date,
        generator: (date: Date) -> T
    ): Single<List<T>> {
        val hours = TimeUnit.HOURS.convert(endTime.time - startTime.time, TimeUnit.MILLISECONDS)
        val days = (hours / 24 + 1).toInt()

        val calendar = Calendar.getInstance().apply {
            time = startTime
        }
        val list = mutableListOf<T>()
        repeat(days) {
            list += generator.invoke(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return Observable.just(true)
            .delay(1, TimeUnit.SECONDS)
            .map {
                list.toList()
            }.firstOrError()
    }

    private fun mockSteps(date: Date) = StepsInfo(
        date = date,
        count = Random.nextInt(1, 10_000)
    )

    private fun mockSleep(date: Date) = SleepInfo(
        date = date,
        lightSleep = Duration(Random.nextInt(1, 720)),
        deepSleep = Duration(Random.nextInt(1, 720)),
        awake = Duration(Random.nextInt(1, 720)),
        outOfBed = Duration(Random.nextInt(1, 720)),
    )

    private fun mockActivity(date: Date) = ActivityInfo(
        date = date,
        activityUnknown = Duration(Random.nextInt(1, 720)),
        activityWalking = Duration(Random.nextInt(1, 720)),
        activityRunning = Duration(Random.nextInt(1, 720)),
    )
}