@file:Suppress("DEPRECATION", "unused")

package com.github.panpf.sketch.drawable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Movie
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.internal.calculateInSampleSize
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity.OPAQUE
import com.github.panpf.sketch.transform.PixelOpacity.UNCHANGED
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.util.BitmapInfo
import com.github.panpf.sketch.util.byteCountCompat
import com.github.panpf.sketch.util.isHardware

/**
 * A [Drawable] that supports rendering [Movie]s (i.e. GIFs).
 */
class MovieDrawable constructor(
    private val movie: Movie,
    private val config: Bitmap.Config = Bitmap.Config.ARGB_8888,
    private val bitmapPool: BitmapPool?,
) : Drawable(), Animatable2Compat {

    val bitmapInfo: BitmapInfo by lazy {
        softwareBitmap?.let {
            BitmapInfo(it.width, it.height, it.byteCountCompat, it.config)
        } ?: BitmapInfo(0, 0, 0, null)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    private val callbacks = mutableListOf<Animatable2Compat.AnimationCallback>()

    private val currentBounds = Rect()
    private val tempCanvasBounds = Rect()
    private var softwareCanvas: Canvas? = null
    private var softwareBitmap: Bitmap? = null

    private var softwareScale = 1f
    private var hardwareScale = 1f
    private var hardwareDx = 0f
    private var hardwareDy = 0f

    private var isRunning = false
    private var startTimeMillis = 0L
    private var frameTimeMillis = 0L

    private var repeatCount = ANIMATION_REPEAT_INFINITE
    private var loopIteration = 0

    private var animatedTransformation: AnimatedTransformation? = null
    private var animatedTransformationPicture: Picture? = null
    private var pixelOpacity = UNCHANGED
    private var isSoftwareScalingEnabled = false

    init {
        require(!config.isHardware) { "Bitmap config must not be hardware." }
    }

    override fun draw(canvas: Canvas) {
        // Compute the current frame time.
        val invalidate = updateFrameTime()

        // Update the scaling properties and draw the current frame.
        if (isSoftwareScalingEnabled) {
            updateBounds(canvas.bounds)
            val checkpoint = canvas.save()
            try {
                val scale = 1 / softwareScale
                canvas.scale(scale, scale)
                drawFrame(canvas)
            } finally {
                canvas.restoreToCount(checkpoint)
            }
        } else {
            updateBounds(bounds)
            drawFrame(canvas)
        }

        // Request a new draw pass for the next frame if necessary.
        if (isRunning && invalidate) {
            invalidateSelf()
        } else {
            stop()
        }
    }

    /**
     * Compute the current frame time and update [movie].
     * Return 'true' if there are subsequent frames to be rendered.
     */
    private fun updateFrameTime(): Boolean {
        val invalidate: Boolean
        val time: Int
        val duration = movie.duration()
        if (duration == 0) {
            invalidate = false
            time = 0
        } else {
            if (isRunning) {
                frameTimeMillis = SystemClock.uptimeMillis()
            }
            val elapsedTime = (frameTimeMillis - startTimeMillis).toInt()
            loopIteration = elapsedTime / duration
            invalidate = repeatCount == ANIMATION_REPEAT_INFINITE || loopIteration <= repeatCount
            time = if (invalidate) elapsedTime - loopIteration * duration else duration
        }
        movie.setTime(time)
        return invalidate
    }

    /** Draw the current [movie] frame on the [canvas]. */
    private fun drawFrame(canvas: Canvas) {
        val softwareCanvas = softwareCanvas
        val softwareBitmap = softwareBitmap
        if (softwareCanvas == null || softwareBitmap == null) return

        // Clear the software canvas.
        softwareCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // Draw onto a software canvas first.
        val checkpoint = softwareCanvas.save()
        try {
            softwareCanvas.scale(softwareScale, softwareScale)
            movie.draw(softwareCanvas, 0f, 0f, paint)
            animatedTransformationPicture?.draw(softwareCanvas)
        } finally {
            softwareCanvas.restoreToCount(checkpoint)
        }

        // Draw onto the input canvas (may or may not be hardware).
        val checkpoint1 = canvas.save()
        try {
            canvas.translate(hardwareDx, hardwareDy)
            canvas.scale(hardwareScale, hardwareScale)
            canvas.drawBitmap(softwareBitmap, 0f, 0f, paint)
        } finally {
            canvas.restoreToCount(checkpoint1)
        }
    }

    /**
     * Set the number of times to repeat the animation.
     *
     * If the animation is already running, any iterations that have already occurred will
     * count towards the new count.
     *
     * NOTE: This method matches the behavior of [AnimatedImageDrawable.setRepeatCount].
     * i.e. setting [repeatCount] to 2 will result in the animation playing 3 times. Setting
     * [repeatCount] to 0 will result in the animation playing once.
     *
     * Default: [ANIMATION_REPEAT_INFINITE]
     */
    fun setRepeatCount(repeatCount: Int) {
        require(repeatCount >= ANIMATION_REPEAT_INFINITE) { "Invalid repeatCount: $repeatCount" }
        this.repeatCount = repeatCount
    }

    /** Get the number of times the animation will repeat. */
    fun getRepeatCount(): Int = repeatCount

    /** Set the [AnimatedTransformation] to apply when drawing. */
    fun setAnimatedTransformation(animatedTransformation: AnimatedTransformation?) {
        this.animatedTransformation = animatedTransformation

        if (animatedTransformation != null && movie.width() > 0 && movie.height() > 0) {
            // Precompute the animated transformation.
            val picture = Picture()
            val canvas = picture.beginRecording(movie.width(), movie.height())
            pixelOpacity = animatedTransformation.transform(canvas)
            picture.endRecording()
            animatedTransformationPicture = picture
            isSoftwareScalingEnabled = true
        } else {
            // If width/height are not positive, we're unable to draw the movie.
            animatedTransformationPicture = null
            pixelOpacity = UNCHANGED
            isSoftwareScalingEnabled = false
        }

        // Re-render the drawable.
        invalidateSelf()
    }

    /** Get the [AnimatedTransformation]. */
    fun getAnimatedTransformation(): AnimatedTransformation? = animatedTransformation

    override fun setAlpha(alpha: Int) {
        require(alpha in 0..255) { "Invalid alpha: $alpha" }
        paint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return if (paint.alpha == 255 && (pixelOpacity == OPAQUE || (pixelOpacity == UNCHANGED && movie.isOpaque))) {
            PixelFormat.OPAQUE
        } else {
            PixelFormat.TRANSLUCENT
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    private fun updateBounds(bounds: Rect) {
        if (currentBounds == bounds) return
        currentBounds.set(bounds)

        val boundsWidth = bounds.width()
        val boundsHeight = bounds.height()

        // If width/height are not positive, we're unable to draw the movie.
        val movieWidth = movie.width()
        val movieHeight = movie.height()
        if (movieWidth <= 0 || movieHeight <= 0) return

        softwareScale =
            1f / calculateInSampleSize(movieWidth, movieHeight, boundsWidth, boundsHeight)
//        softwareScale = DecodeUtils
//            .computeSizeMultiplier(movieWidth, movieHeight, boundsWidth, boundsHeight, scale)
//            .run { if (isSoftwareScalingEnabled) this else coerceAtMost(1.0) }
//            .toFloat()
        val bitmapWidth = (softwareScale * movieWidth).toInt()
        val bitmapHeight = (softwareScale * movieHeight).toInt()

        val bitmap = bitmapPool?.getOrCreate(bitmapWidth, bitmapHeight, config)
            ?: Bitmap.createBitmap(bitmapWidth, bitmapHeight, config)
        softwareBitmap?.recycle()
        softwareBitmap = bitmap
        softwareCanvas = Canvas(bitmap)

        if (isSoftwareScalingEnabled) {
            hardwareScale = 1f
            hardwareDx = 0f
            hardwareDy = 0f
        } else {
            hardwareScale =
                1f / calculateInSampleSize(bitmapWidth, bitmapHeight, boundsWidth, boundsHeight)
//            hardwareScale = DecodeUtils
//                .computeSizeMultiplier(bitmapWidth, bitmapHeight, boundsWidth, boundsHeight, scale)
//                .toFloat()
            hardwareDx = bounds.left + (boundsWidth - hardwareScale * bitmapWidth) / 2
            hardwareDy = bounds.top + (boundsHeight - hardwareScale * bitmapHeight) / 2
        }
    }

    override fun getIntrinsicWidth() = movie.width()

    override fun getIntrinsicHeight() = movie.height()

    override fun isRunning() = isRunning

    override fun start() {
        if (isRunning) return
        isRunning = true
        // todo create bitmap

        loopIteration = 0
        startTimeMillis = SystemClock.uptimeMillis()

        callbacks.forEach { it.onAnimationStart(this) }
        invalidateSelf()
    }

    override fun stop() {
        if (!isRunning) return
        isRunning = false
        // todo free bitmap
//        bitmapPool.freeBitmapToPool(softwareBitmap)
//        softwareBitmap = null

        callbacks.forEach { it.onAnimationEnd(this) }
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        callbacks.add(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return callbacks.remove(callback)
    }

    override fun clearAnimationCallbacks() = callbacks.clear()

    private val Canvas.bounds get() = tempCanvasBounds.apply { set(0, 0, width, height) }
}
