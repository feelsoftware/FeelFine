@file:Suppress("UNCHECKED_CAST")

package com.feelsoftware.feelfine.fit.usecase

import com.feelsoftware.feelfine.data.repository.ActivityDataRepository
import com.feelsoftware.feelfine.data.repository.SleepDataRepository
import com.feelsoftware.feelfine.data.repository.StepsDataRepository
import com.feelsoftware.feelfine.fit.FitRepository
import com.feelsoftware.feelfine.fit.model.*
import io.reactivex.rxjava3.core.Observable
import org.koin.core.component.KoinComponent
import java.util.*
import kotlin.math.roundToInt

class GetFitDataUseCase(
    private val stepsRepository: StepsDataRepository,
    private val sleepRepository: SleepDataRepository,
    private val activityRepository: ActivityDataRepository,
) : KoinComponent {

    fun getSteps(startTime: Date, endTime: Date): Observable<List<StepsInfo>> =
        stepsRepository.get(startTime, endTime)

    fun getSleep(startTime: Date, endTime: Date): Observable<List<SleepInfo>> =
        sleepRepository.get(startTime, endTime)

    fun getActivity(startTime: Date, endTime: Date): Observable<List<ActivityInfo>> =
        activityRepository.get(startTime, endTime)
}

// region Current date
fun GetFitDataUseCase.getCurrentSteps(): Observable<StepsInfo> {
    val (startTime, endTime) = currentDates()
    return getSteps(startTime, endTime).oneItem(startTime)
}

fun GetFitDataUseCase.getCurrentSleep(): Observable<SleepInfo> {
    val (startTime, endTime) = currentDates(forSleep = true)
    return getSleep(startTime, endTime).oneItem(startTime)
}

fun GetFitDataUseCase.getCurrentActivity(): Observable<ActivityInfo> {
    val (startTime, endTime) = currentDates()
    return getActivity(startTime, endTime).oneItem(startTime)
}
// endregion

// region Percent data
fun GetFitDataUseCase.getPercentSteps(): Observable<Int> {
    val currentData = getCurrentSteps()

    val (startTime, endTime) = yesterdayDates()
    val yesterdayData = getSteps(startTime, endTime).oneItem(startTime)

    return Observable.zip(currentData, yesterdayData, { current, yesterday ->
        (current.count * 100f / yesterday.count.coerceAtLeast(1)).roundToInt() - 100
    })
}

fun GetFitDataUseCase.getPercentSleep(): Observable<Int> {
    val currentData = getCurrentSleep()

    val (startTime, endTime) = yesterdayDates(forSleep = true)
    val yesterdayData = getSleep(startTime, endTime).oneItem(startTime)

    return Observable.zip(currentData, yesterdayData, { current, yesterday ->
        (current.total.minutes * 100f / yesterday.total.minutes.coerceAtLeast(1)).roundToInt() - 100
    })
}

fun GetFitDataUseCase.getPercentActivity(): Observable<Int> {
    val currentData = getCurrentActivity()

    val (startTime, endTime) = yesterdayDates()
    val yesterdayData = getActivity(startTime, endTime).oneItem(startTime)

    return Observable.zip(currentData, yesterdayData, { current, yesterday ->
        (current.total.minutes * 100f / yesterday.total.minutes.coerceAtLeast(1)).roundToInt() - 100
    })
}
// endregion

@JvmName("OneItemSteps")
private fun Observable<List<StepsInfo>>.oneItem(
    date: Date
): Observable<StepsInfo> = map { list ->
    list.firstOrNull() ?: StepsInfo(date, 0)
}

@JvmName("OneItemSleep")
private fun Observable<List<SleepInfo>>.oneItem(
    date: Date
): Observable<SleepInfo> = map { list ->
    list.firstOrNull() ?: SleepInfo(
        date, Duration(0), Duration(0), Duration(0), Duration(0)
    )
}

@JvmName("OneItemActivity")
private fun Observable<List<ActivityInfo>>.oneItem(
    date: Date
): Observable<ActivityInfo> = map { list ->
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