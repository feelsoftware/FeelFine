package com.feelsoftware.feelfine

import android.app.Application
import com.feelsoftware.feelfine.di.KoinInit
import com.feelsoftware.feelfine.utils.CrashReportingTree
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import timber.log.Timber
import timber.log.Timber.DebugTree

class FeelFineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinInit.init(this)
        initTimber()
        initRxJava()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private fun initRxJava() {
        RxJavaPlugins.setErrorHandler { Timber.e(it) }
    }
}