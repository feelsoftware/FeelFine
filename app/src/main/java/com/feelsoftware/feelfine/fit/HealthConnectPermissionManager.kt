package com.feelsoftware.feelfine.fit

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.lifecycleScope
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

interface HealthConnectPermissionManager {

    fun init(activity: ComponentActivity)

    fun dispose()

    fun hasPermission(): StateFlow<Boolean>

    suspend fun requestPermission(): Result<Boolean>
}

class HealthConnectPermissionManagerImpl(
    private val clientProvider: HealthConnectClientProvider,
) : HealthConnectPermissionManager {

    private val requiredPermissions: Set<String> = setOf(
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
    )

    private var activity: ComponentActivity? = null
    private var permissionsLauncher: ActivityResultLauncher<Set<String>>? = null
    private var requestPermissionContinuation: CancellableContinuation<Boolean>? = null

    private inline val coroutineScope: CoroutineScope?
        get() = activity?.lifecycleScope

    private val hasPermissionFlow = MutableStateFlow(false)

    override fun init(activity: ComponentActivity) {
        this.activity = activity
        permissionsLauncher = activity.registerForActivityResult(
            PermissionController.createRequestPermissionResultContract()
        ) { granted ->
            val hasPermission = granted.containsAll(requiredPermissions)
            hasPermissionFlow.update { hasPermission }
            requestPermissionContinuation?.resume(hasPermission)
            requestPermissionContinuation = null
        }
    }

    override fun dispose() {
        activity = null
        permissionsLauncher = null
        requestPermissionContinuation?.cancel()
        requestPermissionContinuation = null
    }

    override fun hasPermission(): StateFlow<Boolean> {
        coroutineScope?.launch { hasPermissionInternal() }
        return hasPermissionFlow.asStateFlow()
    }

    override suspend fun requestPermission(): Result<Boolean> {
        return hasPermissionInternal()
            .mapCatching { hasPermission ->
                if (hasPermission) {
                    // Permissions already granted
                    true
                } else {
                    // Lack of required permissions
                    requestPermissionContinuation?.cancel()
                    suspendCancellableCoroutine {
                        requestPermissionContinuation = it

                        requireNotNull(permissionsLauncher) {
                            "Permissions launcher is not ready, did you call init?"
                        }.launch(requiredPermissions)
                    }
                }
            }
    }

    private suspend fun hasPermissionInternal(): Result<Boolean> {
        return clientProvider()
            .mapCatching { client ->
                client.permissionController.getGrantedPermissions()
            }
            .mapCatching { grantedPermissions ->
                grantedPermissions.containsAll(requiredPermissions)
            }
            .onSuccess {
                hasPermissionFlow.value = it
            }
            .onFailure {
                hasPermissionFlow.value = false
            }
    }
}
