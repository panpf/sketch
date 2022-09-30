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
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.requiredMainThread

class RequestContext constructor(firstRequest: ImageRequest, val resizeSize: Size) {

    private var pendingCountDrawable: SketchCountBitmapDrawable? = null

    private val _requestList = mutableListOf(firstRequest)
    val requestList: List<ImageRequest>
        get() = _requestList.toList()

    private var _request: ImageRequest = firstRequest
    val request: ImageRequest
        get() = _request

    // todo test
    private var _resize: Resize? = null
    val resize: Resize
        get() = _resize ?: Resize(
            size = resizeSize,
            precision = request.resizePrecisionDecider,
            scale = request.resizeScaleDecider
        ).apply {
            _resize = this
        }

    // todo test
    private var _key: String? = null
    val key: String
        get() = _key ?: request.newKey(resizeSize).apply {
            _key = this
        }

    // todo test
    private var _cacheKey: String? = null

    /** Used to cache bitmaps in memory and on disk */
    val cacheKey: String
        get() = _cacheKey ?: request.newCacheKey(resizeSize).apply {
            _cacheKey = this
        }

    internal fun addRequest(request: ImageRequest) {
        val lastRequest = this.request
        if (lastRequest != request) {
            _requestList.add(request)
            _request = request
            _resize = null
            _key = null
            _cacheKey = null
        }
    }

    @MainThread
    fun pendingCountDrawable(drawable: SketchCountBitmapDrawable, caller: String) {
        requiredMainThread()
        completeCountDrawable(caller)
        pendingCountDrawable = drawable.apply {
            countBitmap.setIsPending(true, caller)
        }
    }

    @MainThread
    fun completeCountDrawable(caller: String) {
        requiredMainThread()
        pendingCountDrawable?.apply {
            countBitmap.setIsPending(false, caller)
        }
    }
}