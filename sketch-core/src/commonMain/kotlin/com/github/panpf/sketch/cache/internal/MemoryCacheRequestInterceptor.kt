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

package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.annotation.MainThread
import com.github.panpf.sketch.cache.getExtras
import com.github.panpf.sketch.cache.getImageInfo
import com.github.panpf.sketch.cache.getResize
import com.github.panpf.sketch.cache.getTransformeds
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.cache.memoryCacheKey
import com.github.panpf.sketch.cache.newCacheValueExtras
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.resizeOnDraw
import com.github.panpf.sketch.source.DataFrom

/**
 * Memory cache request interceptor, used to read and write the decode result to the memory cache
 *
 * Note: Although LruMemoryCache is thread-unsafe, Sketch requests are executed in the main thread, so there is no need to do thread-safety processing here.
 *
 * @see com.github.panpf.sketch.core.common.test.cache.internal.MemoryCacheRequestInterceptorTest
 */
class MemoryCacheRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 90

    @MainThread
    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        val requestContext = chain.requestContext
        val memoryCachePolicy = request.memoryCachePolicy

        if (memoryCachePolicy.isReadOrWrite) {
            val memoryCache = chain.sketch.memoryCache
            val memoryCacheKey = requestContext.memoryCacheKey
            return memoryCache.withLock(memoryCacheKey) {
                val imageDataFromCache = readFromMemoryCache(requestContext)
                if (imageDataFromCache != null) {
                    Result.success(imageDataFromCache)
                } else if (memoryCachePolicy.readEnabled && request.depthHolder.depth >= Depth.MEMORY) {
                    Result.failure(DepthException("Request depth limited to ${request.depthHolder.depth}. ${request.key}"))
                } else {
                    callbackPlaceholder(chain)
                    chain.proceed(request).apply {
                        val newImageData = getOrNull()
                        if (newImageData != null) {
                            saveToMemoryCache(requestContext, newImageData)
                        }
                    }
                }
            }
        } else {
            callbackPlaceholder(chain)
            return chain.proceed(request)
        }
    }

    @MainThread
    private fun readFromMemoryCache(requestContext: RequestContext): ImageData? {
        if (!requestContext.request.memoryCachePolicy.readEnabled) return null
        val memoryCache = requestContext.sketch.memoryCache
        val cachedValue = memoryCache[requestContext.memoryCacheKey] ?: return null
        return ImageData(
            image = cachedValue.image,
            imageInfo = cachedValue.getImageInfo()!!,
            resize = cachedValue.getResize()!!,
            transformeds = cachedValue.getTransformeds(),
            extras = cachedValue.getExtras(),
            dataFrom = DataFrom.MEMORY_CACHE,
        )
    }

    private fun callbackPlaceholder(chain: Chain) {
        val sketch = chain.sketch
        val request = chain.request
        val requestContext = chain.requestContext
        val target = request.target
        if (target != null) {
            val placeholderDrawable = request.placeholder
                ?.getImage(sketch, request, null)
                ?.resizeOnDraw(request, requestContext.size)
            target.onStart(requestContext, placeholderDrawable)
        }
    }

    @MainThread
    private fun saveToMemoryCache(requestContext: RequestContext, imageData: ImageData): Boolean {
        val request = requestContext.request
        if (!request.memoryCachePolicy.writeEnabled) return false
        val newCacheValue = imageData.image.cacheValue(
            extras = newCacheValueExtras(
                imageInfo = imageData.imageInfo,
                resize = imageData.resize,
                transformeds = imageData.transformeds,
                extras = imageData.extras,
            )
        )
        if (newCacheValue == null) {
            // Usually AnimatedDrawable
            return false
        }
        val sketch = requestContext.sketch
        val saveState = sketch.memoryCache.put(requestContext.memoryCacheKey, newCacheValue)
        if (saveState != 0) {
            sketch.logger.w(
                "MemoryCacheRequestInterceptor. " +
                        "Memory cache save failed. " +
                        "state is $saveState. ${imageData.image}. ${request.key}"
            )
        }
        return saveState == 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "MemoryCacheRequestInterceptor(sortWeight=$sortWeight)"
}
