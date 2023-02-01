@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.fit.model.*
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentActivity
import com.feelsoftware.feelfine.fit.usecase.getPercentActivity
import com.feelsoftware.feelfine.score.*
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.ui.profile.DemoProfileBadge
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityScoreFragment :
    BaseFragment<ActivityScoreViewModel>(R.layout.fragment_activity_score) {

    override val viewModel: ActivityScoreViewModel by viewModel()

    override val statusBarColorResId: Int = R.color.pastelPink

    // TODO: Replace with ViewBinding
    private inline val walkingTV: TextView get() = requireView().findViewById(R.id.walkingTV)
    private inline val runningTV: TextView get() = requireView().findViewById(R.id.runningTV)
    private inline val otherTV: TextView get() = requireView().findViewById(R.id.otherTV)
    private inline val scorePercentTV: TextView get() = requireView().findViewById(R.id.scorePercentTV)
    private inline val activityText: TextView get() = requireView().findViewById(R.id.activityText)
    private inline val totalScore: CircularProgressBar get() = requireView().findViewById(R.id.totalScore)
    private inline val demoLabel: DemoProfileBadge get() = requireView().findViewById(R.id.demoLabel)
    private inline val backIV: ImageView get() = requireView().findViewById(R.id.backIV)

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
            val current = it.currentDuration?.toHoursMinutes() ?: "-"
            val target = it.targetDuration.toHoursMinutes()
            activityText.text = getString(R.string.activity_score_text, current, target)
            totalScore.progress = it.score
        }
        viewModel.userProfile.observe { profile ->
            demoLabel.isVisible = profile.isDemo
        }

        backIV.setOnClickListener { requireActivity().onBackPressed() }
    }
}

class ActivityScoreViewModel(
    useCase: GetFitDataUseCase,
    scoreTargetProvider: ScoreTargetProvider,
    userRepository: UserRepository,
) : BaseViewModel() {

    val activityInfo = MutableLiveData<ActivityInfo>()
    val percents = MutableLiveData<PercentData>()
    val score = combineScoreData(
        activityInfo.map { it.total.minutesTotal },
        scoreTargetProvider.getActivityDuration().map { it.minutesTotal }
    )

    val userProfile = MutableLiveData<UserProfile>()

    init {
        activityInfo.attachSource(useCase.getCurrentActivity()) { it }
        combinePercentData(percents, useCase.getPercentActivity())
        userProfile.attachSource(userRepository.getProfileLegacy()) { it }
    }
}