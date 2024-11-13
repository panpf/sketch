/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

package com.github.panpf.sketch.drawable

import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.withSave
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.internal.AnimatableCallbackHelper
import com.github.panpf.sketch.util.computeScaleMultiplierWithFit
import com.github.panpf.sketch.util.toLogString
import kotlin.math.roundToInt

/**
 * A [Drawable] that centers and scales its [drawable] to fill its bounds.
 *
 * This allows drawables that only draw within their intrinsic dimensions
 * (e.g. [AnimatedImageDrawable]) to fill their entire bounds.
 *
 * @see com.github.panpf.sketch.animated.core.android.test.drawable.ScaledAnimatableDrawableTest
 */
class ScaledAnimatableDrawable @JvmOverloads constructor(
    val drawable: Drawable,
    val fitScale: Boolean = true
) : Drawable(), Animatable2Compat, Callback, SketchDrawable {

    private var childDx = 0f
    private var childDy = 0f
    private var childScale = 1f

    private var callbackHelper: AnimatableCallbackHelper? = null

    init {
        require(drawable is Animatable) { "Drawable must be an Animatable" }
        callbackHelper = AnimatableCallbackHelper(drawable)
        drawable.callback = this
    }

    override fun draw(canvas: Canvas) {
        canvas.withSave {
            translate(childDx, childDy)
            scale(childScale, childScale)
            drawable.draw(this)
        }
    }

    override fun getAlpha() = DrawableCompat.getAlpha(drawable)

    override fun setAlpha(alpha: Int) {
        drawable.alpha = alpha
    }

    @Deprecated("Deprecated in Java", ReplaceWith("child.opacity"))
    override fun getOpacity() = drawable.opacity

    override fun getColorFilter(): ColorFilter? = DrawableCompat.getColorFilter(drawable)

    override fun setColorFilter(colorFilter: ColorFilter?) {
        drawable.colorFilter = colorFilter
    }

    override fun onBoundsChange(bounds: Rect) {
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        if (width <= 0 || height <= 0) {
            drawable.bounds = bounds
            childDx = 0f
            childDy = 0f
            childScale = 1f
            return
        }

        val targetWidth = bounds.width()
        val targetHeight = bounds.height()
        val multiplier =
            computeScaleMultiplierWithFit(width, height, targetWidth, targetHeight, fitScale)

        val left = ((targetWidth - multiplier * width) / 2).roundToInt()
        val top = ((targetHeight - multiplier * height) / 2).roundToInt()
        val right = left + width
        val bottom = top + height
        drawable.setBounds(left, top, right, bottom)

        childDx = bounds.left.toFloat()
        childDy = bounds.top.toFloat()
        childScale = multiplier.toFloat()
    }

    override fun onLevelChange(level: Int) = drawable.setLevel(level)

    override fun onStateChange(state: IntArray) = drawable.setState(state)

    override fun getIntrinsicWidth() = drawable.intrinsicWidth

    override fun getIntrinsicHeight() = drawable.intrinsicHeight

    override fun setTint(tintColor: Int) = DrawableCompat.setTint(drawable, tintColor)

    override fun setTintList(tint: ColorStateList?) = DrawableCompat.setTintList(drawable, tint)

    override fun setTintMode(tintMode: PorterDuff.Mode?) =
        DrawableCompat.setTintMode(drawable, tintMode ?: PorterDuff.Mode.SRC_IN)

    @RequiresApi(29)
    override fun setTintBlendMode(blendMode: BlendMode?) = drawable.setTintBlendMode(blendMode)

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        callbackHelper?.registerAnimationCallback(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return callbackHelper?.unregisterAnimationCallback(callback) == true
    }

    override fun clearAnimationCallbacks() {
        callbackHelper?.clearAnimationCallbacks()
    }

    override fun start() {
        callbackHelper?.start()
    }

    override fun stop() {
        callbackHelper?.stop()
    }

    override fun isRunning(): Boolean {
        return callbackHelper?.isRunning == true
    }

    override fun mutate(): ScaledAnimatableDrawable {
        val mutateDrawable = drawable.mutate()
        return if (mutateDrawable !== drawable) {
            ScaledAnimatableDrawable(drawable, fitScale)
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
        if (other == null || this::class != other::class) return false
        other as ScaledAnimatableDrawable
        if (drawable != other.drawable) return false
        if (fitScale != other.fitScale) return false
        return true
    }

    override fun hashCode(): Int {
        var result = drawable.hashCode()
        result = 31 * result + fitScale.hashCode()
        return result
    }

    override fun toString(): String {
        return "ScaledAnimatableDrawable(drawable=${drawable.toLogString()}, fitScale=$fitScale)"
    }
}
