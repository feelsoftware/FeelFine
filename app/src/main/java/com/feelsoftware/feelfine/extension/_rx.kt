@file:Suppress("HasPlatformType")

package com.feelsoftware.feelfine.extension

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun <T : Any> Observable<T>.subscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onNext: (T) -> Unit = {}
): Disposable = subscribe(Consumer(onNext), Consumer(onError), Action(onComplete))

fun <T : Any> Single<T>.subscribeBy(
    onError: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit = {}
): Disposable = subscribe(Consumer(onSuccess), Consumer(onError))

fun <T : Any> Maybe<T>.subscribeBy(
    onError: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit = {},
    onComplete: () -> Unit = {},
): Disposable = subscribe(Consumer(onSuccess), Consumer(onError), Action(onComplete))

fun Completable.subscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {}
): Disposable = subscribe(Action(onComplete), Consumer(onError))

suspend fun <T : Any> Single<T>.await() = suspendCancellableCoroutine<T> { continuation ->
    val disposable = subscribeBy(onSuccess = {
        continuation.resume(it)
    }, onError = {
        continuation.resumeWithException(it)
    })
    continuation.invokeOnCancellation { disposable.dispose() }
}

fun <T : Any> Observable<T>.toLiveData(): LiveData<T> {
    var disposable: Disposable? = null

    return object : LiveData<T>() {
        override fun onActive() {
            super.onActive()
            disposable = observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                    value = it
                }, onError = {
                    Timber.e(it, "Failed to observe values")
                })
        }

        override fun onInactive() {
            super.onInactive()
            disposable?.dispose()
        }
    }
}