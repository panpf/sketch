package com.github.panpf.sketch.sample.ui.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat

open class BackgroundAnimatableDrawable constructor(
    wrapped: Drawable,
    val color: Int,
) : DrawableWrapperCompat(wrapped), Animatable {

    private val mPaint = Paint().apply {
        color = this@BackgroundAnimatableDrawable.color
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
        other as BackgroundAnimatableDrawable
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
        return "BackgroundAnimatableDrawable(drawable=$drawable, color=$color)"
    }

    override fun start() {
        val drawable = drawable
        if (drawable is Animatable) {
            (drawable as Animatable).start()
        }
    }

    override fun stop() {
        val drawable = drawable
        if (drawable is Animatable) {
            (drawable as Animatable).stop()
        }
    }

    override fun isRunning(): Boolean {
        val drawable = drawable
        return if (drawable is Animatable) {
            (drawable as Animatable).isRunning
        } else {
            false
        }
    }
}