package com.feelsoftware.feelfine.permission

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme

class PermissionsRationaleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FeelFineTheme {
                PermissionRationaleView()
            }
        }
    }
}
