package com.feelsoftware.feelfine.ui.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.feelsoftware.feelfine.extension.subscribeBy
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber

open class BaseViewModel : ViewModel() {

    // region LifeCycle
    open fun onActive() {}

    @CallSuper
    open fun onInActive() {
        disposableInActive.clear()
    }

    @CallSuper
    override fun onCleared() {
        disposableDestroy.clear()
    }
    // endregion

    // region RxJava
    private val disposableInActive = CompositeDisposable()
    private val disposableDestroy = CompositeDisposable()

    fun Disposable.disposeOnInActive() {
        disposableInActive.add(this)
    }

    fun Disposable.disposeOnDestroy() {
        disposableDestroy.add(this)
    }

    protected fun <V, T : Any> MutableLiveData<V>.attachSource(
        source: Observable<T>,
        mapper: (T) -> V
    ) {
        source.observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = {
                value = mapper.invoke(it)
            }, onError = {
                Timber.e(it, "Failed to attach source")
            })
            .disposeOnDestroy()
    }
    // endregion

    // region Navigation
    val navigation = SingleLiveData<NavDirections>()
    val backNavigation = SingleLiveData<Unit>()

    fun navigate(@IdRes actionId: Int) {
        navigation.postValue(ActionOnlyNavDirections(actionId))
    }

    fun navigate(@IdRes actionId: Int, arguments: Bundle) {
        navigation.postValue(object : NavDirections {
            override val actionId: Int = actionId
            override val arguments: Bundle = arguments
        })
    }

    fun navigateBack() {
        backNavigation.postValue(Unit)
    }
    // endregion
}