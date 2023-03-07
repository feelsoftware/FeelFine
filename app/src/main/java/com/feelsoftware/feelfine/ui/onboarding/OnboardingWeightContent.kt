package com.feelsoftware.feelfine.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun WeightPicker(
    weight: Float,
    range: ClosedRange<Float>,
    onChange: (weigh: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val initialPage = remember(range) {
        (weight - range.start).toInt()
    }
    val pagesCount = remember(range) {
        (range.endInclusive - range.start).toInt()
    }
    val pagerState = rememberPagerState(initialPage)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onChange(page + range.start)
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WeightButton(
            isPlus = false,
            onClick = {
                coroutineScope.launch {
                    val newPage = (pagerState.currentPage - 1)
                        .coerceIn(0, pagesCount - 1)
                    pagerState.animateScrollToPage(newPage)
                }
            },
            onLongClick = {
                coroutineScope.launch {
                    val newPage = (pagerState.currentPage - 10)
                        .coerceIn(0, pagesCount - 1)
                    pagerState.animateScrollToPage(newPage)
                }
            },
        )

        HorizontalPager(
            count = pagesCount,
            state = pagerState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 72.dp),
        ) { page ->
            @Suppress("NAME_SHADOWING")
            val weight = range.start.toInt() + page

            Text(
                weight.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset =
                            (3 - calculateCurrentOffsetForPage(page).absoluteValue).absoluteValue

                        scaleX = pageOffset.coerceAtLeast(1f) / 3f
                        scaleY = pageOffset.coerceAtLeast(1f) / 3f

                        alpha = pageOffset.coerceAtLeast(1f) / 3f
                    }
                    .fillMaxWidth()
            )
        }

        WeightButton(
            isPlus = true,
            onClick = {
                coroutineScope.launch {
                    val newPage = (pagerState.currentPage + 1)
                        .coerceIn(0, pagesCount - 1)
                    pagerState.animateScrollToPage(newPage)
                }
            },
            onLongClick = {
                coroutineScope.launch {
                    val newPage = (pagerState.currentPage + 10)
                        .coerceIn(0, pagesCount - 1)
                    pagerState.animateScrollToPage(newPage)
                }
            },
        )
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