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

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.internal.ResultCacheRequestInterceptor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

/**
 * Adds Pause loading new images while the list is scrolling support
 *
 * @see com.github.panpf.sketch.extensions.core.common.test.request.PauseLoadWhenScrollingRequestInterceptorTest.testSupportPauseLoadWhenScrolling
 */
fun ComponentRegistry.Builder.supportPauseLoadWhenScrolling(): ComponentRegistry.Builder = apply {
    addRequestInterceptor(PauseLoadWhenScrollingRequestInterceptor())
}

/**
 * Pause loading new images while the list is scrolling
 *
 * @see ImageRequest.Builder.pauseLoadWhenScrolling
 * @see com.github.panpf.sketch.extensions.core.common.test.request.PauseLoadWhenScrollingRequestInterceptorTest
 */
class PauseLoadWhenScrollingRequestInterceptor() : RequestInterceptor {

    companion object Companion {
        const val SORT_WEIGHT = ResultCacheRequestInterceptor.SORT_WEIGHT - 1
        private val scrollingFlow = MutableStateFlow(false)

        var scrolling: Boolean
            get() = scrollingFlow.value
            set(value) {
                scrollingFlow.value = value
            }
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT
    var enabled = true

    override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
        val request = chain.request
        if (enabled
            && request.isPauseLoadWhenScrolling
            && !request.isIgnoredPauseLoadWhenScrolling
            && scrollingFlow.value
        ) {
            scrollingFlow.filter { !it }.first()
        }
        return chain.proceed(request)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "PauseLoadWhenScrollingRequestInterceptor"
}