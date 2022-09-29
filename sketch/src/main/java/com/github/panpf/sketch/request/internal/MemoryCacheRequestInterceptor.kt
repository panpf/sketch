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

import androidx.annotation.MainThread
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.ifOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemoryCacheRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    @MainThread
    override suspend fun intercept(chain: Chain): ImageData {
        val request = chain.request
        val requestContext = chain.requestContext

        if (request is DisplayRequest) {
            val memoryCacheKey = requestContext.cacheKey
            val cachedValue = ifOrNull(request.memoryCachePolicy.readEnabled) {
                chain.sketch.memoryCache[memoryCacheKey]
            }
            if (cachedValue != null) {
                val countDrawable = SketchCountBitmapDrawable(
                    resources = request.context.resources,
                    countBitmap = cachedValue.countBitmap,
                    imageUri = cachedValue.imageUri,
                    requestKey = cachedValue.requestKey,
                    requestCacheKey = cachedValue.requestCacheKey,
                    imageInfo = cachedValue.imageInfo,
                    transformedList = cachedValue.transformedList,
                    extras = cachedValue.extras,
                    dataFrom = DataFrom.MEMORY_CACHE,
                ).apply {
                    withContext(Dispatchers.Main) {
                        chain.requestContext.pendingCountDrawable(this@apply, "loadBefore")
                    }
                }
                return DisplayData(
                    drawable = countDrawable,
                    imageInfo = cachedValue.imageInfo,
                    transformedList = cachedValue.transformedList,
                    extras = cachedValue.extras,
                    dataFrom = DataFrom.MEMORY_CACHE,
                )
            }

            val depth = request.depth
            if (depth >= Depth.MEMORY) {
                throw DepthException("Request depth limited to $depth. ${request.uriString}")
            }

            return if (request.memoryCachePolicy.writeEnabled) {
                chain.proceed(request).also { imageData ->
                    val countDrawable = imageData.asOrThrow<DisplayData>()
                        .drawable.asOrNull<SketchCountBitmapDrawable>()
                    if (countDrawable != null) {
                        chain.requestContext.pendingCountDrawable(countDrawable, "newDecode")
                        val newCacheValue = MemoryCache.Value(
                            countBitmap = countDrawable.countBitmap,
                            imageUri = countDrawable.imageUri,
                            requestKey = countDrawable.requestKey,
                            requestCacheKey = countDrawable.requestCacheKey,
                            imageInfo = countDrawable.imageInfo,
                            transformedList = countDrawable.transformedList,
                            extras = countDrawable.extras,
                        )
                        chain.sketch.memoryCache.put(memoryCacheKey, newCacheValue)
                    }
                }
            } else {
                chain.proceed(request)
            }
        } else {
            return chain.proceed(request)
        }
    }

    override fun toString(): String = "MemoryCacheRequestInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}