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

package com.github.panpf.sketch

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.RequestContext

/**
 * Use with [com.github.panpf.sketch.request.ImageRequest] and the global [ComponentRegistry]
 *
 * @see com.github.panpf.sketch.core.common.test.ComponentsTest
 */
class Components(val registry: ComponentRegistry) {

    /**
     * Get the [com.github.panpf.sketch.request.ImageRequest] plus the global [com.github.panpf.sketch.request.Interceptor] list
     */
    fun getInterceptors(request: ImageRequest): List<Interceptor> {
        val finalRegistry = request.componentRegistry
            ?.takeIf { it.isNotEmpty() }
            ?.merged(registry)
            ?: registry
        return finalRegistry.interceptors
    }

    /**
     * Get the [ImageRequest] plus the global [Interceptor] list
     */
    @Deprecated(
        message = "Use getInterceptors instead. Will be removed in the future",
        replaceWith = ReplaceWith("getInterceptors(request)")
    )
    fun getRequestInterceptorList(request: ImageRequest): List<Interceptor> =
        getInterceptors(request)

    /**
     * Create a [com.github.panpf.sketch.fetch.Fetcher] with [ImageRequest]'s (preferred) and global [com.github.panpf.sketch.fetch.Fetcher.Factory]
     */
    fun newFetcherOrThrow(requestContext: RequestContext): Fetcher {
        val finalRegistry = requestContext.request.componentRegistry
            ?.takeIf { it.isNotEmpty() }
            ?.merged(registry)
            ?: registry
        return finalRegistry.newFetcherOrThrow(requestContext)
    }

    /**
     * Create a [com.github.panpf.sketch.decode.Decoder] with [ImageRequest]'s (preferred) and global [com.github.panpf.sketch.decode.Decoder.Factory]
     */
    @WorkerThread
    fun newDecoderOrThrow(
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): Decoder {
        val finalRegistry = requestContext.request.componentRegistry
            ?.takeIf { it.isNotEmpty() }
            ?.merged(registry)
            ?: registry
        return finalRegistry.newDecoderOrThrow(requestContext, fetchResult)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Components
        if (registry != other.registry) return false
        return true
    }

    override fun hashCode(): Int {
        return registry.hashCode()
    }

    override fun toString(): String {
        return "Components($registry)"
    }
}