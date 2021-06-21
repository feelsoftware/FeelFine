package com.feelsoftware.feelfine.ui.score

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.extension.subscribeBy
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

typealias PercentData = String

fun managePercentData(
    source: Observable<Int>,
    target: MutableLiveData<PercentData>
): Disposable = source.observeOn(AndroidSchedulers.mainThread())
    .subscribeBy(onNext = { percent ->
        target.value = if (percent >= 0) "+$percent%" else "$percent%"
    })

fun TextView.applyPercentData(data: PercentData) {
    text = data
}