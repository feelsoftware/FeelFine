package com.feelsoftware.feelfine.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.feelsoftware.feelfine.MainActivity
import timber.log.Timber

interface ActivityEngine {

    val activity: Activity?
}

class ActivityEngineImpl(
    application: Application
) : ActivityEngine {

    override var activity: Activity? = null

    init {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Timber.d("onActivityCreated $activity")
                if (activity is MainActivity) {
                    this@ActivityEngineImpl.activity = activity
                }
            }

            override fun onActivityStarted(activity: Activity) {
                Timber.d("onActivityStarted $activity")
            }

            override fun onActivityResumed(activity: Activity) {
                Timber.d("onActivityResumed $activity")
            }

            override fun onActivityPaused(activity: Activity) {
                Timber.d("onActivityPaused $activity")
            }

            override fun onActivityStopped(activity: Activity) {
                Timber.d("onActivityStopped $activity")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                Timber.d("onActivityDestroyed $activity")
                if (activity is MainActivity) {
                    this@ActivityEngineImpl.activity = null
                }
            }
        })
    }
}