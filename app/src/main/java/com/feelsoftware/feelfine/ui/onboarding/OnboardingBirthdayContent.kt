package com.feelsoftware.feelfine.ui.onboarding

import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OnboardingBirthdayContent(
    birthday: Date?,
    onBirthdayChange: (birthday: Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = birthday?.time,
        yearRange = remember {
            1900..Calendar.getInstance().get(Calendar.YEAR)
        }
    )
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { onBirthdayChange(Date(it)) }
    }

    DatePicker(
        datePickerState = datePickerState,
        title = {},
        headline = {},
        modifier = modifier
    )
}

@Preview
@Composable
private fun NullBirthdayPreview() {
    FeelFineTheme {
        OnboardingBirthdayContent(birthday = null, onBirthdayChange = {})
    }
}

@Preview
@Composable
private fun BirthdayPreview() {
    FeelFineTheme {
        OnboardingBirthdayContent(birthday = Date(), onBirthdayChange = {})
    }
}