package com.feelsoftware.feelfine.ui.onboarding

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick

import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import kotlinx.android.synthetic.main.fragment_nick_name.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.widget.doAfterTextChanged

class NickNameFragment : BaseFragment<NickNameViewModel>(R.layout.fragment_nick_name) {

    override val viewModel: NickNameViewModel by viewModel()

    override fun onReady() {
        continueB.onClick { viewModel.setNickName(nickET.text.toString()) }
        nickET.doAfterTextChanged {
            if (nickET.text.toString().isEmpty()) {
                continueB.isEnabled = false
                nickIL.error = getString(R.string.nick_fill_error)
                nickIL.isErrorEnabled = true
            } else {
                continueB.isEnabled = true
                nickIL.isErrorEnabled = false
            }
        }
    }
}

class NickNameViewModel(
    private val flowManager: OnBoardingFlowManager
) : BaseViewModel() {

    fun setNickName(nick: String) {
        flowManager.name = nick
        navigate(R.id.toGenderFragment)
    }
}