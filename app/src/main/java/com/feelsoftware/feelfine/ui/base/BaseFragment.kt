package com.feelsoftware.feelfine.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.feelsoftware.feelfine.R

abstract class BaseFragment<VM : BaseViewModel>(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    abstract val viewModel: VM

    abstract fun onReady()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigation.observe {
            // A hack to support internal and external navigations
            try {
                findNavController().navigate(it)
            } catch (error: Throwable) {
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(it)
            }
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