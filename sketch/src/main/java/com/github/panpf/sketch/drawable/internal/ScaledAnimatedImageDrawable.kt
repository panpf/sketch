/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import android.graphics.drawable.Drawable.Callback
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.withSave
import com.github.panpf.sketch.decode.internal.computeSizeMultiplier
import com.github.panpf.sketch.util.requiredMainThread
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
) : Drawable(), Animatable2, Callback {

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

    @Deprecated("Deprecated in Java", ReplaceWith("child.opacity"))
    @Suppress("DEPRECATION")
    override fun getOpacity() = child.opacity

    override fun getColorFilter(): ColorFilter? = DrawableCompat.getColorFilter(child)

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

    override fun setTint(tintColor: Int) = DrawableCompat.setTint(child, tintColor)

    override fun setTintList(tint: ColorStateList?) = DrawableCompat.setTintList(child, tint)

    override fun setTintMode(tintMode: PorterDuff.Mode?) =
        DrawableCompat.setTintMode(child, tintMode ?: PorterDuff.Mode.SRC_IN)

    @RequiresApi(29)
    override fun setTintBlendMode(blendMode: BlendMode?) = child.setTintBlendMode(blendMode)

    override fun isRunning() = child.isRunning

    override fun registerAnimationCallback(callback: AnimationCallback) {
        requiredMainThread()    // Consistent with AnimatedImageDrawable
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

    override fun invalidateDrawable(who: Drawable) {
        invalidateSelf()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        scheduleSelf(what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        unscheduleSelf(what)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScaledAnimatedImageDrawable) return false
        if (child != other.child) return false
        if (fitScale != other.fitScale) return false
        return true
    }

    override fun hashCode(): Int {
        var result = child.hashCode()
        result = 31 * result + fitScale.hashCode()
        return result
    }

    override fun toString(): String {
        return "ScaledAnimatedImageDrawable(child=$child, fitScale=$fitScale)"
    }
}
