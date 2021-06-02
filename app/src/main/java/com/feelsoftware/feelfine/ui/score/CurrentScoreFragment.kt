package com.feelsoftware.feelfine.ui.score

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.fragment_current_score.*

class CurrentScoreFragment : BaseFragment<CurrentScoreViewModel>(R.layout.fragment_current_score) {

    override val viewModel: CurrentScoreViewModel by viewModel()

    override fun onReady() {
        stepsView.onClick { viewModel.navigate(R.id.toStepScoreFragment) }
        sleepView.onClick { viewModel.navigate(R.id.toSleepScoreFragment) }
        activityView.onClick { viewModel.navigate(R.id.toActivityScoreFragment) }
    }
}

class CurrentScoreViewModel() : BaseViewModel()