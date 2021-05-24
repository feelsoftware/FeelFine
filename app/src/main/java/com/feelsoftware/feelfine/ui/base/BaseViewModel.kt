package com.feelsoftware.feelfine.ui.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

open class BaseViewModel : ViewModel() {

    // region LifeCycle
    open fun onActive() {}

    @CallSuper
    open fun onInActive() {
        disposableInActive.clear()
    }
    // endregion

    // region RxJava
    private val disposableInActive = CompositeDisposable()

    protected fun Disposable.disposeOnInActive() {
        disposableInActive.add(this)
    }
    // endregion

    // region Navigation
    val navigation = MutableLiveData<NavDirections>()

    fun navigate(@IdRes actionId: Int) {
        navigation.postValue(ActionOnlyNavDirections(actionId))
    }

    fun navigate(@IdRes actionId: Int, arguments: Bundle) {
        navigation.postValue(object : NavDirections {
            override fun getActionId(): Int = actionId

            override fun getArguments(): Bundle = arguments
        })
    }
    // endregion
}