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
package com.github.panpf.sketch.drawable

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Cap.ROUND
import android.graphics.Paint.Style.STROKE
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.core.animation.addListener
import androidx.core.graphics.ColorUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.github.panpf.sketch.util.format
import java.lang.ref.WeakReference

/**
 * Ring Progress Drawable
 */
class RingProgressDrawable(
    private val size: Int = (50f * Resources.getSystem().displayMetrics.density + 0.5f).toInt(),
    private val ringWidth: Float = size * 0.1f,
    @ColorInt private val ringColor: Int = Color.WHITE,
) : ProgressDrawable(), Animatable {

    private val bgPaint = Paint().apply {
        isAntiAlias = true
        color = ColorUtils.setAlphaComponent(ringColor, 60)
        strokeWidth = ringWidth
        style = STROKE
    }
    private var helper: Helper? = null
    private var pendingProgress: Float = 0f

    private var _progress: Float = -1f
        set(value) {
            field = value
            invalidateSelf()
            if (value >= 1f) {
                onProgressEnd?.invoke()
            }
        }

    override var progress: Float
        get() = _progress
        set(value) {
            val newProgress = value.format(1).coerceAtLeast(0f).coerceAtMost(1f)
            if (newProgress != _progress) {
                applyProgress(newProgress)
            }
        }
    override var onProgressEnd: (() -> Unit)? = null

    init {
        applyProgress(0f)
    }

    private fun applyProgress(newProgress: Float) {
        val oldProgress = _progress
        val oldHelper = helper
        if (newProgress == 0f) {
            oldHelper?.stop()
            helper = IndeterminateHelper(WeakReference(this))
            if (isActive()) {
                helper?.start()
            }
            _progress = newProgress
        } else if (newProgress == 1f && oldProgress == 0f) {
            // Here is the loading of the local image, no loading progress, quickly complete
            oldHelper?.stop()
            helper = null
            _progress = newProgress
        } else {
            when {
                oldProgress == 0f && oldHelper is IndeterminateHelper -> {
                    oldHelper.stop()
                    pendingProgress = newProgress
                    val startProgress: (animator: Animator) -> Unit = {
                        helper = ProgressHelper(WeakReference(this)).apply {
                            updateProgress(pendingProgress)
                        }
                        helper?.start()
                    }
                    helper = TransitionHelper(
                        drawableReference = WeakReference(this),
                        initStartAngle = oldHelper.startAngle,
                        onEnd = startProgress
                    )
                    helper?.start()
                }

                oldProgress == 0f && oldHelper is TransitionHelper -> {
                    pendingProgress = newProgress
                }

                oldProgress == 0f && oldHelper is ProgressHelper -> {
                    oldHelper.updateProgress(newProgress)
                }

                oldProgress == 0f -> {
                    helper = ProgressHelper(WeakReference(this)).apply {
                        updateProgress(newProgress)
                    }
                    helper?.start()
                }

                oldHelper is ProgressHelper -> {
                    oldHelper.updateProgress(newProgress)
                }

                else -> {
                    throw IllegalStateException("There was an error")
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds.takeIf { !it.isEmpty } ?: return
        val widthRadius = bounds.width() / 2f
        val heightRadius = bounds.height() / 2f
        val radius = widthRadius.coerceAtMost(heightRadius)
        val cx = bounds.left + widthRadius
        val cy = bounds.top + heightRadius

        val saveCount = canvas.save()

        // background
        canvas.drawCircle(cx, cy, radius, bgPaint)
        helper?.draw(canvas, cx, cy, radius)
        canvas.restoreToCount(saveCount)
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        val changed = super.setVisible(visible, restart)
        if (changed) {
            if (visible) {
                start()
            } else {
                stop()
            }
        }
        return changed
    }

    override fun setAlpha(alpha: Int) {
        helper?.setAlpha(alpha)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        helper?.setColorFilter(colorFilter)
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun start() {
        helper?.start()
    }

    override fun stop() {
        helper?.stop()
    }

    override fun isRunning(): Boolean = helper?.isRunning() == true

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = size

    override fun mutate(): ProgressDrawable {
        return RingProgressDrawable(
            size = size,
            ringWidth = ringWidth,
            ringColor = ringColor
        )
    }

    private interface Helper {
        fun draw(canvas: Canvas, cx: Float, cy: Float, radius: Float)

        fun isRunning(): Boolean

        fun start()

        fun stop()

        fun setAlpha(alpha: Int)

        fun setColorFilter(colorFilter: ColorFilter?)
    }

    // WeakReference: ThreadLocal -> ValueAnimator -> IndeterminateHelper -> drawable -> ImageView
    private class IndeterminateHelper(private val drawableReference: WeakReference<RingProgressDrawable>) :
        Helper {

        companion object {
            private const val TURNS_COUNT = 5
        }

        val drawable: RingProgressDrawable?
            get() = drawableReference.get()

        private val interpolator = FastOutSlowInInterpolator()
        private val indeterminateAnimator: Animator = createAnimator()
        private val oval = RectF()
        private val paint = Paint().apply {
            isAntiAlias = true
            style = STROKE
            drawable?.also {
                strokeWidth = it.ringWidth
                color = it.ringColor
                strokeCap = ROUND
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    alpha = it.alpha
                }
                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                    colorFilter = it.colorFilter
                }
            }
        }
        var startAngle: Float = 0f
            private set(value) {
                field = value
                drawable?.invalidateSelf()
            }

        override fun draw(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
            oval.set(cx - radius, cy - radius, cx + radius, cy + radius)
            canvas.drawArc(oval, startAngle, 100f, false, paint)
        }

        override fun isRunning(): Boolean = indeterminateAnimator.isRunning

        override fun start() {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT && indeterminateAnimator.isPaused) {
                indeterminateAnimator.resume()
            } else {
                indeterminateAnimator.start()
            }
        }

        override fun stop() {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                indeterminateAnimator.pause()
            } else {
                indeterminateAnimator.cancel()
            }
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        private fun createAnimator(): Animator {
            val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1500
                interpolator = LinearInterpolator()
                repeatMode = ValueAnimator.RESTART
                repeatCount = ValueAnimator.INFINITE
            }
            var currentRepeatCount = 0
            var lastValue = 0f
            animator.addUpdateListener {
                if (drawable?.isActive() == true) {
                    val value = (animator.animatedValue as Float).format(2)
                    // 为什么不用 AnimatorListener.onAnimationRepeat 方法来计算圈数，因为它有时会在到达 1.0 时提前回调 onAnimationRepeat 导致圈数提前加 1
                    // 为什么不直接判断 value == 1.0f，因为 value 可能到 0.99 就结束了
                    if (value < lastValue) {
                        currentRepeatCount = (currentRepeatCount + 1) % TURNS_COUNT
                    }
                    startAngle =
                        ((interpolator.getInterpolation(value) * 360f)).format(2)
                    val rotationDegreesOfTurns = 360f / TURNS_COUNT
                    val rotation =
                        (((currentRepeatCount * rotationDegreesOfTurns) + (value * rotationDegreesOfTurns)) % 360f)
                            .format(2)
                    startAngle += rotation
                    startAngle %= 360
                    lastValue = value
                } else {
                    it.cancel()
                }
            }
            animator.addListener(onStart = {
                currentRepeatCount = 0
                lastValue = 0f
            })
            return animator
        }
    }

    // WeakReference: ThreadLocal -> ValueAnimator -> IndeterminateHelper -> drawable -> ImageView
    private class TransitionHelper(
        private val drawableReference: WeakReference<RingProgressDrawable>,
        val initStartAngle: Float,
        val onEnd: (animator: Animator) -> Unit
    ) : Helper {
        val drawable: RingProgressDrawable?
            get() = drawableReference.get()
        private val oval = RectF()
        private val paint = Paint().apply {
            isAntiAlias = true
            style = STROKE
            drawable?.also {
                strokeWidth = it.ringWidth
                color = it.ringColor
                strokeCap = ROUND
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    alpha = it.alpha
                }
                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                    colorFilter = it.colorFilter
                }
            }
        }
        private var startAngle: Float = 0f
            set(value) {
                field = value
                drawable?.invalidateSelf()
            }
        private var sweepAngle: Float = 0f
        private var animator: Animator? = null

        override fun draw(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
            Log.d(
                "RingProgressDrawable",
                "TransitionHelper. draw. startAngle=$startAngle, sweepAngle=$sweepAngle"
            )
            oval.set(cx - radius, cy - radius, cx + radius, cy + radius)
            canvas.drawArc(oval, startAngle, sweepAngle, false, paint)
        }

        override fun isRunning(): Boolean = animator?.isRunning == true

        override fun start() {
            val angleDistance = if (initStartAngle > 270f) {
                360f - (initStartAngle - 270f)
            } else {
                270f - initStartAngle
            }
            animator?.cancel()
            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener {
                    if (drawable?.isActive() == true) {
                        val value = animatedValue as Float
                        startAngle = initStartAngle + (value * angleDistance)
                        startAngle %= 360
                        sweepAngle = ((1f - value) * 100f)
                    } else {
                        it.cancel()
                    }
                }
                addListener(onEnd = this@TransitionHelper.onEnd)
                duration = if (angleDistance >= 180) 600 else 300
                start()
            }
        }

        override fun stop() {
            animator?.end()
        }

        override fun setAlpha(alpha: Int) {

        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
        }
    }

    // WeakReference: ThreadLocal -> ValueAnimator -> IndeterminateHelper -> drawable -> ImageView
    private class ProgressHelper(private val drawableReference: WeakReference<RingProgressDrawable>) :
        Helper {
        val drawable: RingProgressDrawable?
            get() = drawableReference.get()
        private var progressAnimator: ValueAnimator? = null

        private val progressPaint = Paint().apply {
            isAntiAlias = true
            style = STROKE
            drawable?.also {
                strokeWidth = it.ringWidth
                color = it.ringColor
                strokeCap = ROUND
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    alpha = it.alpha
                }
                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                    colorFilter = it.colorFilter
                }
            }
        }
        private val progressOval = RectF()

        override fun draw(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
            val drawable = drawable ?: return
            progressOval.set(cx - radius, cy - radius, cx + radius, cy + radius)
            val sweepAngle = drawable._progress.coerceAtLeast(0.01f) * 360f
            canvas.drawArc(progressOval, 270f, sweepAngle, false, progressPaint)
        }

        fun updateProgress(newProgress: Float) {
            progressAnimator?.cancel()
            val drawable1 = drawable ?: return
            progressAnimator = ValueAnimator.ofFloat(drawable1._progress, newProgress).apply {
                addUpdateListener {
                    if (drawable?.isActive() == true) {
                        drawable?._progress = animatedValue as Float
                    } else {
                        it.cancel()
                    }
                }
                duration = 300
            }
            progressAnimator?.start()
        }

        override fun isRunning(): Boolean = progressAnimator?.isRunning == true

        override fun start() {
        }

        override fun stop() {
            progressAnimator?.end()
        }

        override fun setAlpha(alpha: Int) {
            progressPaint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            progressPaint.colorFilter = colorFilter
        }
    }
}