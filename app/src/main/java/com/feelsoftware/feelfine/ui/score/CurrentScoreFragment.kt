@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.fit.model.Duration
import com.feelsoftware.feelfine.fit.model.toHours
import com.feelsoftware.feelfine.fit.model.total
import com.feelsoftware.feelfine.fit.usecase.*
import com.feelsoftware.feelfine.score.*
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.fragment_current_score.*
import kotlinx.android.synthetic.main.fragment_current_score.stepsText

class CurrentScoreFragment : BaseFragment<CurrentScoreViewModel>(R.layout.fragment_current_score) {

    override val viewModel: CurrentScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.stepsPercents.observe {
            stepsPercentTV.applyPercentData(it)
        }
        viewModel.sleepPercents.observe {
            sleepPercentTV.applyPercentData(it)
        }
        viewModel.activityPercents.observe {
            activityPercentTV.applyPercentData(it)
        }

        viewModel.totalScore.observe {
            totalScore.progress = it
            scoreTV.text = getString(R.string.current_score, it)
        }
        viewModel.stepsScore.observe {
            stepsText.text = getString(R.string.current_score_steps, it.current, it.target)
            stepsScore.progress = it.score
        }
        viewModel.sleepScore.observe {
            sleepText.text = getString(
                R.string.current_score_sleep,
                it.currentDuration.hours.toString(),
                it.targetDuration.toHours()
            )
            sleepScore.progress = it.score
        }
        viewModel.activityScore.observe {
            activityText.text = getString(
                R.string.current_score_activity,
                it.currentDuration.hours.toString(),
                it.targetDuration.toHours()
            )
            activityScore.progress = it.score
        }

        stepLayout.onClick { viewModel.navigate(R.id.stepScoreFragment) }
        sleepLayout.onClick { viewModel.navigate(R.id.sleepScoreFragment) }
        activityLayout.onClick { viewModel.navigate(R.id.activityScoreFragment) }
        todayScoreTV.onClick { AboutScoreWindow.show(requireView(), todayScoreTV) }
        moodLayout.onClick { viewModel.navigate(R.id.moodFragment) }
    }
}

class CurrentScoreViewModel(
    useCase: GetFitDataUseCase,
    scoreTargetProvider: ScoreTargetProvider,
    scoreCalculator: ScoreCalculator
) : BaseViewModel() {

    private val stepsCount = MutableLiveData<Int>()
    private val sleepDuration = MutableLiveData<Duration>()
    private val activityDuration = MutableLiveData<Duration>()

    val stepsPercents = MutableLiveData<PercentData>()
    val sleepPercents = MutableLiveData<PercentData>()
    val activityPercents = MutableLiveData<PercentData>()

    val stepsScore = combineScoreData(
        stepsCount,
        scoreTargetProvider.getSteps()
    )
    val sleepScore = combineScoreData(
        sleepDuration.map { it.minutesTotal },
        scoreTargetProvider.getSleepDuration().map { it.minutesTotal }
    )
    val activityScore = combineScoreData(
        activityDuration.map { it.minutesTotal },
        scoreTargetProvider.getActivityDuration().map { it.minutesTotal }
    )

    val totalScore = scoreCalculator.calculate(stepsScore, sleepScore, activityScore)

    init {
        stepsCount.attachSource(useCase.getCurrentSteps()) { it.count }
        sleepDuration.attachSource(useCase.getCurrentSleep()) { it.total }
        activityDuration.attachSource(useCase.getCurrentActivity()) { it.total }
        combinePercentData(stepsPercents, useCase.getPercentSteps())
        combinePercentData(sleepPercents, useCase.getPercentSleep())
        combinePercentData(activityPercents, useCase.getPercentActivity())
    }
}