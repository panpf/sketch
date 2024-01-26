package com.github.panpf.sketch.compose.painter.internal

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.drawable.internal.toLogString
import com.google.accompanist.drawablepainter.DrawablePainter

actual fun Painter.platformToLongString(): String? = when{
//    this is SVGPainter
//    this is BufferedImagePainter
    this is DrawablePainter -> "DrawablePainter(drawable=${drawable.toLogString()})"
    else -> null
}
