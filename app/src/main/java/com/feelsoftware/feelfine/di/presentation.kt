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
import com.feelsoftware.feelfine.ui.onboarding.usecase.IsOnboardingCompletedUseCase
import com.feelsoftware.feelfine.ui.profile.ProfileViewModel
import com.feelsoftware.feelfine.ui.score.ActivityScoreViewModel
import com.feelsoftware.feelfine.ui.score.CurrentScoreViewModel
import com.feelsoftware.feelfine.ui.score.MoodViewModel
import com.feelsoftware.feelfine.ui.score.SleepScoreViewModel
import com.feelsoftware.feelfine.ui.score.StepScoreViewModel
import com.feelsoftware.feelfine.ui.statistic.StatisticViewModel
import com.feelsoftware.feelfine.utils.ActivityEngine
import com.feelsoftware.feelfine.utils.MoodTracker
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
        EntryPointViewModel(get<IsOnboardingCompletedUseCase>())
    }
    viewModel {
        HomeViewModel(get<MoodTracker>())
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
            scoreTargetProvider = get<ScoreTargetProvider>(),
            userRepository = get<UserRepository>(),
        )
    }
    viewModel {
        MoodViewModel(get<SetMoodUseCase>())
    }
}