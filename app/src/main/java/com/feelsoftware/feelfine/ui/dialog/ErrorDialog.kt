package com.feelsoftware.feelfine.ui.dialog

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.feelsoftware.feelfine.R
import java.io.Closeable

fun Activity.showErrorDialog(
    title: String,
    body: String,
) {
    (this as? LifecycleOwner)?.bindCloseable(showErrorDialog(this, title, body))
}

fun Fragment.showErrorDialog(
    title: String,
    body: String,
) {
    bindCloseable(showErrorDialog(requireContext(), title, body))
}

fun showErrorDialog(
    context: Context,
    title: String,
    body: String,
): Closeable {
    val alertDialog = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(body)
        .setPositiveButton(context.getString(R.string.error_alert_button), null)
        .show()

    return Closeable { alertDialog.dismiss() }
}

private fun LifecycleOwner.bindCloseable(closeable: Closeable) {
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onPause(owner: LifecycleOwner) {
            closeable.close()
        }
    })
}