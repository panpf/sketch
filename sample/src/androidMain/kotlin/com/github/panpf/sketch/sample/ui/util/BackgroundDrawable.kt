package com.github.panpf.sketch.sample.ui.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat

fun Drawable.wrappedBackground(color: Int): Drawable {
    return if (this is Animatable) {
        BackgroundAnimatableDrawable(this, color)
    } else {
        BackgroundDrawable(this, color)
    }
}

open class BackgroundDrawable constructor(
    wrapped: Drawable,
    val color: Int,
) : DrawableWrapperCompat(wrapped) {

    private val mPaint = Paint().apply {
        color = this@BackgroundDrawable.color
        isAntiAlias = true
        isDither = true
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(bounds, mPaint)
        super.draw(canvas)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BackgroundDrawable
        if (drawable != other.drawable) return false
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        var result = drawable.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }

    override fun toString(): String {
        return "BackgroundDrawable(drawable=$drawable, color=$color)"
    }
}