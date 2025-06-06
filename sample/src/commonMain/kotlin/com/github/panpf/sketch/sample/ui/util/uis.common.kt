package com.github.panpf.sketch.sample.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.fastRoundToInt

@Composable
expect fun windowSize(): IntSize

fun Painter.toImageBitmap(
    density: Density,
    layoutDirection: LayoutDirection,
    size: Size = intrinsicSize
): ImageBitmap {
    val bitmap =
        ImageBitmap(size.width.fastRoundToInt(), size.height.fastRoundToInt())
    val canvas = Canvas(bitmap)
    CanvasDrawScope().draw(density, layoutDirection, canvas, size) {
        draw(size)
    }
    return bitmap
}

fun ImageBitmap.scale(size: IntSize): ImageBitmap {
    val outBitmap = ImageBitmap(size.width, size.height)
    val canvas = Canvas(outBitmap)
    canvas.drawImageRect(
        image = this,
        srcSize = IntSize(width, height),
        dstSize = size,
        paint = Paint().apply { isAntiAlias = true }
    )
    return outBitmap
}