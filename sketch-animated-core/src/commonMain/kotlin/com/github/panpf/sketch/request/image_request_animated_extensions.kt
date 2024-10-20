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

import com.github.panpf.sketch.transform.AnimatedTransformation

/**
 * Set Number of repeat plays. -1: Indicates infinite repetition. When it is greater than or equal to 0, the total number of plays is equal to '1 + repeatCount'
 *
 * @see com.github.panpf.sketch.animated.core.common.test.request.ImageRequestAnimatedExtensionsTest.testRepeatCount
 */
fun ImageRequest.Builder.repeatCount(repeatCount: Int?): ImageRequest.Builder = apply {
    require(repeatCount == null || repeatCount >= ANIMATION_REPEAT_INFINITE) { "Invalid repeatCount: $repeatCount" }
    if (repeatCount != null) {
        setExtra(key = ANIMATION_REPEAT_COUNT_KEY, value = repeatCount)
    } else {
        removeExtra(ANIMATION_REPEAT_COUNT_KEY)
    }
}

/**
 * Number of repeat plays. -1: Indicates infinite repetition. When it is greater than or equal to 0, the total number of plays is equal to '1 + repeatCount'
 *
 * @see com.github.panpf.sketch.animated.core.common.test.request.ImageRequestAnimatedExtensionsTest.testRepeatCount
 */
val ImageRequest.repeatCount: Int?
    get() = extras?.value(ANIMATION_REPEAT_COUNT_KEY)


/**
 * Set the callback to be invoked at the start of the animation if the result is an animated Image.
 *
 * @see com.github.panpf.sketch.animated.core.common.test.request.ImageRequestAnimatedExtensionsTest.testOnAnimationStart
 */
fun ImageRequest.Builder.onAnimationStart(callback: (() -> Unit)?): ImageRequest.Builder = apply {
    if (callback != null) {
        setExtra(
            key = ANIMATION_START_CALLBACK_KEY,
            value = callback,
            cacheKey = null,
            requestKey = null
        )
    } else {
        removeExtra(ANIMATION_START_CALLBACK_KEY)
    }
}

/**
 * Get the callback to be invoked at the start of the animation if the result is an animated Image.
 *
 * @see com.github.panpf.sketch.animated.core.common.test.request.ImageRequestAnimatedExtensionsTest.testOnAnimationStart
 */
val ImageRequest.animationStartCallback: (() -> Unit)?
    get() = extras?.value(ANIMATION_START_CALLBACK_KEY)


/**
 * Set the callback to be invoked at the end of the animation if the result is an animated Image.
 *
 * @see com.github.panpf.sketch.animated.core.common.test.request.ImageRequestAnimatedExtensionsTest.testOnAnimationEnd
 */
fun ImageRequest.Builder.onAnimationEnd(callback: (() -> Unit)?): ImageRequest.Builder = apply {
    if (callback != null) {
        setExtra(
            key = ANIMATION_END_CALLBACK_KEY,
            value = callback,
            cacheKey = null,
            requestKey = null
        )
    } else {
        removeExtra(ANIMATION_END_CALLBACK_KEY)
    }
}

/**
 * Get the callback to be invoked at the end of the animation if the result is an animated Image.
 *
 * @see com.github.panpf.sketch.animated.core.common.test.request.ImageRequestAnimatedExtensionsTest.testOnAnimationEnd
 */
val ImageRequest.animationEndCallback: (() -> Unit)?
    get() = extras?.value(ANIMATION_END_CALLBACK_KEY)


/**
 * Set the [AnimatedTransformation] that will be applied to the result if it is an animated Image.
 *
 * Default: `null`
 *
 * @see com.github.panpf.sketch.animated.core.common.test.request.ImageRequestAnimatedExtensionsTest.testAnimatedTransformation
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
 * Get the [AnimatedTransformation] that will be applied to the result if it is an animated Image.
 *
 * @see com.github.panpf.sketch.animated.core.common.test.request.ImageRequestAnimatedExtensionsTest.testAnimatedTransformation
 */
val ImageRequest.animatedTransformation: AnimatedTransformation?
    get() = extras?.value(ANIMATED_TRANSFORMATION_KEY)
