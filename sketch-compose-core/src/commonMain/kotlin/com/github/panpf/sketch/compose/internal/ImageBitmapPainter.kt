package com.github.panpf.sketch.compose.internal

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize

fun ImageBitmap.asPainter(): Painter = ImageBitmapPainter(this)

class ImageBitmapPainter(val imageBitmap: ImageBitmap) : Painter() {

    override val intrinsicSize = Size(imageBitmap.width.toFloat(), imageBitmap.height.toFloat())

    override fun DrawScope.onDraw() {
        val intSize = IntSize(size.width.toInt(), size.height.toInt())
        drawImage(imageBitmap, dstSize = intSize)
    }
}