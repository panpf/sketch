package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toLogString

open class SizeDrawable(drawable: Drawable, val size: Size) : DrawableWrapperCompat(drawable),
    SketchDrawable {

    override fun getIntrinsicWidth(): Int {
        return size.width
    }

    override fun getIntrinsicHeight(): Int {
        return size.height
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SizeDrawable
        if (drawable != other.drawable) return false
        if (size != other.size) return false
        return true
    }

    override fun hashCode(): Int {
        var result = drawable.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

    override fun toString(): String {
        return "SizeDrawable(drawable=${drawable?.toLogString()}, size=$size)"
    }
}