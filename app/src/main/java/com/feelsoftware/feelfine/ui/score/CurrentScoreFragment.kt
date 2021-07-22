@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.fit.model.toHours
import com.feelsoftware.feelfine.fit.model.total
import com.feelsoftware.feelfine.fit.usecase.*
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.fragment_current_score.*

class CurrentScoreFragment : BaseFragment<CurrentScoreViewModel>(R.layout.fragment_current_score) {

    override val viewModel: CurrentScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.stepsData.observe {
            stepsText.text = "$it /15000 steps"
        }
        viewModel.sleepData.observe {
            sleepText.text = "$it /8 hours"
        }
        viewModel.activityData.observe {
            activityText.text = "$it /4 hours"
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

    val stepsData = MutableLiveData<String>()
    val sleepData = MutableLiveData<String>()
    val activityData = MutableLiveData<String>()
    val stepsPercents = MutableLiveData<PercentData>()
    val sleepPercents = MutableLiveData<PercentData>()
    val activityPercents = MutableLiveData<PercentData>()

    init {
        stepsData.combine(useCase.getCurrentSteps()) {
            it.count.toString()
        }
        sleepData.combine(useCase.getCurrentSleep()) {
            it.total.toHours()
        }
        activityData.combine(useCase.getCurrentActivity()) {
            it.total.toHours()
        }
        managePercentData(useCase.getPercentSteps(), stepsPercents).disposeOnInActive()
        managePercentData(useCase.getPercentSleep(), sleepPercents).disposeOnInActive()
        managePercentData(useCase.getPercentActivity(), activityPercents).disposeOnInActive()
    }
}