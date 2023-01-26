package com.feelsoftware.feelfine.ui.onboarding

import android.widget.Button
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.EmptyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingFragment : BaseFragment<EmptyViewModel>(R.layout.fragment_onboarding) {

    override val viewModel: EmptyViewModel by viewModel()

    // TODO: Replace with ViewBinding
    private inline val registerB: Button get() = requireView().findViewById(R.id.registerB)

    override fun onReady() {
        registerB.onClick { viewModel.navigate(R.id.toNickNameFragment) }
    }
}
