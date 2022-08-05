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
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.request.toDisplayData
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.ifOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemoryCacheRequestInterceptor : RequestInterceptor {

    @MainThread
    override suspend fun intercept(chain: Chain): ImageData {
        val request = chain.request

        if (request is DisplayRequest) {
            val cachedCountBitmap = ifOrNull(request.memoryCachePolicy.readEnabled) {
                chain.sketch.memoryCache[request.memoryCacheKey]
            }
            if (cachedCountBitmap != null) {
                val countDrawable = SketchCountBitmapDrawable(
                    request.context.resources, cachedCountBitmap, DataFrom.MEMORY_CACHE
                ).apply {
                    withContext(Dispatchers.Main) {
                        chain.requestContext.pendingCountDrawable(this@apply, "loadBefore")
                    }
                }

                return DrawableDecodeResult(
                    drawable = countDrawable,
                    imageInfo = cachedCountBitmap.imageInfo,
                    dataFrom = DataFrom.MEMORY_CACHE,
                    transformedList = cachedCountBitmap.transformedList,
                ).toDisplayData()
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
                        chain.sketch.memoryCache
                            .put(request.memoryCacheKey, countDrawable.countBitmap)
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

val ImageRequest.memoryCacheKey: String
    get() = cacheKey

val ImageRequest.memoryCacheLockKey: String
    get() = cacheKey