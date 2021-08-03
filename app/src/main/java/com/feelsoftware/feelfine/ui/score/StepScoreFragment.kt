@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.fit.model.*
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentSteps
import com.feelsoftware.feelfine.fit.usecase.getPercentSteps
import com.feelsoftware.feelfine.score.ScoreTargetProvider
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import kotlinx.android.synthetic.main.fragment_step_score.*
import kotlinx.android.synthetic.main.fragment_step_score.backIV
import kotlinx.android.synthetic.main.fragment_step_score.circularProgressBar
import kotlinx.android.synthetic.main.fragment_step_score.scorePercentTV
import org.koin.androidx.viewmodel.ext.android.viewModel

class StepScoreFragment : BaseFragment<StepScoreViewModel>(R.layout.fragment_step_score) {

    override val viewModel: StepScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.stepsInfo.observe {
            caloriesTV.text = getString(R.string.steps_calories, it.calories)
            distanceTV.text = getString(R.string.steps_distance, it.distance)
        }
        viewModel.percents.observe {
            scorePercentTV.applyPercentData(it)
        }
        viewModel.score.observe {
            val current = it.current.toString()
            val target = it.target.toString()
            stepsText.text = getString(R.string.steps_score_text, current, target)
            circularProgressBar.progress = it.score
        }
        backIV.setOnClickListener { requireActivity().onBackPressed() }
    }
}

class StepScoreViewModel(
    useCase: GetFitDataUseCase,
    scoreTargetProvider: ScoreTargetProvider
) : BaseViewModel() {

    val stepsInfo = MutableLiveData<StepsInfo>()
    val percents = MutableLiveData<PercentData>()
    val score = combineScoreData(
        stepsInfo.map { it.count },
        scoreTargetProvider.getSteps()
    )

    init {
        stepsInfo.attachSource(useCase.getCurrentSteps()) { it }
        combinePercentData(percents, useCase.getPercentSteps())
    }
}