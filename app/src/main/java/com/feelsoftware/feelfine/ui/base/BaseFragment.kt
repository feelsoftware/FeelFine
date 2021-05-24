package com.feelsoftware.feelfine.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController

abstract class BaseFragment<VM : BaseViewModel>(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    abstract val viewModel: VM

    abstract fun onReady()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigation.observe {
            findNavController().navigate(it)
        }
        onReady()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActive()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onInActive()
    }

    protected fun <T> LiveData<T>.observe(block: (value: T) -> Unit) {
        observe(viewLifecycleOwner) { block.invoke(it) }
    }
}

class EmptyViewModel : BaseViewModel()