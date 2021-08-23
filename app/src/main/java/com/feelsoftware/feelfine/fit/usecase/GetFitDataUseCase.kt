@file:Suppress("UNCHECKED_CAST")

package com.feelsoftware.feelfine.fit.usecase

import com.feelsoftware.feelfine.data.model.Optional
import com.feelsoftware.feelfine.data.repository.ActivityDataRepository
import com.feelsoftware.feelfine.data.repository.SleepDataRepository
import com.feelsoftware.feelfine.data.repository.StepsDataRepository
import com.feelsoftware.feelfine.fit.model.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.core.component.KoinComponent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class GetFitDataUseCase(
    private val stepsRepository: StepsDataRepository,
    private val sleepRepository: SleepDataRepository,
    private val activityRepository: ActivityDataRepository
) : KoinComponent {

    fun getSteps(startTime: Date, endTime: Date): Observable<List<StepsInfo>> =
        stepsRepository.get(startTime, endTime)
            .flatMap { values ->
                withEmptyValues(
                    values,
                    startTime,
                    endTime,
                    builder = {
                        StepsInfo(it, 0)
                    },
                    mapper = {
                        it.date
                    },
                )
            }

    fun getSleep(startTime: Date, endTime: Date): Observable<List<SleepInfo>> =
        sleepRepository.get(startTime, endTime)
            .flatMap { values ->
                withEmptyValues(
                    values,
                    startTime,
                    endTime,
                    builder = {
                        SleepInfo(it, Duration(0), Duration(0), Duration(0), Duration(0))
                    },
                    mapper = {
                        it.date
                    },
                )
            }

    fun getActivity(startTime: Date, endTime: Date): Observable<List<ActivityInfo>> =
        activityRepository.get(startTime, endTime)
            .flatMap { values ->
                withEmptyValues(
                    values,
                    startTime,
                    endTime,
                    builder = {
                        ActivityInfo(it, Duration(0), Duration(0), Duration(0))
                    },
                    mapper = {
                        it.date
                    },
                )
            }

    /**
     * Create empty values for date range [startTime], [endTime] and merge them with [values].
     */
    private fun <T> withEmptyValues(
        values: List<T>,
        startTime: Date,
        endTime: Date,
        builder: (time: Date) -> T,
        mapper: (item: T) -> Date,
    ): Observable<List<T>> = Observable.create<List<T>> { emitter ->
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        val calendar = Calendar.getInstance().apply { time = startTime }
        val emptyValues = mutableListOf<T>()
        while (true) {
            emptyValues += builder(calendar.time)
            if (format.format(calendar.time) == format.format(endTime)) break
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val valuesByDate = values.map {
            format.format(mapper(it)) to it
        }.toMap()
        val result = emptyValues.map { emptyValue ->
            valuesByDate[format.format(mapper(emptyValue))] ?: emptyValue
        }
        emitter.onNext(result)
        emitter.onComplete()
    }.subscribeOn(Schedulers.computation())
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
fun GetFitDataUseCase.getPercentSteps(): Observable<Optional<Int>> {
    val currentData = getCurrentSteps()

    val (startTime, endTime) = yesterdayDates()
    val yesterdayData = getSteps(startTime, endTime).oneItem(startTime)

    return Observable.zip(currentData, yesterdayData, { current, yesterday ->
        calculatePercent(current.count, yesterday.count)
    })
}

fun GetFitDataUseCase.getPercentSleep(): Observable<Optional<Int>> {
    val currentData = getCurrentSleep()

    val (startTime, endTime) = yesterdayDates(forSleep = true)
    val yesterdayData = getSleep(startTime, endTime).oneItem(startTime)

    return Observable.zip(currentData, yesterdayData, { current, yesterday ->
        calculatePercent(current.total.minutesTotal, yesterday.total.minutesTotal)
    })
}

fun GetFitDataUseCase.getPercentActivity(): Observable<Optional<Int>> {
    val currentData = getCurrentActivity()

    val (startTime, endTime) = yesterdayDates()
    val yesterdayData = getActivity(startTime, endTime).oneItem(startTime)

    return Observable.zip(currentData, yesterdayData, { current, yesterday ->
        calculatePercent(current.total.minutesTotal, yesterday.total.minutesTotal)
    })
}

private fun calculatePercent(current: Int, yesterday: Int): Optional<Int> =
    if (current == 0 && yesterday == 0) {
        Optional.empty()
    } else {
        Optional.of(
            (current * 100f / yesterday.coerceAtLeast(1)).roundToInt() - 100
        )
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