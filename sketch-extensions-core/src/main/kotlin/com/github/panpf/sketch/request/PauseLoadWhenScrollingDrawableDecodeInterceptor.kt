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

import com.github.panpf.sketch.decode.DrawableDecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeInterceptor.Chain
import com.github.panpf.sketch.decode.DrawableDecodeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

/**
 * Pause loading new images while the list is scrolling
 *
 * @see DisplayRequest.Builder.pauseLoadWhenScrolling
 */
class PauseLoadWhenScrollingDrawableDecodeInterceptor(override val sortWeight: Int = 0) :
    DrawableDecodeInterceptor {

    companion object {
        private val scrollingFlow = MutableStateFlow(false)

        var scrolling: Boolean
            get() = scrollingFlow.value
            set(value) {
                scrollingFlow.value = value
            }
    }

    var enabled = true

    override val key: String? = null

    override suspend fun intercept(chain: Chain): Result<DrawableDecodeResult> {
        val request = chain.request
        if (enabled
            && request is DisplayRequest
            && request.isPauseLoadWhenScrolling
            && !request.isIgnoredPauseLoadWhenScrolling
            && scrollingFlow.value
        ) {
            scrollingFlow.filter { !it }.first()
        }
        return chain.proceed()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PauseLoadWhenScrollingDrawableDecodeInterceptor
        if (sortWeight != other.sortWeight) return false
        return true
    }

    override fun hashCode(): Int {
        return sortWeight
    }

    override fun toString(): String =
        "PauseLoadWhenScrollingDrawableDecodeInterceptor(sortWeight=$sortWeight,enabled=$enabled)"
}