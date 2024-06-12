package com.github.panpf.sketch.painter

import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.ComposeBitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.PainterImage
import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.SkiaBitmapImage

actual fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    is ComposeBitmapImage -> bitmap.asPainter()
    is SkiaBitmapImage -> bitmap.asComposeImageBitmap().asPainter()
    is SkiaAnimatedImage -> SkiaAnimatedImagePainter(this)
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}

actual fun Painter.platformToLogString(): String? = null