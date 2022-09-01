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

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.pauseLoadWhenScrollingError
import com.github.panpf.sketch.util.PauseLoadWhenScrollingMixedScrollListener

/**
 * Pause loading new images while the list is scrolling
 *
 * @see DisplayRequest.Builder.pauseLoadWhenScrolling
 * @see PauseLoadWhenScrollingMixedScrollListener
 * @see ErrorStateImage.Builder.pauseLoadWhenScrollingError
 */
class PauseLoadWhenScrollingDisplayInterceptor : RequestInterceptor {

    override val key: String? = null

    companion object {
        var scrolling = false
        private const val PAUSE_LOAD_WHEN_SCROLLING_OLD_DEPTH_KEY =
            "sketch#pause_load_when_scrolling_old_depth"
    }

    var enabled = true

    @MainThread
    override suspend fun intercept(chain: Chain): ImageData {
        val request = chain.request
        val finalRequest = when {
            request !is DisplayRequest -> {
                request
            }
            enabled
                    && scrolling
                    && request.isPauseLoadWhenScrolling
                    && !request.isIgnoredPauseLoadWhenScrolling -> {
                if (request.depth != Depth.MEMORY) {
                    val oldDepth = request.depth
                    request.newDisplayRequest {
                        depth(Depth.MEMORY, PAUSE_LOAD_WHEN_SCROLLING_KEY)
                        setParameter(PAUSE_LOAD_WHEN_SCROLLING_OLD_DEPTH_KEY, oldDepth.name, null)
                    }
                } else {
                    request
                }
            }
            else -> {
                val oldDepth =
                    request.parameters?.value<String>(PAUSE_LOAD_WHEN_SCROLLING_OLD_DEPTH_KEY)
                        ?.let {
                            try {
                                Depth.valueOf(it)
                            } catch (e: Exception) {
                                e.toString()
                                null
                            }
                        }
                if (oldDepth != null && request.depth != oldDepth) {
                    request.newDisplayRequest {
                        depth(oldDepth)
                        removeParameter(PAUSE_LOAD_WHEN_SCROLLING_OLD_DEPTH_KEY)
                    }
                } else {
                    request
                }
            }
        }
        return chain.proceed(finalRequest)
    }

    override fun toString(): String = "PauseLoadWhenScrollingDisplayInterceptor($enabled)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PauseLoadWhenScrollingDisplayInterceptor) return false
        if (enabled != other.enabled) return false
        return true
    }

    override fun hashCode(): Int {
        return enabled.hashCode()
    }
}