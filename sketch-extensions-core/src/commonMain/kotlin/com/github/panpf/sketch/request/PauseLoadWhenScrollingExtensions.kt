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

private const val PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY = "sketch#pause_load_when_scrolling_enabled"
private const val PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY = "sketch#pause_load_when_scrolling_ignored"

/**
 * Set to enable or disable the function of pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDecodeInterceptor]
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.request.PauseLoadWhenScrollingExtensionsTest.testPauseLoadWhenScrolling
 */
fun ImageRequest.Builder.pauseLoadWhenScrolling(enabled: Boolean? = true): ImageRequest.Builder =
    apply {
        if (enabled == true) {
            setExtra(key = PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY, value = true, cacheKey = null)
        } else {
            removeExtra(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY)
        }
    }

/**
 * Returns true if pause load when scrolling has been enabled
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.request.PauseLoadWhenScrollingExtensionsTest.testPauseLoadWhenScrolling
 */
val ImageRequest.isPauseLoadWhenScrolling: Boolean
    get() = extras?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY) == true

/**
 * Set to enable or disable the function of pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDecodeInterceptor]
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.request.PauseLoadWhenScrollingExtensionsTest.testPauseLoadWhenScrolling
 */
fun ImageOptions.Builder.pauseLoadWhenScrolling(enabled: Boolean? = true): ImageOptions.Builder =
    apply {
        if (enabled == true) {
            setExtra(
                key = PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY,
                value = true,
                cacheKey = null,
            )
        } else {
            removeExtra(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY)
        }
    }

/**
 * Returns true if pause load when scrolling has been enabled
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.request.PauseLoadWhenScrollingExtensionsTest.testPauseLoadWhenScrolling
 */
val ImageOptions.isPauseLoadWhenScrolling: Boolean
    get() = extras?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY) == true


/**
 * Set to enable or disable the function of ignore pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDecodeInterceptor]
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.request.PauseLoadWhenScrollingExtensionsTest.testIgnorePauseLoadWhenScrolling
 */
fun ImageRequest.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean? = true): ImageRequest.Builder =
    apply {
        if (ignore == true) {
            setExtra(key = PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY, value = true, cacheKey = null)
        } else {
            removeExtra(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY)
        }
    }

/**
 * Returns true if ignore pause load when scrolling has been enabled
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.request.PauseLoadWhenScrollingExtensionsTest.testIgnorePauseLoadWhenScrolling
 */
val ImageRequest.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = extras?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY) == true

/**
 * Set to enable or disable the function of ignore pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDecodeInterceptor]
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.request.PauseLoadWhenScrollingExtensionsTest.testIgnorePauseLoadWhenScrolling
 */
fun ImageOptions.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean? = true): ImageOptions.Builder =
    apply {
        if (ignore == true) {
            setExtra(key = PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY, value = true, cacheKey = null)
        } else {
            removeExtra(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY)
        }
    }

/**
 * Returns true if ignore pause load when scrolling has been enabled
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.request.PauseLoadWhenScrollingExtensionsTest.testIgnorePauseLoadWhenScrolling
 */
val ImageOptions.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = extras?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY) == true