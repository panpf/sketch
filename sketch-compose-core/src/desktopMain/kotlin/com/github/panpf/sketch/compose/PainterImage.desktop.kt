package com.github.panpf.sketch.compose

import androidx.compose.ui.graphics.asPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.BufferedImageImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.compose.internal.asPainter

actual fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    is ImageBitmapImage -> imageBitmap.asPainter()
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}

fun d(bufferedImageImage: BufferedImageImage) {
    bufferedImageImage.bufferedImage.asPainter()
}