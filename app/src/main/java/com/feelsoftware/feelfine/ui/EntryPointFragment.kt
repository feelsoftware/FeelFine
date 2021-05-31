package com.feelsoftware.feelfine.ui

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class EntryPointFragment : BaseFragment<EntryPointViewModel>(R.layout.fragment_entry_point) {

    override val viewModel: EntryPointViewModel by viewModel()

    override fun onReady() {}
}

class EntryPointViewModel(
    private val flowManager: OnBoardingFlowManager
) : BaseViewModel() {

    override fun onActive() {
        flowManager.isPassed()
            .subscribeBy(onSuccess = { isPassed ->
                if (isPassed) {
                navigate(R.id.toCurrentScoreFragment)
                } else {
                     navigate(R.id.toOnboarding)
                }
            }).disposeOnInActive()
    }
}