package com.feelsoftware.feelfine.ui.score

import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.fit.usecase.getCurrentSteps
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

        stepsView.onClick { viewModel.navigate(R.id.toStepScoreFragment) }
        sleepView.onClick { viewModel.navigate(R.id.toSleepScoreFragment) }
        activityView.onClick { viewModel.navigate(R.id.toActivityScoreFragment) }
        // TODO temporary navigation
        moodView.onClick { viewModel.navigate(R.id.toStatisticFragment) }
    }
}

class CurrentScoreViewModel(
    useCase: GetFitDataUseCase
) : BaseViewModel() {

    val stepsData = MutableLiveData<String>()
    val sleepData = MutableLiveData<String>()
    val activityData = MutableLiveData<String>()

    init {
        useCase.getCurrentSteps()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                stepsData.value = it.count.toString()
            }).disposeOnInActive()
    }
}