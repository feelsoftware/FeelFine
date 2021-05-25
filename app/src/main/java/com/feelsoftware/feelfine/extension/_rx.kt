@file:Suppress("HasPlatformType")

package com.feelsoftware.feelfine.extension

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer

fun <T : Any?> Observable<T>.subscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onNext: (T) -> Unit = {}
) = subscribe(Consumer(onNext), Consumer(onError), Action(onComplete))

fun <T : Any?> Single<T>.subscribeBy(
    onError: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit = {}
) = subscribe(Consumer(onSuccess), Consumer(onError))

fun Completable.subscribeBy(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {}
) = subscribe(Action(onComplete), Consumer(onError))