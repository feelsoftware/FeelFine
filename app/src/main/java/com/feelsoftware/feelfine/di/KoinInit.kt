package com.feelsoftware.feelfine.di

import android.app.Application
import com.feelsoftware.feelfine.utils.ActivityEngine
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

object KoinInit {

    fun init(application: Application) {
        startKoin {
            androidContext(application)
            modules(
                dbModule,
                fitModule,
                presentationModule,
                repositoryModule,
                utilsModule
            )
            koin.get<ActivityEngine>()
        }
    }
}