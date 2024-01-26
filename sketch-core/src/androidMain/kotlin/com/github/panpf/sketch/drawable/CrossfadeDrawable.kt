/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------------
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
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.TintAwareDrawable
import androidx.core.graphics.withSave
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.decode.internal.computeSizeMultiplier
import com.github.panpf.sketch.drawable.internal.SketchDrawable
import com.github.panpf.sketch.drawable.internal.toLogString
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.requiredMainThread
import kotlin.math.max
import kotlin.math.roundToInt

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
 */
class CrossfadeDrawable @JvmOverloads constructor(
    val start: Drawable?,
    val end: Drawable?,
    val fitScale: Boolean = true,
    val durationMillis: Int = Transition.DEFAULT_DURATION,
    val fadeStart: Boolean = true,
    val preferExactIntrinsicSize: Boolean = false,
) : Drawable(), Animatable2Compat, Callback, SketchDrawable {

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

    private var _start: Drawable? = start?.mutate()
    private val _end: Drawable? = end?.mutate()

    init {
        require(durationMillis > 0) { "durationMillis must be > 0." }
        setChildCallback()
    }

    private fun setChildCallback() {
        this._start?.apply {
            if (callback == null || callback !== this@CrossfadeDrawable) {
                callback = this@CrossfadeDrawable
            }
        }
        this._end?.apply {
            if (callback == null || callback !== this@CrossfadeDrawable) {
                callback = this@CrossfadeDrawable
            }
        }
    }

    override fun draw(canvas: Canvas) {
        if (state == STATE_START) {
            _start?.apply {
                alpha = maxAlpha
                canvas.withSave { draw(canvas) }
            }
            return
        }

        if (state == STATE_DONE) {
            _end?.apply {
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
            _start?.apply {
                alpha = startAlpha
                canvas.withSave { draw(canvas) }
            }
        }

        // Draw the end drawable.
        _end?.apply {
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
        val _start = _start
        val _end = _end

        if (state == STATE_START) {
            return _start?.opacity ?: PixelFormat.TRANSPARENT
        }

        if (state == STATE_DONE) {
            return _end?.opacity ?: PixelFormat.TRANSPARENT
        }

        return when {
            _start != null && _end != null -> resolveOpacity(_start.opacity, _end.opacity)
            _start != null -> _start.opacity
            _end != null -> _end.opacity
            else -> PixelFormat.TRANSPARENT
        }
    }

    override fun getColorFilter(): ColorFilter? =
        if (VERSION.SDK_INT >= 21) {
            when (state) {
                STATE_START -> _start?.colorFilter
                STATE_RUNNING -> _end?.colorFilter ?: _start?.colorFilter
                STATE_DONE -> _end?.colorFilter
                else -> null
            }
        } else {
            null
        }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        _start?.colorFilter = colorFilter
        _end?.colorFilter = colorFilter
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
        _start?.let { updateBounds(it, bounds) }
        _end?.let { updateBounds(it, bounds) }
    }

    override fun onLevelChange(level: Int): Boolean {
        val startChanged = _start?.setLevel(level) ?: false
        val endChanged = _end?.setLevel(level) ?: false
        return startChanged || endChanged
    }

    override fun onStateChange(state: IntArray): Boolean {
        val startChanged = _start?.setState(state) ?: false
        val endChanged = _end?.setState(state) ?: false
        return startChanged || endChanged
    }

    override fun getIntrinsicWidth() = intrinsicWidth

    override fun getIntrinsicHeight() = intrinsicHeight

    override fun setTint(tintColor: Int) {
        _start?.let { DrawableCompat.setTint(it, tintColor) }
        _end?.let { DrawableCompat.setTint(it, tintColor) }
    }

    override fun setTintList(tint: ColorStateList?) {
        _start?.let { DrawableCompat.setTintList(it, tint) }
        _end?.let { DrawableCompat.setTintList(it, tint) }
    }

    @SuppressLint("RestrictedApi")
    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        if (VERSION.SDK_INT >= 21) {
            _start?.setTintMode(tintMode)
            _end?.setTintMode(tintMode)
        } else {
            if (_start is TintAwareDrawable && tintMode != null) {
                (_start as TintAwareDrawable).setTintMode(tintMode)
            }
            if (_end is TintAwareDrawable && tintMode != null) {
                (_end as TintAwareDrawable).setTintMode(tintMode)
            }
        }
    }

    @RequiresApi(29)
    override fun setTintBlendMode(blendMode: BlendMode?) {
        _start?.setTintBlendMode(blendMode)
        _end?.setTintBlendMode(blendMode)
    }

    override fun isRunning() = state == STATE_RUNNING

    override fun start() {
        (_start as? Animatable)?.start()
        (_end as? Animatable)?.start()

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
        (_start as? Animatable)?.stop()
        (_end as? Animatable)?.stop()

        if (state != STATE_DONE) {
            markDone()
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
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        if (width <= 0 || height <= 0) {
            drawable.bounds = targetBounds
            return
        }

        val targetWidth = targetBounds.width()
        val targetHeight = targetBounds.height()
        val multiplier = computeSizeMultiplier(
            srcWidth = width,
            srcHeight = height,
            dstWidth = targetWidth,
            dstHeight = targetHeight,
            fitScale = fitScale
        )
        val dx = ((targetWidth - multiplier * width) / 2).roundToInt()
        val dy = ((targetHeight - multiplier * height) / 2).roundToInt()

        val left = targetBounds.left + dx
        val top = targetBounds.top + dy
        val right = targetBounds.right - dx
        val bottom = targetBounds.bottom - dy
        drawable.setBounds(left, top, right, bottom)
    }

    private fun computeIntrinsicDimension(startSize: Int?, endSize: Int?): Int {
        if (preferExactIntrinsicSize || (startSize != -1 && endSize != -1)) {
            return max(startSize ?: -1, endSize ?: -1)
        }
        return -1
    }

    private fun markDone() {
        state = STATE_DONE
        _start = null
        handler.post {
            callbacks.forEach { it.onAnimationEnd(this) }
        }
    }

    override fun mutate(): CrossfadeDrawable {
        val newStart = _start?.mutate()
        val newEnd = _end?.mutate()
        return if (newStart !== _start || newEnd !== _end) {
            CrossfadeDrawable(
                start = newStart,
                end = newEnd,
                fitScale = fitScale,
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
        if (fitScale != other.fitScale) return false
        if (durationMillis != other.durationMillis) return false
        if (fadeStart != other.fadeStart) return false
        return preferExactIntrinsicSize == other.preferExactIntrinsicSize
    }

    override fun hashCode(): Int {
        var result = start?.hashCode() ?: 0
        result = 31 * result + (end?.hashCode() ?: 0)
        result = 31 * result + fitScale.hashCode()
        result = 31 * result + durationMillis
        result = 31 * result + fadeStart.hashCode()
        result = 31 * result + preferExactIntrinsicSize.hashCode()
        return result
    }

    override fun toString(): String {
        return "CrossfadeDrawable(start=${start?.toLogString()}, end=${end?.toLogString()}, fitScale=$fitScale, durationMillis=$durationMillis, fadeStart=$fadeStart, preferExactIntrinsicSize=$preferExactIntrinsicSize)"
    }
}