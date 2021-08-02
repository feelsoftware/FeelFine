package com.feelsoftware.feelfine.fit

import android.content.Intent
import androidx.activity.ComponentActivity
import com.feelsoftware.feelfine.utils.ActivityEngine
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber

interface FitPermissionManager {

    fun hasPermission(): Boolean

    fun hasPermissionObservable(): Observable<Boolean>

    fun requestPermission()

    fun onPermissionResult(requestCode: Int, resultCode: Int, data: Intent?)
}

private const val REQUEST_CODE = 1717

class GoogleFitPermissionManager(
    private val activityEngine: ActivityEngine,
) : FitPermissionManager, FitnessOptions {

    private val hasPermissionRelay = PublishRelay.create<Boolean>()

    override fun hasPermission(): Boolean = hasPermissionInternal()

    override fun hasPermissionObservable(): Observable<Boolean> {
        return Observable.merge(Observable.just(hasPermissionInternal()), hasPermissionRelay)
    }

    override fun requestPermission() {
        if (hasPermissionInternal()) return
        requestPermissionInternal()
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

    private fun hasPermissionInternal(): Boolean {
        val activity = activityEngine.activity as? ComponentActivity
        if (activity == null) {
            Timber.e("checkPermission, activity == null")
            hasPermissionRelay.accept(false)
            return false
        }

        val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
        val hasPermission = GoogleSignIn.hasPermissions(account, fitnessOptions)

        hasPermissionRelay.accept(hasPermission)
        return hasPermission
    }

    private fun requestPermissionInternal() {
        val activity = activityEngine.activity as? ComponentActivity ?: return
        val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
        GoogleSignIn.requestPermissions(activity, REQUEST_CODE, account, fitnessOptions)
    }
}