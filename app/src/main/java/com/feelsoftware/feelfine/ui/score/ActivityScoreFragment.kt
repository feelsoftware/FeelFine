package com.feelsoftware.feelfine.ui.score

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityScoreFragment : BaseFragment<ActivityScoreViewModel>(R.layout.fragment_activity_score) {

    override val viewModel: ActivityScoreViewModel by viewModel()

    override fun onReady() {
        //TODO("Not yet implemented")
    }
}

class ActivityScoreViewModel() : BaseViewModel()