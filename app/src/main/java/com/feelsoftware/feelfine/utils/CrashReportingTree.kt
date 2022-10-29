@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "LogNotTimber")

package com.feelsoftware.feelfine.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashReportingTree : Timber.Tree() {

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        throwable: Throwable?
    ) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        if (priority == Log.ERROR) {
            Log.e("CrashReportingTree", "[$tag] $message")
            crashlytics.recordException(throwable ?: Exception(message))
        } else {
            Log.d("CrashReportingTree", "[$tag] $message")
            crashlytics.log("[$tag] $message")
        }
    }
}