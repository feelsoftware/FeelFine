package com.feelsoftware.feelfine.ui.onboarding

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import kotlinx.android.synthetic.main.fragment_age.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AgeFragment : BaseFragment<AgeViewModel>(R.layout.fragment_age) {

    override val viewModel: AgeViewModel by viewModel()

    private val today = Calendar.getInstance()

    override fun onReady() {
        var selectedYear = 0
        calendarView.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { _, year, _, _ ->
            selectedYear = year
        }

        getStartedB.onClick { viewModel.setAge(selectedYear) }
    }
}

class AgeViewModel(
    private val flowManager: OnBoardingFlowManager
) : BaseViewModel() {

    fun setAge(age: Int) {
        flowManager.age = Calendar.getInstance().get(Calendar.YEAR) - age
        flowManager.markAsPassed(flowManager.buildUserProfile() ?: return)
            .subscribeBy(onComplete = {
                navigate(R.id.toHomeFragment)
            }, onError = {
                print("error")
            })
    }
}