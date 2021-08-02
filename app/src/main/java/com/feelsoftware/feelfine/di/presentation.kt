@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.data.usecase.SetMoodUseCase
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.ui.EntryPointViewModel
import com.feelsoftware.feelfine.ui.base.EmptyViewModel
import com.feelsoftware.feelfine.ui.home.HomeViewModel
import com.feelsoftware.feelfine.ui.onboarding.AgeViewModel
import com.feelsoftware.feelfine.ui.onboarding.GenderViewModel
import com.feelsoftware.feelfine.ui.onboarding.NickNameViewModel
import com.feelsoftware.feelfine.ui.onboarding.WeightViewModel
import com.feelsoftware.feelfine.ui.profile.ProfileViewModel
import com.feelsoftware.feelfine.ui.score.*
import com.feelsoftware.feelfine.ui.statistic.StatisticViewModel
import com.feelsoftware.feelfine.utils.MoodTracker
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        EmptyViewModel()
    }
    viewModel {
        EntryPointViewModel(get<OnBoardingFlowManager>())
    }
    viewModel {
        HomeViewModel(get<MoodTracker>())
    }
    viewModel {
        NickNameViewModel(get<OnBoardingFlowManager>())
    }
    viewModel {
        GenderViewModel(get<OnBoardingFlowManager>())
    }
    viewModel {
        WeightViewModel(get<OnBoardingFlowManager>())
    }
    viewModel {
        AgeViewModel(get<OnBoardingFlowManager>())
    }
    viewModel {
        CurrentScoreViewModel(get<GetFitDataUseCase>())
    }
    viewModel {
        SleepScoreViewModel(get<GetFitDataUseCase>())
    }
    viewModel {
        StepScoreViewModel(get<GetFitDataUseCase>())
    }
    viewModel {
        ActivityScoreViewModel(get<GetFitDataUseCase>())
    }
    viewModel {
        StatisticViewModel(get<GetFitDataUseCase>())
    }
    viewModel {
        ProfileViewModel(get<GetFitDataUseCase>())
    }
    viewModel {
        MoodViewModel(get<SetMoodUseCase>())
    }
}