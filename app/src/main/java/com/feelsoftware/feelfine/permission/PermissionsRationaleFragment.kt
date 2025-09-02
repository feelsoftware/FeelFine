package com.feelsoftware.feelfine.permission

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.feelsoftware.feelfine.ui.base.BaseComposeFragment
import timber.log.Timber

private const val REQUEST_KEY = "PermissionsRationale.REQUEST_KEY"
private const val KEY_CANCELLED = "KEY_CANCELLED"
private const val KEY_CONFIRMED = "KEY_CONFIRMED"

fun Fragment.observePermissionsRationaleResult(
    onCancelled: () -> Unit,
    onConfirmed: () -> Unit,
) {
    setFragmentResultListener(REQUEST_KEY) { _, bundle ->
        Timber.d("PermissionsRationaleResult $bundle")

        when {
            bundle.getBoolean(KEY_CANCELLED) -> onCancelled()

            bundle.getBoolean(KEY_CONFIRMED) -> onConfirmed()

            else -> {
                Timber.e(
                    IllegalArgumentException("PermissionsRationale"),
                    "Unsupported result $bundle",
                )
            }
        }
    }
}

private fun Fragment.setResult(
    isCancelled: Boolean,
    isConfirmed: Boolean,
) {
    setFragmentResult(
        REQUEST_KEY,
        bundleOf(
            KEY_CANCELLED to isCancelled,
            KEY_CONFIRMED to isConfirmed,
        )
    )
    findNavController().navigateUp()
}

class PermissionsRationaleFragment : BaseComposeFragment() {

    @Composable
    override fun Content() {
        PermissionsRationaleView(
            onBack = {
                setResult(isCancelled = true, isConfirmed = false)
            },
            onContinue = {
                setResult(isCancelled = false, isConfirmed = true)
            },
        )
    }

}