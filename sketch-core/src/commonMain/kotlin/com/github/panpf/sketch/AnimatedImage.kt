package com.github.panpf.sketch

import com.github.panpf.sketch.util.Rect

/**
 * Animated [Image]
 *
 * @see com.github.panpf.sketch.animated.core.nonandroid.test.SkiaAnimatedImageTest
 */
interface AnimatedImage : Image {

    /**
     * The number of times the animation will repeat. -1: Unlimited repetition
     */
    val repeatCount: Int

    /**
     * The number of frames in the animation
     */
    val frameCount: Int

    /**
     * The duration of each frame in milliseconds.
     */
    val frameDurations: Array<Int>

    /**
     * Whether to cache the decoded timeout frame in memory
     */
    val cacheDecodeTimeoutFrame: Boolean

    /**
     * [com.github.panpf.sketch.transform.AnimatedTransformation]
     */
    var animatedTransformation: ((Any, Rect) -> Unit)?

    /**
     * Callback when the animation starts
     */
    var animationStartCallback: (() -> Unit)?

    /**
     * Callback when the animation ends
     */
    var animationEndCallback: (() -> Unit)?

    /**
     * Create a new [Bitmap] with the specified width and height to hold a single frame of the animation.
     * The default size is the size of the animated image.
     *
     * @param width The width of the frame bitmap.
     * @param height The height of the frame bitmap.
     */
    fun createFrameBitmap(width: Int = this.width, height: Int = this.height): Bitmap

    /**
     * Read a specific frame of the animation into the provided [Bitmap].
     */
    fun readFrame(bitmap: Bitmap, frameIndex: Int)
}