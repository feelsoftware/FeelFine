package com.feelsoftware.feelfine.ui.score

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.model.StepsInfo
import com.feelsoftware.feelfine.fit.model.calories
import com.feelsoftware.feelfine.fit.model.distance
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentSteps
import com.feelsoftware.feelfine.fit.usecase.getPercentSteps
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_step_score.*
import kotlinx.android.synthetic.main.fragment_step_score.scoreView
import org.koin.androidx.viewmodel.ext.android.viewModel

class StepScoreFragment : BaseFragment<StepScoreViewModel>(R.layout.fragment_step_score) {

    override val viewModel: StepScoreViewModel by viewModel()

    override fun onReady() {
        viewModel.stepsData.observe {
            scoreView.text = "Steps\n${it.count}"
            caloriesTV.text = "Burned: " + it.calories + " kkal"
            distanceTV.text = "Distance: " + it.distance + " kilometers"
        }
        viewModel.stepsPercents.observe {
            scorePercentTV.text = it.first
            scorePercentTV.background = it.second
        }
    }

}

class StepScoreViewModel(context: Context, useCase: GetFitDataUseCase) : BaseViewModel() {
    val stepsData = MutableLiveData<StepsInfo>()
    val stepsPercents = MutableLiveData<Pair<String, Drawable?>>()

    init {
        useCase.getCurrentSteps()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                stepsData.value = it
            }).disposeOnInActive()
        useCase.getPercentSteps()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = { percent ->
                val text = if (percent >= 0) "+$percent%" else "$percent%"
                val drawableResId =
                    if (percent >= 0) R.drawable.outline_trending_up_24 else R.drawable.outline_trending_down_24
                stepsPercents.value = text to AppCompatResources.getDrawable(context, drawableResId)
            }).disposeOnInActive()
    }
}