package com.github.panpf.sketch.compose

import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.compose.painter.SkiaAnimatedImagePainter
import com.github.panpf.sketch.compose.painter.asPainter

actual fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    is ComposeBitmapImage -> bitmap.asPainter()
    is SkiaBitmapImage -> bitmap.asComposeImageBitmap().asPainter()
    is SkiaAnimatedImage -> SkiaAnimatedImagePainter(this)
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}