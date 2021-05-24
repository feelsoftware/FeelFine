package com.feelsoftware.feelfine

import android.app.Application
import com.feelsoftware.feelfine.di.KoinInit

class FeelFineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinInit.init(this)
    }
}