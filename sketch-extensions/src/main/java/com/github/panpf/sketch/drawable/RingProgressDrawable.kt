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
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.core.animation.addListener
import androidx.core.graphics.ColorUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.github.panpf.sketch.util.format

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
    private var helper: Helper? = null
//    private var pendingProgress: Float = 0f

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
            helper = IndeterminateHelper(this)
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
            if (oldHelper is ProgressHelper) {
                oldHelper.updateProgress(newProgress)
            } else {
                helper = ProgressHelper(this).apply {
                    updateProgress(newProgress)
                }
                helper?.start()
            }
//            when {
//                oldProgress == 0f && oldHelper is IndeterminateHelper -> {
//                    oldHelper.stop()
//                    pendingProgress = newProgress
//                    helper = TransitionHelper(this, oldHelper.startAngle, oldHelper.rotation) {
////                        helper = null
////                        progress = pendingProgress
//                        helper = ProgressHelper(this).apply {
//                            updateProgress(newProgress)
//                        }
//                        helper?.start()
//                    }
//                    helper?.start()
//                }
//                oldProgress == 0f && oldHelper is TransitionHelper -> {
//                    pendingProgress = newProgress
//                }
//                oldProgress == 0f && oldHelper is ProgressHelper -> {
//                    oldHelper.updateProgress(newProgress)
//                }
//                oldProgress == 0f -> {
//                    helper = ProgressHelper(this).apply {
//                        updateProgress(newProgress)
//                    }
//                    helper?.start()
//                }
//                oldHelper is ProgressHelper -> {
//                    oldHelper.updateProgress(newProgress)
//                }
//                else -> {
//                    throw IllegalStateException("There was an error")
//                }
//            }
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

    private interface Helper {
        fun draw(canvas: Canvas, cx: Float, cy: Float, radius: Float)

        fun isRunning(): Boolean

        fun start()

        fun stop()

        fun setAlpha(alpha: Int)

        fun setColorFilter(colorFilter: ColorFilter?)
    }

    private class IndeterminateHelper(val drawable: RingProgressDrawable) : Helper {

        companion object {
            private const val TURNS_COUNT = 5
        }

        private val interpolator = FastOutSlowInInterpolator()
        private val indeterminateAnimator: Animator = createAnimator()
        private val oval = RectF()
        private val paint = Paint().apply {
            isAntiAlias = true
            style = STROKE
            strokeWidth = drawable.ringWidth
            color = drawable.ringColor
            strokeCap = ROUND
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                alpha = drawable.alpha
            }
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                colorFilter = drawable.colorFilter
            }
        }
        var startAngle: Float = 0f
            private set(value) {
                field = value
                drawable.invalidateSelf()
            }
        var rotation = 0f
            private set

        override fun draw(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
            canvas.rotate(rotation, cx, cy)
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
                duration = 2000
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

//    private class TransitionHelper(
//        val drawable: RingProgressDrawable,
//        val initStartAngle: Float,
//        val initRotation: Float,
//        val onEnd: (animator: Animator) -> Unit
//    ) : Helper {
//        private val oval = RectF()
//        private val paint = Paint().apply {
//            isAntiAlias = true
//            style = STROKE
//            strokeWidth = drawable.ringWidth
//            color = drawable.ringColor
//            strokeCap = ROUND
//            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//                alpha = drawable.alpha
//            }
//            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
//                colorFilter = drawable.colorFilter
//            }
//        }
//        private var startAngle: Float = 0f
//            set(value) {
//                field = value
//                drawable.invalidateSelf()
//            }
//        private var sweepAngle: Float = 0f
//        private var rotation = 0f
//        private var animator: Animator? = null
//
//        override fun draw(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
//            canvas.rotate(rotation, cx, cy)
//            oval.set(cx - radius, cy - radius, cx + radius, cy + radius)
//            canvas.drawArc(oval, startAngle, sweepAngle, false, paint)
//        }
//
//        override fun isRunning(): Boolean = animator?.isRunning == true
//
//        override fun start() {
//            val angleDistance = if (initStartAngle > 270f) {
//                360f - (initStartAngle - 270f)
//            } else {
//                270f - initStartAngle
//            }
//            val rotationDistance = 360 - initRotation
//            animator?.cancel()
//            animator = ValueAnimator.ofFloat(0f, 1f).apply {
//                addUpdateListener {
//                    if (drawable.isActive()) {
//                        val value = animatedValue as Float
//                        rotation = initRotation + (value * rotationDistance)
//                        sweepAngle = ((1f- value) * 100f)
//                        startAngle = initStartAngle + (value * angleDistance)
//                    } else {
//                        it.cancel()
//                    }
//                }
//                addListener(onEnd = this@TransitionHelper.onEnd)
//                duration = 300
//                start()
//            }
//        }
//
//        override fun stop() {
//            animator?.end()
//        }
//
//        override fun setAlpha(alpha: Int) {
//
//        }
//
//        override fun setColorFilter(colorFilter: ColorFilter?) {
//        }
//    }

    private class ProgressHelper(val drawable: RingProgressDrawable) : Helper {
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
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                alpha = drawable.alpha
            }
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                colorFilter = drawable.colorFilter
            }
        }

        //        private val bufferedProgressPaint = Paint().apply {
//            isAntiAlias = true
//            style = STROKE
//            strokeWidth = drawable.ringWidth
//            color = ColorUtils.setAlphaComponent(drawable.ringColor, 180)
//            strokeCap = ROUND
//        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//            alpha = drawable.alpha
//        }
//        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
//            colorFilter = drawable.colorFilter
//        }
//        }
        private val progressOval = RectF()

        override fun draw(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
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
                duration = 300
            }
            progressAnimator?.start()
        }

        override fun isRunning(): Boolean = progressAnimator?.isRunning == true

        override fun start() {

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

        override fun stop() {
            progressAnimator?.end()
        }

        override fun setAlpha(alpha: Int) {
            progressPaint.alpha = alpha
//            bufferedProgressPaint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            progressPaint.colorFilter = colorFilter
//            bufferedProgressPaint.colorFilter = colorFilter
        }
    }
}