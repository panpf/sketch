package com.github.panpf.sketch.drawable

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Animatable
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

class MaskProgressDrawable(
    @ColorInt private val maskColor: Int = DEFAULT_MASK_COLOR
) : ProgressDrawable(), Animatable {

    companion object {
        const val DEFAULT_MASK_COLOR = 0x22000000
    }

    private val paint = Paint().apply {
        color = maskColor
        isAntiAlias = true
    }
    private var progress: Float = 0f
        set(value) {
            field = value
            invalidateSelf()
        }
    private var progressAnimator: ValueAnimator? = null

    override fun draw(canvas: Canvas) {
        val currentProgress = progress.takeIf { it >= 0f } ?: return
        val bounds = bounds.takeIf { !it.isEmpty } ?: return
        canvas.save()

        canvas.drawRect(
            bounds.left.toFloat(),
            bounds.top + ((currentProgress * bounds.height())),
            bounds.right.toFloat(),
            bounds.bottom.toFloat(),
            paint
        )

        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
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

    override fun getIntrinsicWidth(): Int = -1

    override fun getIntrinsicHeight(): Int = -1
}