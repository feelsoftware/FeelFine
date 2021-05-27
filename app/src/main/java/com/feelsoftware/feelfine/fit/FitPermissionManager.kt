package com.feelsoftware.feelfine.fit

import android.content.Intent
import androidx.activity.ComponentActivity
import com.feelsoftware.feelfine.utils.ActivityEngine
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber

interface FitPermissionManager {

    fun hasPermission(): Observable<Boolean>

    fun checkPermission()

    fun onPermissionResult(requestCode: Int, resultCode: Int, data: Intent?)
}

private const val REQUEST_CODE = 1717

class GoogleFitPermissionManager(
    private val activityEngine: ActivityEngine,
) : FitPermissionManager {

    private val fitnessOptions = FitnessOptions.builder()
        // Activities
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_READ)
        // Steps
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        // Sleep
        .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
        // Heart rate
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
        .build()

    private val hasPermissionRelay = PublishRelay.create<Boolean>()

    override fun hasPermission(): Observable<Boolean> = hasPermissionRelay

    override fun checkPermission() {
        val activity = activityEngine.activity as? ComponentActivity
        if (activity == null) {
            Timber.e("checkPermission, activity == null")
            hasPermissionRelay.accept(false)
            return
        }

        val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
        if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            hasPermissionRelay.accept(true)
            return
        }
        GoogleSignIn.requestPermissions(activity, REQUEST_CODE, account, fitnessOptions)
    }

    override fun onPermissionResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onPermissionResult $requestCode, $resultCode, $data")
        if (requestCode != REQUEST_CODE) return
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            Timber.d("onPermissionResult success ${account?.displayName}")
            hasPermissionRelay.accept(true)
        } catch (error: ApiException) {
            Timber.e(error, "onPermissionResult error")
            hasPermissionRelay.accept(false)
        }
    }
}