package com.feelsoftware.feelfine.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun OnboardingWeightContent(
    weight: Float,
    range: ClosedRange<Float>,
    onChange: (weigh: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_onboarding_weight),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .semantics {
                    invisibleToUser()
                }
        )

        WeightPicker(
            weight = weight,
            range = range,
            onChange = onChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp)
        )
    }
}

@Composable
private fun WeightPicker(
    weight: Float,
    range: ClosedRange<Float>,
    onChange: (weigh: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WeightButton(
            isPlus = false,
            onClick = {
                onChange(weight.previousWeight(range) ?: return@WeightButton)
            },
            onLongClick = {
                onChange(
                    weight.previousWeight(range, offset = -10f)
                        ?: weight.previousWeight(range)
                        ?: return@WeightButton
                )
            },
        )

        WeightText(
            weight = weight,
            range = range,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
        )

        WeightButton(
            isPlus = true,
            onClick = {
                onChange(weight.nextWeight(range) ?: return@WeightButton)
            },
            onLongClick = {
                onChange(
                    weight.nextWeight(range, offset = 10f)
                        ?: weight.nextWeight(range)
                        ?: return@WeightButton
                )
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun WeightText(
    weight: Float,
    range: ClosedRange<Float>,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            weight.previousWeight(range, offset = -2f)?.roundToInt()?.toString().orEmpty(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge.let {
                it.copy(fontSize = it.fontSize * 0.6f)
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .weight(0.6f)
                .semantics {
                    invisibleToUser()
                }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            weight.previousWeight(range)?.roundToInt()?.toString().orEmpty(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge.let {
                it.copy(fontSize = it.fontSize * 0.75f)
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .weight(0.75f)
                .semantics {
                    invisibleToUser()
                }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            weight.roundToInt().toString(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = "Weight ${weight.roundToInt()}"
                }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            weight.nextWeight(range)?.roundToInt()?.toString().orEmpty(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge.let {
                it.copy(fontSize = it.fontSize * 0.75f)
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .weight(0.75f)
                .semantics {
                    invisibleToUser()
                }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            weight.nextWeight(range, offset = 2f)?.roundToInt()?.toString().orEmpty(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge.let {
                it.copy(fontSize = it.fontSize * 0.6f)
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .weight(0.6f)
                .semantics {
                    invisibleToUser()
                }
        )
    }
}

private fun Float.previousWeight(range: ClosedRange<Float>, offset: Float = -1f): Float? {
    val weight = this
    return if (weight + offset >= range.start) {
        weight + offset
    } else {
        null
    }
}

private fun Float.nextWeight(range: ClosedRange<Float>, offset: Float = 1f): Float? {
    val weight = this
    return if (weight + offset <= range.endInclusive) {
        weight + offset
    } else {
        null
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WeightButton(
    isPlus: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val lineColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            )
            .drawBehind {
                val offset = size.width / 3
                drawLine(
                    color = lineColor,
                    start = Offset(offset, center.y),
                    end = Offset(size.width - offset, center.y),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
                if (!isPlus) return@drawBehind
                drawLine(
                    color = lineColor,
                    start = Offset(center.x, offset),
                    end = Offset(center.x, size.height - offset),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            .semantics {
                role = Role.Button
                contentDescription = if (isPlus) "Increase weight" else "Decrease weight"
            }
    )
}

@Preview(widthDp = 300)
@Composable
private fun OnboardingWeightContentPreview() {
    FeelFineTheme {
        OnboardingWeightContent(weight = 65f, range = 50f..75f, onChange = {})
    }
}