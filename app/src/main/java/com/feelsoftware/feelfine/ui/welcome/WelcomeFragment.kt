package com.feelsoftware.feelfine.ui.welcome

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.base.BaseComposeFragment
import com.feelsoftware.feelfine.ui.base.EmptyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeFragment : BaseComposeFragment() {

    @get:ColorRes
    override val statusBarColorResId: Int = R.color.white

    @Composable
    override fun Content() {
        val viewModel: EmptyViewModel by remember { viewModel() }
        WelcomeContent(
            onContinueClick = {
                viewModel.navigate(R.id.toNickNameFragment)
            }
        )
    }
}
