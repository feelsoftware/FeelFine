package com.feelsoftware.feelfine.data.usecase

import com.feelsoftware.feelfine.data.model.Mood
import com.feelsoftware.feelfine.data.repository.MoodRepository
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import java.util.*
import kotlin.math.roundToInt

class GetMoodByDateUseCase(
    private val repository: MoodRepository
) {

    operator fun invoke(date: Date): Maybe<Mood> =
        repository.getMoodByDay(date)
            .firstElement()
            .flatMap { list ->
                val mood = list.firstOrNull()
                if (mood == null) {
                    Maybe.empty()
                } else {
                    Maybe.just(mood)
                }
            }
}

class GetCurrentMoodUseCase(
    private val getMoodByDateUseCase: GetMoodByDateUseCase
) {

    operator fun invoke(): Maybe<Mood> {
        val date = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, -1)
        }.time

        return getMoodByDateUseCase(date)
    }
}

class GetPercentMoodUseCase(
    private val getMoodByDateUseCase: GetMoodByDateUseCase
) {

    operator fun invoke(): Observable<Int> {
        val currentDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, -1)
        }.time
        val yesterdayDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, -2)
        }.time

        val currentData = getMoodByDateUseCase(currentDate)
        val yesterdayData = getMoodByDateUseCase(yesterdayDate)

        return Maybe.zip(currentData, yesterdayData, { current, yesterday ->
            (current.intensity * 100f / yesterday.intensity.coerceAtLeast(1)).roundToInt() - 100
        }).toObservable()
    }
}