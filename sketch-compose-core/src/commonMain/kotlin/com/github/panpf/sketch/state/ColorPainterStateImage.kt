package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.util.asEquality

@Composable
fun rememberColorPainterStateImage(color: Color): PainterStateImage = remember(color) {
    PainterStateImage(ColorPainter(color).asEquality())
}