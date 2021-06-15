package com.feelsoftware.feelfine.ui.score

import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
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
            stepsView.text = "Steps\n$it"
        }
        viewModel.sleepData.observe {
            sleepView.text = "Sleep\n$it hours"
        }
        viewModel.activityData.observe {
            activityView.text = "Activity\n$it hours"
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
        stepsView.onClick { viewModel.navigate(R.id.toStepScoreFragment) }
        sleepView.onClick { viewModel.navigate(R.id.toSleepScoreFragment) }
        activityView.onClick { viewModel.navigate(R.id.toActivityScoreFragment) }
        // TODO temporary navigation
        moodView.onClick { viewModel.navigate(R.id.toStatisticFragment) }
    }

    private fun manageScorePercents(scoreView: TextView, scorePercent: Int) {
        if (scorePercent >= 0) {
            scoreView.text = "$scorePercent%"
            scoreView.background =
                AppCompatResources.getDrawable(requireContext(),R.drawable.outline_trending_up_24)
        } else {
            scoreView.text = "-$scorePercent%"
            scoreView.background =
            AppCompatResources.getDrawable(requireContext(),R.drawable.outline_trending_down_24)
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