package com.feelsoftware.feelfine.ui.score

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.Mood
import com.feelsoftware.feelfine.data.model.Optional
import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.data.usecase.GetCurrentMoodUseCase
import com.feelsoftware.feelfine.data.usecase.GetPercentMoodUseCase
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.fit.model.Duration
import com.feelsoftware.feelfine.fit.model.toHours
import com.feelsoftware.feelfine.fit.model.total
import com.feelsoftware.feelfine.fit.usecase.*
import com.feelsoftware.feelfine.score.*
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.ui.profile.DemoProfileBadge
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrentScoreFragment : BaseFragment<CurrentScoreViewModel>(R.layout.fragment_current_score) {

    override val viewModel: CurrentScoreViewModel by viewModel()

    // TODO: Replace with ViewBinding
    private inline val stepsPercentTV: TextView get() = requireView().findViewById(R.id.stepsPercentTV)
    private inline val sleepPercentTV: TextView get() = requireView().findViewById(R.id.sleepPercentTV)
    private inline val activityPercentTV: TextView get() = requireView().findViewById(R.id.activityPercentTV)
    private inline val moodPercentTV: TextView get() = requireView().findViewById(R.id.moodPercentTV)
    private inline val currentScore: CircularProgressBar get() = requireView().findViewById(R.id.currentScore)
    private inline val scoreTV: TextView get() = requireView().findViewById(R.id.scoreTV)
    private inline val stepsScore: CircularProgressBar get() = requireView().findViewById(R.id.stepsScore)
    private inline val stepsText: TextView get() = requireView().findViewById(R.id.stepsText)
    private inline val sleepScore: CircularProgressBar get() = requireView().findViewById(R.id.sleepScore)
    private inline val sleepText: TextView get() = requireView().findViewById(R.id.sleepText)
    private inline val activityScore: CircularProgressBar get() = requireView().findViewById(R.id.activityScore)
    private inline val activityText: TextView get() = requireView().findViewById(R.id.activityText)
    private inline val moodScore: CircularProgressBar get() = requireView().findViewById(R.id.moodScore)
    private inline val moodText: TextView get() = requireView().findViewById(R.id.moodText)
    private inline val demoLabel: DemoProfileBadge get() = requireView().findViewById(R.id.demoLabel)
    private inline val currentScoreTV: TextView get() = requireView().findViewById(R.id.currentScoreTV)
    private inline val stepLayout: View get() = requireView().findViewById(R.id.stepLayout)
    private inline val sleepLayout: View get() = requireView().findViewById(R.id.sleepLayout)
    private inline val activityLayout: View get() = requireView().findViewById(R.id.activityLayout)
    private inline val moodLayout: View get() = requireView().findViewById(R.id.moodLayout)

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
        viewModel.moodPercents.observe {
            moodPercentTV.applyPercentData(it)
        }

        viewModel.currentScore.observe {
            currentScore.progress = it
            scoreTV.text = getString(R.string.current_score, it)
        }
        viewModel.stepsScore.observe {
            stepsText.text = getString(
                R.string.current_score_steps,
                it.current?.toString() ?: "-",
                it.target.toString()
            )
            stepsScore.progress = it.score
        }
        viewModel.sleepScore.observe {
            sleepText.text = getString(
                R.string.current_score_sleep,
                it.currentDuration?.hours?.toString() ?: "-",
                it.targetDuration.toHours()
            )
            sleepScore.progress = it.score
        }
        viewModel.activityScore.observe {
            activityText.text = getString(
                R.string.current_score_activity,
                it.currentDuration?.hours?.toString() ?: "-",
                it.targetDuration.toHours()
            )
            activityScore.progress = it.score
        }
        viewModel.moodScore.observe {
            moodText.text = getString(
                R.string.current_score_mood,
                it.current?.toString() ?: "-",
                it.target.toString()
            )
            moodScore.progress = it.score
        }
        viewModel.userProfile.observe { profile ->
            demoLabel.isVisible = profile.isDemo
        }

        stepLayout.onClick { viewModel.navigate(R.id.stepScoreFragment) }
        sleepLayout.onClick { viewModel.navigate(R.id.sleepScoreFragment) }
        activityLayout.onClick { viewModel.navigate(R.id.activityScoreFragment) }
        currentScoreTV.onClick { AboutScoreWindow.show(requireView(), currentScoreTV) }
        moodLayout.onClick { viewModel.navigate(R.id.moodFragment) }
    }
}

class CurrentScoreViewModel(
    fitDataUseCase: GetFitDataUseCase,
    getCurrentMoodUseCase: GetCurrentMoodUseCase,
    getPercentMoodUseCase: GetPercentMoodUseCase,
    scoreTargetProvider: ScoreTargetProvider,
    scoreCalculator: ScoreCalculator,
    userRepository: UserRepository,
) : BaseViewModel() {

    private val stepsCount = MutableLiveData<Int>()
    private val sleepDuration = MutableLiveData<Duration>()
    private val activityDuration = MutableLiveData<Duration>()
    private val mood = MutableLiveData<Optional<Mood>>()

    val stepsPercents = MutableLiveData<PercentData>()
    val sleepPercents = MutableLiveData<PercentData>()
    val activityPercents = MutableLiveData<PercentData>()
    val moodPercents = MutableLiveData<PercentData>()

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
    val moodScore = combineScoreData(
        mood.map { optional ->
            if (optional.isPresent) {
                Optional.of(optional.value.intensity)
            } else {
                Optional.empty()
            }
        },
        scoreTargetProvider.getMood().map { it.intensity }
    )

    val currentScore = scoreCalculator.calculate(stepsScore, sleepScore, activityScore, moodScore)

    val userProfile = MutableLiveData<UserProfile>()

    init {
        stepsCount.attachSource(fitDataUseCase.getCurrentSteps()) { it.count }
        sleepDuration.attachSource(fitDataUseCase.getCurrentSleep()) { it.total }
        activityDuration.attachSource(fitDataUseCase.getCurrentActivity()) { it.total }
        mood.attachSource(getCurrentMoodUseCase().toObservable()) { it }
        combinePercentData(stepsPercents, fitDataUseCase.getPercentSteps())
        combinePercentData(sleepPercents, fitDataUseCase.getPercentSleep())
        combinePercentData(activityPercents, fitDataUseCase.getPercentActivity())
        combinePercentData(moodPercents, getPercentMoodUseCase())
        userProfile.attachSource(userRepository.getProfileLegacy()) { it }
    }
}