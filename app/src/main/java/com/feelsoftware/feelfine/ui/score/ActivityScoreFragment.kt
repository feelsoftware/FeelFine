@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.model.*
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentActivity
import com.feelsoftware.feelfine.fit.usecase.getPercentActivity
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_activity_score.*
import kotlinx.android.synthetic.main.fragment_activity_score.backIV
import kotlinx.android.synthetic.main.fragment_activity_score.circularProgressBar
import kotlinx.android.synthetic.main.fragment_activity_score.scorePercentTV
import org.koin.androidx.viewmodel.ext.android.viewModel


class ActivityScoreFragment :
    BaseFragment<ActivityScoreViewModel>(R.layout.fragment_activity_score) {

    override val viewModel: ActivityScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.activityData.observe {
            circularProgressBar.progress = it.total.toIntMinutes().applyScore(8*60)
            stepsText.text = resources.getString(R.string.activity_placeholder, it.total.toHours())
            walkingTV.text = "Walking: " + it.activityWalking.toHoursMinutes()
            runningTV.text = "Running: " + it.activityRunning.toHoursMinutes()
            otherTV.text = "Other: " + it.activityUnknown.toHoursMinutes()
        }
        viewModel.activityPercents.observe {
            scorePercentTV.applyPercentData(it)
        }
        backIV.setOnClickListener { requireActivity().onBackPressed() }
    }
}

class ActivityScoreViewModel(useCase: GetFitDataUseCase) : BaseViewModel() {

    val activityData = MutableLiveData<ActivityInfo>()
    val activityPercents = MutableLiveData<PercentData>()

    init {
        useCase.getCurrentActivity()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = {
                activityData.value = it
            }).disposeOnInActive()
        managePercentData(useCase.getPercentActivity(), activityPercents).disposeOnInActive()
    }
}