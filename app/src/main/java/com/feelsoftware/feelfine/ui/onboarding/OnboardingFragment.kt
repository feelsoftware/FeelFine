package com.feelsoftware.feelfine.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.base.BaseComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingFragment : BaseComposeFragment() {

    @Composable
    override fun Content() {
        val viewModel: OnboardingViewModel by remember { viewModel() }
        val step by viewModel.step.collectAsState()
        OnboardingContent(
            step = step,
            onNextStepClick = viewModel::nextStep
        )

        val backHandlerEnabled by viewModel.backHandlerEnabled.collectAsState()
        BackHandler(
            enabled = backHandlerEnabled,
            onBack = viewModel::previousStep
        )

        val onFinish by viewModel.onFinish.collectAsState()
        if (onFinish) {
            this.viewModel.navigate(R.id.toHomeFragment)
        }
    }
}