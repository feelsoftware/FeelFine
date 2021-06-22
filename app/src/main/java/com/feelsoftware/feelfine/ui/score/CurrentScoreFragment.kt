package com.feelsoftware.feelfine.ui.score

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.model.total
import com.feelsoftware.feelfine.fit.usecase.*
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.fragment_current_score.*

class CurrentScoreFragment : BaseFragment<CurrentScoreViewModel>(R.layout.fragment_current_score) {

    override val viewModel: CurrentScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.stepsData.observe {
            stepsText.text = "$it /15000 steps"
        }
        viewModel.sleepData.observe {
            sleepText.text = "$it /8 hours"
        }
        viewModel.activityData.observe {
            activityText.text = "$it /4 hours"
        }

        viewModel.stepsPercents.observe {
            manageScorePercents(stepsPercentTV, it)
        }
        viewModel.sleepPercents.observe {
            manageScorePercents(sleepPercentTV, it)
        }
        viewModel.activityPercents.observe {
            manageScorePercents(activityPercentTV, it)
        }
        stepLayout.onClick { viewModel.navigate(R.id.toStepScoreFragment) }
        sleepLayout.onClick { viewModel.navigate(R.id.toSleepScoreFragment) }
        activityLayout.onClick { viewModel.navigate(R.id.toActivityScoreFragment) }
        // TODO temporary navigation
        moodLayout.onClick { viewModel.navigate(R.id.toStatisticFragment) }
    }

    private fun manageScorePercents(scoreView: TextView, scorePercent: Int) {
        if (scorePercent >= 0) {
            scoreView.text = "$scorePercent%"
        } else {
            scoreView.text = "$scorePercent%"
        }
    }
}

class CurrentScoreViewModel(
    useCase: GetFitDataUseCase
) : BaseViewModel() {

    val stepsData = MutableLiveData<String>()
    val sleepData = MutableLiveData<String>()
    val activityData = MutableLiveData<String>()
    val stepsPercents = MutableLiveData<Int>()
    val sleepPercents = MutableLiveData<Int>()
    val activityPercents = MutableLiveData<Int>()

    init {
        useCase.getCurrentSteps()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                stepsData.value = it.count.toString()
            }).disposeOnInActive()
        useCase.getCurrentSleep()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                sleepData.value = it.total.hours.toString()
            }).disposeOnInActive()
        useCase.getCurrentActivity()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                activityData.value = it.total.hours.toString()
            }).disposeOnInActive()
        useCase.getPercentSteps()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                stepsPercents.value = it
            }).disposeOnInActive()
        useCase.getPercentSleep()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                sleepPercents.value = it
            }).disposeOnInActive()
        useCase.getPercentSteps()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                stepsPercents.value = it
            }).disposeOnInActive()
        useCase.getPercentActivity()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                activityPercents.value = it
            }).disposeOnInActive()
    }
}