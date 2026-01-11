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

package com.github.panpf.sketch.fetch.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import kotlinx.coroutines.withContext

/**
 * The request interceptor that fetches data
 *
 * @see com.github.panpf.sketch.core.common.test.fetch.internal.FetcherRequestInterceptorTest
 */
class FetcherRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 99

    @MainThread
    override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
        val sketch = chain.sketch
        val request = chain.request
        val requestContext = chain.requestContext
        if (requestContext.fetchResult == null) {
            val fetchResultResult = withContext(sketch.decodeTaskDispatcher) {
                runCatching {
                    val fetcher = sketch.components.newFetcherOrThrow(requestContext)
                    fetcher.fetch().getOrThrow()
                }
            }
            val fetchResult = fetchResultResult.getOrNull()
            if (fetchResult != null) {
                requestContext.fetchResult = fetchResult
            } else {
                return Result.failure(fetchResultResult.exceptionOrNull()!!)
            }
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

    override fun toString(): String = "FetcherRequestInterceptor"
}