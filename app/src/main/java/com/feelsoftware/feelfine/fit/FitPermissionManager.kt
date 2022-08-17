package com.feelsoftware.feelfine.fit

import android.content.Intent
import com.feelsoftware.feelfine.utils.ActivityEngine
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

interface FitPermissionManager {

    fun hasPermission(): Boolean

    fun hasPermissionObservable(): Observable<Boolean>

    fun requestPermission()

    fun resetPermission(): Single<Boolean>

    fun onPermissionResult(requestCode: Int, resultCode: Int, data: Intent?)
}

private const val REQUEST_CODE = 1717

class GoogleFitPermissionManager(
    private val activityEngine: ActivityEngine,
) : FitPermissionManager, SignInOptions {

    private val hasPermissionRelay = PublishRelay.create<Boolean>()
    private val hasPendingPermission = AtomicBoolean(false)

    override fun hasPermission(): Boolean = hasPermissionInternal()

    override fun hasPermissionObservable(): Observable<Boolean> {
        return Observable.merge(Observable.just(hasPermissionInternal()), hasPermissionRelay)
    }

    override fun requestPermission() {
        if (hasPendingPermission.get()) return
        if (hasPermissionInternal()) return
        requestPermissionInternal()
    }

    override fun resetPermission(): Single<Boolean> {
        hasPermissionRelay.accept(false)

        val activity = activityEngine.activity
        if (activity == null) {
            Timber.e("Failed to checkPermission, activity == null")
            return Single.just(false)
        }

        return Single.create { emitter ->
            val client = GoogleSignIn.getClient(activity, logoutOptions)
            client.signOut()
                .addOnCompleteListener { task ->
                    if (task.exception != null) {
                        Timber.e(task.exception, "Failed to log out")
                    } else {
                        Timber.d("Logged out")
                    }
                    if (emitter.isDisposed) return@addOnCompleteListener
                    emitter.onSuccess(true)
                }
        }
    }

    override fun onPermissionResult(requestCode: Int, resultCode: Int, data: Intent?) {
        hasPendingPermission.set(false)
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
        val activity = activityEngine.activity
        if (activity == null) {
            Timber.e("Failed to checkPermission, activity == null")
            hasPermissionRelay.accept(false)
            return false
        }

        val account = GoogleSignIn.getLastSignedInAccount(activity)
        val hasPermission = GoogleSignIn.hasPermissions(account, signInOptions)

        hasPermissionRelay.accept(hasPermission)
        return hasPermission
    }

    private fun requestPermissionInternal() {
        val activity = activityEngine.activity ?: return
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        GoogleSignIn.requestPermissions(activity, REQUEST_CODE, account, signInOptions)
    }
}