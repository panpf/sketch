package com.github.panpf.sketch.compose.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.compose.painter.internal.SketchPainter

fun ImageBitmap.asPainter(): Painter = ImageBitmapPainter(this)

fun ImageBitmap.toLogString(): String = "ImageBitmap(${width.toFloat()}x${height.toFloat()},$config)@${hashCode().toString(16)}"

// TODO Not used
class ImageBitmapPainter(val imageBitmap: ImageBitmap) : Painter(), SketchPainter {

    override val intrinsicSize = Size(imageBitmap.width.toFloat(), imageBitmap.height.toFloat())

    override fun DrawScope.onDraw() {
        val intSize = IntSize(size.width.toInt(), size.height.toInt())
        drawImage(imageBitmap, dstSize = intSize)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ImageBitmapPainter
        return imageBitmap == other.imageBitmap
    }

    override fun hashCode(): Int {
        return imageBitmap.hashCode()
    }

    override fun toString(): String {
        return "ImageBitmapPainter(imageBitmap=${imageBitmap.toLogString()})"
    }
}