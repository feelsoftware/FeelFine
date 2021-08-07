package com.feelsoftware.feelfine.score

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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

sealed class PercentData {
    data class Value(val percent: String) : PercentData()
    object Empty : PercentData()
    object Error : PercentData()
}

inline val ScoreInfo.currentDuration: Duration?
    get() = current?.let(::Duration)

inline val ScoreInfo.targetDuration: Duration
    get() = Duration(target)

fun BaseViewModel.combinePercentData(
    target: MutableLiveData<PercentData>,
    source: Observable<Optional<Int>>
) {
    source.observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onNext = { optional ->
            target.value = if (optional.isPresent) {
                val percent = optional.value
                when {
                    percent == 0 -> PercentData.Empty
                    percent > 0 -> PercentData.Value("+$percent%")
                    else -> PercentData.Value("$percent%")
                }
            } else {
                PercentData.Error
            }
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
    text = when (data) {
        is PercentData.Value -> data.percent
        is PercentData.Error -> "!"
        else -> null
    }
    isVisible = data !is PercentData.Empty

    val textColor = when (data) {
        is PercentData.Value -> R.color.greyishBrown
        is PercentData.Error -> R.color.darkishPink
        else -> return
    }
    setTextColor(ContextCompat.getColor(context, textColor))
}