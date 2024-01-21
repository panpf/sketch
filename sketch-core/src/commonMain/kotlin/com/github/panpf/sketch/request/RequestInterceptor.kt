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
import com.github.panpf.sketch.Key
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Intercept the execution of [ImageRequest], you can change the input and output, register to [ComponentRegistry] to take effect
 */
interface RequestInterceptor : Key {

    /**
     * If the current RequestInterceptor will change the ImageData,
     * please provide a valid key to build request key and cache key, otherwise return [Key.INVALID_KEY]
     */
    override val key: String

    /**
     * For sorting, larger values go lower in the list. It ranges from 0 to 100. It's usually zero.
     * Convention 51-100 is the exclusive range for sketch. [EngineRequestInterceptor] is 100
     */
    val sortWeight: Int

    @MainThread
    suspend fun intercept(chain: Chain): Result<ImageData>

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
        suspend fun proceed(request: ImageRequest): Result<ImageData>
    }
}