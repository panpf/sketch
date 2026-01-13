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

package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.DecoderInterceptor
import com.github.panpf.sketch.util.NullableKey

@Deprecated(
    message = "Use Interceptor instead. Will be removed in the future",
    replaceWith = ReplaceWith("Interceptor")
)
typealias RequestInterceptor = Interceptor

/**
 * Intercept the execution of [ImageRequest], you can change the input and output, register to [ComponentRegistry] to take effect
 *
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
interface Interceptor : NullableKey {

    /**
     * If the current Interceptor will change the ImageData,
     * please provide a valid key to build request key and cache key, otherwise return null
     */
    override val key: String?

    /**
     * For sorting, larger values go lower in the list. It ranges from 0 to 100. It's usually zero. Only [DecoderInterceptor] can be 100
     */
    val sortWeight: Int

    /**
     * Intercept the execution of [ImageRequest], you can change the input and output
     */
    @MainThread
    suspend fun intercept(chain: Chain): Result<ImageData>

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String

    /**
     * A chain is a series of interceptors that are called in order.
     */
    interface Chain {

        /**
         * The request context
         */
        val requestContext: RequestContext

        /**
         * The [Sketch] instance
         */
        val sketch: Sketch
            get() = requestContext.sketch

        /**
         * The original request
         */
        val initialRequest: ImageRequest
            get() = requestContext.initialRequest

        /**
         * The request to proceed with.
         */
        val request: ImageRequest
            get() = requestContext.request

        /**
         * Continue executing the chain.
         *
         * @param request The request to proceed with.
         */
        @MainThread
        suspend fun proceed(request: ImageRequest): Result<ImageData>
    }
}