package com.github.panpf.sketch.test.utils

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
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

open class SizeColorDrawable(
    val color: Int,
    val size: Size
) : DrawableWrapperCompat(ColorDrawable(color)), SketchDrawable {

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
        return "SizeDrawable(color=$color, size=$size)"
    }
}