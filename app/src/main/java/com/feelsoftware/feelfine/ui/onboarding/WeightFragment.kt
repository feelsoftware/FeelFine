package com.feelsoftware.feelfine.ui.onboarding

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeightFragment : BaseFragment<WeightViewModel>(R.layout.fragment_wage) {

    override val viewModel: WeightViewModel by viewModel()

    private val wageArgsList = (0..150).toList().toTypedArray()
    private var currentScore = wageArgsList[wageArgsList.size / 2]

    // TODO: Replace with ViewBinding
    private inline val startTV: TextView get() = requireView().findViewById(R.id.startTV)
    private inline val middleTV: TextView get() = requireView().findViewById(R.id.middleTV)
    private inline val endTV: TextView get() = requireView().findViewById(R.id.endTV)
    private inline val startIcon: ImageView get() = requireView().findViewById(R.id.startIcon)
    private inline val endIcon: ImageView get() = requireView().findViewById(R.id.endIcon)
    private inline val continueB: Button get() = requireView().findViewById(R.id.continueB)

    private fun manageWeight() {
        startTV.text = wageArgsList[currentScore - 1].toString()
        middleTV.text = wageArgsList[currentScore].toString()
        endTV.text = wageArgsList[currentScore + 1].toString()
    }

    override fun onReady() {
        continueB.onClick { viewModel.setWeight(currentScore) }
        manageWeight()
        startIcon.setOnClickListener {
            if (currentScore > 1) {
                currentScore--
                manageWeight()
            }
        }
        endIcon.setOnClickListener {
            if (currentScore < wageArgsList.size - 2) {
                currentScore++
                manageWeight()
            }
        }
    }
}

class WeightViewModel(
    private val flowManager: OnBoardingFlowManager
) : BaseViewModel() {
    fun setWeight(wage: Int) {
        flowManager.weight = wage
        navigate(R.id.toAgeFragment)
    }
}