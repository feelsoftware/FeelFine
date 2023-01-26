@file:Suppress("unused")

package com.feelsoftware.feelfine.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
)

val Shapes.button: Shape
    @Composable
    get() = RoundedCornerShape(8.dp)