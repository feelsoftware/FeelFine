package com.feelsoftware.feelfine.data.usecase

import com.feelsoftware.feelfine.data.model.Mood
import com.feelsoftware.feelfine.data.model.Optional
import com.feelsoftware.feelfine.data.repository.MoodRepository
import com.feelsoftware.feelfine.data.repository.UserRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*
import kotlin.math.roundToInt

class SetMoodUseCase(
    private val repository: MoodRepository
) {

    operator fun invoke(mood: Mood): Completable =
        repository.setMood(mood)
}

class GetMoodByDateUseCase(
    private val repository: MoodRepository,
    private val userRepository: UserRepository,
) {

    operator fun invoke(date: Date): Single<Optional<Mood>> =
        userRepository.getProfileLegacy()
            .firstOrError()
            .flatMap { profile ->
                if (profile.isDemo) {
                    getMockMood()
                } else {
                    getMood(date)
                }
            }

    private fun getMockMood(): Single<Optional<Mood>> =
        Single.just(Optional.of(Mood.values().random()))

    private fun getMood(date: Date): Single<Optional<Mood>> =
        repository.getMoodByDay(date)
            .firstOrError()
            .map { list ->
                val mood = list.firstOrNull()
                Optional.ofNullable(mood)
            }
}

class GetCurrentMoodUseCase(
    private val getMoodByDateUseCase: GetMoodByDateUseCase
) {

    operator fun invoke(): Single<Optional<Mood>> {
        val date = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.time

        return getMoodByDateUseCase(date)
    }
}

class GetPercentMoodUseCase(
    private val getMoodByDateUseCase: GetMoodByDateUseCase
) {

    operator fun invoke(): Observable<Optional<Int>> {
        val currentDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.time
        val yesterdayDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -2)
        }.time

        val currentData = getMoodByDateUseCase(currentDate)
        val yesterdayData = getMoodByDateUseCase(yesterdayDate)

        return Single.zip(currentData, yesterdayData) { current, yesterday ->
            if (current.isPresent.not() || yesterday.isPresent.not()) {
                return@zip Optional.empty<Int>()
            }

            Optional.of(
                (current.value.intensity * 100f / yesterday.value.intensity.coerceAtLeast(1)).roundToInt() - 100
            )
        }.toObservable()
    }
}