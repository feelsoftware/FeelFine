package com.feelsoftware.feelfine.ui

import androidx.lifecycle.viewModelScope
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.ui.onboarding.usecase.IsOnboardingCompletedUseCase
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EntryPointFragment : BaseFragment<EntryPointViewModel>(R.layout.fragment_entry_point) {

    override val viewModel: EntryPointViewModel by viewModel()

    override fun onReady() {}
}

class EntryPointViewModel(
    private val isOnboardingCompleted: IsOnboardingCompletedUseCase,
) : BaseViewModel() {

    init {
        viewModelScope.launch {
            if (isOnboardingCompleted()) {
                navigate(R.id.toHomeFragment)
            } else {
                navigate(R.id.toWelcomeFragment)
            }
        }
    }
}