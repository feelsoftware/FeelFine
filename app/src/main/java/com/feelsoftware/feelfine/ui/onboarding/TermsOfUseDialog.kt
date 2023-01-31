package com.feelsoftware.feelfine.ui.onboarding

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy

class TermsOfUseDialog : DialogFragment() {

    companion object {
        fun show(fragment: Fragment) {
            TermsOfUseDialog().show(fragment.childFragmentManager, "TermsOfUseDialog")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // TODO: Replace with ViewBinding
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_tou, null, false)
        view.findViewById<PDFView>(R.id.pdfView)
            .fromStream(resources.openRawResource(R.raw.terms_of_use))
            .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
            .load()
        view.findViewById<Button>(R.id.btn_close).onClick { dismiss() }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}