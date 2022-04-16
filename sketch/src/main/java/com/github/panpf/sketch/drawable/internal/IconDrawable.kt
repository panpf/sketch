package com.github.panpf.sketch.drawable.internal

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.alpha

@SuppressLint("RestrictedApi")
class IconDrawable(
    private val icon: Drawable,
    @param:ColorInt private val backgroundColor: Int? = null
) : Drawable(), Drawable.Callback {

    init {
        icon.callback = this
    }

    private val backgroundPaint = backgroundColor?.let { Paint().apply { color = it } }

    override fun mutate(): IconDrawable {
        return IconDrawable(icon.mutate(), backgroundColor)
    }

    override fun draw(canvas: Canvas) {
        backgroundPaint?.let {
            val checkpoint = canvas.save()
            try {
                canvas.drawRect(bounds, it)
            } finally {
                canvas.restoreToCount(checkpoint)
            }
        }
        icon.draw(canvas)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        val iconWidth = icon.intrinsicWidth
        val iconHeight = icon.intrinsicHeight
        val left = bounds.left + (bounds.width() - iconWidth) / 2
        val top = bounds.top + (bounds.height() - iconHeight) / 2
        icon.setBounds(left, top, left + iconWidth, top + iconHeight)
    }

    override fun setAlpha(alpha: Int) {
        backgroundPaint?.alpha = alpha
        icon.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        backgroundPaint?.colorFilter = colorFilter
        icon.colorFilter = colorFilter
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int =
        icon.opacity.takeIf { it != PixelFormat.OPAQUE }
            ?: when (backgroundColor?.alpha ?: 255) {
                255 -> PixelFormat.OPAQUE
                0 -> PixelFormat.TRANSPARENT
                else -> PixelFormat.TRANSLUCENT
            }

    override fun invalidateDrawable(who: Drawable) {
        callback?.invalidateDrawable(this)
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        callback?.scheduleDrawable(this, what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        callback?.unscheduleDrawable(this, what)
    }
}