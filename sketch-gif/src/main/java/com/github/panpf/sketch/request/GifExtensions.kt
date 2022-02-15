package com.github.panpf.sketch.request

import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.transform.AnimatedTransformation

const val ANIMATED_TRANSFORMATION_KEY = "sketch#animated_transformation"

/**
 * Set the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 *
 * Default: `null`
 *
 * @see MovieDrawable.setAnimatedTransformation
 * @see ImageDecoder.setPostProcessor
 */
fun DisplayRequest.Builder.animatedTransformation(animatedTransformation: AnimatedTransformation): DisplayRequest.Builder {
    return setParameter(ANIMATED_TRANSFORMATION_KEY, animatedTransformation)
}

/**
 * Get the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 */
fun DisplayRequest.animatedTransformation(): AnimatedTransformation? = parameters?.value(ANIMATED_TRANSFORMATION_KEY)