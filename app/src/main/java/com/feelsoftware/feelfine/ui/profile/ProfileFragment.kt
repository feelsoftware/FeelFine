package com.feelsoftware.feelfine.ui.profile

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseFragment<ProfileViewModel>(R.layout.fragment_profile) {

    override val viewModel: ProfileViewModel by viewModel()

    override fun onReady() {}
}

class ProfileViewModel(useCase: GetFitDataUseCase) : BaseViewModel() {

}