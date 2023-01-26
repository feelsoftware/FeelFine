@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.fit.model.*
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentSleep
import com.feelsoftware.feelfine.fit.usecase.getPercentSleep
import com.feelsoftware.feelfine.score.*
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.ui.profile.DemoProfileBadge
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import org.koin.androidx.viewmodel.ext.android.viewModel

class SleepScoreFragment : BaseFragment<SleepScoreViewModel>(R.layout.fragment_sleep_score) {

    override val viewModel: SleepScoreViewModel by viewModel()

    override val statusBarColorResId: Int = R.color.lavenderTwo

    // TODO: Replace with ViewBinding
    private inline val awakeTV: TextView get() = requireView().findViewById(R.id.awakeTV)
    private inline val lightSleepTV: TextView get() = requireView().findViewById(R.id.lightSleepTV)
    private inline val deepSleepTV: TextView get() = requireView().findViewById(R.id.deepSleepTV)
    private inline val outOfBedTV: TextView get() = requireView().findViewById(R.id.outOfBedTV)
    private inline val scorePercentTV: TextView get() = requireView().findViewById(R.id.scorePercentTV)
    private inline val sleepText: TextView get() = requireView().findViewById(R.id.sleepText)
    private inline val totalScore: CircularProgressBar get() = requireView().findViewById(R.id.totalScore)
    private inline val demoLabel: DemoProfileBadge get() = requireView().findViewById(R.id.demoLabel)
    private inline val backIV: ImageView get() = requireView().findViewById(R.id.backIV)

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
        viewModel.userProfile.observe { profile ->
            demoLabel.isVisible = profile.isDemo
        }

        backIV.setOnClickListener { requireActivity().onBackPressed() }
    }
}

class SleepScoreViewModel(
    useCase: GetFitDataUseCase,
    scoreTargetProvider: ScoreTargetProvider,
    userRepository: UserRepository,
) : BaseViewModel() {

    val sleepInfo = MutableLiveData<SleepInfo>()
    val percents = MutableLiveData<PercentData>()
    val score = combineScoreData(
        sleepInfo.map { it.total.minutesTotal },
        scoreTargetProvider.getSleepDuration().map { it.minutesTotal }
    )

    val userProfile = MutableLiveData<UserProfile>()

    init {
        sleepInfo.attachSource(useCase.getCurrentSleep()) { it }
        combinePercentData(percents, useCase.getPercentSleep())
        userProfile.attachSource(userRepository.getProfile()) { it }
    }
}