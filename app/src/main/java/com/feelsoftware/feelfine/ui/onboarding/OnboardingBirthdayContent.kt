package com.feelsoftware.feelfine.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
    termsAccepted: Boolean,
    onTermsAcceptedChange: (termsAccepted: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = birthday?.time,
        yearRange = remember {
            1900..Calendar.getInstance().get(Calendar.YEAR)
        },
    )
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { onBirthdayChange(Date(it)) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        DatePicker(
            datePickerState = datePickerState,
            title = {},
            headline = {},
            modifier = Modifier.fillMaxWidth(),
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = MaterialTheme.colorScheme.tertiary,
                selectedDayContentColor = MaterialTheme.colorScheme.onTertiary,
                selectedYearContainerColor = MaterialTheme.colorScheme.tertiary,
                selectedYearContentColor = MaterialTheme.colorScheme.onTertiary,
                todayDateBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                todayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        OnboardingTermsContent(
            accepted = termsAccepted,
            onAcceptedChange = onTermsAcceptedChange,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Preview
@Composable
private fun NullBirthdayPreview() {
    FeelFineTheme {
        OnboardingBirthdayContent(
            birthday = null,
            onBirthdayChange = {},
            termsAccepted = false,
            onTermsAcceptedChange = {},
        )
    }
}

@Preview
@Composable
private fun BirthdayPreview() {
    FeelFineTheme {
        OnboardingBirthdayContent(
            birthday = Date(),
            onBirthdayChange = {},
            termsAccepted = false,
            onTermsAcceptedChange = {},
        )
    }
}