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
package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.annotation.MainThread
import com.github.panpf.sketch.decode.internal.DecodeInterceptorChain
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import kotlinx.coroutines.withContext

class EngineRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 100

    @MainThread
    override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
        val sketch = chain.sketch
        val request = chain.request
        val requestContext = chain.requestContext
        val decodeResult = withContext(sketch.decodeTaskDispatcher) {
            DecodeInterceptorChain(
                sketch = sketch,
                request = request,
                requestContext = requestContext,
                fetchResult = null,
                interceptors = sketch.components.getDecodeInterceptorList(request),
                index = 0,
            ).proceed()
        }.let {
            it.getOrNull() ?: return Result.failure(it.exceptionOrNull()!!)
        }
        return Result.success(
            ImageData(
                image = decodeResult.image,
                imageInfo = decodeResult.imageInfo,
                dataFrom = decodeResult.dataFrom,
                transformedList = decodeResult.transformedList,
                extras = decodeResult.extras
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is EngineRequestInterceptor
    }

    override fun hashCode(): Int {
        return this@EngineRequestInterceptor::class.hashCode()
    }

    override fun toString(): String = "EngineRequestInterceptor(sortWeight=$sortWeight)"
}