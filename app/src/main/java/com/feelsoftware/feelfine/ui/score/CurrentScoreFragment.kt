package com.feelsoftware.feelfine.ui.score

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
        userProfile.attachSource(userRepository.getProfile()) { it }
    }
}