package com.feelsoftware.feelfine.ui.dialog

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.feelsoftware.feelfine.R

fun Fragment.showErrorDialog(
    title: String,
    body: String,
) {
    val alertDialog = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(body)
        .setPositiveButton(getString(R.string.error_alert_button), null)
        .show()

    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onPause(owner: LifecycleOwner) {
            alertDialog.dismiss()
        }
    })
}