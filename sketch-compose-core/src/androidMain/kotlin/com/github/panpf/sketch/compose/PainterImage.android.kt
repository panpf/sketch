package com.github.panpf.sketch.compose

import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.Image
import com.google.accompanist.drawablepainter.DrawablePainter


actual fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    is BitmapImage -> BitmapPainter(bitmap.asImageBitmap())
    is DrawableImage -> DrawablePainter(drawable.mutate())
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}