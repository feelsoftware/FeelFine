package com.feelsoftware.feelfine.sandbox

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.feelsoftware.feelfine.fit.HealthConnectClientProvider
import com.feelsoftware.feelfine.fit.HealthConnectClientProviderImpl
import com.feelsoftware.feelfine.fit.HealthConnectPermissionManager
import com.feelsoftware.feelfine.fit.HealthConnectPermissionManagerImpl
import com.feelsoftware.feelfine.fit.HealthConnectRepository
import com.feelsoftware.feelfine.fit.HealthConnectRepositoryImpl
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme

class SandboxActivity : AppCompatActivity() {

    private val clientProvider: HealthConnectClientProvider =
        HealthConnectClientProviderImpl(
            context = this,
        )
    private val permissionManager: HealthConnectPermissionManager =
        HealthConnectPermissionManagerImpl(
            clientProvider = clientProvider,
        )
    private val repository: HealthConnectRepository =
        HealthConnectRepositoryImpl(
            clientProvider = clientProvider,
            permissionManager = permissionManager,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager.init(this)

        setContent {
            FeelFineTheme {
                HealthConnectSandbox(
                    permissionManager = permissionManager,
                    repository = repository,
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionManager.dispose()
    }
}
