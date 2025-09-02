package com.feelsoftware.feelfine.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.feelsoftware.feelfine.R
import org.koin.android.ext.android.inject
import timber.log.Timber

private const val NO_ID = -1

abstract class BaseFragment<VM : BaseViewModel>(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    abstract val viewModel: VM

    abstract fun onReady()

    @get:ColorRes
    open val statusBarColorResId: Int = NO_ID

    private val statusBarColorModifier: StatusBarColorModifier by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(this::class.java.name).d("onCreate")
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigation.observe {
            val navOptions = NavOptions.Builder()
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .setPopEnterAnim(android.R.anim.fade_in)
                .setPopExitAnim(android.R.anim.fade_out)
                .build()

            // A hack to support internal and external navigation
            try {
                findNavController().navigate(it, navOptions)
            } catch (_: Throwable) {
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(it, navOptions)
            }
        }
        viewModel.backNavigation.observe {
            navigateBack()
        }
        onReady()
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(this::class.java.name).d("onResume")
        viewModel.onActive()
        statusBarColorResId.takeIf { it != NO_ID }?.let(statusBarColorModifier::setColor)
    }

    override fun onPause() {
        super.onPause()
        Timber.tag(this::class.java.name).d("onPause")
        viewModel.onInActive()
        statusBarColorModifier.setDefaultColor()
    }

    protected fun navigateBack() {
        Timber.tag(this::class.java.name).d("navigateBack")
        requireActivity().onBackPressed()
    }

    protected fun <T> LiveData<T>.observe(block: (value: T) -> Unit) {
        observe(viewLifecycleOwner) { block.invoke(it) }
    }
}

class EmptyViewModel : BaseViewModel()