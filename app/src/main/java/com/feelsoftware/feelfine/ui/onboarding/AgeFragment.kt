package com.feelsoftware.feelfine.ui.onboarding

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.buildSpannedString
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.extension.subscribeBy
import com.feelsoftware.feelfine.fit.FitPermissionManager
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.ui.base.SingleLiveData
import com.feelsoftware.feelfine.ui.dialog.showErrorDialog
import com.feelsoftware.feelfine.utils.OnBoardingFlowManager
import kotlinx.android.synthetic.main.fragment_age.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class AgeFragment : BaseFragment<AgeViewModel>(R.layout.fragment_age) {

    override val viewModel: AgeViewModel by viewModel()

    override fun onReady() {
        viewModel.termsNotSetError.observe {
            showErrorDialog(
                title = getString(R.string.onboarding_terms_not_set_alert_title),
                body = getString(R.string.onboarding_terms_not_set_alert_body)
            )
        }
        viewModel.birthdayNotSetError.observe {
            showErrorDialog(
                title = getString(R.string.onboarding_age_not_set_alert_title),
                body = getString(R.string.onboarding_age_not_set_alert_body)
            )
        }

        calendarView.init(viewModel.year, viewModel.month, viewModel.day) { _, year, _, _ ->
            viewModel.onDateChanged(year)
        }

        getStartedB.onClick { viewModel.onContinue() }

        checkbox_terms.applyTermsOfUseLink {
            TermsOfUseDialog.show(this)
        }
        checkbox_terms.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onTermsChecked(isChecked)
        }
    }

    private inline fun TextView.applyTermsOfUseLink(crossinline onClicked: () -> Unit) {
        text = buildSpannedString {
            val link = getString(R.string.i_accept_terms_link)
            val text = getString(R.string.i_accept_terms, link)
            append(text)

            val span = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onClicked()
                }
            }
            val start = text.indexOf(link)
            val end = start + link.length
            setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        movementMethod = LinkMovementMethod.getInstance()
    }
}

class AgeViewModel(
    private val flowManager: OnBoardingFlowManager,
    private val fitPermissionManager: FitPermissionManager
) : BaseViewModel() {

    private val birthday: Calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.YEAR, 1990)
    }
    val year: Int
        get() = birthday.get(Calendar.YEAR)
    val month: Int
        get() = birthday.get(Calendar.MONTH)
    val day: Int
        get() = birthday.get(Calendar.DAY_OF_MONTH)

    private var isTermsChecked = false

    val termsNotSetError = SingleLiveData<Unit>()
    val birthdayNotSetError = SingleLiveData<Unit>()

    init {
        fitPermissionManager.hasPermissionObservable()
            .subscribeBy(onNext = { hasPermission ->
                if (hasPermission) navigate(R.id.toHomeFragment)
            })
            .disposeOnDestroy()
    }

    fun onDateChanged(year: Int) {
        birthday.set(Calendar.YEAR, year)
        flowManager.age = Calendar.getInstance().get(Calendar.YEAR) - year
    }

    fun onTermsChecked(isChecked: Boolean) {
        isTermsChecked = isChecked
    }

    fun onContinue() {
        if (isTermsChecked.not()) {
            termsNotSetError.value = Unit
            return
        }
        if (flowManager.age == null) {
            birthdayNotSetError.value = Unit
            return
        }

        flowManager.markAsPassed(flowManager.buildUserProfile() ?: return)
            .subscribeBy(onComplete = {
                checkGoogleAccount()
            }, onError = {
                Timber.e(it, "Failed to mark onboarding as passed")
            })
            .disposeOnDestroy()
    }

    private fun checkGoogleAccount() {
        fitPermissionManager.requestPermission()
    }
}