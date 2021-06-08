@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.fit.FitPermissionManager
import com.feelsoftware.feelfine.fit.FitRepository
import com.feelsoftware.feelfine.fit.GoogleFitPermissionManager
import com.feelsoftware.feelfine.fit.mock.MockFitRepository
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.utils.ActivityEngine
import org.koin.dsl.module

val fitModule = module {
    single<FitRepository> {
//        GoogleFitRepository(androidContext(), get<FitPermissionManager>())
        MockFitRepository()
    }
    single<FitPermissionManager> {
        GoogleFitPermissionManager(get<ActivityEngine>())
    }
    factory<GetFitDataUseCase> {
        GetFitDataUseCase(get<FitRepository>())
    }
}