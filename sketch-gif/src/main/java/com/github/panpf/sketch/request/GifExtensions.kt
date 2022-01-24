package com.github.panpf.sketch.request

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.decode.GifDrawableDecoder
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.transform.AnimatedTransformation

/**
 * Set the number of times to repeat the animation if the result is an animated [Drawable].
 *
 * Default: [MovieDrawable.REPEAT_INFINITE]
 *
 * @see MovieDrawable.setRepeatCount
 * @see AnimatedImageDrawable.setRepeatCount
 */
fun DisplayRequest.Builder.repeatCount(repeatCount: Int): DisplayRequest.Builder {
    require(repeatCount >= MovieDrawable.REPEAT_INFINITE) { "Invalid repeatCount: $repeatCount" }
    return setParameter(GifDrawableDecoder.REPEAT_COUNT_KEY, repeatCount)
}

/**
 * Get the number of times to repeat the animation if the result is an animated [Drawable].
 */
fun DisplayRequest.repeatCount(): Int? = parameters?.value(GifDrawableDecoder.REPEAT_COUNT_KEY)

/**
 * Set the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 *
 * Default: `null`
 *
 * @see MovieDrawable.setAnimatedTransformation
 * @see ImageDecoder.setPostProcessor
 */
fun DisplayRequest.Builder.animatedTransformation(animatedTransformation: AnimatedTransformation): DisplayRequest.Builder {
    return setParameter(GifDrawableDecoder.ANIMATED_TRANSFORMATION_KEY, animatedTransformation)
}

/**
 * Get the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 */
fun DisplayRequest.animatedTransformation(): AnimatedTransformation? = parameters?.value(GifDrawableDecoder.ANIMATED_TRANSFORMATION_KEY)

/**
 * Set the callback to be invoked at the start of the animation if the result is an animated [Drawable].
 */
fun DisplayRequest.Builder.onAnimationStart(callback: (() -> Unit)?): DisplayRequest.Builder {
    return setParameter(GifDrawableDecoder.ANIMATION_START_CALLBACK_KEY, callback)
}

/**
 * Get the callback to be invoked at the start of the animation if the result is an animated [Drawable].
 */
fun DisplayRequest.animationStartCallback(): (() -> Unit)? = parameters?.value(GifDrawableDecoder.ANIMATION_START_CALLBACK_KEY)

/**
 * Set the callback to be invoked at the end of the animation if the result is an animated [Drawable].
 */
fun DisplayRequest.Builder.onAnimationEnd(callback: (() -> Unit)?): DisplayRequest.Builder {
    return setParameter(GifDrawableDecoder.ANIMATION_END_CALLBACK_KEY, callback)
}

/**
 * Get the callback to be invoked at the end of the animation if the result is an animated [Drawable].
 */
fun DisplayRequest.animationEndCallback(): (() -> Unit)? = parameters?.value(GifDrawableDecoder.ANIMATION_END_CALLBACK_KEY)
