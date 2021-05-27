package com.feelsoftware.feelfine.ui.onboarding

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.extension.onClick

import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import kotlinx.android.synthetic.main.fragment_gender.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GenderFragment : BaseFragment<GenderViewModel>(R.layout.fragment_gender) {

    override val viewModel: GenderViewModel by viewModel()

    override fun onReady() {
        continueB.onClick { viewModel.setGender(male.isChecked) }
    }
}

class GenderViewModel(
    private val flowManager: OnBoardingFlowManager
) : BaseViewModel() {
    fun setGender(isMale: Boolean) {
        flowManager.gender = if (isMale) UserProfile.Gender.MALE else UserProfile.Gender.FEMALE
        navigate(R.id.toWeightFragment)
    }
}