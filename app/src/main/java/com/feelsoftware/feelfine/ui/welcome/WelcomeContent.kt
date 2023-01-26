package com.feelsoftware.feelfine.ui.welcome

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme
import com.feelsoftware.feelfine.ui.theme.button

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WelcomeContent(onContinueClick: () -> Unit) {
    Surface {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_welcome),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .semantics {
                            invisibleToUser()
                        }
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .semantics {
                            invisibleToUser()
                        }
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    buildAnnotatedString {
                        val appName = stringResource(R.string.app_name)
                        val text = stringResource(R.string.welcome_title, appName)

                        val start = text.indexOf(appName)
                        val end = start + appName.length

                        append(text)
                        addStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiary), start, end)
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(id = R.string.welcome_label),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                            .semantics {
                                invisibleToUser()
                            }
                    )
                    Button(
                        onClick = onContinueClick,
                        shape = MaterialTheme.shapes.button,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .width(200.dp)
                    ) {
                        Text(text = stringResource(id = R.string.continue_text))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 300)
@Composable
private fun WelcomeContentPreview() {
    FeelFineTheme {
        WelcomeContent(onContinueClick = {})
    }
}