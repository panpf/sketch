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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.request

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CacheKeyMapper
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.coerceAtLeast
import com.github.panpf.sketch.util.resetLazy
import com.github.panpf.sketch.util.times

/**
 * Create a new [RequestContext] based on the specified [Sketch] and [ImageRequest]
 *
 * @see com.github.panpf.sketch.core.common.test.request.RequestContextTest.testRequestContext
 */
suspend fun RequestContext(
    sketch: Sketch,
    request: ImageRequest,
): RequestContext {
    val size = resolveSize(request)
    return RequestContext(sketch = sketch, initialRequest = request, size = size)
}

/**
 * Resolve the size of the request
 *
 * @see com.github.panpf.sketch.core.common.test.request.RequestContextTest.testResolveSize
 */
suspend fun resolveSize(request: ImageRequest): Size {
    val size = request.sizeResolver.size().coerceAtLeast(Size.Empty)
    val sizeMultiplier = request.sizeMultiplier ?: 1f
    val finalSize = size.times(sizeMultiplier)
    return finalSize
}

/**
 * Request context, used to cache some data during the request process
 *
 * @see com.github.panpf.sketch.core.common.test.request.RequestContextTest
 */
class RequestContext constructor(
    val sketch: Sketch,
    val initialRequest: ImageRequest,
    size: Size
) {

    private val _requestList = mutableListOf(initialRequest)
    private var _request: ImageRequest = initialRequest

    private val cacheKeyLazy = resetLazy {
        val request = this@RequestContext.request
        val cacheKey = request.newCacheKey(this@RequestContext.size)
        cacheKey
    }
    private val memoryCacheKeyLazy = resetLazy {
        val request = this@RequestContext.request
        val memoryCacheKey = buildCacheKey(
            fromRequestCacheKey = request.memoryCacheKey,
            cacheKey = { cacheKeyLazy.value },
            cacheKeyMapper = request.memoryCacheKeyMapper,
            type = "memory",
        )
        memoryCacheKey
    }
    private val resultCacheKeyLazy = resetLazy {
        val request = this@RequestContext.request
        val resultCacheKey = buildCacheKey(
            fromRequestCacheKey = request.resultCacheKey,
            cacheKey = { cacheKeyLazy.value },
            cacheKeyMapper = request.resultCacheKeyMapper,
            type = "result",
        )
        resultCacheKey
    }
    private val downloadCacheKeyLazy = resetLazy {
        val request = this@RequestContext.request
        val downloadCacheKey = buildCacheKey(
            fromRequestCacheKey = request.downloadCacheKey,
            cacheKey = { request.uri.toString() },
            cacheKeyMapper = request.downloadCacheKeyMapper,
            type = "download",
        )
        downloadCacheKey
    }

    /**
     * The request list, the first element is the initial request, and the last element is the current request
     */
    val requestList: List<ImageRequest>
        get() = _requestList.toList()

    /**
     * The current request
     */
    val request: ImageRequest
        get() = _request

    /**
     * The size of the request
     */
    var size: Size = size
        private set

    /**
     * The log key of the request
     */
    val logKey: String by lazy { initialRequest.newCacheKey(size) }

    /**
     * Used to cache bitmaps in memory and on disk
     */
    @Deprecated("Use `memoryCacheKey` or `resultCacheKey` instead")
    val cacheKey: String
        get() = cacheKeyLazy.value

    /**
     * The key currently requesting read and write memory cache
     */
    val memoryCacheKey: String
        get() = memoryCacheKeyLazy.value

    /**
     * The key currently requesting read and write result cache
     */
    val resultCacheKey: String
        get() = resultCacheKeyLazy.value

    /**
     * The key currently requesting read and write download cache
     */
    val downloadCacheKey: String
        get() = downloadCacheKeyLazy.value

    /**
     * The result of fetch, null if not fetched yet
     */
    var fetchResult: FetchResult? = null


    /**
     * Set up a new request and recalculate the size of the request
     */
    internal suspend fun setNewRequest(request: ImageRequest) {
        val lastRequest = this.request
        if (lastRequest != request) {
            if (lastRequest.sizeResolver != request.sizeResolver) {
                size = resolveSize(request)
            }
            _requestList.add(request)
            _request = request
            cacheKeyLazy.reset()
            memoryCacheKeyLazy.reset()
            resultCacheKeyLazy.reset()
            downloadCacheKeyLazy.reset()
        }
    }

    /**
     * Calculate Resize based on size and imageSize
     */
    fun computeResize(imageSize: Size): Resize {
        val precision = request.precisionDecider.get(imageSize = imageSize, targetSize = size)
        val scale = request.scaleDecider.get(imageSize = imageSize, targetSize = size)
        return Resize(size = size, precision = precision, scale = scale)
    }

    private fun buildCacheKey(
        fromRequestCacheKey: String?,
        cacheKey: () -> String,
        cacheKeyMapper: CacheKeyMapper?,
        type: String,
    ): String {
        if (fromRequestCacheKey != null) {
            require(fromRequestCacheKey.isNotEmpty() && fromRequestCacheKey.isNotBlank()) {
                "ImageRequest.${type}CacheKey is empty or blank"
            }
            return fromRequestCacheKey
        }

        val newCacheKey = cacheKey()
        if (cacheKeyMapper != null) {
            val mapperCacheKey = cacheKeyMapper.map(newCacheKey)
            require(mapperCacheKey.isNotEmpty() && mapperCacheKey.isNotBlank()) {
                "ImageRequest.${type}CacheKeyMapper map result is empty or blank"
            }
            return mapperCacheKey
        }
        return newCacheKey
    }
}