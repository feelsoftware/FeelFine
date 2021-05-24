package com.feelsoftware.feelfine.extension

import android.view.View

inline fun View.onClick(crossinline block: () -> Unit) {
    setOnClickListener { block.invoke() }
}