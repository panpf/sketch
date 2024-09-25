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
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.coerceAtLeast
import com.github.panpf.sketch.util.times
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

// TODO Check the use of synchronized.
//  Use 'kotlinx.coroutines.internal.synchronized' in the coroutine.
//  Use 'kotlinx.atomicfu.locks.synchronized' outside the coroutine.
//  Avoid using 'kotlin.synchronized'.

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

    private val lock = SynchronizedObject()
    private val _requestList = mutableListOf(initialRequest)
    private var _request: ImageRequest = initialRequest
    private var _cacheKey: String? = null

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
    val logKey: String = initialRequest.key

    /**
     * Used to cache bitmaps in memory and on disk
     */
    val cacheKey: String
        get() = synchronized(lock) {
            _cacheKey ?: request.newCacheKey(size).apply {
                _cacheKey = this
            }
        }

    /**
     * Set up a new request and recalculate the size of the request
     */
    internal suspend fun setNewRequest(request: ImageRequest) {
        val lastRequest = this.request
        if (lastRequest != request) {
            _requestList.add(request)
            _request = request
            if (lastRequest.sizeResolver != request.sizeResolver) {
                size = resolveSize(request)
            }
            _cacheKey = null
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
}