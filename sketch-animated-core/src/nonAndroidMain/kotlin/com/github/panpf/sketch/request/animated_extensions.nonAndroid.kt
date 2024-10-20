/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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


const val ANIMATED_CACHE_DECODE_TIMEOUT_FRAME_KEY = "sketch#animated_cache_decode_timeout_frame"

/**
 * Cache decoding timed-out frames to improve playback smoothness, but will use more memory
 *
 * @see com.github.panpf.sketch.animated.core.nonandroid.test.request.AnimatedExtensionsNonAndroidTest.testCacheDecodeTimeoutFrame
 */
fun ImageRequest.Builder.cacheDecodeTimeoutFrame(cache: Boolean? = true): ImageRequest.Builder =
    apply {
        if (cache != null) {
            setExtra(key = ANIMATED_CACHE_DECODE_TIMEOUT_FRAME_KEY, value = cache, cacheKey = null)
        } else {
            removeExtra(ANIMATED_CACHE_DECODE_TIMEOUT_FRAME_KEY)
        }
    }

/**
 * Cache decoding timed-out frames to improve playback smoothness, but will use more memory
 *
 * @see com.github.panpf.sketch.animated.core.nonandroid.test.request.AnimatedExtensionsNonAndroidTest.testCacheDecodeTimeoutFrame
 */
val ImageRequest.cacheDecodeTimeoutFrame: Boolean?
    get() = extras?.value(ANIMATED_CACHE_DECODE_TIMEOUT_FRAME_KEY)

/**
 * Cache decoding timed-out frames to improve playback smoothness, but will use more memory
 *
 * @see com.github.panpf.sketch.animated.core.nonandroid.test.request.AnimatedExtensionsNonAndroidTest.testCacheDecodeTimeoutFrame
 */
fun ImageOptions.Builder.cacheDecodeTimeoutFrame(cache: Boolean? = true): ImageOptions.Builder =
    apply {
        if (cache != null) {
            setExtra(key = ANIMATED_CACHE_DECODE_TIMEOUT_FRAME_KEY, value = cache, cacheKey = null)
        } else {
            removeExtra(ANIMATED_CACHE_DECODE_TIMEOUT_FRAME_KEY)
        }
    }

/**
 * Cache decoding timed-out frames to improve playback smoothness, but will use more memory
 *
 * @see com.github.panpf.sketch.animated.core.nonandroid.test.request.AnimatedExtensionsNonAndroidTest.testCacheDecodeTimeoutFrame
 */
val ImageOptions.cacheDecodeTimeoutFrame: Boolean?
    get() = extras?.value(ANIMATED_CACHE_DECODE_TIMEOUT_FRAME_KEY)