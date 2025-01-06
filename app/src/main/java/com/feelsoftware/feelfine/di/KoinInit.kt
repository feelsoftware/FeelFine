package com.feelsoftware.feelfine.di

import android.app.Application
import com.feelsoftware.feelfine.fit.FitPermissionManager
import com.feelsoftware.feelfine.ui.onboarding.onboardingModule
import com.feelsoftware.feelfine.utils.ActivityEngine
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

// TODO: migrate to Hilt
object KoinInit {

    fun init(application: Application) {
        startKoin {
            androidContext(application)
            modules(
                dbModule,
                fitModule,
                presentationModule,
                repositoryModule,
                useCaseModule,
                utilsModule,
                onboardingModule,
            )
            // FIXME: ActivityEngine to initialize ActivityLifecycleCallbacks
            koin.get<ActivityEngine>()
            // FIXME: FitPermissionManager to initialize HealthConnectFitPermissionManagerWrapper#activityEngine
            koin.get<FitPermissionManager>()
        }
    }
}