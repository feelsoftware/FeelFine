@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.fit.model.*
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentActivity
import com.feelsoftware.feelfine.fit.usecase.getPercentActivity
import com.feelsoftware.feelfine.score.ScoreTargetProvider
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import kotlinx.android.synthetic.main.fragment_activity_score.*
import kotlinx.android.synthetic.main.fragment_activity_score.backIV
import kotlinx.android.synthetic.main.fragment_activity_score.circularProgressBar
import kotlinx.android.synthetic.main.fragment_activity_score.scorePercentTV
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityScoreFragment :
    BaseFragment<ActivityScoreViewModel>(R.layout.fragment_activity_score) {

    override val viewModel: ActivityScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.activityInfo.observe {
            walkingTV.text = getString(
                R.string.activity_walking_duration, it.activityWalking.toHoursMinutes()
            )
            runningTV.text = getString(
                R.string.activity_running_duration, it.activityRunning.toHoursMinutes()
            )
            otherTV.text = getString(
                R.string.activity_other_duration, it.activityUnknown.toHoursMinutes()
            )
        }
        viewModel.percents.observe {
            scorePercentTV.applyPercentData(it)
        }
        viewModel.score.observe {
            val current = it.currentDuration.toHoursMinutes()
            val target = it.targetDuration.toHoursMinutes()
            activityText.text = getString(R.string.activity_score_text, current, target)
            circularProgressBar.progress = it.score
        }
        backIV.setOnClickListener { requireActivity().onBackPressed() }
    }
}

class ActivityScoreViewModel(
    useCase: GetFitDataUseCase,
    scoreTargetProvider: ScoreTargetProvider
) : BaseViewModel() {

    val activityInfo = MutableLiveData<ActivityInfo>()
    val percents = MutableLiveData<PercentData>()
    val score = combineScoreData(
        activityInfo.map { it.total.minutesTotal },
        scoreTargetProvider.getActivityDuration().map { it.minutesTotal }
    )

    init {
        activityInfo.attachSource(useCase.getCurrentActivity()) { it }
        combinePercentData(percents, useCase.getPercentActivity())
    }
}