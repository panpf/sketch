package com.github.panpf.sketch.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.util.PainterEqualizer
import com.github.panpf.sketch.util.asEquality


fun CurrentStateImage(defaultPainter: PainterEqualizer): CurrentStateImage =
    CurrentStateImage(PainterStateImage(defaultPainter))

fun CurrentStateImage(defaultColor: Color): CurrentStateImage =
    CurrentStateImage(PainterStateImage(ColorPainter(defaultColor).asEquality()))