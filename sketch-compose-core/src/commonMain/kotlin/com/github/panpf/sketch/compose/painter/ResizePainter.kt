package com.github.panpf.sketch.compose.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.times
import com.github.panpf.sketch.compose.painter.internal.SketchPainter
import com.github.panpf.sketch.compose.painter.internal.toLogString
import com.github.panpf.sketch.decode.internal.computeSizeMultiplier
import com.github.panpf.sketch.resize.Scale
import kotlin.math.roundToInt

class ResizePainter(
    val painter: Painter,
    val size: Size,
    val scale: Scale
) : Painter(), SketchPainter {

    override val intrinsicSize: Size = size

    private var alpha: Float = 1.0f
    private var colorFilter: ColorFilter? = null

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override fun DrawScope.onDraw() {
        with(painter) {
            val drawSize = this@onDraw.size
            val painterScaledSize = computeScaledSize(this@with.intrinsicSize, drawSize)
            if (drawSize.isUnspecified || drawSize.isEmpty()) {
                draw(painterScaledSize, alpha, colorFilter)
            } else {
                val (horizontal, vertical) = when (scale) {
                    Scale.START_CROP -> 0f to 0f
                    Scale.END_CROP -> drawSize.width - painterScaledSize.width to drawSize.height - painterScaledSize.height
                    else -> (drawSize.width - painterScaledSize.width) / 2 to (drawSize.height - painterScaledSize.height) / 2
                }
                // TODO 有问题
                inset(
                    horizontal = horizontal,
                    vertical = vertical
                ) {
                    draw(painterScaledSize, alpha, colorFilter)
                }
            }
        }
    }

    private fun computeScaledSize(srcSize: Size, dstSize: Size): Size {
        if (srcSize.isUnspecified || srcSize.isEmpty()) return dstSize
        if (dstSize.isUnspecified || dstSize.isEmpty()) return dstSize
        val sizeMultiplier = computeSizeMultiplier(
            srcWidth = srcSize.width.roundToInt(),
            srcHeight = srcSize.height.roundToInt(),
            dstWidth = dstSize.width.roundToInt(),
            dstHeight = dstSize.height.roundToInt(),
            fitScale = true
        )
        return srcSize * ScaleFactor(sizeMultiplier.toFloat(), sizeMultiplier.toFloat())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ResizePainter
        if (painter != other.painter) return false
        if (size != other.size) return false
        return scale == other.scale
    }

    override fun hashCode(): Int {
        var result = painter.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + scale.hashCode()
        return result
    }

    override fun toString(): String {
        return "ResizePainter(painter=${painter.toLogString()}, size=$size, scale=$scale)"
    }
}