package com.feelsoftware.feelfine.ui.score

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SleepScoreFragment : BaseFragment<SleepScoreViewModel>(R.layout.fragment_sleep_score) {

    override val viewModel: SleepScoreViewModel by viewModel()

    override fun onReady() {
        //TODO("Not yet implemented")
    }
}

class SleepScoreViewModel() : BaseViewModel()