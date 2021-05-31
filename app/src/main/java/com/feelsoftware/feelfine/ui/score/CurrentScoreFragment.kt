package com.feelsoftware.feelfine.ui.score

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrentScoreFragment : BaseFragment<CurrentScoreViewModel>(R.layout.fragment_current_score) {

    override val viewModel: CurrentScoreViewModel by viewModel()

    override fun onReady() {
        //TODO("Not yet implemented")
    }
}

class CurrentScoreViewModel() : BaseViewModel()