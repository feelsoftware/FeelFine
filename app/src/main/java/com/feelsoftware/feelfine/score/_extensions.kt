package com.feelsoftware.feelfine.score

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.feelsoftware.feelfine.data.model.ScoreInfo
import com.feelsoftware.feelfine.extension.await
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.model.Duration
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber

typealias PercentData = String

inline val ScoreInfo.currentDuration: Duration
    get() = Duration(current)

inline val ScoreInfo.targetDuration: Duration
    get() = Duration(target)

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

fun combineScoreData(
    currentData: LiveData<Int>,
    scoreData: Single<Int>
): LiveData<ScoreInfo> = currentData.switchMap { current ->
    liveData {
        val score = calculateScore(current, scoreData.await())
        emit(score)
    }
}

private fun calculateScore(currentValue: Int, targetValue: Int): ScoreInfo {
    val userProgress = currentValue * 100 / targetValue
    val score = if (userProgress >= targetValue) {
        100f
    } else {
        userProgress.toFloat()
    }
    return ScoreInfo(currentValue, targetValue, score.coerceAtMost(100f))
}

fun TextView.applyPercentData(data: PercentData) {
    text = data
}