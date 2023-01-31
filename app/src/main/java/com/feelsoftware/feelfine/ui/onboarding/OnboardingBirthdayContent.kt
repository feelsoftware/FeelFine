package com.feelsoftware.feelfine.ui.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.offset
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
        initialDisplayedMonthMillis = remember {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, Calendar.JUNE)
            calendar.set(Calendar.YEAR, 1990)
            calendar.time.time
        },
        yearRange = remember {
            1900..Calendar.getInstance().get(Calendar.YEAR)
        },
    )
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { onBirthdayChange(Date(it)) }
    }

    OnboardingBirthdayLayout(
        modifier = modifier,
    ) {
        DatePicker(
            datePickerState = datePickerState,
            title = {},
            headline = {},
            modifier = Modifier
                .fillMaxWidth()
                .layoutId(OnboardingBirthdayLayoutId.DATE_PICKER),
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
                .layoutId(OnboardingBirthdayLayoutId.TERMS),
        )
    }
}

private enum class OnboardingBirthdayLayoutId {
    DATE_PICKER, TERMS,
}

@Composable
private fun OnboardingBirthdayLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        require(measurables.size == 2)

        val termsPlaceable = measurables.first {
            it.layoutId == OnboardingBirthdayLayoutId.TERMS
        }.measure(constraints)

        // There is no way to hide title and headline of DatePicker
        // even if we pass empty Composable to title and headline they still take space
        // DatePickerModalTokens.HeaderContainerHeight + HeaderPadding.top + HeaderPadding.bottom
        // https://issuetracker.google.com/issues/267194809
        val datePickerPlaceable = measurables.first {
            it.layoutId == OnboardingBirthdayLayoutId.DATE_PICKER
        }.measure(constraints.offset(vertical = termsPlaceable.height + 120 + 16 + 12))

        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.maxHeight) {
            // There is no way to hide title and headline of DatePicker
            // even if we pass empty Composable to title and headline they still take space
            // DatePickerModalTokens.HeaderContainerHeight + HeaderPadding.top + HeaderPadding.bottom
            // https://issuetracker.google.com/issues/267194809
            datePickerPlaceable.placeRelative(
                x = (constraints.maxWidth - datePickerPlaceable.width) / 2,
                y = -120 - 16 - 12,
            )

            termsPlaceable.placeRelative(
                x = (constraints.maxWidth - termsPlaceable.width) / 2,
                y = constraints.maxHeight - termsPlaceable.height
            )
        }
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