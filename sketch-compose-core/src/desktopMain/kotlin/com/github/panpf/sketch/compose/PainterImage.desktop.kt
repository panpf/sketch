package com.github.panpf.sketch.compose

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Image

actual fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}