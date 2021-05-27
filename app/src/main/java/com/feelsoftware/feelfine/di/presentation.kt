@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.ui.EntryPointViewModel
import com.feelsoftware.feelfine.ui.MainViewModel
import com.feelsoftware.feelfine.ui.base.EmptyViewModel
import com.feelsoftware.feelfine.ui.onboarding.AgeViewModel
import com.feelsoftware.feelfine.ui.onboarding.GenderViewModel
import com.feelsoftware.feelfine.ui.onboarding.NickNameViewModel
import com.feelsoftware.feelfine.ui.onboarding.WeightViewModel
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
        MainViewModel(get<UserRepository>())
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
}