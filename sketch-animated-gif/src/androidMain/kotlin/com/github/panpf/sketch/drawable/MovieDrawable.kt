/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.drawable

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.HARDWARE
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
import android.os.Build.VERSION
import android.os.SystemClock
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity.OPAQUE
import com.github.panpf.sketch.transform.PixelOpacity.UNCHANGED
import com.github.panpf.sketch.util.calculateScaleMultiplierWithFit

/**
 * A [Drawable] that supports rendering [Movie]s (i.e. GIFs).
 *
 * @see com.github.panpf.sketch.animated.gif.android.test.drawable.MovieDrawableTest
 */
class MovieDrawable(
    private val movie: Movie,
    val config: Bitmap.Config = Bitmap.Config.ARGB_8888,
) : Drawable(), Animatable2Compat, SketchDrawable {

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
        require(!(VERSION.SDK_INT >= 26 && config == HARDWARE)) { "Bitmap config must not be hardware." }
    }

    override fun draw(canvas: Canvas) {
        // Compute the current frame time.
        val invalidate = updateFrameTime()

        // Update the scaling properties and draw the current frame.
        if (isSoftwareScalingEnabled) {
            updateBounds(canvas.bounds)
            canvas.withSave {
                val scale = 1 / softwareScale
                scale(scale, scale)
                drawFrame(canvas)
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
        softwareCanvas.withSave {
            scale(softwareScale, softwareScale)
            movie.draw(this, 0f, 0f, paint)
            animatedTransformationPicture?.draw(this)
        }

        // Draw onto the input canvas (may or may not be hardware).
        canvas.withSave {
            translate(hardwareDx, hardwareDy)
            scale(hardwareScale, hardwareScale)
            drawBitmap(softwareBitmap, 0f, 0f, paint)
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
            val bounds = com.github.panpf.sketch.util.Rect(0, 0, movie.width(), movie.height())
            pixelOpacity = animatedTransformation.transform(canvas, bounds)
            picture.endRecording()
            animatedTransformationPicture = picture
            // Disable software scaling because it will affect the drawing of animatedTransformation
//            isSoftwareScalingEnabled = true
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

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun getOpacity(): Int {
        return if (paint.alpha == 255 &&
            (pixelOpacity == OPAQUE || (pixelOpacity == UNCHANGED && movie.isOpaque))
        ) {
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

        softwareScale = calculateScaleMultiplierWithFit(
            srcWidth = movieWidth.toFloat(),
            srcHeight = movieHeight.toFloat(),
            dstWidth = boundsWidth.toFloat(),
            dstHeight = boundsHeight.toFloat(),
            fitScale = true
        ).run { if (isSoftwareScalingEnabled) this else coerceAtMost(1f) }
        val bitmapWidth = (softwareScale * movieWidth).toInt()
        val bitmapHeight = (softwareScale * movieHeight).toInt()

        val bitmap = createBitmap(bitmapWidth, bitmapHeight, config)
        softwareBitmap?.recycle()
        softwareBitmap = bitmap
        softwareCanvas = Canvas(bitmap)

        if (isSoftwareScalingEnabled) {
            hardwareScale = 1f
            hardwareDx = 0f
            hardwareDy = 0f
        } else {
            hardwareScale = calculateScaleMultiplierWithFit(
                srcWidth = bitmapWidth.toFloat(),
                srcHeight = bitmapHeight.toFloat(),
                dstWidth = boundsWidth.toFloat(),
                dstHeight = boundsHeight.toFloat(),
                fitScale = true
            )
            hardwareDx = bounds.left + (boundsWidth - hardwareScale * bitmapWidth) / 2f
            hardwareDy = bounds.top + (boundsHeight - hardwareScale * bitmapHeight) / 2f
        }
    }

    override fun getIntrinsicWidth() = movie.width()

    override fun getIntrinsicHeight() = movie.height()

    override fun isRunning() = isRunning

    override fun start() {
        if (isRunning) return
        isRunning = true

        loopIteration = 0
        startTimeMillis = SystemClock.uptimeMillis()

        for (index in callbacks.indices) {
            callbacks[index].onAnimationStart(this)
        }
        invalidateSelf()
    }

    override fun stop() {
        if (!isRunning) return
        isRunning = false

        for (index in callbacks.indices) {
            callbacks[index].onAnimationEnd(this)
        }
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        callbacks.add(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return callbacks.remove(callback)
    }

    override fun clearAnimationCallbacks() = callbacks.clear()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MovieDrawable
        if (movie != other.movie) return false
        if (config != other.config) return false
        return true
    }

    override fun hashCode(): Int {
        var result = movie.hashCode()
        result = 31 * result + config.hashCode()
        return result
    }

    override fun toString(): String {
        return "MovieDrawable(size=${movie.width()}x${movie.height()}, config=$config)"
    }

    private val Canvas.bounds get() = tempCanvasBounds.apply { set(0, 0, width, height) }
}
