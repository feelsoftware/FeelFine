package com.feelsoftware.feelfine.utils

import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.data.repository.UserRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class OnBoardingFlowManager(
    private val userRepository: UserRepository,
) {

    var name: String? = null
    var gender: UserProfile.Gender? = null
    var weight: Float? = null
    var age: Int? = null

    /**
     * @return true if OnBoarding is passed
     */
    fun isPassed(): Single<Boolean> =
        userRepository.getProfile()
            .firstOrError()
            .map {
                it != UserProfile.EMPTY
            }

    /**
     * Mark OnBoarding flow as passed
     */
    fun markAsPassed(profile: UserProfile): Completable =
        userRepository.setProfile(profile)

    fun buildUserProfile(): UserProfile? {
        return UserProfile(
            name = name ?: return null,
            gender = gender ?: return null,
            weight = weight ?: return null,
            age = age ?: return null
        )
    }
}