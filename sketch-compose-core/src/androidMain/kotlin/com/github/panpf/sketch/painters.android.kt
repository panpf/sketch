package com.github.panpf.sketch

import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.AndroidDrawableImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.painter.asPainter


actual fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    is AndroidBitmapImage -> bitmap.asImageBitmap().asPainter()
    is AndroidDrawableImage -> drawable.asPainter()
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}