package com.feelsoftware.feelfine.ui.onboarding

import android.widget.SeekBar
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick

import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import kotlinx.android.synthetic.main.fragment_nick_name.continueB
import kotlinx.android.synthetic.main.fragment_wage.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeightFragment : BaseFragment<WeightViewModel>(R.layout.fragment_wage) {

    override val viewModel: WeightViewModel by viewModel()

    override fun onReady() {
        continueB.onClick { viewModel.setWeight(simpleSeekBar.progress)}
        simpleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                wageValue.text = progressChangedValue.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                continueB.isEnabled =true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
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