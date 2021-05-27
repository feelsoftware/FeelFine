@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.utils.ActivityEngine
import com.feelsoftware.feelfine.utils.ActivityEngineImpl
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val utilsModule = module {
    single<ActivityEngine> {
        ActivityEngineImpl(androidApplication())
    }
    single<OnBoardingFlowManager> {
        OnBoardingFlowManager(get<UserRepository>())
    }
}