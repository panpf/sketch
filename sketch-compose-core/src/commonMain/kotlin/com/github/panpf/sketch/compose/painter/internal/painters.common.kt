package com.github.panpf.sketch.compose.painter.internal

import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.VectorPainter
import com.github.panpf.sketch.compose.painter.SketchPainter


fun Painter.toLogString(): String = when (this) {
    is SketchPainter -> toString()
    is BitmapPainter -> "BitmapPainter@${hashCode().toString(16)}(${toSizeString()})"
    is ColorPainter -> "ColorPainter@${hashCode().toString(16)}(${color})"
    is BrushPainter -> "BrushPainter@${hashCode().toString(16)}(${brush})"
    is VectorPainter -> "VectorPainter@${hashCode().toString(16)}(${toSizeString()})"
    else -> platformToLogString() ?: toString()
}

expect fun Painter.platformToLogString(): String?

internal fun Painter.toSizeString(): String =
    if (intrinsicSize.isSpecified) "$intrinsicSize" else "unspecified"