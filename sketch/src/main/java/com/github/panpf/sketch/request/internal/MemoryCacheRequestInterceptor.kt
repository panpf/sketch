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

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.MainThread
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CountBitmap
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

class MemoryCacheRequestInterceptor : RequestInterceptor {

    override val key: String? = null
    override val sortWeight: Int = 90

    @MainThread
    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        val requestContext = chain.requestContext
        val memoryCache = chain.sketch.memoryCache
        val bitmapPool = chain.sketch.bitmapPool

        if (request is DisplayRequest && request.memoryCachePolicy.readEnabled) {
            val cachedDisplayData =
                readFromMemoryCache(request.context, memoryCache, requestContext)
            if (cachedDisplayData != null) {
                return Result.success(cachedDisplayData)
            } else if (request.depth >= Depth.MEMORY) {
                return Result.failure(DepthException("Request depth limited to ${request.depth}. ${request.uriString}"))
            }
        }

        val result = chain.proceed(request)

        val imageData = result.getOrNull()
        if (imageData != null
            && imageData is DisplayData
            && request is DisplayRequest
            && request.memoryCachePolicy.writeEnabled
            && imageData.drawable is BitmapDrawable
        ) {
            val countDrawable = saveToMemoryCache(
                memoryCache, requestContext, bitmapPool, imageData, imageData.drawable
            )
            return Result.success(imageData.copy(drawable = countDrawable))
        }

        return result
    }

    @MainThread
    private fun readFromMemoryCache(
        context: Context,
        memoryCache: MemoryCache,
        requestContext: RequestContext,
    ): DisplayData? {
        val cachedValue = memoryCache[requestContext.memoryCacheKey] ?: return null
        val countDrawable = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = cachedValue.countBitmap,
            imageUri = cachedValue.imageUri,
            requestKey = cachedValue.requestKey,
            requestCacheKey = cachedValue.requestCacheKey,
            imageInfo = cachedValue.imageInfo,
            transformedList = cachedValue.transformedList,
            extras = cachedValue.extras,
            dataFrom = DataFrom.MEMORY_CACHE,
        )
        requestContext.pendingCountDrawable(countDrawable, "loadBefore")
        return DisplayData(
            drawable = countDrawable,
            imageInfo = cachedValue.imageInfo,
            transformedList = cachedValue.transformedList,
            extras = cachedValue.extras,
            dataFrom = DataFrom.MEMORY_CACHE,
        )
    }

    @MainThread
    private fun saveToMemoryCache(
        memoryCache: MemoryCache,
        requestContext: RequestContext,
        bitmapPool: BitmapPool,
        displayData: DisplayData,
        bitmapDrawable: BitmapDrawable,
    ): SketchCountBitmapDrawable {
        val countBitmap = CountBitmap(
            cacheKey = requestContext.cacheKey,
            originBitmap = bitmapDrawable.bitmap,
            bitmapPool = bitmapPool,
            disallowReuseBitmap = requestContext.request.disallowReuseBitmap,
        )
        val countDrawable = SketchCountBitmapDrawable(
            resources = requestContext.request.context.resources,
            countBitmap = countBitmap,
            imageUri = requestContext.request.uriString,
            requestKey = requestContext.key,
            requestCacheKey = requestContext.cacheKey,
            imageInfo = displayData.imageInfo,
            transformedList = displayData.transformedList,
            extras = displayData.extras,
            dataFrom = displayData.dataFrom
        )
        requestContext.pendingCountDrawable(countDrawable, "newDecode")
        val newCacheValue = MemoryCache.Value(
            countBitmap = countDrawable.countBitmap,
            imageUri = countDrawable.imageUri,
            requestKey = countDrawable.requestKey,
            requestCacheKey = countDrawable.requestCacheKey,
            imageInfo = countDrawable.imageInfo,
            transformedList = countDrawable.transformedList,
            extras = countDrawable.extras,
        )
        memoryCache.put(requestContext.memoryCacheKey, newCacheValue)
        return countDrawable
    }

    override fun toString(): String = "MemoryCacheRequestInterceptor(sortWeight=$sortWeight)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

val RequestContext.memoryCacheKey: String
    get() = cacheKey