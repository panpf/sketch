/*
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

private const val PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY = "sketch#pause_load_when_scrolling_enabled"
private const val PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY = "sketch#pause_load_when_scrolling_ignored"

/**
 * Set to enable or disable the function of pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDrawableDecodeInterceptor]
 */
fun ImageRequest.Builder.pauseLoadWhenScrolling(enabled: Boolean = true): ImageRequest.Builder =
    apply {
        if (enabled) {
            setParameter(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY, true, null)
        } else {
            removeParameter(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY)
        }
    }

/**
 * Returns true if pause load when scrolling has been enabled
 */
val ImageRequest.isPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY) == true

/**
 * Set to enable or disable the function of pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDrawableDecodeInterceptor]
 */
fun ImageOptions.Builder.pauseLoadWhenScrolling(enabled: Boolean = true): ImageOptions.Builder =
    apply {
        if (enabled) {
            setParameter(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY, true, null)
        } else {
            removeParameter(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY)
        }
    }

/**
 * Returns true if pause load when scrolling has been enabled
 */
val ImageOptions.isPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_ENABLED_KEY) == true


/**
 * Set to enable or disable the function of ignore pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDrawableDecodeInterceptor]
 */
fun ImageRequest.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true): ImageRequest.Builder =
    apply {
        if (ignore) {
            setParameter(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY, true, null)
        } else {
            removeParameter(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY)
        }
    }

/**
 * Returns true if ignore pause load when scrolling has been enabled
 */
val ImageRequest.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY) == true

/**
 * Set to enable or disable the function of ignore pause load when scrolling, it needs to be used together with [PauseLoadWhenScrollingDrawableDecodeInterceptor]
 */
fun ImageOptions.Builder.ignorePauseLoadWhenScrolling(ignore: Boolean = true): ImageOptions.Builder =
    apply {
        if (ignore) {
            setParameter(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY, true, null)
        } else {
            removeParameter(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY)
        }
    }

/**
 * Returns true if ignore pause load when scrolling has been enabled
 */
val ImageOptions.isIgnoredPauseLoadWhenScrolling: Boolean
    get() = parameters?.value<Boolean>(PAUSE_LOAD_WHEN_SCROLLING_IGNORED_KEY) == true