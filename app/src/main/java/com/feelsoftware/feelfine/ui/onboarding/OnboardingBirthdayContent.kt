package com.feelsoftware.feelfine.ui.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    val dateRange = remember {
        val dateFrom = Calendar.getInstance().apply {
            set(Calendar.YEAR, 1900)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val dateTo = Calendar.getInstance().apply {
            add(Calendar.YEAR, -10)
            add(Calendar.MONTH, Calendar.DECEMBER)
            add(Calendar.DAY_OF_MONTH, 31)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        dateFrom..dateTo
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = birthday?.time,
        initialDisplayedMonthMillis = remember(dateRange) {
            dateRange.endInclusive.time.time
        },
        yearRange = remember(dateRange) {
            dateRange.start.get(Calendar.YEAR)..dateRange.endInclusive.get(Calendar.YEAR)
        },
        initialDisplayMode = DisplayMode.Input,
    )
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { onBirthdayChange(Date(it)) }
    }

    OnboardingBirthdayLayout(
        modifier = modifier,
        datePickerMode = datePickerState.displayMode,
    ) {
        DatePicker(
            state = datePickerState,
            title = null,
            // Set empty text to place icon to change display mode at the end
            headline = { Text("") },
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
            ),
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

@ExperimentalMaterial3Api
@Composable
private fun OnboardingBirthdayLayout(
    modifier: Modifier = Modifier,
    datePickerMode: DisplayMode,
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

        // There is no way to hide headline of DatePicker
        // even if we pass empty Composable to headline its padding still takes the space
        // DatePickerHeaderPadding.top + DatePickerHeaderPadding.bottom
        // https://issuetracker.google.com/issues/267194809
        val headerSize = 16 + 12
        val datePickerPlaceable = measurables.first {
            it.layoutId == OnboardingBirthdayLayoutId.DATE_PICKER
        }.measure(constraints.offset(vertical = termsPlaceable.height + headerSize))

        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.maxHeight) {
            val pickerModeY = -headerSize
            val inputModeY = (constraints.maxHeight - datePickerPlaceable.height) / 2
            // There is no way to hide headline of DatePicker
            // even if we pass empty Composable to headline its padding still takes the space
            // DatePickerHeaderPadding.top + DatePickerHeaderPadding.bottom
            // https://issuetracker.google.com/issues/267194809
            datePickerPlaceable.placeRelative(
                x = (constraints.maxWidth - datePickerPlaceable.width) / 2,
                y = if (datePickerMode == DisplayMode.Picker) pickerModeY else inputModeY,
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