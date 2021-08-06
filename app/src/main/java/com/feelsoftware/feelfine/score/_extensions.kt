package com.feelsoftware.feelfine.score

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.Optional
import com.feelsoftware.feelfine.data.model.ScoreInfo
import com.feelsoftware.feelfine.extension.await
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.model.Duration
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber

data class PercentData(val percent: String?)

inline val ScoreInfo.currentDuration: Duration?
    get() = current?.let(::Duration)

inline val ScoreInfo.targetDuration: Duration
    get() = Duration(target)

fun BaseViewModel.combinePercentData(
    target: MutableLiveData<PercentData>,
    source: Observable<Int>
) = combinePercentData(target, source.map { Optional.of(it) })

@JvmName("combinePercentDataOptional")
fun BaseViewModel.combinePercentData(
    target: MutableLiveData<PercentData>,
    source: Observable<Optional<Int>>
) {
    source.observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onNext = { optional ->
            val percent = if (optional.isPresent) {
                val percent = optional.value
                when {
                    percent == 0 -> "-"
                    percent > 0 -> "+$percent%"
                    else -> "$percent%"
                }
            } else {
                null
            }
            target.value = PercentData(percent)
        }, onError = {
            Timber.e(it, "Failed to combine percent data")
        })
        .disposeOnDestroy()
}

fun combineScoreData(
    currentData: LiveData<Int>,
    targetData: Single<Int>
): LiveData<ScoreInfo> = combineScoreData(currentData.map { Optional.of(it) }, targetData)

@JvmName("combineScoreDataOptional")
fun combineScoreData(
    currentData: LiveData<Optional<Int>>,
    targetData: Single<Int>
): LiveData<ScoreInfo> = currentData.switchMap { current ->
    liveData {
        val target = targetData.await()
        val score: ScoreInfo = if (current.isPresent) {
            calculateScore(current.value, target)
        } else {
            ScoreInfo(null, target, 100f)
        }
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
    text = data.percent ?: "!"

    val textColor = if (data.percent != null) R.color.greyishBrown else R.color.darkishPink
    setTextColor(ContextCompat.getColor(context, textColor))
}