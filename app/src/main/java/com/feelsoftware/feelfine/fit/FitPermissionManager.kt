package com.feelsoftware.feelfine.fit

import android.content.Intent
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.db.dao.ActivityDao
import com.feelsoftware.feelfine.data.db.dao.SleepDao
import com.feelsoftware.feelfine.data.db.dao.StepsDao
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.ui.dialog.showErrorDialog
import com.feelsoftware.feelfine.utils.ActivityEngine
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

interface FitPermissionManager {

    fun hasPermission(): Boolean

    fun hasPermissionObservable(): Observable<Boolean>

    fun requestPermission(): Single<Boolean>

    fun resetPermission(): Single<Boolean>

    fun onPermissionResult(requestCode: Int, resultCode: Int, data: Intent?)
}

private const val REQUEST_CODE = 1717

class GoogleFitPermissionManager(
    private val activityDao: ActivityDao,
    private val activityEngine: ActivityEngine,
    private val sleepDao: SleepDao,
    private val stepsDao: StepsDao,
    private val userRepository: UserRepository,
) : FitPermissionManager, SignInOptions {

    private val hasPermissionRelay = PublishRelay.create<Boolean>()
    private val hasPendingPermission = AtomicBoolean(false)
    private val permissionRequest = PublishRelay.create<Boolean>()

    override fun hasPermission(): Boolean = hasPermissionInternal()

    override fun hasPermissionObservable(): Observable<Boolean> {
        return Observable.merge(Observable.just(hasPermissionInternal()), hasPermissionRelay)
    }

    override fun requestPermission(): Single<Boolean> {
        if (hasPendingPermission.get()) return Single.just(true)
        if (hasPermissionInternal()) return Single.just(true)
        requestPermissionInternal()
        return permissionRequest.firstOrError()
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

            userRepository.getProfile()
                .firstOrError()
                .flatMapCompletable { profile ->
                    userRepository.setProfile(profile.copy(isDemo = false))
                }
                // Clear cached mocked data
                .andThen(activityDao.delete())
                .andThen(sleepDao.delete())
                .andThen(stepsDao.delete())
                .subscribeOn(Schedulers.io())
                .doFinally {
                    hasPermissionRelay.accept(true)
                    permissionRequest.accept(true)
                }
                .subscribeBy(onError = { error ->
                    Timber.e(error, "Failed to update profile")
                })
        } catch (error: ApiException) {
            Timber.e(error, "onPermissionResult error")
            if (error.statusCode != GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                activityEngine.activity?.apply {
                    showErrorDialog(
                        title = getString(R.string.sign_in),
                        body = getString(
                            R.string.failed_sign_in_error_alert_body,
                            error.toString()
                        ),
                    )
                }
            }
            hasPermissionRelay.accept(false)
            permissionRequest.accept(false)
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
        val activity = activityEngine.activity ?: run {
            permissionRequest.accept(false)
            return
        }
        hasPendingPermission.set(true)
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        GoogleSignIn.requestPermissions(activity, REQUEST_CODE, account, signInOptions)
    }
}