@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.fit.FitPermissionManager
import com.feelsoftware.feelfine.fit.FitRepository
import com.feelsoftware.feelfine.fit.GoogleFitPermissionManager
import com.feelsoftware.feelfine.fit.GoogleFitRepository
import com.feelsoftware.feelfine.utils.ActivityEngine
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val fitModule = module {
    single<FitRepository> {
        GoogleFitRepository(androidContext(), get<FitPermissionManager>())
    }
    single<FitPermissionManager> {
        GoogleFitPermissionManager(get<ActivityEngine>())
    }
}