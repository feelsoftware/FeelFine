package com.feelsoftware.feelfine.ui

import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.FitPermissionManager
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class EntryPointFragment : BaseFragment<EntryPointViewModel>(R.layout.fragment_entry_point) {

    override val viewModel: EntryPointViewModel by viewModel()

    override fun onReady() {}
}

class EntryPointViewModel(
    private val flowManager: OnBoardingFlowManager,
    private val fitPermissionManager: FitPermissionManager
) : BaseViewModel() {

    override fun onActive() {
        flowManager.isPassed()
            .subscribeBy(onSuccess = { isPassed ->
                if (isPassed) {
                    checkGoogleAccount()
                } else {
                    navigate(R.id.toOnboarding)
                }
            }, onError = {
                Timber.e(it, "Failed to check if onboarding is passed")
            }).disposeOnInActive()
    }

    private fun checkGoogleAccount() {
        fitPermissionManager.hasPermissionObservable()
            .subscribeBy(onNext = { hasPermission ->
                if (hasPermission) {
                    navigate(R.id.toHomeFragment)
                } else {
                    fitPermissionManager.requestPermission()
                }
            }, onError = {
                Timber.e(it, "Failed to check Google account")
            })
            .disposeOnInActive()
    }
}