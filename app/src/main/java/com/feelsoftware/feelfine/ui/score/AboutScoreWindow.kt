package com.feelsoftware.feelfine.ui.score

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import com.feelsoftware.feelfine.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import com.feelsoftware.feelfine.extension.onClick

object AboutScoreWindow {
    fun show(root: View, anchorView: View) {
        PopupWindow(anchorView.context).apply {
            width =
                root.measuredWidth - root.resources.getDimensionPixelSize(R.dimen.about_score_margin_horizontal)
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isOutsideTouchable = true
            isFocusable = true
            contentView = LayoutInflater.from(anchorView.context)
                .inflate(R.layout.about_score_dialog, root as ViewGroup, false)
            contentView.findViewById<Button>(R.id.btnClose).onClick { dismiss() }
            showAsDropDown(anchorView, 0, 0, Gravity.BOTTOM or Gravity.END)
        }
    }
}