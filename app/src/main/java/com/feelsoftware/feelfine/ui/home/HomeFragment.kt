package com.feelsoftware.feelfine.ui.home

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<HomeViewModel>(R.layout.fragment_home) {

    override val viewModel: HomeViewModel by viewModel()

    override fun onReady() {
        val navHostFragment =
            (childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        bottomNavigation.setupWithNavController(navHostFragment)
    }
}

class HomeViewModel : BaseViewModel()