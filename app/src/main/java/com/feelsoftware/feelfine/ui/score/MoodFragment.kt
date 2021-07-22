package com.feelsoftware.feelfine.ui.score

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.Mood
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import kotlinx.android.synthetic.main.fragment_mood.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoodFragment : BaseFragment<MoodViewModel>(R.layout.fragment_mood) {

    override val viewModel: MoodViewModel by viewModel()

    override fun onReady() {
        backIV.onClick { requireActivity().onBackPressed() }
        apathyIV.onClick { onMoodClicked(Mood.APATHY) }
        blameIV.onClick { onMoodClicked(Mood.BLAME) }
        angryIV.onClick { onMoodClicked(Mood.ANGRY) }
        anxietyIV.onClick { onMoodClicked(Mood.ANXIETY) }
        sedIV.onClick { onMoodClicked(Mood.SED) }
        neutralityIV.onClick { onMoodClicked(Mood.NEUTRALITY) }
        calmIV.onClick { onMoodClicked(Mood.CALM) }
        happyIV.onClick { onMoodClicked(Mood.HAPPY) }
        harmonyIV.onClick { onMoodClicked(Mood.HARMONY) }
    }

    private fun onMoodClicked(mood: Mood) {
        viewModel.setMood(mood)
        requireActivity().onBackPressed()
    }

}

class MoodViewModel : BaseViewModel() {

    // TODO save mood
    fun setMood(mood: Mood) {

    }

}

