package com.github.panpf.sketch.drawable

import android.animation.Animator
import android.animation.ValueAnimator
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

// todo developing
class RingProgressDrawable(
    private val size: Int,
    private val ringWidth: Float = size * 0.1f,
    @ColorInt private val ringColor: Int = Color.WHITE,
) : ProgressDrawable(), Animatable {

    private val bgPaint = Paint().apply {
        isAntiAlias = true
        color = ColorUtils.setAlphaComponent(ringColor, 60)
        strokeWidth = ringWidth
        style = STROKE
    }
    private val indeterminateHelper = IndeterminateHelper(this)
    private val progressHelper = ProgressHelper(this)

    private var _progress: Float = 0f
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
            val newValue = value.format(1).coerceAtLeast(0f).coerceAtMost(1f)
            if (newValue != _progress) {
                if (_progress == 0f && newValue == 1f) {
                    // Here is the loading of the local image, no loading progress, quickly complete
                    _progress = newValue
                } else if (newValue > _progress) {
                    if (newValue != 0f && _progress == 0f) {
                        indeterminateHelper.stop()
                        progressHelper.start()
                    }
                    progressHelper.updateProgress(newValue)
                } else {
                    _progress = newValue
                    if (newValue == 0f && _progress != 0f) {
                        indeterminateHelper.start()
                        progressHelper.stop()
                    }
                }
            }
        }
    override var onProgressEnd: (() -> Unit)? = null

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

        if (_progress == 0f) {
            indeterminateHelper.draw(canvas, cx, cy, radius)
        } else {
            progressHelper.draw(canvas, cx, cy, radius)
        }

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
        indeterminateHelper.setAlpha(alpha)
        progressHelper.setAlpha(alpha)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        indeterminateHelper.setColorFilter(colorFilter)
        progressHelper.setColorFilter(colorFilter)
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun start() {
        if (_progress == 0f) {
            indeterminateHelper.start()
        } else {
            progressHelper.start()
        }
    }

    override fun stop() {
        indeterminateHelper.stop()
        progressHelper.stop()
    }

    override fun isRunning(): Boolean =
        indeterminateHelper.isRunning() || progressHelper.isRunning()

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = size

    private class IndeterminateHelper(val drawable: RingProgressDrawable) {

        companion object {
            private const val TURNS_COUNT = 5
        }

        private val oval = RectF()
        private val paint = Paint().apply {
            isAntiAlias = true
            style = STROKE
            strokeWidth = drawable.ringWidth
            color = drawable.ringColor
            strokeCap = ROUND
        }
        private val interpolator = FastOutSlowInInterpolator()
        private val indeterminateAnimator: Animator = createAnimator()

        private var startAngle: Float = 0f
            set(value) {
                field = value
                drawable.invalidateSelf()
            }
        private var rotation = 0f

        fun draw(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
            canvas.rotate(rotation, cx, cy)
            oval.set(cx - radius, cy - radius, cx + radius, cy + radius)
            canvas.drawArc(oval, startAngle, 100f, false, paint)
        }

        fun isRunning(): Boolean = indeterminateAnimator.isRunning

        fun start() {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT && indeterminateAnimator.isPaused) {
                indeterminateAnimator.resume()
            } else {
                indeterminateAnimator.start()
            }
        }

        fun stop() {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                indeterminateAnimator.pause()
            } else {
                indeterminateAnimator.cancel()
            }
        }

        fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        private fun createAnimator(): Animator {
            val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1600
                interpolator = LinearInterpolator()
                repeatMode = ValueAnimator.RESTART
                repeatCount = ValueAnimator.INFINITE
            }
            var currentRepeatCount = 0
            var lastValue = 0f
            animator.addUpdateListener {
                if (drawable.isActive()) {
                    val value = (animator.animatedValue as Float).format(2)
                    // 为什么不用 AnimatorListener.onAnimationRepeat 方法来计算圈数，因为它有时会在到达 1.0 时提前回调 onAnimationRepeat 导致圈数提前加 1
                    // 为什么不直接判断 value == 1.0f，因为 value 可能到 0.99 就结束了
                    if (value < lastValue) {
                        currentRepeatCount = (currentRepeatCount + 1) % TURNS_COUNT
                    }
                    startAngle =
                        ((interpolator.getInterpolation(value) * 360f)).format(2)
                    val rotationDegreesOfTurns = 360f / TURNS_COUNT
                    rotation =
                        (((currentRepeatCount * rotationDegreesOfTurns) + (value * rotationDegreesOfTurns)) % 360f)
                            .format(2)
                    Log.d(
                        "CircleProgressDrawable",
                        "value=$value, startAngle=$startAngle, waitingRotation=$rotation, mRotationCount=$currentRepeatCount"
                    )
                    lastValue = value
                } else {
                    it?.cancel()
                }
            }
            animator.addListener(onStart = {
                currentRepeatCount = 0
                lastValue = 0f
            })
            return animator
        }
    }

    private class ProgressHelper(val drawable: RingProgressDrawable) {
        //        private var bufferedProgress: Float = 0f
//            set(value) {
//                field = value
//                drawable.invalidateSelf()
//            }
        private var progressAnimator: ValueAnimator? = null

        //        private var bufferedProgressAnimator: ValueAnimator? = null
        private val progressPaint = Paint().apply {
            isAntiAlias = true
            style = STROKE
            strokeWidth = drawable.ringWidth
            color = drawable.ringColor
            strokeCap = ROUND
        }

        //        private val bufferedProgressPaint = Paint().apply {
//            isAntiAlias = true
//            style = STROKE
//            strokeWidth = drawable.ringWidth
//            color = ColorUtils.setAlphaComponent(drawable.ringColor, 180)
//            strokeCap = ROUND
//        }
        private val progressOval = RectF()

        fun draw(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
            // _progress
            progressOval.set(cx - radius, cy - radius, cx + radius, cy + radius)
            val sweepAngle = drawable._progress.coerceAtLeast(0.01f) * 360f
            canvas.drawArc(progressOval, 270f, sweepAngle, false, progressPaint)

//         buffered _progress
//        val bufferedProgressSweepAngle = bufferedProgress.coerceAtLeast(0.01f) * 360f
//        canvas.drawArc(progressOval, 270f, bufferedProgressSweepAngle, false, bufferedProgressPaint)
        }

        fun updateProgress(newProgress: Float) {
            progressAnimator?.cancel()
            progressAnimator = ValueAnimator.ofFloat(drawable._progress, newProgress).apply {
                addUpdateListener {
                    if (drawable.isActive()) {
                        drawable._progress = animatedValue as Float
                    } else {
                        it.cancel()
                    }
                }
                duration = 150
            }
            progressAnimator?.start()
        }

        fun isRunning(): Boolean = progressAnimator?.isRunning == true

        fun start() {

            //        bufferedProgressAnimator?.cancel()
            //        bufferedProgressAnimator = ValueAnimator.ofFloat(_progress, 1f).apply {
            //            addUpdateListener {
            //                bufferedProgress = animatedValue as Float
            //        if (callback == null) {
            //            bufferedProgressAnimator?.cancel()
            //        }
            //            }
            //            duration = 2000
            //            interpolator = AccelerateDecelerateInterpolator()
            //            repeatMode = ValueAnimator.RESTART
            //            repeatCount = ValueAnimator.INFINITE
            //        }
            //        bufferedProgressAnimator?.start()
        }

        fun stop() {
            progressAnimator?.end()
        }

        fun setAlpha(alpha: Int) {
            progressPaint.alpha = alpha
//            bufferedProgressPaint.alpha = alpha
        }

        fun setColorFilter(colorFilter: ColorFilter?) {
            progressPaint.colorFilter = colorFilter
//            bufferedProgressPaint.colorFilter = colorFilter
        }
    }
}