package com.feelsoftware.feelfine.ui.onboarding

import android.widget.Button
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class NickNameFragment : BaseFragment<NickNameViewModel>(R.layout.fragment_nick_name) {

    override val viewModel: NickNameViewModel by viewModel()

    // TODO: Replace with ViewBinding
    private inline val nickIL: TextInputLayout get() = requireView().findViewById(R.id.nickIL)
    private inline val nickET: TextInputEditText get() = requireView().findViewById(R.id.nickET)
    private inline val continueB: Button get() = requireView().findViewById(R.id.continueB)

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