package com.feelsoftware.feelfine.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.feelsoftware.feelfine.ui.theme.FeelFineTheme

private val circleSize = 24.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun OnboardingProgress(
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    require(currentIndex < totalCount)

    val currentDensity = LocalDensity.current
    val pathEffect = remember(currentDensity) {
        val dashSize = currentDensity.run { 4.dp.toPx() }
        PathEffect.dashPathEffect(floatArrayOf(dashSize, dashSize), 0f)
    }

    Row(
        modifier = modifier
            .requiredHeight(circleSize)
            .semantics(mergeDescendants = true) {
                contentDescription = "Onboarding progress"
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = currentIndex.toFloat(),
                    range = 0f..totalCount.toFloat(),
                    steps = totalCount
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(totalCount) { index ->
            val colorActive = MaterialTheme.colorScheme.tertiary
            val colorInActive = MaterialTheme.colorScheme.primary

            Row(modifier = Modifier
                .size(circleSize)
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    drawCircle(
                        color = colorInActive,
                        radius = (size.minDimension - strokeWidth) / 2f,
                        style = Stroke(width = strokeWidth)
                    )
                }
            ) {
                AnimatedVisibility(
                    visible = index == currentIndex,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Canvas(
                        modifier = Modifier.size(circleSize),
                        onDraw = {
                            drawCircle(
                                color = colorActive,
                                radius = circleSize.toPx() / 2f,
                                style = Fill
                            )
                        }
                    )
                }
            }

            if (index < totalCount - 1) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(circleSize)
                        .padding(horizontal = 8.dp),
                    onDraw = {
                        drawLine(
                            color = colorInActive,
                            strokeWidth = 1.dp.toPx(),
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            pathEffect = pathEffect
                        )
                    }
                )
            }
        }
    }
}

@Preview(widthDp = 200)
@Composable
private fun OnboardingProgressEmptyPreview() {
    FeelFineTheme {
        OnboardingProgress(currentIndex = 0, totalCount = 4)
    }
}

@Preview(widthDp = 200)
@Composable
private fun OnboardingProgressHalfPreview() {
    FeelFineTheme {
        OnboardingProgress(currentIndex = 1, totalCount = 4)
    }
}

@Preview(widthDp = 200)
@Composable
private fun OnboardingProgressFullPreview() {
    FeelFineTheme {
        OnboardingProgress(currentIndex = 3, totalCount = 4)
    }
}