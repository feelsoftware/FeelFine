package com.feelsoftware.feelfine.ui.score

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.model.SleepInfo
import com.feelsoftware.feelfine.fit.model.toHours
import com.feelsoftware.feelfine.fit.model.toHoursMinutes
import com.feelsoftware.feelfine.fit.model.total
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentSleep
import com.feelsoftware.feelfine.fit.usecase.getPercentSleep
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_sleep_score.*
import kotlinx.android.synthetic.main.fragment_sleep_score.scoreView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SleepScoreFragment : BaseFragment<SleepScoreViewModel>(R.layout.fragment_sleep_score) {

    override val viewModel: SleepScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.sleepData.observe {
            scoreView.text = "Sleep\n" + it.total.toHours()
            awakeTV.text = "Awake: " + it.awake.toHoursMinutes()
            lightSleepTV.text =
                "Light sleep: " + it.lightSleep.toHoursMinutes()
            deepSleepTV.text =
                "Deep sleep: " + it.deepSleep.toHoursMinutes()
            outOfBedTV.text =
                "Out of bed: " + it.outOfBed.toHoursMinutes()
        }
        viewModel.sleepPercents.observe {
            scorePercentTV.text = it.first
            scorePercentTV.background = it.second
        }
    }
}

class SleepScoreViewModel(context: Context, useCase: GetFitDataUseCase) : BaseViewModel() {

    val sleepData = MutableLiveData<SleepInfo>()
    val sleepPercents = MutableLiveData<Pair<String, Drawable?>>()

    init {
        useCase.getCurrentSleep()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                sleepData.value = it
            }).disposeOnInActive()
        useCase.getPercentSleep()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = { percent ->
                val text = if (percent >= 0) "+$percent%" else "$percent%"
                val drawableResId =
                    if (percent >= 0) R.drawable.outline_trending_up_24 else R.drawable.outline_trending_down_24
                sleepPercents.value = text to AppCompatResources.getDrawable(context, drawableResId)
            }).disposeOnInActive()
    }
}