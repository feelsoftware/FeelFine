@file:SuppressLint("SetTextI18n")

package com.feelsoftware.feelfine.ui.score

import android.annotation.SuppressLint
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
            scorePercentTV.applyPercentData(it)
        }
    }

}

class StepScoreViewModel(useCase: GetFitDataUseCase) : BaseViewModel() {

    val stepsData = MutableLiveData<StepsInfo>()
    val stepsPercents = MutableLiveData<PercentData>()

    init {
        useCase.getCurrentSteps()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = {
                stepsData.value = it
            }).disposeOnInActive()
        managePercentData(useCase.getPercentSteps(), stepsPercents).disposeOnInActive()
    }
}