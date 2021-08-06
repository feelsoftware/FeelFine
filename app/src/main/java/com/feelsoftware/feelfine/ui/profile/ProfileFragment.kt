package com.feelsoftware.feelfine.ui.profile

import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.model.Duration
import com.feelsoftware.feelfine.fit.model.toHours
import com.feelsoftware.feelfine.score.ScoreTargetProvider
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ProfileFragment : BaseFragment<ProfileViewModel>(R.layout.fragment_profile) {

    override val viewModel: ProfileViewModel by viewModel()

    override fun onReady() {
        viewModel.userProfile.observe {
            nameTV.text = it.name
            wageTV.text = getString(R.string.user_weight_placeholder, it.weight.toString())
            ageTV.text = getString(R.string.user_age_placeholder, it.age.toString())
        }
        viewModel.stepsInfo.observe {
            stepsTV.text = it.toString()
        }
        viewModel.sleepInfo.observe {
            sleepTV.text = it.toHours()
        }
        viewModel.activityInfo.observe {
            activityTV.text = it.toHours()
        }

    }
}

class ProfileViewModel(scoreTargetProvider: ScoreTargetProvider, userRepo: UserRepository) :
    BaseViewModel() {

    var userProfile = MutableLiveData<UserProfile>()
    var stepsInfo = MutableLiveData<Int>()
    var sleepInfo = MutableLiveData<Duration>()
    var activityInfo = MutableLiveData<Duration>()

    init {
        scoreTargetProvider.getSteps()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                stepsInfo.value = it
            }, onError = {
                Timber.e(it, "Failed to fetch steps")
            })
            .disposeOnInActive()
        scoreTargetProvider.getSleepDuration()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                sleepInfo.value = it
            }, onError = {
                Timber.e(it, "Failed to sleep steps")
            })
            .disposeOnInActive()
        scoreTargetProvider.getActivityDuration()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = {
                activityInfo.value = it
            }, onError = {
                Timber.e(it, "Failed to activity steps")
            })
            .disposeOnInActive()
        userProfile.attachSource(userRepo.getProfile()) { it }
    }
}