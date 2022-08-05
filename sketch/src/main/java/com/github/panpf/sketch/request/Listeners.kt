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
package com.github.panpf.sketch.request

class Listeners<REQUEST : ImageRequest, SUCCESS_RESULT : ImageResult.Success, ERROR_RESULT : ImageResult.Error>(
    val listenerList: List<Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT>>
) : Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT> {

    constructor(vararg listeners: Listener<REQUEST, SUCCESS_RESULT, ERROR_RESULT>) : this(listeners.toList())

    override fun onStart(request: REQUEST) {
        listenerList.forEach {
            it.onStart(request)
        }
    }

    override fun onCancel(request: REQUEST) {
        listenerList.forEach {
            it.onCancel(request)
        }
    }

    override fun onError(request: REQUEST, result: ERROR_RESULT) {
        listenerList.forEach {
            it.onError(request, result)
        }
    }

    override fun onSuccess(request: REQUEST, result: SUCCESS_RESULT) {
        listenerList.forEach {
            it.onSuccess(request, result)
        }
    }
}