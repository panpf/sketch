package com.github.panpf.sketch.drawable.internal

import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Animatable2.AnimationCallback
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.withSave
import com.github.panpf.sketch.decode.internal.computeSizeMultiplier
import kotlin.math.roundToInt

/**
 * A [Drawable] that centers and scales its [child] to fill its bounds.
 *
 * This allows drawables that only draw within their intrinsic dimensions
 * (e.g. [AnimatedImageDrawable]) to fill their entire bounds.
 */
@RequiresApi(VERSION_CODES.P)
class ScaledAnimatedImageDrawable @JvmOverloads constructor(
    val child: AnimatedImageDrawable,
    val fitScale: Boolean = true
) : Drawable(), Drawable.Callback, Animatable2 {

    private var childDx = 0f
    private var childDy = 0f
    private var childScale = 1f

    init {
        child.callback = this
    }

    override fun draw(canvas: Canvas) {
        canvas.withSave {
            translate(childDx, childDy)
            scale(childScale, childScale)
            child.draw(this)
        }
    }

    override fun getAlpha() = DrawableCompat.getAlpha(child)

    override fun setAlpha(alpha: Int) {
        child.alpha = alpha
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun getOpacity() = child.opacity

    override fun getColorFilter(): ColorFilter = DrawableCompat.getColorFilter(child)

    override fun setColorFilter(colorFilter: ColorFilter?) {
        child.colorFilter = colorFilter
    }

    override fun onBoundsChange(bounds: Rect) {
        val width = child.intrinsicWidth
        val height = child.intrinsicHeight
        if (width <= 0 || height <= 0) {
            child.bounds = bounds
            childDx = 0f
            childDy = 0f
            childScale = 1f
            return
        }

        val targetWidth = bounds.width()
        val targetHeight = bounds.height()
        val multiplier = computeSizeMultiplier(width, height, targetWidth, targetHeight, fitScale)

        val left = ((targetWidth - multiplier * width) / 2).roundToInt()
        val top = ((targetHeight - multiplier * height) / 2).roundToInt()
        val right = left + width
        val bottom = top + height
        child.setBounds(left, top, right, bottom)

        childDx = bounds.left.toFloat()
        childDy = bounds.top.toFloat()
        childScale = multiplier.toFloat()
    }

    override fun onLevelChange(level: Int) = child.setLevel(level)

    override fun onStateChange(state: IntArray) = child.setState(state)

    override fun getIntrinsicWidth() = child.intrinsicWidth

    override fun getIntrinsicHeight() = child.intrinsicHeight

    override fun unscheduleDrawable(who: Drawable, what: Runnable) = unscheduleSelf(what)

    override fun invalidateDrawable(who: Drawable) = invalidateSelf()

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) =
        scheduleSelf(what, `when`)

    override fun setTint(tintColor: Int) = DrawableCompat.setTint(child, tintColor)

    override fun setTintList(tint: ColorStateList?) = DrawableCompat.setTintList(child, tint)

    override fun setTintMode(tintMode: PorterDuff.Mode?) =
        DrawableCompat.setTintMode(child, tintMode ?: PorterDuff.Mode.SRC_IN)

    @RequiresApi(29)
    override fun setTintBlendMode(blendMode: BlendMode?) = child.setTintBlendMode(blendMode)

    override fun isRunning() = child.isRunning

    override fun registerAnimationCallback(callback: AnimationCallback) {
        child.registerAnimationCallback(callback)
    }

    override fun unregisterAnimationCallback(callback: AnimationCallback): Boolean {
        return child.unregisterAnimationCallback(callback)
    }

    override fun clearAnimationCallbacks() {
        child.clearAnimationCallbacks()
    }

    override fun start() {
        child.start()
    }

    override fun stop() {
        child.stop()
    }

    override fun mutate(): ScaledAnimatedImageDrawable {
        val mutateDrawable = child.mutate()
        return if (mutateDrawable !== child) {
            ScaledAnimatedImageDrawable(child, fitScale)
        } else {
            this
        }
    }
}
