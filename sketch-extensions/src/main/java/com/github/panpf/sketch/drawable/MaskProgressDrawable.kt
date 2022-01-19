package com.github.panpf.sketch.drawable

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import androidx.annotation.ColorInt
import com.github.panpf.sketch.util.format

class MaskProgressDrawable(
    @ColorInt private val maskColor: Int = DEFAULT_MASK_COLOR
) : ProgressDrawable() {

    companion object {
        const val DEFAULT_MASK_COLOR = 0x22000000
    }

    private val paint = Paint().apply {
        color = maskColor
        isAntiAlias = true
    }

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

    override fun draw(canvas: Canvas) {
        val currentProgress = _progress.takeIf { it >= 0f } ?: return
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

    private fun updateProgress(newProgress: Float) {
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofFloat(_progress, newProgress).apply {
            addUpdateListener {
                if (isActive()) {
                    _progress = animatedValue as Float
                } else {
                    progressAnimator?.cancel()
                }
            }
            duration = 150
        }
        progressAnimator?.start()
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        val changed = super.setVisible(visible, restart)
        if (changed && !visible) {
            progressAnimator?.cancel()
        }
        return changed
    }

    override fun getIntrinsicWidth(): Int = -1

    override fun getIntrinsicHeight(): Int = -1
}