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
import com.github.panpf.sketch.util.requiredWorkThread

/**
 * Create a [ComponentRegistry] based on the [block]
 *
 * @see com.github.panpf.sketch.core.common.test.ComponentRegistryTest
 */
fun ComponentRegistry(block: (ComponentRegistry.Builder.() -> Unit)? = null): ComponentRegistry {
    return ComponentRegistry.Builder().apply {
        block?.invoke(this)
    }.build()
}

/**
 * Register components that are required to perform [ImageRequest] and can be extended,
 * such as [Fetcher], [Decoder], [Interceptor]
 *
 * @see com.github.panpf.sketch.core.common.test.ComponentRegistryTest
 */
open class ComponentRegistry private constructor(
    /**
     * Registered [Fetcher.Factory]
     */
    val fetchers: List<Fetcher.Factory>,
    /**
     * Registered [Decoder.Factory]
     */
    val decoders: List<Decoder.Factory>,
    /**
     * All [Interceptor]
     */
    val interceptors: List<Interceptor>,
) {

    /**
     * Registered [Fetcher.Factory]
     */
    @Deprecated("Use fetchers instead", ReplaceWith("fetchers"))
    val fetcherFactoryList: List<Fetcher.Factory> = fetchers

    /**
     * Registered [Decoder.Factory]
     */
    @Deprecated("Use decoders instead", ReplaceWith("decoders"))
    val decoderFactoryList: List<Decoder.Factory> = decoders

    /**
     * All [Interceptor]
     */
    @Deprecated("Use interceptors instead", ReplaceWith("interceptors"))
    val interceptorLists: List<Interceptor> = interceptors

    fun isEmpty(): Boolean {
        return fetchers.isEmpty()
                && decoders.isEmpty()
                && interceptors.isEmpty()
    }

    /**
     * Create a new [ComponentRegistry.Builder] based on the current [ComponentRegistry].
     *
     * You can extend it with a trailing lambda function [block]
     */
    fun newBuilder(
        block: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        block?.invoke(this)
    }

    /**
     * Create a new [ComponentRegistry] based on the current [ComponentRegistry]
     *
     * You can extend it with a trailing lambda function [block]
     */
    fun newRegistry(
        block: (Builder.() -> Unit)? = null
    ): ComponentRegistry = Builder(this).apply {
        block?.invoke(this)
    }.build()

    /**
     * Create a [Fetcher] with the registered [Fetcher.Factory]
     */
    internal fun newFetcherOrNull(requestContext: RequestContext): Fetcher? {
        return fetchers.firstNotNullOfOrNull {
            it.create(requestContext)
        }
    }

    /**
     * Create a [Fetcher] with the registered [Fetcher.Factory]
     */
    internal fun newFetcherOrThrow(requestContext: RequestContext): Fetcher {
        return newFetcherOrNull(requestContext)
            ?: throw IllegalArgumentException(
                "No Fetcher can handle this uri '${requestContext.request.uri}', " +
                        "Please add a new Fetcher to support it, " +
                        "refer to the documentation: https://github.com/panpf/sketch/blob/main/docs/fetcher.md"
            )
    }

    /**
     * Create a [Decoder] with the registered [Decoder.Factory]
     */
    @WorkerThread
    internal fun newDecoderOrNull(
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): Decoder? {
        requiredWorkThread()
        return decoders.firstNotNullOfOrNull {
            it.create(requestContext, fetchResult)
        }
    }

    /**
     * Create a [Decoder] with the registered [Decoder.Factory]
     */
    @WorkerThread
    internal fun newDecoderOrThrow(
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): Decoder {
        return newDecoderOrNull(requestContext, fetchResult)
            ?: throw IllegalArgumentException(
                "No Decoder can handle this uri '${requestContext.request.uri}', Please add a new Decoder to support it, refer to the documentation: https://github.com/panpf/sketch/blob/main/docs/decoder.md"
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ComponentRegistry
        if (fetchers != other.fetchers) return false
        if (decoders != other.decoders) return false
        if (interceptors != other.interceptors) return false
        return true
    }

    override fun hashCode(): Int {
        var result = fetchers.hashCode()
        result = 31 * result + decoders.hashCode()
        result = 31 * result + interceptors.hashCode()
        return result
    }

    override fun toString(): String {
        val fetchersString = fetchers
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val decodersString = decoders
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val interceptorsString = interceptors
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        return "ComponentRegistry(" +
                "fetchers=${fetchersString}," +
                "decoders=${decodersString}," +
                "interceptors=${interceptorsString}" +
                ")"
    }

    class Builder {

        private val fetchers: MutableList<Fetcher.Factory>
        private val decoders: MutableList<Decoder.Factory>
        private val interceptors: MutableList<Interceptor>

        constructor() {
            this.fetchers = mutableListOf()
            this.decoders = mutableListOf()
            this.interceptors = mutableListOf()
        }

        constructor(componentRegistry: ComponentRegistry) {
            this.fetchers = componentRegistry.fetchers.toMutableList()
            this.decoders =
                componentRegistry.decoders.toMutableList()
            this.interceptors = componentRegistry.interceptors.toMutableList()
        }

        /**
         * Register an [Fetcher.Factory]
         */
        fun add(fetchFactory: Fetcher.Factory): Builder = apply {
            require(fetchFactory.sortWeight in 0..100) {
                "sortWeight has a valid range of 0 to 100"
            }
            fetchers.add(fetchFactory)
        }

        /**
         * Register an [Fetcher.Factory]
         */
        @Deprecated(
            message = "Use add instead. Will be removed in the future",
            replaceWith = ReplaceWith("add(fetchFactory)")
        )
        fun addFetcher(fetchFactory: Fetcher.Factory): Builder = add(fetchFactory)

        /**
         * Register an [Decoder.Factory]
         */
        fun add(decoderFactory: Decoder.Factory): Builder = apply {
            require(decoderFactory.sortWeight in 0..100) {
                "sortWeight has a valid range of 0 to 100"
            }
            decoders.add(decoderFactory)
        }

        /**
         * Register an [Decoder.Factory]
         */
        @Deprecated(
            message = "Use add instead. Will be removed in the future",
            replaceWith = ReplaceWith("add(decoderFactory)")
        )
        fun addDecoder(decoderFactory: Decoder.Factory): Builder = add(decoderFactory)

        /**
         * Append an [Interceptor]
         */
        fun add(interceptor: Interceptor): Builder = apply {
            require(interceptor.sortWeight in 0..100) {
                "sortWeight has a valid range of 0 to 100"
            }
            this.interceptors.add(interceptor)
        }

        /**
         * Append an [Interceptor]
         */
        @Deprecated(
            message = "Use add instead. Will be removed in the future",
            replaceWith = ReplaceWith("add(interceptor)")
        )
        fun addInterceptor(interceptor: Interceptor): Builder = add(interceptor)

        /**
         * Append an [Interceptor]
         */
        @Deprecated(
            message = "Use add instead. Will be removed in the future",
            replaceWith = ReplaceWith("add(interceptor)")
        )
        fun addRequestInterceptor(interceptor: Interceptor): Builder = add(interceptor)

        /**
         * Merge the [ComponentRegistry]
         */
        fun addComponents(components: ComponentRegistry) {
            components.interceptors.forEach {
                add(it)
            }
            components.decoders.forEach {
                add(it)
            }
            components.fetchers.forEach {
                add(it)
            }
        }

        fun build(): ComponentRegistry {
            val sortedFetchers = fetchers.sortedBy { it.sortWeight }
            val sortedDecoders = decoders.sortedBy { it.sortWeight }
            val sortedInterceptors = interceptors.sortedBy { it.sortWeight }
            return ComponentRegistry(
                fetchers = sortedFetchers,
                decoders = sortedDecoders,
                interceptors = sortedInterceptors,
            )
        }
    }
}

/**
 * Check if the [ComponentRegistry] is not empty
 *
 * @see com.github.panpf.sketch.core.common.test.ComponentRegistryTest.testIsEmpty
 */
fun ComponentRegistry.isNotEmpty(): Boolean = !isEmpty()

/**
 * Merge two [ComponentRegistry]s
 *
 * @see com.github.panpf.sketch.core.common.test.ComponentRegistryTest.testMerged
 */
fun ComponentRegistry?.merged(other: ComponentRegistry?): ComponentRegistry? {
    if (this == null || other == null) {
        return this ?: other
    }
    return this.newBuilder().apply {
        other.fetchers.forEach {
            add(it)
        }
        other.decoders.forEach {
            add(it)
        }
        other.interceptors.forEach {
            add(it)
        }
    }.build()
}