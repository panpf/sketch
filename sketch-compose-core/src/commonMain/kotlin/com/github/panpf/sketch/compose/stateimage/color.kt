package com.github.panpf.sketch.compose.stateimage

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter

fun colorPainterStateImage(color: Color): PainterStateImage =
    PainterStateImage(ColorPainter(color))

fun colorPainterStateImage(@ColorInt color: Int): PainterStateImage =
    PainterStateImage(ColorPainter(Color(color)))