@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.fit.model.*
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentSleep
import com.feelsoftware.feelfine.fit.usecase.getPercentSleep
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import kotlinx.android.synthetic.main.fragment_sleep_score.*
import kotlinx.android.synthetic.main.fragment_sleep_score.backIV
import kotlinx.android.synthetic.main.fragment_sleep_score.scorePercentTV
import org.koin.androidx.viewmodel.ext.android.viewModel

class SleepScoreFragment : BaseFragment<SleepScoreViewModel>(R.layout.fragment_sleep_score) {

    override val viewModel: SleepScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.sleepData.observe {
            // TODO fetch userGoal from main source of personal goals
            circularProgressBar.progress = it.total.toIntMinutes().applyScore(8 * 60)
            sleepText.text = resources.getString(R.string.activity_placeholder, it.total.toHours())
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
        backIV.setOnClickListener { requireActivity().onBackPressed() }
    }
}

class SleepScoreViewModel(useCase: GetFitDataUseCase) : BaseViewModel() {

    val sleepData = MutableLiveData<SleepInfo>()
    val sleepPercents = MutableLiveData<PercentData>()

    init {
        sleepData.attachSource(useCase.getCurrentSleep()) { it }
        combinePercentData(sleepPercents, useCase.getPercentSleep())
    }
}