package com.feelsoftware.feelfine.sandbox

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.feelsoftware.feelfine.fit.Activity
import com.feelsoftware.feelfine.fit.HealthConnectPermissionManager
import com.feelsoftware.feelfine.fit.HealthConnectRepository
import com.feelsoftware.feelfine.fit.Sleep
import com.feelsoftware.feelfine.fit.Steps
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HealthConnectSandbox(
    permissionManager: HealthConnectPermissionManager,
    repository: HealthConnectRepository,
) {
    val coroutineScope = rememberCoroutineScope()
    val hasPermission by permissionManager.hasPermission().collectAsState()

    var sleep by remember { mutableStateOf<Sleep?>(null) }
    var steps by remember { mutableStateOf<Steps?>(null) }
    var activity by remember { mutableStateOf<Activity?>(null) }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Has permission $hasPermission")
            Button(
                enabled = !hasPermission,
                onClick = {
                    coroutineScope.launch {
                        permissionManager.requestPermission()
                    }
                },
            ) {
                Text("Request permission")
            }

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Sleep $sleep"
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        sleep = repository.getSleep(date = now()).getOrElse { null }
                    }
                },
            ) {
                Text("Get sleep")
            }

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Steps $steps"
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        steps = repository.getSteps(date = now()).getOrElse { null }
                    }
                },
            ) {
                Text("Get steps")
            }

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Activity $activity"
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        activity = repository.getActivity(date = now()).getOrElse { null }
                    }
                },
            ) {
                Text("Get activity")
            }
        }
    }
}

private fun now(): LocalDate {
    return Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
}
