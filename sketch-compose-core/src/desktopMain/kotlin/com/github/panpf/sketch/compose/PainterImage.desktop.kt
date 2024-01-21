package com.github.panpf.sketch.compose

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.compose.internal.asPainter

actual fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    is ImageBitmapImage -> imageBitmap.asPainter()
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}