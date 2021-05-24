@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.ui.MainViewModel
import com.feelsoftware.feelfine.ui.base.EmptyViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        EmptyViewModel()
    }
    viewModel {
        MainViewModel(get<UserRepository>())
    }
}