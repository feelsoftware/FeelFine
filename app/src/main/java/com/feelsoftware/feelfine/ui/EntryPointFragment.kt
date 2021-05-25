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
                    // Navigate to Dashboard screen
                    // navigate(R.id.toMainFragment)
                } else {
                    // Navigate to OnBoarding screen
                    // navigate(R.id.toMainFragment)
                }
            }).disposeOnInActive()
    }
}