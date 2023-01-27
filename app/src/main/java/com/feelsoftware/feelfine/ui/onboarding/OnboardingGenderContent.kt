package com.feelsoftware.feelfine.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.UserProfile.Gender
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme

@Composable
internal fun OnboardingGenderContent(
    gender: Gender?,
    onSelected: (gender: Gender) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Max)
    ) {
        GenderItem(
            icon = painterResource(id = R.drawable.ic_gender_male),
            title = stringResource(id = R.string.gender_male),
            isSelected = gender == Gender.MALE,
            onSelected = { onSelected(Gender.MALE) },
            modifier = Modifier
                .weight(1f)
        )
        Spacer(modifier = Modifier.width(32.dp))
        GenderItem(
            icon = painterResource(id = R.drawable.ic_gender_female),
            title = stringResource(id = R.string.gender_female),
            isSelected = gender == Gender.FEMALE,
            onSelected = { onSelected(Gender.FEMALE) },
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun GenderItem(
    icon: Painter,
    title: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.tertiary else Color.Transparent
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(backgroundColor)
            .clickable {
                onSelected()
            }
            .padding(vertical = 32.dp, horizontal = 8.dp)
            .semantics(mergeDescendants = true) {
                role = Role.RadioButton
                contentDescription = title
                selected = isSelected
            }
    ) {
        Image(icon, contentDescription = null)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun NoneGenderPreview() {
    FeelFineTheme {
        OnboardingGenderContent(gender = null, onSelected = {})
    }
}

@Preview
@Composable
private fun MaleGenderPreview() {
    FeelFineTheme {
        OnboardingGenderContent(gender = Gender.MALE, onSelected = {})
    }
}

@Preview
@Composable
private fun FemaleGenderPreview() {
    FeelFineTheme {
        OnboardingGenderContent(gender = Gender.FEMALE, onSelected = {})
    }
}