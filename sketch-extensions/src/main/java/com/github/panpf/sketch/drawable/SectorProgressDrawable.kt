package com.github.panpf.sketch.drawable

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.PixelFormat
import android.graphics.RectF
import com.github.panpf.sketch.util.format

/**
 * Sector progress Drawable
 */
class SectorProgressDrawable(
    private val size: Int,
    private val backgroundColor: Int,
    private val strokeColor: Int,
    private val progressColor: Int,
    private val strokeWidth: Float,
) : ProgressDrawable() {

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        color = backgroundColor
    }
    private val strokePaint = Paint().apply {
        isAntiAlias = true
        color = strokeColor
        strokeWidth = this@SectorProgressDrawable.strokeWidth
        style = STROKE
    }
    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = FILL
        color = progressColor
    }
    private val progressOval = RectF()

    private var _progress: Float = 0f
        set(value) {
            field = value
            invalidateSelf()
            if (value >= 1f) {
                onProgressEnd?.invoke()
            }
        }

    private var progressAnimator: ValueAnimator? = null

    override var progress: Float
        get() = _progress
        set(value) {
            val newValue = value.format(1).coerceAtLeast(0f).coerceAtMost(1f)
            if (newValue != _progress) {
                if (_progress == 0f && newValue == 1f) {
                    // Here is the loading of the local image, no loading progress, quickly complete
                    _progress = newValue
                } else if (newValue > _progress) {
                    animationUpdateProgress(newValue)
                } else {
                    // If newValue is less than _progress, you can reset it quickly
                    _progress = newValue
                }
            }
        }
    override var onProgressEnd: (() -> Unit)? = null

    private fun animationUpdateProgress(newProgress: Float) {
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofFloat(_progress, newProgress).apply {
            addUpdateListener {
                if (isActive()) {
                    _progress = animatedValue as Float
                } else {
                    it?.cancel()
                }
            }
            duration = 300
        }
        progressAnimator?.start()
    }

    override fun draw(canvas: Canvas) {
        val progress = _progress.takeIf { it >= 0f } ?: return
        val bounds = bounds.takeIf { !it.isEmpty } ?: return
        val saveCount = canvas.save()

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

        canvas.restoreToCount(saveCount)
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

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        val changed = super.setVisible(visible, restart)
        if (changed && !visible) {
            progressAnimator?.cancel()
        }
        return changed
    }

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = size
}