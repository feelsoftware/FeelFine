package com.feelsoftware.feelfine.ui.onboarding.usecase

import com.feelsoftware.feelfine.ui.onboarding.data.OnboardingManager

class IsOnboardingCompletedUseCase internal constructor(
    private val manager: OnboardingManager,
) {

    suspend operator fun invoke(): Boolean = manager.isCompleted()
}