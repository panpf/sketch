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

/**
 * [Listener] Combination of two [Listener]
 *
 * @see com.github.panpf.sketch.core.common.test.request.internal.PairListenerTest
 */
data class PairListener constructor(
    val first: Listener,
    val second: Listener,
) : Listener {

    override fun onStart(request: ImageRequest) {
        first.onStart(request)
        second.onStart(request)
    }

    override fun onCancel(request: ImageRequest) {
        first.onCancel(request)
        second.onCancel(request)
    }

    override fun onError(request: ImageRequest, error: ImageResult.Error) {
        first.onError(request, error)
        second.onError(request, error)
    }

    override fun onSuccess(request: ImageRequest, result: ImageResult.Success) {
        first.onSuccess(request, result)
        second.onSuccess(request, result)
    }
}