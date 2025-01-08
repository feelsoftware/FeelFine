@file:OptIn(ExperimentalMaterial3Api::class)

package com.feelsoftware.feelfine.permission

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme

@Composable
fun PermissionRationaleView() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.health_connect_permission_details))
        }
    }
}

@Composable
@Preview
private fun PermissionRationaleViewPreview() {
    FeelFineTheme {
        PermissionRationaleView()
    }
}
