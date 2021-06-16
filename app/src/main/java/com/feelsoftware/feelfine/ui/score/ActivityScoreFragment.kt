package com.feelsoftware.feelfine.ui.score

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.model.ActivityInfo
import com.feelsoftware.feelfine.fit.model.toHours
import com.feelsoftware.feelfine.fit.model.toHoursMinutes
import com.feelsoftware.feelfine.fit.model.total
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentActivity
import com.feelsoftware.feelfine.fit.usecase.getPercentActivity
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_activity_score.*
import kotlinx.android.synthetic.main.fragment_activity_score.scoreView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityScoreFragment :
    BaseFragment<ActivityScoreViewModel>(R.layout.fragment_activity_score) {

    override val viewModel: ActivityScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.activityData.observe {
            scoreView.text = "Activity\n" + it.total.toHours()
            walkingTV.text = "Walking: " + it.activityWalking.toHoursMinutes()
            runningTV.text = "Running: " + it.activityRunning.toHoursMinutes()
            otherTV.text = "Other: " + it.activityUnknown.toHoursMinutes()
        }
        viewModel.activityPercents.observe {
            scorePercentTV.text = it.first
            scorePercentTV.background = it.second
        }
    }
}

class ActivityScoreViewModel(context: Context, useCase: GetFitDataUseCase) : BaseViewModel() {

    val activityData = MutableLiveData<ActivityInfo>()
    val activityPercents = MutableLiveData<Pair<String, Drawable?>>()

    init {
        useCase.getCurrentActivity()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                activityData.value = it
            }).disposeOnInActive()
        useCase.getPercentActivity()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = { percent ->
                val text = if (percent >= 0) "+$percent%" else "$percent%"
                val drawableResId =
                    if (percent >= 0) R.drawable.outline_trending_up_24 else R.drawable.outline_trending_down_24
                activityPercents.value =
                    text to AppCompatResources.getDrawable(context, drawableResId)
            }).disposeOnInActive()
    }
}