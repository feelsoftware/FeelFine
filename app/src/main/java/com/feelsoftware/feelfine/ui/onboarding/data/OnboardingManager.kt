package com.feelsoftware.feelfine.ui.onboarding.data

import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.data.repository.UserRepository

internal interface OnboardingManager {
    /**
     * @return `true` if onboarding is completed, `false` otherwise.
     */
    suspend fun isCompleted(): Boolean

    suspend fun complete(steps: List<OnboardingStep>)
}

internal class OnboardingManagerImpl(
    private val userRepository: UserRepository,
) : OnboardingManager {

    override suspend fun isCompleted(): Boolean =
        userRepository.hasProfile()

    override suspend fun complete(steps: List<OnboardingStep>) {
        val name =
            requireNotNull(steps.filterIsInstance<OnboardingStep.Name>().first().name)
        val gender =
            requireNotNull(steps.filterIsInstance<OnboardingStep.Gender>().first().gender)
        val weight =
            steps.filterIsInstance<OnboardingStep.Weight>().first().weight
        val birthday =
            requireNotNull(steps.filterIsInstance<OnboardingStep.Birthday>().first().birthday)

        userRepository.setProfile(
            UserProfile(
                name = name,
                gender = gender,
                weight = weight,
                birthday = birthday,
                isDemo = true,
            )
        )
    }
}