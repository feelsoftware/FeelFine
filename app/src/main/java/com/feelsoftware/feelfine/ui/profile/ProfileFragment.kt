package com.feelsoftware.feelfine.ui.profile

import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.db.AppDatabase
import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.FitPermissionManager
import com.feelsoftware.feelfine.fit.model.Duration
import com.feelsoftware.feelfine.fit.model.toHours
import com.feelsoftware.feelfine.score.ScoreTargetProvider
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseFragment<ProfileViewModel>(R.layout.fragment_profile) {

    override val viewModel: ProfileViewModel by viewModel()

    override fun onReady() {
        viewModel.userProfile.observe {
            tvName.text = it.name
            wageTV.text = getString(R.string.user_weight_placeholder, it.weight.toString())
            ageTV.text = getString(R.string.user_age_placeholder, it.age.toString())
        }
        viewModel.stepsTarget.observe {
            stepsTV.text = getString(R.string.steps_placeholder, it.toString())
        }
        viewModel.sleepTarget.observe {
            sleepTV.text = getString(R.string.sleep_placeholder, it.toHours())
        }
        viewModel.activityTarget.observe {
            activityTV.text = getString(R.string.activity_placeholder, it.toHours())
        }

        btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }
}

class ProfileViewModel(
    private val appDatabase: AppDatabase,
    private val fitPermissionManager: FitPermissionManager,
    private val onBoardingFlowManager: OnBoardingFlowManager,
    scoreTargetProvider: ScoreTargetProvider,
    userRepo: UserRepository,
) : BaseViewModel() {

    val userProfile = MutableLiveData<UserProfile>()
    val stepsTarget = MutableLiveData<Int>()
    val sleepTarget = MutableLiveData<Duration>()
    val activityTarget = MutableLiveData<Duration>()

    init {
        stepsTarget.attachSource(scoreTargetProvider.getSteps().toObservable()) { it }
        sleepTarget.attachSource(scoreTargetProvider.getSleepDuration().toObservable()) { it }
        activityTarget.attachSource(scoreTargetProvider.getActivityDuration().toObservable()) { it }
        userProfile.attachSource(userRepo.getProfile()) { it }
    }

    fun logout() {
        appDatabase.clear()
            .andThen(onBoardingFlowManager.clear())
            .andThen(fitPermissionManager.resetPermission())
            .subscribeBy(onSuccess = {
                navigate(R.id.toEntryPoint)
            }, onError = {
                navigate(R.id.toEntryPoint)
            })
            .disposeOnDestroy()
    }
}