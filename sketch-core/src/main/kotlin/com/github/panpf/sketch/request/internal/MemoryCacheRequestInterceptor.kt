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
import android.graphics.Bitmap
import androidx.annotation.MainThread
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.request.asSketchImage
import com.github.panpf.sketch.request.getBitmap
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.util.asOrNull

class MemoryCacheRequestInterceptor : RequestInterceptor {

    override val key: String? = null
    override val sortWeight: Int = 90

    @MainThread
    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        val requestContext = chain.requestContext
        val memoryCache = chain.sketch.memoryCache
        val bitmapPool = chain.sketch.bitmapPool
        val memoryCachePolicy = request.memoryCachePolicy
        val targetSupportDisplayCount =
            request.target.asOrNull<Target>()?.supportDisplayCount == true

        if (memoryCachePolicy.readEnabled && targetSupportDisplayCount) {
            val countDrawable = readFromMemoryCache(
                context = request.context,
                memoryCache = memoryCache,
                requestContext = requestContext,
                dataFrom = DataFrom.MEMORY_CACHE
            )
            if (countDrawable != null) {
                requestContext.pendingCountDrawable(countDrawable, "loadBefore")
                val displayData = ImageData(
                    image = countDrawable.asSketchImage(),
                    imageInfo = countDrawable.imageInfo,
                    transformedList = countDrawable.transformedList,
                    extras = countDrawable.extras,
                    dataFrom = DataFrom.MEMORY_CACHE,
                )
                return Result.success(displayData)
            } else if (request.depth >= Depth.MEMORY) {
                return Result.failure(DepthException("Request depth limited to ${request.depth}. ${request.uriString}"))
            }
        }

        val result = chain.proceed(request)

        val imageData = result.getOrNull()
        val bitmap = imageData?.image?.getBitmap()
        if (imageData != null
            && memoryCachePolicy.writeEnabled
            && targetSupportDisplayCount
            && bitmap != null
        ) {
            val saveSuccess = saveToMemoryCache(
                memoryCache = memoryCache,
                requestContext = requestContext,
                bitmapPool = bitmapPool,
                imageData = imageData,
                bitmap = bitmap
            )
            if (saveSuccess && memoryCachePolicy.readEnabled) {
                val countDrawable = readFromMemoryCache(
                    context = request.context,
                    memoryCache = memoryCache,
                    requestContext = requestContext,
                    dataFrom = imageData.dataFrom,
                )
                if (countDrawable != null) {
                    requestContext.pendingCountDrawable(countDrawable, "newDecode")
                    val displayData = ImageData(
                        image = countDrawable.asSketchImage(),
                        imageInfo = countDrawable.imageInfo,
                        transformedList = countDrawable.transformedList,
                        extras = countDrawable.extras,
                        dataFrom = imageData.dataFrom,
                    )
                    return Result.success(displayData)
                }
            }
        }

        return result
    }

    @MainThread
    private fun readFromMemoryCache(
        context: Context,
        memoryCache: MemoryCache,
        requestContext: RequestContext,
        dataFrom: DataFrom,
    ): SketchCountBitmapDrawable? {
        val cachedValue = memoryCache[requestContext.memoryCacheKey] ?: return null
        return SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = cachedValue.countBitmap,
            imageUri = cachedValue.imageUri,
            requestKey = cachedValue.requestKey,
            requestCacheKey = cachedValue.requestCacheKey,
            imageInfo = cachedValue.imageInfo,
            transformedList = cachedValue.transformedList,
            extras = cachedValue.extras,
            dataFrom = dataFrom,
        )
    }

    @MainThread
    private fun saveToMemoryCache(
        memoryCache: MemoryCache,
        requestContext: RequestContext,
        bitmapPool: BitmapPool,
        imageData: ImageData,
        bitmap: Bitmap,
    ): Boolean {
        val countBitmap = CountBitmap(
            cacheKey = requestContext.cacheKey,
            originBitmap = bitmap,
            bitmapPool = bitmapPool,
            disallowReuseBitmap = requestContext.request.disallowReuseBitmap,
        )
        val newCacheValue = MemoryCache.Value(
            countBitmap = countBitmap,
            imageUri = requestContext.request.uriString,
            requestKey = requestContext.key,
            requestCacheKey = requestContext.cacheKey,
            imageInfo = imageData.imageInfo,
            transformedList = imageData.transformedList,
            extras = imageData.extras,
        )
        return memoryCache.put(requestContext.memoryCacheKey, newCacheValue)
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
