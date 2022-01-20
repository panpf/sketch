package com.github.panpf.sketch.drawable

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Cap.ROUND
import android.graphics.Paint.Style.STROKE
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.github.panpf.sketch.util.format

// todo 还没写完
class NewCircleProgressDrawable(
    private val size: Int,
    @ColorInt private val backgroundColor: Int,
    @ColorInt private val strokeColor: Int,
    @ColorInt private val progressColor: Int,
    private val strokeWidth: Float,
) : ProgressDrawable(), Animatable {

    companion object {
        private const val TURNS_COUNT = 5
    }

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        color = backgroundColor
    }
    private val strokePaint = Paint().apply {
        isAntiAlias = true
        color = ColorUtils.setAlphaComponent(strokeColor, 60)
        strokeWidth = size * 0.1f
        style = STROKE
    }
    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = STROKE
        strokeWidth = size * 0.1f
        color = progressColor
        strokeCap = ROUND
    }
    private val bufferedProgressPaint = Paint().apply {
        isAntiAlias = true
        style = STROKE
        strokeWidth = size * 0.1f
        color = ColorUtils.setAlphaComponent(strokeColor, 180)
        strokeCap = ROUND
    }
    private val progressOval = RectF()
    private val waitingInterpolator = FastOutSlowInInterpolator()

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
            val valueFormat = value.format(1).coerceAtLeast(0f).coerceAtMost(1f)
            if (valueFormat != _progress) {
                if (valueFormat > _progress) {
                    updateProgress(valueFormat)
                } else {
                    _progress = valueFormat
                }
            }
        }
    override var onProgressEnd: (() -> Unit)? = null
    private var bufferedProgress: Float = 0f
        set(value) {
            field = value
            invalidateSelf()
        }
    private var progressAnimator: ValueAnimator? = null
    private var bufferedProgressAnimator: ValueAnimator? = null
    private var waitingArcStartAngle: Float = 0f
        set(value) {
            field = value
            invalidateSelf()
        }
    private var waitingAnimator: ValueAnimator? = null
    private var waitingRotation = 0f
    private var mRotationCount = 0

    override fun draw(canvas: Canvas) {
//        val _progress = _progress.takeIf { it >= 0f } ?: return
        val bounds = bounds.takeIf { !it.isEmpty } ?: return
        val widthRadius = bounds.width() / 2f
        val heightRadius = bounds.height() / 2f
        val radius = widthRadius.coerceAtMost(heightRadius)
        val cx = bounds.left + widthRadius
        val cy = bounds.top + heightRadius
        val saveCount = canvas.save()

        // background
        canvas.drawCircle(cx, cy, radius, strokePaint)
        if (_progress == 0f) {
            drawWaiting(canvas, bounds, cx, cy, radius)
        } else {
            drawableProgress(canvas, bounds, cx, cy, radius, _progress)
        }

        canvas.restoreToCount(saveCount)
    }

    private fun drawWaiting(canvas: Canvas, bounds: Rect, cx: Float, cy: Float, radius: Float) {
        canvas.rotate(waitingRotation, cx, cy)
//        canvas.rotate(330f, cx, cy)

        // _progress
        progressOval.set(cx - radius, cy - radius, cx + radius, cy + radius)
        val startAngle = waitingArcStartAngle
//        val startAngle = 270f
//        val startAngle = 90f
        val sweepAngle = 100f
//        val sweepAngle = (startAngle + 90)
        canvas.drawArc(
            progressOval,
            startAngle,
            sweepAngle,
            false,
            progressPaint
        )
    }

    private fun drawableProgress(
        canvas: Canvas,
        bounds: Rect,
        cx: Float,
        cy: Float,
        radius: Float,
        progress: Float
    ) {

//        // _progress
//        progressOval.set(cx - radius, cy - radius, cx + radius, cy + radius)
//        val sweepAngle = _progress.coerceAtLeast(0.01f) * 360f
//        canvas.drawArc(progressOval, 270f, sweepAngle, false, progressPaint)
//
//        // buffered _progress
//        val bufferedProgressSweepAngle = bufferedProgress.coerceAtLeast(0.01f) * 360f
//        canvas.drawArc(progressOval, 270f, bufferedProgressSweepAngle, false, bufferedProgressPaint)
    }

    override fun setAlpha(alpha: Int) {
        backgroundPaint.alpha = alpha
        strokePaint.alpha = alpha
        progressPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        backgroundPaint.colorFilter = colorFilter
        strokePaint.colorFilter = colorFilter
        progressPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    private fun updateProgress(newProgress: Float) {
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofFloat(_progress, newProgress).apply {
            addUpdateListener {
                _progress = animatedValue as Float
            }
            if (callback == null) {
                progressAnimator?.cancel()
            }
            duration = 150
        }
        progressAnimator?.start()
    }

    override fun start() {
        if (_progress == 0f) {
            waitingAnimator?.cancel()
            mRotationCount = 0
            var lastValue = 0f
            waitingAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener {
                    if (isActive()) {
                        val value = (animatedValue as Float).format(2)
                        // 为什么不用 AnimatorListener.onAnimationRepeat 方法来计算圈数，因为它有时会在到达 1.0 时提前回调 onAnimationRepeat 导致圈数提前加 1
                        // 为什么不直接判断 value == 1.0f，因为 value 可能到 0.99 就结束了
                        if (value < lastValue) {
                            mRotationCount = (mRotationCount + 1) % TURNS_COUNT
                        }
                        waitingArcStartAngle =
                            ((waitingInterpolator.getInterpolation(value) * 360f)).format(2)
                        val rotationDegreesOfTurn = 360f / TURNS_COUNT
                        waitingRotation = (((mRotationCount * rotationDegreesOfTurn) + (value * rotationDegreesOfTurn)) % 360f).format(2)
                        Log.d(
                            "CircleProgressDrawable",
                            "value=$value, startAngle=$waitingArcStartAngle, waitingRotation=$waitingRotation, mRotationCount=$mRotationCount"
                        )
                        lastValue = value
                    } else {
                        waitingAnimator?.cancel()
                    }
                }
                duration = 1600
                interpolator = LinearInterpolator()
                repeatMode = ValueAnimator.RESTART
                repeatCount = ValueAnimator.INFINITE
            }
            waitingAnimator?.start()
        } else {
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
    }

    override fun stop() {
        progressAnimator?.cancel()
        bufferedProgressAnimator?.cancel()
        waitingAnimator?.cancel()
    }

    override fun isRunning(): Boolean = progressAnimator?.isRunning == true

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = size

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        val changed = super.setVisible(visible, restart)
        stop()
        return changed
    }
}