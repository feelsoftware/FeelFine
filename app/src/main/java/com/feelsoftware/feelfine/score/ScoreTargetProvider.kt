package com.feelsoftware.feelfine.score

import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.fit.model.Duration
import io.reactivex.rxjava3.core.Single

interface ScoreTargetProvider {

    fun getSteps(): Single<Int>

    fun getActivityDuration(): Single<Duration>

    fun getSleepDuration(): Single<Duration>
}

class ScoreTargetProviderImpl(
    private val userRepository: UserRepository,
) : ScoreTargetProvider {

    override fun getSteps(): Single<Int> =
        getUserInfo().map(::provideSteps)

    override fun getActivityDuration(): Single<Duration> =
        getUserInfo().map(::provideActivityDuration)

    override fun getSleepDuration(): Single<Duration> =
        getUserInfo().map(::provideSleepDuration)

    private fun getUserInfo(): Single<UserInfo> =
        userRepository.getProfile()
            .firstOrError()
            .map { profile ->
                UserInfo(
                    weight = profile.weight,
                    age = profile.age,
                    isMale = profile.gender == UserProfile.Gender.MALE
                )
            }

    private fun provideSteps(info: UserInfo): Int = with(info) {
        // TODO: calculate real data based on weight, age and gender
        when (age) {
            in 0..6 -> 12_000
            in 7..10 -> 17_000
            in 11..15 -> 22_000
            in 16..35 -> 15_000
            in 36..50 -> 10_000
            else -> 8_000
        }
    }

    private fun provideActivityDuration(info: UserInfo): Duration = with(info) {
        // TODO: calculate real data based on weight, age and gender
        when (age) {
            in 0..6 -> Duration.of(hours = 4)
            in 7..18 -> Duration.of(hours = 3)
            in 19..25 -> Duration.of(hours = 2)
            else -> Duration.of(hours = 1, minutes = 30)
        }
    }

    private fun provideSleepDuration(info: UserInfo): Duration = with(info) {
        // TODO: calculate real data based on weight, age and gender
        when (age) {
            in 0..1 -> Duration.of(hours = 16)
            in 2..3 -> Duration.of(hours = 13)
            in 4..5 -> Duration.of(hours = 12)
            in 6..12 -> Duration.of(hours = 11, minutes = 30)
            in 13..18 -> Duration.of(hours = 9)
            else -> Duration.of(hours = 7, minutes = 30)
        }
    }

    private class UserInfo(
        val weight: Int,
        val age: Int,
        val isMale: Boolean,
    )
}