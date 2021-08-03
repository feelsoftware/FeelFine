@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.fit.model.*
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentSteps
import com.feelsoftware.feelfine.fit.usecase.getPercentSteps
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import kotlinx.android.synthetic.main.fragment_step_score.*
import kotlinx.android.synthetic.main.fragment_step_score.backIV
import kotlinx.android.synthetic.main.fragment_step_score.scorePercentTV
import org.koin.androidx.viewmodel.ext.android.viewModel

class StepScoreFragment : BaseFragment<StepScoreViewModel>(R.layout.fragment_step_score) {

    override val viewModel: StepScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.stepsData.observe {
            // TODO fetch userGoal from main source of personal goals
            circularProgressBar.progress = it.count.applyScore(15000)
            stepsText.text = resources.getString(R.string.steps_placeholder, it.count.toString())
            caloriesTV.text = "Burned: " + it.calories + " kkal"
            distanceTV.text = "Distance: " + it.distance + " km"
        }
        viewModel.stepsPercents.observe {
            scorePercentTV.applyPercentData(it)
        }
        backIV.setOnClickListener { requireActivity().onBackPressed() }
    }

}

class StepScoreViewModel(useCase: GetFitDataUseCase) : BaseViewModel() {

    val stepsData = MutableLiveData<StepsInfo>()
    val stepsPercents = MutableLiveData<PercentData>()

    init {
        stepsData.attachSource(useCase.getCurrentSteps()) { it }
        combinePercentData(stepsPercents, useCase.getPercentSteps())
    }
}