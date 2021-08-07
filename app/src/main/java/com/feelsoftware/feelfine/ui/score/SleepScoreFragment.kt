@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.fit.model.*
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentSleep
import com.feelsoftware.feelfine.fit.usecase.getPercentSleep
import com.feelsoftware.feelfine.score.*
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import kotlinx.android.synthetic.main.fragment_sleep_score.*
import kotlinx.android.synthetic.main.fragment_sleep_score.backIV
import kotlinx.android.synthetic.main.fragment_sleep_score.totalScore
import kotlinx.android.synthetic.main.fragment_sleep_score.scorePercentTV
import org.koin.androidx.viewmodel.ext.android.viewModel

class SleepScoreFragment : BaseFragment<SleepScoreViewModel>(R.layout.fragment_sleep_score) {

    override val viewModel: SleepScoreViewModel by viewModel()

    override val statusBarColorResId: Int = R.color.lavenderTwo

    override fun onReady() {
        viewModel.sleepInfo.observe {
            awakeTV.text = getString(
                R.string.sleep_awake_duration, it.awake.toHoursMinutes()
            )
            lightSleepTV.text = getString(
                R.string.sleep_light_duration, it.lightSleep.toHoursMinutes()
            )
            deepSleepTV.text = getString(
                R.string.sleep_deep_duration, it.deepSleep.toHoursMinutes()
            )
            outOfBedTV.text = getString(
                R.string.sleep_out_of_bed_duration, it.outOfBed.toHoursMinutes()
            )
        }
        viewModel.percents.observe {
            scorePercentTV.applyPercentData(it)
        }
        viewModel.score.observe {
            val current = it.currentDuration?.toHoursMinutes() ?: "-"
            val target = it.targetDuration.toHoursMinutes()
            sleepText.text = getString(R.string.sleep_score_text, current, target)
            totalScore.progress = it.score
        }
        backIV.setOnClickListener { requireActivity().onBackPressed() }
    }
}

class SleepScoreViewModel(
    useCase: GetFitDataUseCase,
    scoreTargetProvider: ScoreTargetProvider
) : BaseViewModel() {

    val sleepInfo = MutableLiveData<SleepInfo>()
    val percents = MutableLiveData<PercentData>()
    val score = combineScoreData(
        sleepInfo.map { it.total.minutesTotal },
        scoreTargetProvider.getSleepDuration().map { it.minutesTotal }
    )

    init {
        sleepInfo.attachSource(useCase.getCurrentSleep()) { it }
        combinePercentData(percents, useCase.getPercentSleep())
    }
}