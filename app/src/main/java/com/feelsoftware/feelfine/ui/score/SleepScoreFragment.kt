@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
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
            scorePercentTV.applyPercentData(it)
        }
    }
}

class SleepScoreViewModel(useCase: GetFitDataUseCase) : BaseViewModel() {

    val sleepData = MutableLiveData<SleepInfo>()
    val sleepPercents = MutableLiveData<PercentData>()

    init {
        useCase.getCurrentSleep()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = {
                sleepData.value = it
            }).disposeOnInActive()
        managePercentData(useCase.getPercentSleep(), sleepPercents).disposeOnInActive()
    }
}