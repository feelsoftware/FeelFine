package com.feelsoftware.feelfine.ui.score

import android.widget.ImageView
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.Mood
import com.feelsoftware.feelfine.data.usecase.SetMoodUseCase
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.MoodTracker
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MoodFragment : BaseFragment<MoodViewModel>(R.layout.fragment_mood) {

    override val viewModel: MoodViewModel by viewModel()

    override val statusBarColorResId: Int = R.color.sandTwo

    // TODO: Replace with ViewBinding
    private inline val backIV: ImageView get() = requireView().findViewById(R.id.backIV)
    private inline val apathyIV: ImageView get() = requireView().findViewById(R.id.apathyIV)
    private inline val blameIV: ImageView get() = requireView().findViewById(R.id.blameIV)
    private inline val angryIV: ImageView get() = requireView().findViewById(R.id.angryIV)
    private inline val anxietyIV: ImageView get() = requireView().findViewById(R.id.anxietyIV)
    private inline val sedIV: ImageView get() = requireView().findViewById(R.id.sedIV)
    private inline val neutralityIV: ImageView get() = requireView().findViewById(R.id.neutralityIV)
    private inline val calmIV: ImageView get() = requireView().findViewById(R.id.calmIV)
    private inline val happyIV: ImageView get() = requireView().findViewById(R.id.happyIV)
    private inline val harmonyIV: ImageView get() = requireView().findViewById(R.id.harmonyIV)

    override fun onReady() {
        backIV.onClick { navigateBack() }
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
    }
}

class MoodViewModel(
    moodTracker: MoodTracker,
    private val setMoodUseCase: SetMoodUseCase,
) : BaseViewModel() {

    init {
        moodTracker.checkPermission()
    }

    fun setMood(mood: Mood) {
        setMoodUseCase(mood)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onComplete = {
                navigateBack()
            }, onError = {
                Timber.e(it, "Failed to set mood $mood")
            })
            .disposeOnInActive()
    }
}

