package com.github.panpf.sketch.drawable

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Animatable
import androidx.annotation.FloatRange

class ArcProgressDrawable(
    private val size: Int,
    private val backgroundColor: Int,
    private val strokeColor: Int,
    private val progressColor: Int,
    private val strokeWidth: Float,
) : ProgressDrawable(), Animatable {

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        color = backgroundColor
    }
    private val strokePaint = Paint().apply {
        isAntiAlias = true
        color = strokeColor
        strokeWidth = this@ArcProgressDrawable.strokeWidth
        style = STROKE
    }
    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = FILL
        color = progressColor
    }
    private val progressOval = RectF()
    private var progress: Float = -1F
        set(value) {
            field = value
            invalidateSelf()
        }
    private var progressAnimator: ValueAnimator? = null

    override fun draw(canvas: Canvas) {
        val progress = progress.takeIf { it >= 0f } ?: return
        val bounds = bounds.takeIf { !it.isEmpty } ?: return
        canvas.save()

        // background
        val widthRadius = bounds.width() / 2f
        val heightRadius = bounds.height() / 2f
        val radius = widthRadius.coerceAtMost(heightRadius)
        val cx = bounds.left + widthRadius
        val cy = bounds.top + heightRadius
        canvas.drawCircle(cx, cy, radius, backgroundPaint)

        // stroke
        canvas.drawCircle(cx, cy, radius, strokePaint)

        // progress
        val space = strokeWidth * 2
        progressOval.set(
            cx - radius + space,
            cy - radius + space,
            cx + radius - space,
            cy + radius - space,
        )
        val sweepAngle = progress.coerceAtLeast(0.01f) * 360f
        canvas.drawArc(progressOval, 270f, sweepAngle, true, progressPaint)

        canvas.restore()
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

    override fun updateProgress(
        @FloatRange(from = 0.0, to = 1.0) newProgress: Float,
        onAnimationEnd: (() -> Unit)?
    ) {
        val targetProgress = newProgress.coerceAtLeast(0f).coerceAtMost(1f)
        val lastProgress = progress
        if (lastProgress < targetProgress) {
            progressAnimator?.cancel()
            progressAnimator = ValueAnimator.ofFloat(lastProgress, targetProgress).apply {
                addUpdateListener {
                    progress = animatedValue as Float
                }
                if (callback == null) {
                    progressAnimator?.cancel()
                }
                if (onAnimationEnd != null) {
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            onAnimationEnd()
                        }
                    })
                }
                duration = 150
            }
            progressAnimator?.start()
        } else {
            onAnimationEnd?.invoke()
        }
    }

    override fun start() {

    }

    override fun stop() {
        progressAnimator?.cancel()
    }

    override fun isRunning(): Boolean = progressAnimator?.isRunning == true

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = size
}