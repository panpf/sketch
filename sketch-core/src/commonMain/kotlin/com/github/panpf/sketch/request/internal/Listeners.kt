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

package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.Listener

class Listeners constructor(val list: List<Listener>) : Listener {

    constructor(vararg listeners: Listener) : this(listeners.toList())

    override fun onStart(request: ImageRequest) {
        list.forEach {
            it.onStart(request)
        }
    }

    override fun onCancel(request: ImageRequest) {
        list.forEach {
            it.onCancel(request)
        }
    }

    override fun onError(request: ImageRequest, error: ImageResult.Error) {
        list.forEach {
            it.onError(request, error)
        }
    }

    override fun onSuccess(request: ImageRequest, result: ImageResult.Success) {
        list.forEach {
            it.onSuccess(request, result)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Listeners) return false
        if (list != other.list) return false
        return true
    }

    override fun hashCode(): Int {
        return list.hashCode()
    }

    override fun toString(): String {
        return "Listeners($list)"
    }
}