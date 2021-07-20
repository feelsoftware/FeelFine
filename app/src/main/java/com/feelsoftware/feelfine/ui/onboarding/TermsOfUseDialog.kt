package com.feelsoftware.feelfine.ui.onboarding

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.github.barteksc.pdfviewer.util.FitPolicy
import kotlinx.android.synthetic.main.dialog_tou.view.*

class TermsOfUseDialog : DialogFragment() {

    companion object {
        fun show(fragment: Fragment) {
            TermsOfUseDialog().show(fragment.childFragmentManager, "TermsOfUseDialog")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_tou, null, false)
        view.pdfView.fromStream(resources.openRawResource(R.raw.terms_of_use))
            .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
            .load()
        view.btn_close.onClick { dismiss() }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}