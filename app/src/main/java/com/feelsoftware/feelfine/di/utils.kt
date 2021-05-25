@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import org.koin.dsl.module

val utilsModule = module {
    single<OnBoardingFlowManager> {
        OnBoardingFlowManager(get<UserRepository>())
    }
}