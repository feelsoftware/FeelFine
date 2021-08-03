@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.fit.model.Duration
import com.feelsoftware.feelfine.fit.model.toIntMinutes
import com.feelsoftware.feelfine.fit.model.total
import com.feelsoftware.feelfine.fit.usecase.*
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.fragment_current_score.*
import kotlinx.android.synthetic.main.fragment_current_score.stepsText

class CurrentScoreFragment : BaseFragment<CurrentScoreViewModel>(R.layout.fragment_current_score) {

    override val viewModel: CurrentScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.stepsData.observe {
            // TODO fetch userGoal from main source of personal goals
            circularProgressBarSteps.progress = it.applyScore(15000)
            stepsText.text = "${it.toString()} /15000 steps"
        }
        viewModel.sleepData.observe {
            // TODO fetch userGoal from main source of personal goals
            circularProgressBarSleep.progress = it.toIntMinutes().applyScore(60 * 8)
            sleepText.text = "${it.hours} /8 hours"
        }
        viewModel.activityData.observe {
            // TODO fetch userGoal from main source of personal goals
            activityCircularProgressBar.progress = it.toIntMinutes().applyScore(60 * 8)
            activityText.text = "${it.hours} /8 hours"
        }

        viewModel.stepsPercents.observe {
            stepsPercentTV.applyPercentData(it)
        }
        viewModel.sleepPercents.observe {
            sleepPercentTV.applyPercentData(it)
        }
        viewModel.activityPercents.observe {
            activityPercentTV.applyPercentData(it)
        }
        stepLayout.onClick { viewModel.navigate(R.id.stepScoreFragment) }
        sleepLayout.onClick { viewModel.navigate(R.id.sleepScoreFragment) }
        activityLayout.onClick { viewModel.navigate(R.id.activityScoreFragment) }
        todayScoreTV.onClick { AboutScoreWindow.show(requireView(), todayScoreTV) }
        moodLayout.onClick { viewModel.navigate(R.id.moodFragment) }
    }
}

class CurrentScoreViewModel(
    useCase: GetFitDataUseCase
) : BaseViewModel() {

    val stepsData = MutableLiveData<Int>()
    val sleepData = MutableLiveData<Duration>()
    val activityData = MutableLiveData<Duration>()
    val stepsPercents = MutableLiveData<PercentData>()
    val sleepPercents = MutableLiveData<PercentData>()
    val activityPercents = MutableLiveData<PercentData>()

    init {
        stepsData.attachSource(useCase.getCurrentSteps()) { it.count }
        sleepData.attachSource(useCase.getCurrentSleep()) { it.total }
        activityData.attachSource(useCase.getCurrentActivity()) { it.total }
        combinePercentData(stepsPercents, useCase.getPercentSteps())
        combinePercentData(sleepPercents, useCase.getPercentSleep())
        combinePercentData(activityPercents, useCase.getPercentActivity())
    }
}