/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ------------------------------------------------------------------------
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

/** Pass this to [repeatCount] to repeat infinitely. */
const val ANIMATION_REPEAT_INFINITE = -1
const val ANIMATION_REPEAT_COUNT_KEY = "sketch#animation_repeat_count"
const val ANIMATION_START_CALLBACK_KEY = "sketch#animation_start_callback"
const val ANIMATION_END_CALLBACK_KEY = "sketch#animation_end_callback"
const val ANIMATED_TRANSFORMATION_KEY = "sketch#animated_transformation"

/**
 * Set Number of repeat plays. -1: Indicates infinite repetition. When it is greater than or equal to 0, the total number of plays is equal to '1 + repeatCount'
 */
fun ImageRequest.Builder.repeatCount(repeatCount: Int): ImageRequest.Builder {
    require(repeatCount >= ANIMATION_REPEAT_INFINITE) { "Invalid repeatCount: $repeatCount" }
    return setParameter(
        key = ANIMATION_REPEAT_COUNT_KEY,
        value = repeatCount,
        cacheKey = null,
        requestKey = null
    )
}

/**
 * Number of repeat plays. -1: Indicates infinite repetition. When it is greater than or equal to 0, the total number of plays is equal to '1 + repeatCount'
 */
val ImageRequest.repeatCount: Int?
    get() = parameters?.value(ANIMATION_REPEAT_COUNT_KEY)

/**
 * Set Number of repeat plays. -1: Indicates infinite repetition. When it is greater than or equal to 0, the total number of plays is equal to '1 + repeatCount'
 */
fun ImageOptions.Builder.repeatCount(repeatCount: Int): ImageOptions.Builder {
    require(repeatCount >= ANIMATION_REPEAT_INFINITE) { "Invalid repeatCount: $repeatCount" }
    return setParameter(
        key = ANIMATION_REPEAT_COUNT_KEY,
        value = repeatCount,
        cacheKey = null,
        requestKey = null
    )
}

/**
 * Number of repeat plays. -1: Indicates infinite repetition. When it is greater than or equal to 0, the total number of plays is equal to '1 + repeatCount'
 */
val ImageOptions.repeatCount: Int?
    get() = parameters?.value(ANIMATION_REPEAT_COUNT_KEY)


/**
 * Set the callback to be invoked at the start of the animation if the result is an animated Image.
 */
fun ImageRequest.Builder.onAnimationStart(callback: (() -> Unit)?): ImageRequest.Builder {
    return setParameter(
        key = ANIMATION_START_CALLBACK_KEY,
        value = callback,
        cacheKey = null,
        requestKey = null
    )
}

/**
 * Get the callback to be invoked at the start of the animation if the result is an animated Image.
 */
val ImageRequest.animationStartCallback: (() -> Unit)?
    get() = parameters?.value(ANIMATION_START_CALLBACK_KEY)

/**
 * Set the callback to be invoked at the start of the animation if the result is an animated Image.
 */
fun ImageOptions.Builder.onAnimationStart(callback: (() -> Unit)?): ImageOptions.Builder {
    return setParameter(
        key = ANIMATION_START_CALLBACK_KEY,
        value = callback,
        cacheKey = null,
        requestKey = null
    )
}

/**
 * Get the callback to be invoked at the start of the animation if the result is an animated Image.
 */
val ImageOptions.animationStartCallback: (() -> Unit)?
    get() = parameters?.value(ANIMATION_START_CALLBACK_KEY)


/**
 * Set the callback to be invoked at the end of the animation if the result is an animated Image.
 */
fun ImageRequest.Builder.onAnimationEnd(callback: (() -> Unit)?): ImageRequest.Builder {
    return setParameter(
        key = ANIMATION_END_CALLBACK_KEY,
        value = callback,
        cacheKey = null,
        requestKey = null
    )
}

/**
 * Get the callback to be invoked at the end of the animation if the result is an animated Image.
 */
val ImageRequest.animationEndCallback: (() -> Unit)?
    get() = parameters?.value(ANIMATION_END_CALLBACK_KEY)

/**
 * Set the callback to be invoked at the end of the animation if the result is an animated Image.
 */
fun ImageOptions.Builder.onAnimationEnd(callback: (() -> Unit)?): ImageOptions.Builder {
    return setParameter(
        key = ANIMATION_END_CALLBACK_KEY,
        value = callback,
        cacheKey = null,
        requestKey = null
    )
}

/**
 * Get the callback to be invoked at the end of the animation if the result is an animated Image.
 */
val ImageOptions.animationEndCallback: (() -> Unit)?
    get() = parameters?.value(ANIMATION_END_CALLBACK_KEY)
