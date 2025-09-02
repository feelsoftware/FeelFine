@file:OptIn(ExperimentalMaterial3Api::class)

package com.feelsoftware.feelfine.permission

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme

@Composable
fun PermissionsRationaleView(
    onBack: (() -> Unit)?,
    onContinue: (() -> Unit)?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.health_connect_permission_title))
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.health_connect_permission_details),
                    style = MaterialTheme.typography.bodyLarge,
                )
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

                DropDownSection(
                    title = R.string.health_connect_permission_exercise,
                    content = R.string.health_connect_permission_details_exercise,
                )
                DropDownSection(
                    title = R.string.health_connect_permission_sleep,
                    content = R.string.health_connect_permission_details_sleep,
                )
                DropDownSection(
                    title = R.string.health_connect_permission_steps,
                    content = R.string.health_connect_permission_details_steps,
                )
            }

            if (onContinue != null) {
                Button(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    onClick = onContinue,
                ) {
                    Text(stringResource(R.string.continue_text))
                }
            }
        }
    }
}

@Composable
private fun DropDownSection(
    @StringRes title: Int,
    @StringRes content: Int,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        Row(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                text = stringResource(title),
                style = MaterialTheme.typography.bodyLarge,
            )

            val iconRotation by animateFloatAsState(
                targetValue = if (isExpanded) 180f else 0f,
                label = "iconRotation",
            )
            Icon(
                modifier = Modifier
                    .graphicsLayer { rotationZ = iconRotation },
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
            )
        }

        if (isExpanded) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(content),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@Preview
private fun PermissionRationalePreview() {
    FeelFineTheme {
        PermissionsRationaleView(
            onBack = null,
            onContinue = null,
        )
    }
}

@Composable
@Preview
private fun PermissionRationaleWithContinuePreview() {
    FeelFineTheme {
        PermissionsRationaleView(
            onBack = {},
            onContinue = {},
        )
    }
}
