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
package com.github.panpf.sketch.test.utils

import android.os.Looper
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener

class DisplayListenerSupervisor constructor(
    private val name: String? = null,
    private val callbackOnStart: (() -> Unit)? = null
) : Listener<DisplayRequest, Success, Error> {

    val callbackActionList = mutableListOf<String>()

    override fun onStart(request: DisplayRequest) {
        super.onStart(request)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onStart" + (name?.let { ":$it" } ?: ""))
        callbackOnStart?.invoke()
    }

    override fun onCancel(request: DisplayRequest) {
        super.onCancel(request)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onCancel" + (name?.let { ":$it" } ?: ""))
    }

    override fun onError(request: DisplayRequest, result: Error) {
        super.onError(request, result)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onError" + (name?.let { ":$it" } ?: ""))
    }

    override fun onSuccess(request: DisplayRequest, result: Success) {
        super.onSuccess(request, result)
        check(Looper.getMainLooper() === Looper.myLooper())
        callbackActionList.add("onSuccess" + (name?.let { ":$it" } ?: ""))
    }
}