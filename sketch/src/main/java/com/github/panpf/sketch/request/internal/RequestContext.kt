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
import com.github.panpf.sketch.util.requiredMainThread

class RequestContext constructor(val firstRequest: ImageRequest) {

    private val _requests = mutableListOf(firstRequest)
    private var _lastRequest: ImageRequest = firstRequest
    private var pendingCountDrawable: SketchCountBitmapDrawable? = null

    val requests: List<ImageRequest>
        get() = _requests.toList()

    val lastRequest: ImageRequest
        get() = _lastRequest

    fun addRequest(request: ImageRequest) {
        val lastRequest = lastRequest
        if (lastRequest != request) {
            _requests.add(request)
            _lastRequest = request
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