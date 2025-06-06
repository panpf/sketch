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

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.os.Build.VERSION
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.ImageView.ScaleType
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.TintAwareDrawable
import androidx.core.graphics.withSave
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.TransitionDrawable
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateBoundsWithScaleType
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.toLogString
import kotlin.math.max

/**
 * A [Drawable] that crossfades from [start] to [end].
 *
 * NOTE: The animation can only be executed once as the [start]
 * drawable is dereferenced at the end of the transition.
 *
 * @param start The [Drawable] to crossfade from.
 * @param end The [Drawable] to crossfade to.
 * @param durationMillis The duration of the crossfade animation.
 * @param fadeStart If false, the start drawable will not fade out while the end drawable fades in.
 * @param preferExactIntrinsicSize If true, this drawable's intrinsic width/height will only be -1
 *  if [start] **and** [end] return -1 for that dimension. If false, the intrinsic width/height will
 *  be -1 if [start] **or** [end] return -1 for that dimension. This is useful for views that
 *  require an exact intrinsic size to scale the drawable.
 *
 * @see com.github.panpf.sketch.view.core.test.drawable.CrossfadeDrawableTest
 */
class CrossfadeDrawable @JvmOverloads constructor(
    val start: Drawable?,
    val end: Drawable?,
    val scaleType: ScaleType = ScaleType.FIT_CENTER,
    val durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
    val fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
    val preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
) : Drawable(), Animatable2Compat, Callback, SketchDrawable, TransitionDrawable {

    companion object {
        private const val STATE_START = 0
        private const val STATE_RUNNING = 1
        private const val STATE_DONE = 2
    }

    private val callbacks = mutableListOf<Animatable2Compat.AnimationCallback>()
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private val intrinsicWidth =
        computeIntrinsicDimension(start?.intrinsicWidth, end?.intrinsicWidth)
    private val intrinsicHeight =
        computeIntrinsicDimension(start?.intrinsicHeight, end?.intrinsicHeight)

    private var startTimeMillis = 0L
    private var maxAlpha = 255
    private var state = STATE_START

    private var startDrawable: Drawable? = start?.mutate()
    private val endDrawable: Drawable? = end?.mutate()

    @Deprecated("Use scaleType instead.", ReplaceWith("scaleType"))
    val fitScale: Boolean = scaleType.fitScale

    @JvmOverloads
    @Deprecated("Please use a constructor containing the fitScale parameter instead")
    constructor(
        start: Drawable?,
        end: Drawable?,
        fitScale: Boolean,
        durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
        fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
        preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
    ) : this(
        start = start,
        end = end,
        scaleType = if (fitScale) ScaleType.FIT_CENTER else ScaleType.CENTER_CROP,
        durationMillis = durationMillis,
        fadeStart = fadeStart,
        preferExactIntrinsicSize = preferExactIntrinsicSize
    )

    init {
        require(durationMillis > 0) { "durationMillis must be > 0." }
        setChildCallback()
    }

    private fun setChildCallback() {
        this.startDrawable?.apply {
            if (callback == null || callback !== this@CrossfadeDrawable) {
                callback = this@CrossfadeDrawable
            }
        }
        this.endDrawable?.apply {
            if (callback == null || callback !== this@CrossfadeDrawable) {
                callback = this@CrossfadeDrawable
            }
        }
    }

    override fun draw(canvas: Canvas) {
        if (state == STATE_START) {
            startDrawable?.apply {
                alpha = maxAlpha
                canvas.withSave { draw(canvas) }
            }
            return
        }

        if (state == STATE_DONE) {
            endDrawable?.apply {
                alpha = maxAlpha
                canvas.withSave { draw(canvas) }
            }
            return
        }

        val percent = (SystemClock.uptimeMillis() - startTimeMillis) / durationMillis.toDouble()
        val endAlpha = (percent.coerceIn(0.0, 1.0) * maxAlpha).toInt()
        val startAlpha = if (fadeStart) maxAlpha - endAlpha else maxAlpha
        val isDone = percent >= 1.0

        // Draw the start drawable.
        if (!isDone) {
            startDrawable?.apply {
                alpha = startAlpha
                canvas.withSave { draw(canvas) }
            }
        }

        // Draw the end drawable.
        endDrawable?.apply {
            alpha = endAlpha
            canvas.withSave { draw(canvas) }
        }

        if (isDone) {
            markDone()
        } else {
            invalidateSelf()
        }
    }

    override fun getAlpha() = maxAlpha

    override fun setAlpha(alpha: Int) {
        require(alpha in 0..255) { "Invalid alpha: $alpha" }
        maxAlpha = alpha
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun getOpacity(): Int {
        val startDrawable = startDrawable
        val endDrawable = endDrawable

        if (state == STATE_START) {
            return startDrawable?.opacity ?: PixelFormat.TRANSPARENT
        }

        if (state == STATE_DONE) {
            return endDrawable?.opacity ?: PixelFormat.TRANSPARENT
        }

        return when {
            startDrawable != null && endDrawable != null -> resolveOpacity(
                startDrawable.opacity,
                endDrawable.opacity
            )

            startDrawable != null -> startDrawable.opacity
            endDrawable != null -> endDrawable.opacity
            else -> PixelFormat.TRANSPARENT
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun getColorFilter(): ColorFilter? =
        if (VERSION.SDK_INT >= 21) {
            when (state) {
                STATE_START -> startDrawable?.colorFilter
                STATE_RUNNING -> endDrawable?.colorFilter ?: startDrawable?.colorFilter
                STATE_DONE -> endDrawable?.colorFilter
                else -> null
            }
        } else {
            null
        }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        startDrawable?.colorFilter = colorFilter
        endDrawable?.colorFilter = colorFilter
    }

    override fun onBoundsChange(bounds: Rect) {
        /*
         Why set callback here?
         Because when start and the current Drawable of ImageView are the same instance,
         the callback of start will become null after setImageDrawable() is executed.
         Of course, setChildCallback should be called in the setCallback method,
         but it is final and can only be called in onBoundsChange
         */
        setChildCallback()
        startDrawable?.let { updateBounds(it, bounds) }
        endDrawable?.let { updateBounds(it, bounds) }
    }

    override fun onLevelChange(level: Int): Boolean {
        val startChanged = startDrawable?.setLevel(level) ?: false
        val endChanged = endDrawable?.setLevel(level) ?: false
        return startChanged || endChanged
    }

    override fun onStateChange(state: IntArray): Boolean {
        val startChanged = startDrawable?.setState(state) ?: false
        val endChanged = endDrawable?.setState(state) ?: false
        return startChanged || endChanged
    }

    override fun getIntrinsicWidth() = intrinsicWidth

    override fun getIntrinsicHeight() = intrinsicHeight

    override fun setTint(tintColor: Int) {
        startDrawable?.let { DrawableCompat.setTint(it, tintColor) }
        endDrawable?.let { DrawableCompat.setTint(it, tintColor) }
    }

    override fun setTintList(tint: ColorStateList?) {
        startDrawable?.let { DrawableCompat.setTintList(it, tint) }
        endDrawable?.let { DrawableCompat.setTintList(it, tint) }
    }

    @SuppressLint("RestrictedApi", "ObsoleteSdkInt")
    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        if (VERSION.SDK_INT >= 21) {
            startDrawable?.setTintMode(tintMode)
            endDrawable?.setTintMode(tintMode)
        } else {
            if (startDrawable is TintAwareDrawable && tintMode != null) {
                (startDrawable as TintAwareDrawable).setTintMode(tintMode)
            }
            if (endDrawable is TintAwareDrawable && tintMode != null) {
                (endDrawable as TintAwareDrawable).setTintMode(tintMode)
            }
        }
    }

    @RequiresApi(29)
    override fun setTintBlendMode(blendMode: BlendMode?) {
        startDrawable?.setTintBlendMode(blendMode)
        endDrawable?.setTintBlendMode(blendMode)
    }

    override fun isRunning() = state == STATE_RUNNING

    override fun start() {
        (startDrawable as? Animatable)?.start()
        (endDrawable as? Animatable)?.start()

        if (state != STATE_START) {
            return
        }

        state = STATE_RUNNING
        startTimeMillis = SystemClock.uptimeMillis()
        handler.post {
            callbacks.forEach { it.onAnimationStart(this) }
        }

        invalidateSelf()
    }

    override fun stop() {
        (startDrawable as? Animatable)?.stop()
        (endDrawable as? Animatable)?.stop()

        if (state != STATE_DONE) {
            markDone()
        }
    }

    private fun markDone() {
        state = STATE_DONE
        (startDrawable as? Animatable)?.stop()
        startDrawable?.callback = null
        startDrawable = null
        handler.post {
            callbacks.forEach { it.onAnimationEnd(this) }
        }
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        requiredMainThread()    // Consistent with AnimatedImageDrawable
        callbacks.add(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return callbacks.remove(callback)
    }

    override fun clearAnimationCallbacks() = callbacks.clear()

    /** Update the [Drawable]'s bounds inside [targetBounds] preserving aspect ratio. */
    private fun updateBounds(drawable: Drawable, targetBounds: Rect) {
        val srcSize = Size(
            width = drawable.intrinsicWidth,
            height = drawable.intrinsicHeight
        )
        if (srcSize.isEmpty) {
            drawable.bounds = targetBounds
            return
        }

        val drawableBounds = calculateBoundsWithScaleType(
            srcSize = srcSize,
            dstSize = Size(
                width = targetBounds.width(),
                height = targetBounds.height()
            ),
            scaleType = scaleType
        )
        drawable.setBounds(
            /* left = */ targetBounds.left + drawableBounds.left,
            /* top = */ targetBounds.top + drawableBounds.top,
            /* right = */ targetBounds.left + drawableBounds.right,
            /* bottom = */ targetBounds.top + drawableBounds.bottom
        )
    }

    private fun computeIntrinsicDimension(startSize: Int?, endSize: Int?): Int {
        if (preferExactIntrinsicSize || (startSize != -1 && endSize != -1)) {
            return max(startSize ?: -1, endSize ?: -1)
        }
        return -1
    }

    override fun mutate(): CrossfadeDrawable {
        val mutateStart = startDrawable?.mutate()
        val mutateEnd = endDrawable?.mutate()
        return if (mutateStart !== startDrawable || mutateEnd !== endDrawable) {
            CrossfadeDrawable(
                start = mutateStart,
                end = mutateEnd,
                scaleType = scaleType,
                durationMillis = durationMillis,
                fadeStart = fadeStart,
                preferExactIntrinsicSize = preferExactIntrinsicSize
            )
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
        if (javaClass != other?.javaClass) return false
        other as CrossfadeDrawable
        if (start != other.start) return false
        if (end != other.end) return false
        if (scaleType != other.scaleType) return false
        if (durationMillis != other.durationMillis) return false
        if (fadeStart != other.fadeStart) return false
        if (preferExactIntrinsicSize != other.preferExactIntrinsicSize) return false
        return true
    }

    override fun hashCode(): Int {
        var result = start?.hashCode() ?: 0
        result = 31 * result + (end?.hashCode() ?: 0)
        result = 31 * result + (scaleType.hashCode())
        result = 31 * result + durationMillis.hashCode()
        result = 31 * result + fadeStart.hashCode()
        result = 31 * result + preferExactIntrinsicSize.hashCode()
        return result
    }

    override fun toString(): String = "CrossfadeDrawable(" +
            "start=${start?.toLogString()}, " +
            "end=${end?.toLogString()}, " +
            "scaleType=$scaleType, " +
            "durationMillis=$durationMillis, " +
            "fadeStart=$fadeStart, " +
            "preferExactIntrinsicSize=$preferExactIntrinsicSize" +
            ")"
}