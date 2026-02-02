package com.github.panpf.sketch.sample.ui.util

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.util.Size

/**
 * Wrap a ColorPainter into a Painter with equality
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.EquitablePainterTest.testAsEquitable
 */
fun SizeColorDrawable.asEquitable(): EquitableDrawable =
    EquitableDrawable(drawable = this, equalityKey = this)

open class SizeColorDrawable constructor(
    val color: Int,
    val size: Size
) : Drawable(), SketchDrawable {

    private val mPaint = Paint().apply {
        color = this@SizeColorDrawable.color
        isAntiAlias = true
        isDither = true
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(bounds, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun getIntrinsicWidth(): Int {
        return size.width
    }

    override fun getIntrinsicHeight(): Int {
        return size.height
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SizeColorDrawable
        if (color != other.color) return false
        if (size != other.size) return false
        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

    override fun toString(): String {
        return "SizeColorDrawable(color=$color, size=$size)"
    }
}