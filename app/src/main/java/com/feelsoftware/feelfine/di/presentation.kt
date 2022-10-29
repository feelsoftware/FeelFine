@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.data.db.AppDatabase
import com.feelsoftware.feelfine.data.usecase.GetCurrentMoodUseCase
import com.feelsoftware.feelfine.data.usecase.GetPercentMoodUseCase
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.data.usecase.SetMoodUseCase
import com.feelsoftware.feelfine.fit.FitPermissionManager
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.score.ScoreCalculator
import com.feelsoftware.feelfine.score.ScoreTargetProvider
import com.feelsoftware.feelfine.ui.EntryPointViewModel
import com.feelsoftware.feelfine.ui.base.EmptyViewModel
import com.feelsoftware.feelfine.ui.base.StatusBarColorModifier
import com.feelsoftware.feelfine.ui.base.StatusBarColorModifierImpl
import com.feelsoftware.feelfine.ui.home.HomeViewModel
import com.feelsoftware.feelfine.ui.onboarding.AgeViewModel
import com.feelsoftware.feelfine.ui.onboarding.GenderViewModel
import com.feelsoftware.feelfine.ui.onboarding.NickNameViewModel
import com.feelsoftware.feelfine.ui.onboarding.WeightViewModel
import com.feelsoftware.feelfine.ui.profile.ProfileViewModel
import com.feelsoftware.feelfine.ui.score.*
import com.feelsoftware.feelfine.ui.statistic.StatisticViewModel
import com.feelsoftware.feelfine.utils.ActivityEngine
import com.feelsoftware.feelfine.utils.MoodTracker
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    factory<StatusBarColorModifier> {
        StatusBarColorModifierImpl(get<ActivityEngine>())
    }
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
        CurrentScoreViewModel(
            get<GetFitDataUseCase>(),
            get<GetCurrentMoodUseCase>(),
            get<GetPercentMoodUseCase>(),
            get<ScoreTargetProvider>(),
            get<ScoreCalculator>(),
            get<UserRepository>(),
        )
    }
    viewModel {
        SleepScoreViewModel(
            get<GetFitDataUseCase>(),
            get<ScoreTargetProvider>(),
            get<UserRepository>(),
        )
    }
    viewModel {
        StepScoreViewModel(
            get<GetFitDataUseCase>(),
            get<ScoreTargetProvider>(),
            get<UserRepository>(),
        )
    }
    viewModel {
        ActivityScoreViewModel(
            get<GetFitDataUseCase>(),
            get<ScoreTargetProvider>(),
            get<UserRepository>(),
        )
    }
    viewModel {
        StatisticViewModel(androidContext(), get<GetFitDataUseCase>(), get<UserRepository>())
    }
    viewModel {
        ProfileViewModel(
            appDatabase = get<AppDatabase>(),
            fitPermissionManager = get<FitPermissionManager>(),
            onBoardingFlowManager = get<OnBoardingFlowManager>(),
            scoreTargetProvider = get<ScoreTargetProvider>(),
            userRepository = get<UserRepository>(),
        )
    }
    viewModel {
        MoodViewModel(get<SetMoodUseCase>())
    }
}