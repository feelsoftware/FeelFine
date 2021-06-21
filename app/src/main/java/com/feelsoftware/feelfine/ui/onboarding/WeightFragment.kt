package com.feelsoftware.feelfine.ui.onboarding

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick

import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import kotlinx.android.synthetic.main.fragment_nick_name.continueB
import kotlinx.android.synthetic.main.fragment_wage.endIcon
import kotlinx.android.synthetic.main.fragment_wage.endTV
import kotlinx.android.synthetic.main.fragment_wage.middleTV
import kotlinx.android.synthetic.main.fragment_wage.startIcon
import kotlinx.android.synthetic.main.fragment_wage.startTV
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeightFragment : BaseFragment<WeightViewModel>(R.layout.fragment_wage) {

    override val viewModel: WeightViewModel by viewModel()

    private val wageArgsList = (0..150).toList().toTypedArray()
    private var currentScore = wageArgsList[wageArgsList.size / 2]

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