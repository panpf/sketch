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

package com.github.panpf.sketch.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.util.NullableKey

/**
 * Intercept the execution of Image decode, you can change the output, register to [ComponentRegistry] to take effect
 *
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
interface DecodeInterceptor : NullableKey {

    /**
     * If the current DecodeInterceptor will change the DecodeResult,
     * please provide a valid key to build request key and cache key, otherwise return null
     */
    override val key: String?

    /**
     * For sorting, larger values go lower in the list. It ranges from 0 to 100. It's usually zero.
     * Convention 51-100 is the exclusive range for sketch. [EngineDecodeInterceptor] is 100
     */
    val sortWeight: Int

    /**
     * Intercept the execution of Image decode
     */
    @WorkerThread
    suspend fun intercept(chain: Chain): Result<DecodeResult>

    /**
     * The chain of interceptors to execute
     */
    interface Chain {

        /**
         * The context of the request
         */
        val requestContext: RequestContext

        /**
         * The sketch instance that initiated the request
         */
        val sketch: Sketch
            get() = requestContext.sketch

        /**
         * The request that initiated the decode
         */
        val request: ImageRequest
            get() = requestContext.request

        /**
         * The result of the fetch
         */
        val fetchResult: FetchResult?

        /**
         * Proceed with the request
         */
        @WorkerThread
        suspend fun proceed(): Result<DecodeResult>
    }
}