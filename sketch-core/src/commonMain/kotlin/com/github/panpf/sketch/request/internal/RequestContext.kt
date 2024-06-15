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

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.coerceAtLeast
import com.github.panpf.sketch.util.times
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

class RequestContext constructor(val sketch: Sketch, val initialRequest: ImageRequest) {

    private val lock = SynchronizedObject()
    private val completedListenerList = mutableSetOf<CompletedListener>()
    private val _requestList = mutableListOf(initialRequest)
    private var _request: ImageRequest = initialRequest
    private var _cacheKey: String? = null

    val logger: Logger
        get() = sketch.logger

    val requestList: List<ImageRequest>
        get() = _requestList.toList()

    val request: ImageRequest
        get() = _request

    val logKey: String = initialRequest.key

    /** Used to cache bitmaps in memory and on disk */
    // TODO hidden
    val cacheKey: String
        get() = synchronized(lock) {
            _cacheKey ?: request.newCacheKey(size!!).apply {
                _cacheKey = this
            }
        }

    var size: Size? = null

    internal suspend fun setNewRequest(request: ImageRequest) {
        val lastRequest = this.request
        if (lastRequest != request) {
            _requestList.add(request)
            _request = request
            if (lastRequest.sizeResolver != request.sizeResolver) {
                size = request.sizeResolver.size().coerceAtLeast(Size.Empty) * (request.sizeMultiplier ?: 1f)
            }
            _cacheKey = null
        }
    }

    fun completed() {
        completedListenerList.forEach { it.onCompleted() }
    }

    fun registerCompletedListener(completedListener: CompletedListener) {
        completedListenerList.add(completedListener)
    }

    fun unregisterCompletedListener(completedListener: CompletedListener) {
        completedListenerList.remove(completedListener)
    }

    fun computeResize(imageSize: Size): Resize {
        val size = size!!
        val precision = request.precisionDecider.get(imageSize = imageSize, targetSize = size)
        val scale = request.scaleDecider.get(imageSize = imageSize, targetSize = size)
        return Resize(size = size, precision = precision, scale = scale)
    }

    fun interface CompletedListener {
        fun onCompleted()
    }
}