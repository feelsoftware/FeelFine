@file:Suppress("RemoveExplicitTypeArguments")

package com.feelsoftware.feelfine.di

import com.feelsoftware.feelfine.fit.FitPermissionManager
import com.feelsoftware.feelfine.fit.GoogleFitPermissionManager
import com.feelsoftware.feelfine.utils.ActivityEngine
import org.koin.dsl.module

val fitModule = module {
    single<FitPermissionManager> {
        GoogleFitPermissionManager(get<ActivityEngine>())
    }
}