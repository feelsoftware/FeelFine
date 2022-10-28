package com.feelsoftware.feelfine.ui.profile

import androidx.core.view.isVisible
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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ProfileFragment : BaseFragment<ProfileViewModel>(R.layout.fragment_profile) {

    override val viewModel: ProfileViewModel by viewModel()

    override fun onReady() {
        viewModel.userProfile.observe { profile ->
            tvName.text = profile.name
            wageTV.text = getString(R.string.user_weight_placeholder, profile.weight.toString())
            ageTV.text = getString(R.string.user_age_placeholder, profile.age.toString())

            btnSignIn.isVisible = profile.isDemo
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

        btnSignIn.setOnClickListener {
            viewModel.signIn()
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
    userRepository: UserRepository,
) : BaseViewModel() {

    val userProfile = MutableLiveData<UserProfile>()
    val stepsTarget = MutableLiveData<Int>()
    val sleepTarget = MutableLiveData<Duration>()
    val activityTarget = MutableLiveData<Duration>()

    init {
        stepsTarget.attachSource(scoreTargetProvider.getSteps().toObservable()) { it }
        sleepTarget.attachSource(scoreTargetProvider.getSleepDuration().toObservable()) { it }
        activityTarget.attachSource(scoreTargetProvider.getActivityDuration().toObservable()) { it }
        userProfile.attachSource(userRepository.getProfile()) { it }
    }

    fun signIn() {
        fitPermissionManager.requestPermission()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onSuccess = { hasPermission ->
                if (hasPermission) navigate(R.id.toHomeFragment)
            }, onError = { error ->
                Timber.e(error, "Failed to sign in")
            })
            .disposeOnDestroy()
    }

    fun logout() {
        appDatabase.clear()
            .andThen(onBoardingFlowManager.clear())
            .andThen(fitPermissionManager.resetPermission())
            .doFinally { navigate(R.id.toEntryPoint) }
            .subscribeBy(onError = { error ->
                Timber.e(error, "Failed to logout")
            })
            .disposeOnDestroy()
    }
}