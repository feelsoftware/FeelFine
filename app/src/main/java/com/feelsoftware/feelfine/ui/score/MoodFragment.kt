package com.feelsoftware.feelfine.ui.score

import android.view.View
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import kotlinx.android.synthetic.main.fragment_mood.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoodFragment : BaseFragment<MoodViewModel>(R.layout.fragment_mood) {

    override val viewModel: MoodViewModel by viewModel()

    override fun onReady() {
        backIV.onClick { requireActivity().onBackPressed() }
        apathyIV.onClick { manageClick(apathyIV) }
        blameIV.onClick { manageClick(blameIV) }
        angryIV.onClick { manageClick(angryIV) }
        anxietyIV.onClick { manageClick(anxietyIV) }
        sedIV.onClick { manageClick(sedIV) }
        neutralityIV.onClick { manageClick(neutralityIV) }
        happyIV.onClick { manageClick(happyIV) }
        harmonyIV.onClick { manageClick(harmonyIV) }
    }

    // TODO save response to DB
    private fun manageClick(view: View) {

        when (view.id) {
            R.id.apathyIV -> {
                print("apathyIV")
            }
            R.id.blameIV -> {
                print("blameIV")
            }
            R.id.angryIV -> {
                print("angryIV")
            }
            R.id.anxietyIV -> {
            }
            R.id.sedIV -> {
            }
            R.id.neutralityIV -> {
            }
            R.id.calmIV -> {
            }
            R.id.happyIV -> {
            }
            R.id.harmonyIV -> {
            }
        }
        requireActivity().onBackPressed()
    }

}

class MoodViewModel : BaseViewModel()

