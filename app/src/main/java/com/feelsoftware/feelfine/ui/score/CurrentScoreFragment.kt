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
            stepsView.text = "Steps\n$it"
        }
        viewModel.sleepData.observe {
            sleepView.text = "Sleep\n$it"
        }
        viewModel.activityData.observe {
            activityView.text = "Activity\n$it"
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
        stepsView.onClick { viewModel.navigate(R.id.stepScoreFragment) }
        sleepView.onClick { viewModel.navigate(R.id.sleepScoreFragment) }
        activityView.onClick { viewModel.navigate(R.id.activityScoreFragment) }
        // TODO temporary navigation
        moodView.onClick { viewModel.navigate(R.id.stepScoreFragment) }
    }
}

class CurrentScoreViewModel(useCase: GetFitDataUseCase) : BaseViewModel() {

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