package com.feelsoftware.feelfine.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.base.BaseComposeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class OnboardingFragment : BaseComposeFragment() {

    @Composable
    override fun Content() {
        val viewModel: OnboardingViewModel by remember { viewModel() }
        val step by viewModel.step.collectAsState()
        val index by viewModel.stepIndex.collectAsState()
        val totalSteps by viewModel.stepsCount.collectAsState()
        val nextStepEnabled by viewModel.nextStepEnabled.collectAsState()

        OnboardingContent(
            step = step,
            stepIndex = index,
            stepsCount = totalSteps,
            onStepDataChange = viewModel::updateStepData,
            nextStepEnabled = nextStepEnabled,
            onNextStepClick = viewModel::nextStep,
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