package com.feelsoftware.feelfine.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

object KoinInit {

    fun init(application: Application) {
        startKoin {
            androidContext(application)
            modules(
                dbModule,
                repositoryModule,
                presentationModule
            )
        }
    }
}