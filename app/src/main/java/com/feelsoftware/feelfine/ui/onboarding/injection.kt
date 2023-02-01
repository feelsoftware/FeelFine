@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.ui.onboarding

import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.ui.onboarding.data.OnboardingManager
import com.feelsoftware.feelfine.ui.onboarding.data.OnboardingManagerImpl
import com.feelsoftware.feelfine.ui.onboarding.usecase.CompleteOnboardingUseCase
import com.feelsoftware.feelfine.ui.onboarding.usecase.IsOnboardingCompletedUseCase
import com.feelsoftware.feelfine.utils.StringResources
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// TODO: migrate to Hilt
val onboardingModule = module {
    viewModel {
        OnboardingViewModel(
            get<CompleteOnboardingUseCase>(),
            get<StringResources>(),
        )
    }

    factory {
        IsOnboardingCompletedUseCase(get<OnboardingManager>())
    }
    factory {
        CompleteOnboardingUseCase(get<OnboardingManager>())
    }
    single<OnboardingManager> {
        OnboardingManagerImpl(get<UserRepository>())
    }
}