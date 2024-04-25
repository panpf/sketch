package com.github.panpf.sketch

import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.SkiaAnimatedImagePainter
import com.github.panpf.sketch.painter.asPainter

actual fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    is ComposeBitmapImage -> bitmap.asPainter()
    is SkiaBitmapImage -> bitmap.asComposeImageBitmap().asPainter()
    is SkiaAnimatedImage -> SkiaAnimatedImagePainter(this)
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}