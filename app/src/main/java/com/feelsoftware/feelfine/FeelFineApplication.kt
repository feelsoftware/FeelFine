package com.feelsoftware.feelfine

import android.app.Application
import com.feelsoftware.feelfine.di.KoinInit
import timber.log.Timber
import timber.log.Timber.DebugTree

class FeelFineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinInit.init(this)
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}