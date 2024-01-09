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
import com.github.panpf.sketch.cache.asSketchImage
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DrawableImage
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.util.asOrNull

class MemoryCacheRequestInterceptor : RequestInterceptor {

    override val key: String? = null
    override val sortWeight: Int = 90

    @MainThread
    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        val requestContext = chain.requestContext
        val memoryCachePolicy = request.memoryCachePolicy
        val targetSupportDisplayCount =
            request.target.asOrNull<Target>()?.supportDisplayCount == true

        if (memoryCachePolicy.readEnabled && targetSupportDisplayCount) {
            val imageDataFromCache = readFromMemoryCache(requestContext)
            if (imageDataFromCache != null) {
                val image = imageDataFromCache.image
                if (image is DrawableImage && image.drawable is SketchCountBitmapDrawable) {
                    requestContext.pendingCountDrawable(image.drawable, "loadBefore")
                }
                return Result.success(imageDataFromCache)
            } else if (request.depth >= Depth.MEMORY) {
                return Result.failure(DepthException("Request depth limited to ${request.depth}. ${request.key}"))
            }
        }

        val result = chain.proceed(request)

        val imageData = result.getOrNull()
        if (imageData != null
            && memoryCachePolicy.writeEnabled
            && targetSupportDisplayCount
        ) {
            val saveSuccess = saveToMemoryCache(requestContext, imageData)
            if (saveSuccess && memoryCachePolicy.readEnabled) {
                val imageDataFromCache = readFromMemoryCache(requestContext)
                if (imageDataFromCache != null) {
                    val image = imageDataFromCache.image
                    if (image is DrawableImage && image.drawable is SketchCountBitmapDrawable) {
                        requestContext.pendingCountDrawable(image.drawable, "newDecode")
                    }
                    return Result.success(imageDataFromCache.copy(dataFrom = imageData.dataFrom))
                }
            }
        }

        return result
    }

    @MainThread
    private fun readFromMemoryCache(
        requestContext: RequestContext
    ): ImageData? {
        val request = requestContext.request
        val memoryCache = requestContext.sketch.memoryCache
        val cachedValue = memoryCache[requestContext.memoryCacheKey] ?: return null
        val cacheImage = cachedValue.asSketchImage(request.context.resources)
        return ImageData(
            image = cacheImage,
            imageUri = request.uriString,
            requestKey = request.key,
            cacheKey = requestContext.memoryCacheKey,
            imageInfo = cachedValue.getImageInfo()!!,
            transformedList = cachedValue.getTransformedList(),
            extras = cachedValue.getExtras(),
            dataFrom = DataFrom.MEMORY_CACHE,
        )
    }

    @MainThread
    private fun saveToMemoryCache(
        requestContext: RequestContext,
        imageData: ImageData,
    ): Boolean {
        val newCacheValue = imageData.image.cacheValue(
            requestContext, newCacheValueExtras(
                imageInfo = imageData.imageInfo,
                transformedList = imageData.transformedList,
                extras = imageData.extras,
            )
        ) ?: return false
        val saveSuccess =
            requestContext.sketch.memoryCache.put(requestContext.memoryCacheKey, newCacheValue)
        if (!saveSuccess) {
            requestContext.sketch.logger.w(
                "MemoryCacheRequestInterceptor",
                "Memory cache save failed. ${imageData.image}. . ${requestContext.request.key}"
            )
        }
        return saveSuccess
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

fun MemoryCache.Value.getImageInfo(): ImageInfo? {
    return extras["imageInfo"] as ImageInfo?
}

fun newCacheValueExtras(
    imageInfo: ImageInfo,
    transformedList: List<String>?,
    extras: Map<String, String>?,
): Map<String, Any?> {
    return mapOf(
        "imageInfo" to imageInfo,
        "transformedList" to transformedList,
        "extras" to extras,
    )
}

fun MemoryCache.Value.getTransformedList(): List<String>? {
    @Suppress("UNCHECKED_CAST")
    return extras["transformedList"] as List<String>?
}

fun MemoryCache.Value.getExtras(): Map<String, String>? {
    @Suppress("UNCHECKED_CAST")
    return extras["extras"] as Map<String, String>?
}
