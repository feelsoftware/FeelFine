package com.feelsoftware.feelfine.fit

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient

/**
 * Provide instance of [HealthConnectClient].
 */
interface HealthConnectClientProvider {

    /**
     * @return [Error] when `Result.failure`.
     */
    operator fun invoke(): Result<HealthConnectClient>

    /**
     * Call to perform update after `invoke()` returned `Error.UpdateRequired`.
     */
    suspend fun performUpdate(): Result<Unit>

    @Suppress("JavaIoSerializableObjectMustHaveReadResolve")
    sealed class Error : Throwable() {
        data object UpdateRequired : Error()

        data object NotAvailable : Error()
    }
}

class HealthConnectClientProviderImpl(
    private val context: Context,
) : HealthConnectClientProvider {

    private var client: HealthConnectClient? = null

    override fun invoke(): Result<HealthConnectClient> = runCatching {
        when (HealthConnectClient.getSdkStatus(context, PROVIDER_PACKAGE_NAME)) {
            HealthConnectClient.SDK_AVAILABLE -> {
                client ?: synchronized(this) {
                    HealthConnectClient.getOrCreate(context).also { client = it }
                }
            }

            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                throw HealthConnectClientProvider.Error.UpdateRequired
            }

            else -> {
                throw HealthConnectClientProvider.Error.NotAvailable
            }
        }
    }

    override suspend fun performUpdate(): Result<Unit> = runCatching {
        // Redirect to package installer to find a provider
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setPackage("com.android.vending")
            data = Uri.parse(
                "market://details?id=$PROVIDER_PACKAGE_NAME&url=healthconnect%3A%2F%2Fonboarding"
            )
            putExtra("overlay", true)
            putExtra("callerId", context.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    private companion object {
        private const val PROVIDER_PACKAGE_NAME = "com.google.android.apps.healthdata"
    }
}
