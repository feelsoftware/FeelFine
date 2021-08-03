package com.feelsoftware.feelfine.ui.score

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber

typealias PercentData = String

fun BaseViewModel.combinePercentData(
    target: MutableLiveData<PercentData>,
    source: Observable<Int>
) {
    source.observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onNext = { percent ->
            target.value = if (percent >= 0) "+$percent%" else "$percent%"
        }, onError = {
            Timber.e(it, "Failed to combine percent data")
        })
        .disposeOnDestroy()
}

fun TextView.applyPercentData(data: PercentData) {
    text = data
}

fun Int.applyScore(userScore: Int): Float {
    val userProgress = this * 100 / userScore
    return if (userProgress >= userScore) {
        100f
    } else {
        userProgress.toFloat()
    }
}