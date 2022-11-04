package com.feelsoftware.feelfine.ui.profile

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.ui.dialog.showErrorDialog
import java.io.Closeable

class DemoProfileBadge(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private var closeable: Closeable? = null

    init {
        View.inflate(context, R.layout.view_demo_profile_badge, this)
        isVisible = false

        onClick {
            closeable?.close()
            closeable = showErrorDialog(
                context,
                context.getString(R.string.demo_profile_badge),
                context.getString(R.string.demo_profile_badge_description)
            )
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        closeable?.close()
    }
}