package com.feelsoftware.feelfine.ui.welcome

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.base.BaseComposeFragment

class WelcomeFragment : BaseComposeFragment() {

    @get:ColorRes
    override val statusBarColorResId: Int = R.color.white

    @Composable
    override fun Content() {
        WelcomeContent(
            onContinueClick = {
                this.viewModel.navigate(R.id.toOnboardingFragment)
            },
        )
    }
}
