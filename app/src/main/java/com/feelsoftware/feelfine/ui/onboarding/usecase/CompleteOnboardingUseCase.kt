package com.feelsoftware.feelfine.ui.onboarding.usecase

import com.feelsoftware.feelfine.ui.onboarding.data.OnboardingManager
import com.feelsoftware.feelfine.ui.onboarding.data.OnboardingStep

internal class CompleteOnboardingUseCase(
    private val manager: OnboardingManager,
) {

    suspend operator fun invoke(steps: List<OnboardingStep>) = manager.complete(steps)
}