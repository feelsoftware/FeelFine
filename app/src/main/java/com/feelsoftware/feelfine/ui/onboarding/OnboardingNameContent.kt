package com.feelsoftware.feelfine.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun OnboardingNameContent(
    name: String?,
    onNameChange: (name: String) -> Unit,
    onActionDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.bg_onboarding_name),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .widthIn(max = 120.dp)
                .align(Alignment.CenterVertically)
                .semantics {
                    invisibleToUser()
                }
        )
        TextField(
            value = name.orEmpty(),
            onValueChange = onNameChange,
            label = {
                Text(stringResource(id = R.string.onboarding_name_hint))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onActionDone() }),
            maxLines = 1,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }

}

@Preview(widthDp = 400)
@Composable
private fun OnboardingNameEmptyContentPreview() {
    FeelFineTheme {
        OnboardingNameContent(
            name = null,
            onNameChange = {},
            onActionDone = {}
        )
    }
}

@Preview(widthDp = 400)
@Composable
private fun OnboardingNameFilledContentPreview() {
    FeelFineTheme {
        OnboardingNameContent(
            name = "Victor",
            onNameChange = {},
            onActionDone = {}
        )
    }
}