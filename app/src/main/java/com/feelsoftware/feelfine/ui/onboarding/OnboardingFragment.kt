package com.feelsoftware.feelfine.ui.onboarding

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick

import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.EmptyViewModel
import kotlinx.android.synthetic.main.fragment_onboarding.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingFragment : BaseFragment<EmptyViewModel>(R.layout.fragment_onboarding) {

    override val viewModel: EmptyViewModel by viewModel()

    override fun onReady() {
        registerB.onClick {  viewModel.navigate(R.id.toNickNameFragment) }
    }
}
