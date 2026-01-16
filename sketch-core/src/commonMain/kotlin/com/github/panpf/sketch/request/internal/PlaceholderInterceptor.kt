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

package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.resize.resizeOnDraw

/**
 * Placeholder request interceptor, used to display the placeholder image when the request starts
 *
 * @see com.github.panpf.sketch.core.common.test.request.internal.PlaceholderInterceptorTest
 */
class PlaceholderInterceptor : Interceptor {

    companion object {
        const val SORT_WEIGHT = 30
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT

    @MainThread
    override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
        val request = chain.request
        val placeholder = request.placeholder
        val target = request.target
        if (placeholder != null && target != null) {
            val sketch = chain.sketch
            val requestContext = chain.requestContext
            val placeholderImage = runCatching {
                placeholder.getImage(sketch, request, null)
                    ?.resizeOnDraw(request, requestContext.size)
            }.apply {
                if (isFailure) {
                    val exception = exceptionOrNull()
                    val errorMessage = "Failed to get the placeholder image. '${request.key}'"
                    sketch.logger.e(tr = exception, msg = errorMessage)
                }
            }.getOrNull()
            target.onStart(sketch, request, placeholderImage)
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

    override fun toString(): String = "PlaceholderInterceptor"
}