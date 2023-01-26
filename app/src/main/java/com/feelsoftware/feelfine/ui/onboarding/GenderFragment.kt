package com.feelsoftware.feelfine.ui.onboarding

import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val MALE = 1
private const val FEMALE = 2

class GenderFragment : BaseFragment<GenderViewModel>(R.layout.fragment_gender) {

    override val viewModel: GenderViewModel by viewModel()

    // TODO: Replace with ViewBinding
    private inline val maleBackgroundLL: LinearLayout get() = requireView().findViewById(R.id.maleBackgroundLL)
    private inline val femaleBackgroundLL: LinearLayout get() = requireView().findViewById(R.id.femaleBackgroundLL)
    private inline val continueB: Button get() = requireView().findViewById(R.id.continueB)

    override fun onReady() {
        var genderSelected = 0
        maleBackgroundLL.setOnClickListener {
            if (genderSelected == MALE) return@setOnClickListener
            genderSelected = MALE
            continueB.isEnabled = true
            maleBackgroundLL.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.button_purple_rounded,
                requireContext().theme
            )
            femaleBackgroundLL.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.button_purple_disabled,
                requireContext().theme
            )
        }

        femaleBackgroundLL.setOnClickListener {
            continueB.isEnabled = true
            if (genderSelected == FEMALE) return@setOnClickListener
            genderSelected = FEMALE
            femaleBackgroundLL.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.button_purple_rounded,
                requireContext().theme
            )
            maleBackgroundLL.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.button_purple_disabled,
                requireContext().theme
            )
        }

        continueB.onClick { viewModel.setGender(genderSelected) }
    }

}

class GenderViewModel(
    private val flowManager: OnBoardingFlowManager
) : BaseViewModel() {
    fun setGender(genderSelected: Int) {
        flowManager.gender =
            if (genderSelected == MALE) UserProfile.Gender.MALE else UserProfile.Gender.FEMALE
        navigate(R.id.toWeightFragment)
    }
}