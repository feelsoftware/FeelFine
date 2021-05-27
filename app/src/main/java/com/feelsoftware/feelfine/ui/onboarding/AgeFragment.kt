package com.feelsoftware.feelfine.ui.onboarding

import android.widget.SeekBar
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick

import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import kotlinx.android.synthetic.main.fragment_age.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AgeFragment : BaseFragment<AgeViewModel>(R.layout.fragment_age) {

    override val viewModel: AgeViewModel by viewModel()

    override fun onReady() {
        getStartedB.onClick { viewModel.setAge(simpleSeekBar.progress) }
        simpleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                ageValue.text = progressChangedValue.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                getStartedB.isEnabled = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}

class AgeViewModel(
    private val flowManager: OnBoardingFlowManager
) : BaseViewModel() {
    fun setAge(age: Int) {
        flowManager.age = age
        // TODO navigate to the dashboard
    }
}