package com.feelsoftware.feelfine.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.UiComposable
import androidx.compose.ui.platform.ComposeView
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseComposeFragment : BaseFragment<EmptyViewModel>(R.layout.fragment_compose) {

    override val viewModel: EmptyViewModel by viewModel()

    final override fun onReady() {
        (view as ComposeView).setContent {
            FeelFineTheme {
                Content()
            }
        }
    }

    @Composable
    @UiComposable
    abstract fun Content()
}