package com.feelsoftware.feelfine.ui

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.ui.base.EmptyViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_number.*

class MainFragment : BaseFragment<MainViewModel>(R.layout.fragment_main) {

    override val viewModel: MainViewModel by viewModels()

    override fun onReady() {
        viewModel.profile.observe {
            tvProfile.text = it.toString()
        }

        btnNavigate.onClick {
            val number = editNumber.text?.toString()?.toIntOrNull() ?: return@onClick

            viewModel.navigate(
                R.id.toTempFragment,
                NumberFragmentArgs.Builder(number).build().toBundle()
            )
        }
    }
}

class MainViewModel : BaseViewModel() {

    val profile = MutableLiveData<UserProfile>()
}

class NumberFragment : BaseFragment<EmptyViewModel>(R.layout.fragment_number) {

    override val viewModel: EmptyViewModel by viewModels()

    private val args: NumberFragmentArgs by navArgs()

    @SuppressLint("SetTextI18n")
    override fun onReady() {
        textView.text = "Number #${args.number}"
    }
}