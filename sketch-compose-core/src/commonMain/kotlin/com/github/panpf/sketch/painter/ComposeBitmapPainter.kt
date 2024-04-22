package com.github.panpf.sketch.painter

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.ComposeBitmap

fun ComposeBitmap.asPainter(): Painter = ComposeBitmapPainter(this)

fun ComposeBitmap.toLogString(): String =
    "ComposeBitmap@${hashCode().toString(16)}(${width.toFloat()}x${height.toFloat()},$config)"

@Stable
class ComposeBitmapPainter(val bitmap: ComposeBitmap) : Painter(), SketchPainter {

    override val intrinsicSize = Size(bitmap.width.toFloat(), bitmap.height.toFloat())

    override fun DrawScope.onDraw() {
        val intSize = IntSize(size.width.toInt(), size.height.toInt())
        drawImage(bitmap, dstSize = intSize)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComposeBitmapPainter) return false
        return bitmap == other.bitmap
    }

    override fun hashCode(): Int {
        return bitmap.hashCode()
    }

    override fun toString(): String {
        return "ComposeBitmapPainter(bitmap=${bitmap.toLogString()})"
    }
}