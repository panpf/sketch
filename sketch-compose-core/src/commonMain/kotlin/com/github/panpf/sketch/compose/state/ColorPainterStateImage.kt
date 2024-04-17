package com.github.panpf.sketch.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter

@Composable
fun rememberColorPainterStateImage(color: Color): PainterStateImage = remember(color) {
    PainterStateImage(ColorPainter(color))
}