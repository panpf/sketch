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
import kotlinx.coroutines.withContext

/**
 * The final request interceptor is used to actually execute the request
 *
 * @see com.github.panpf.sketch.core.common.test.request.internal.DecoderInterceptorTest
 */
class DecoderInterceptor : Interceptor {

    companion object Companion {
        const val SORT_WEIGHT = 100
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT

    @MainThread
    override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
        val sketch = chain.sketch
        val requestContext = chain.requestContext
        val fetchResult = requestContext.fetchResult
            ?: return Result.failure(Exception("FetchResult is null, please make sure to add FetcherInterceptor"))
        val result = withContext(sketch.decodeTaskDispatcher) {
            runCatching {
                val components = sketch.components
                val decoder = components.newDecoderOrThrow(requestContext, fetchResult)
                decoder.decode()
            }
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "DecoderInterceptor"
}