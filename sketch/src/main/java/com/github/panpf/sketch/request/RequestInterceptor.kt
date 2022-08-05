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

import androidx.annotation.MainThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Intercept the execution of [ImageRequest], you can change the input and output, register to [ComponentRegistry] to take effect
 */
fun interface RequestInterceptor {

    @MainThread
    suspend fun intercept(chain: Chain): ImageData

    interface Chain {

        val sketch: Sketch

        val initialRequest: ImageRequest

        val request: ImageRequest

        val requestContext: RequestContext

        /**
         * Continue executing the chain.
         *
         * @param request The request to proceed with.
         */
        @MainThread
        suspend fun proceed(request: ImageRequest): ImageData
    }
}