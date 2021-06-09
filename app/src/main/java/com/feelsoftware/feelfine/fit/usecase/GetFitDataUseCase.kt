@file:Suppress("UNCHECKED_CAST")

package com.feelsoftware.feelfine.fit.usecase

import com.feelsoftware.feelfine.fit.FitRepository
import com.feelsoftware.feelfine.fit.model.*
import io.reactivex.rxjava3.core.Single
import org.koin.core.component.KoinComponent
import java.util.*
import kotlin.math.roundToInt

class GetFitDataUseCase(
    private val fitRepository: FitRepository
) : KoinComponent {

    fun getSteps(startTime: Date, endTime: Date): Single<List<StepsInfo>> =
        fitRepository.getSteps(startTime, endTime)

    fun getSleep(startTime: Date, endTime: Date): Single<List<SleepInfo>> =
        fitRepository.getSleep(startTime, endTime)

    fun getActivity(startTime: Date, endTime: Date): Single<List<ActivityInfo>> =
        fitRepository.getActivity(startTime, endTime)
}

// region Current date
fun GetFitDataUseCase.getCurrentSteps(): Single<StepsInfo> {
    val (startTime, endTime) = currentDates()
    return getSteps(startTime, endTime).oneItem(startTime)
}

fun GetFitDataUseCase.getCurrentSleep(): Single<SleepInfo> {
    val (startTime, endTime) = currentDates(forSleep = true)
    return getSleep(startTime, endTime).oneItem(startTime)
}

fun GetFitDataUseCase.getCurrentActivity(): Single<ActivityInfo> {
    val (startTime, endTime) = currentDates()
    return getActivity(startTime, endTime).oneItem(startTime)
}
// endregion

// region Percent data
fun GetFitDataUseCase.getPercentSteps(): Single<Int> {
    val currentData = getCurrentSteps()

    val (startTime, endTime) = yesterdayDates()
    val yesterdayData = getSteps(startTime, endTime).oneItem(startTime)

    return Single.zip(currentData, yesterdayData, { current, yesterday ->
        (current.count * 100f / yesterday.count.coerceAtLeast(1)).roundToInt() - 100
    })
}

fun GetFitDataUseCase.getPercentSleep(): Single<Int> {
    val currentData = getCurrentSleep()

    val (startTime, endTime) = yesterdayDates(forSleep = true)
    val yesterdayData = getSleep(startTime, endTime).oneItem(startTime)

    return Single.zip(currentData, yesterdayData, { current, yesterday ->
        (current.total.minutes * 100f / yesterday.total.minutes.coerceAtLeast(1)).roundToInt() - 100
    })
}

fun GetFitDataUseCase.getPercentActivity(): Single<Int> {
    val currentData = getCurrentActivity()

    val (startTime, endTime) = yesterdayDates()
    val yesterdayData = getActivity(startTime, endTime).oneItem(startTime)

    return Single.zip(currentData, yesterdayData, { current, yesterday ->
        (current.total.minutes * 100f / yesterday.total.minutes.coerceAtLeast(1)).roundToInt() - 100
    })
}
// endregion

@JvmName("OneItemSteps")
private fun Single<List<StepsInfo>>.oneItem(date: Date): Single<StepsInfo> = map { list ->
    list.firstOrNull() ?: StepsInfo(date, 0)
}

@JvmName("OneItemSleep")
private fun Single<List<SleepInfo>>.oneItem(date: Date): Single<SleepInfo> = map { list ->
    list.firstOrNull() ?: SleepInfo(
        date, Duration(0), Duration(0), Duration(0), Duration(0)
    )
}

@JvmName("OneItemActivity")
private fun Single<List<ActivityInfo>>.oneItem(date: Date): Single<ActivityInfo> = map { list ->
    list.firstOrNull() ?: ActivityInfo(
        date, Duration(0), Duration(0), Duration(0)
    )
}

private fun currentDates(forSleep: Boolean = false): Pair<Date, Date> {
    val startTime: Date
    val endTime: Date

    val calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }

    calendar.apply {
        set(Calendar.HOUR_OF_DAY, if (forSleep) 12 else 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        startTime = time
    }
    calendar.apply {
        set(Calendar.HOUR_OF_DAY, if (forSleep) 12 else 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        endTime = time
    }

    return startTime to endTime
}

private fun yesterdayDates(forSleep: Boolean = false): Pair<Date, Date> {
    val startTime: Date
    val endTime: Date

    val calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -2)
    }

    calendar.apply {
        set(Calendar.HOUR_OF_DAY, if (forSleep) 12 else 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        startTime = time
    }
    calendar.apply {
        set(Calendar.HOUR_OF_DAY, if (forSleep) 12 else 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        endTime = time
    }

    return startTime to endTime
}