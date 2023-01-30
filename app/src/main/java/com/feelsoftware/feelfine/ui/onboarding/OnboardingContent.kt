package com.feelsoftware.feelfine.ui.onboarding

import android.content.res.Configuration
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme
import com.feelsoftware.feelfine.ui.theme.button
import com.feelsoftware.feelfine.ui.theme.label

@Composable
internal fun OnboardingContent(
    step: OnboardingStep,
    stepIndex: Int,
    stepsCount: Int,
    onStepDataChange: (step: OnboardingStep) -> Unit,
    nextStepEnabled: Boolean,
    onNextStepClick: () -> Unit,
) {
    val (title, label) = when (step) {
        is OnboardingStep.Name ->
            stringResource(id = R.string.onboarding_name_title) to stringResource(id = R.string.onboarding_name_label)
        is OnboardingStep.Gender ->
            stringResource(id = R.string.onboarding_gender_title) to stringResource(id = R.string.onboarding_gender_label)
        is OnboardingStep.Weight ->
            stringResource(id = R.string.onboarding_weight_title) to stringResource(id = R.string.onboarding_weight_label)
        is OnboardingStep.Birthday ->
            stringResource(id = R.string.onboarding_birthday_title) to stringResource(id = R.string.onboarding_birthday_label)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OnboardingProgress(
                currentIndex = stepIndex,
                totalCount = stepsCount,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 16.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 32.dp)
            ) {
                Text(
                    title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
                Text(
                    label,
                    color = MaterialTheme.colorScheme.label,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    when (step) {
                        is OnboardingStep.Name ->
                            OnboardingNameContent(
                                name = step.name,
                                onNameChange = { onStepDataChange(step.copy(it)) },
                                onActionDone = { onNextStepClick() },
                                modifier = Modifier
                                    .padding(end = 32.dp)
                                    .align(Alignment.Center)
                            )
                        is OnboardingStep.Gender ->
                            OnboardingGenderContent(
                                gender = step.gender,
                                onSelected = { onStepDataChange(step.copy(it)) },
                                modifier = Modifier
                                    .padding(horizontal = 48.dp)
                                    .align(Alignment.Center)
                            )
                        is OnboardingStep.Weight ->
                            OnboardingWeightContent(
                                weight = step.weight,
                                range = step.range,
                                onChange = { onStepDataChange(step.copy(it)) },
                                modifier = Modifier
                                    .padding(horizontal = 48.dp)
                                    .align(Alignment.Center)
                            )
                        is OnboardingStep.Birthday ->
                            OnboardingBirthdayContent(
                                birthday = step.birthday,
                                onBirthdayChange = { onStepDataChange(step.copy(it)) },
                                termsAccepted = step.termsAccepted,
                                onTermsAcceptedChange = { onStepDataChange(step.copy(termsAccepted = it)) },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .align(Alignment.Center)
                            )
                    }
                }
            }

            Button(
                onClick = onNextStepClick,
                shape = MaterialTheme.shapes.button,
                enabled = nextStepEnabled,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .width(200.dp)
            ) {
                Text(text = stringResource(id = R.string.continue_text))
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 300)
@Composable
private fun OnboardingContentPreview() {
    FeelFineTheme {
        OnboardingContent(
            step = OnboardingStep.Name(),
            stepIndex = 0,
            stepsCount = 4,
            onStepDataChange = {},
            nextStepEnabled = true,
            onNextStepClick = {}
        )
    }
}