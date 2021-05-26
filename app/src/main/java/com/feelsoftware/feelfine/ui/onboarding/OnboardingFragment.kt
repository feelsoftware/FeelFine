package com.feelsoftware.feelfine.ui

import com.feelsoftware.feelfine.R

import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingFragment : BaseFragment<OnboardingViewModel>(R.layout.fragment_onboarding) {

    override val viewModel: OnboardingViewModel by viewModel()

    override fun onReady() {}
}

class OnboardingViewModel(
    private val flowManager: OnBoardingFlowManager
) : BaseViewModel() {

    override fun onActive() {
       // TODO
    }
}