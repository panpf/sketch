package com.github.panpf.sketch.compose.painter.internal

import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.VectorPainter


fun Painter.toLogString(): String = when (this) {
    is SketchPainter -> toString()
    is BitmapPainter -> "BitmapPainter(${toSizeString()})"
    is ColorPainter -> "ColorPainter(${color})"
    is BrushPainter -> "BrushPainter(${brush})"
    is VectorPainter -> "VectorPainter(${toSizeString()})"
    else -> platformToLongString() ?: toString()
}

expect fun Painter.platformToLongString(): String?

internal fun Painter.toSizeString(): String =
    if (intrinsicSize.isSpecified) "$intrinsicSize" else "unspecified"