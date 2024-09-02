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

package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.transform.AnimatedTransformation


/**
 * Set the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 *
 * Default: `null`
 *
 * @see com.github.panpf.sketch.animated.android.test.request.AnimatedExtensionsAndroidTest.testAnimatedTransformation
 */
fun ImageRequest.Builder.animatedTransformation(
    animatedTransformation: AnimatedTransformation?
): ImageRequest.Builder = apply {
    if (animatedTransformation != null) {
        setExtra(
            key = ANIMATED_TRANSFORMATION_KEY,
            value = animatedTransformation,
            cacheKey = null,
            requestKey = animatedTransformation.key
        )
    } else {
        removeExtra(ANIMATED_TRANSFORMATION_KEY)
    }
}

/**
 * Get the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 *
 * @see com.github.panpf.sketch.animated.android.test.request.AnimatedExtensionsAndroidTest.testAnimatedTransformation
 */
val ImageRequest.animatedTransformation: AnimatedTransformation?
    get() = extras?.value(ANIMATED_TRANSFORMATION_KEY)

/**
 * Set the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 *
 * Default: `null`
 *
 * @see com.github.panpf.sketch.animated.android.test.request.AnimatedExtensionsAndroidTest.testAnimatedTransformation
 */
fun ImageOptions.Builder.animatedTransformation(
    animatedTransformation: AnimatedTransformation?
): ImageOptions.Builder = apply {
    if (animatedTransformation != null) {
        setExtra(
            key = ANIMATED_TRANSFORMATION_KEY,
            value = animatedTransformation,
            cacheKey = null,
            requestKey = animatedTransformation.key
        )
    } else {
        removeExtra(ANIMATED_TRANSFORMATION_KEY)
    }
}

/**
 * Get the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 *
 * @see com.github.panpf.sketch.animated.android.test.request.AnimatedExtensionsAndroidTest.testAnimatedTransformation
 */
// TODO Support compose
val ImageOptions.animatedTransformation: AnimatedTransformation?
    get() = extras?.value(ANIMATED_TRANSFORMATION_KEY)

/**
 * Wrap onStart and onEnd into [Animatable2Compat.AnimationCallback]
 *
 * @see com.github.panpf.sketch.animated.android.test.request.AnimatedExtensionsAndroidTest.testAnimatable2CompatCallbackOf
 */
fun animatable2CompatCallbackOf(
    onStart: (() -> Unit)?,
    onEnd: (() -> Unit)?
): Animatable2Compat.AnimationCallback = object : Animatable2Compat.AnimationCallback() {
    override fun onAnimationStart(drawable: Drawable?) {
        onStart?.invoke()
    }

    override fun onAnimationEnd(drawable: Drawable?) {
        onEnd?.invoke()
    }
}
