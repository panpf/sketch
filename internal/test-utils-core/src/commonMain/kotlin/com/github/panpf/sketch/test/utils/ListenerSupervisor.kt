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

package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.Listener

class ListenerSupervisor constructor(
    private val name: String? = null,
    private val callbackOnStart: (() -> Unit)? = null
) : Listener {

    val callbackActionList = mutableListOf<String>()

    override fun onStart(request: ImageRequest) {
        super.onStart(request)
        callbackActionList.add("onStart" + (name?.let { ":$it" } ?: ""))
        callbackOnStart?.invoke()
    }

    override fun onCancel(request: ImageRequest) {
        super.onCancel(request)
        callbackActionList.add("onCancel" + (name?.let { ":$it" } ?: ""))
    }

    override fun onError(request: ImageRequest, error: Error) {
        super.onError(request, error)
        callbackActionList.add("onError" + (name?.let { ":$it" } ?: ""))
    }

    override fun onSuccess(request: ImageRequest, result: Success) {
        super.onSuccess(request, result)
        callbackActionList.add("onSuccess" + (name?.let { ":$it" } ?: ""))
    }
}