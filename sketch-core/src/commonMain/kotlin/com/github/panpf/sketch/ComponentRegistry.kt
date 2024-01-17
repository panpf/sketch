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
package com.github.panpf.sketch

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.requiredWorkThread

/**
 * Register components that are required to perform [ImageRequest] and can be extended,
 * such as [Fetcher], [Decoder], [RequestInterceptor], [DecodeInterceptor]
 */
open class ComponentRegistry private constructor(
    /**
     * Registered [Fetcher.Factory]
     */
    val fetcherFactoryList: List<Fetcher.Factory>,
    /**
     * Registered [Decoder.Factory]
     */
    val decoderFactoryList: List<Decoder.Factory>,
    /**
     * All [RequestInterceptor]
     */
    val requestInterceptorList: List<RequestInterceptor>,
    /**
     * All [DecodeInterceptor]
     */
    val decodeInterceptorList: List<DecodeInterceptor>,
) {

    fun isEmpty(): Boolean {
        return fetcherFactoryList.isEmpty()
                && decoderFactoryList.isEmpty()
                && requestInterceptorList.isEmpty()
                && decodeInterceptorList.isEmpty()
    }

    /**
     * Create a new [ComponentRegistry.Builder] based on the current [ComponentRegistry].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    /**
     * Create a new [ComponentRegistry] based on the current [ComponentRegistry]
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newRegistry(
        configBlock: (Builder.() -> Unit)? = null
    ): ComponentRegistry = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    /**
     * Create a [Fetcher] with the registered [Fetcher.Factory]
     */
    internal fun newFetcherOrNull(sketch: Sketch, request: ImageRequest): Fetcher? {
        return fetcherFactoryList.firstNotNullOfOrNull {
            it.create(sketch, request)
        }
    }

    /**
     * Create a [Fetcher] with the registered [Fetcher.Factory]
     */
    internal fun newFetcherOrThrow(sketch: Sketch, request: ImageRequest): Fetcher {
        return newFetcherOrNull(sketch, request)
            ?: throw IllegalArgumentException(
                "No Fetcher can handle this uri '${request.uriString}', " +
                        "please pass ComponentRegistry. Builder addFetcher () function to add a new Fetcher to support it"
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
        return decoderFactoryList.firstNotNullOfOrNull {
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
                "No Decoder can handle this uri '${requestContext.request.uriString}', " +
                        "please pass ComponentRegistry.Builder.addDecoder() function to add a new Decoder to support it"
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ComponentRegistry
        if (fetcherFactoryList != other.fetcherFactoryList) return false
        if (decoderFactoryList != other.decoderFactoryList) return false
        if (requestInterceptorList != other.requestInterceptorList) return false
        if (decodeInterceptorList != other.decodeInterceptorList) return false
        return true
    }

    override fun hashCode(): Int {
        var result = fetcherFactoryList.hashCode()
        result = 31 * result + decoderFactoryList.hashCode()
        result = 31 * result + requestInterceptorList.hashCode()
        result = 31 * result + decodeInterceptorList.hashCode()
        return result
    }

    override fun toString(): String {
        val fetchersString = fetcherFactoryList
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val decodersString = decoderFactoryList
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val requestInterceptorsString = requestInterceptorList
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val decodeInterceptorsString = decodeInterceptorList
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        return "ComponentRegistry(" +
                "fetcherFactoryList=${fetchersString}," +
                "decoderFactoryList=${decodersString}," +
                "requestInterceptorList=${requestInterceptorsString}," +
                "decodeInterceptorList=${decodeInterceptorsString}" +
                ")"
    }

    class Builder {

        private val fetcherFactoryList: MutableList<Fetcher.Factory>
        private val decoderFactoryList: MutableList<Decoder.Factory>
        private val requestInterceptorList: MutableList<RequestInterceptor>
        private val decodeInterceptorList: MutableList<DecodeInterceptor>

        constructor() {
            this.fetcherFactoryList = mutableListOf()
            this.decoderFactoryList = mutableListOf()
            this.requestInterceptorList = mutableListOf()
            this.decodeInterceptorList = mutableListOf()
        }

        constructor(componentRegistry: ComponentRegistry) {
            this.fetcherFactoryList = componentRegistry.fetcherFactoryList.toMutableList()
            this.decoderFactoryList =
                componentRegistry.decoderFactoryList.toMutableList()
            this.requestInterceptorList = componentRegistry.requestInterceptorList.toMutableList()
            this.decodeInterceptorList =
                componentRegistry.decodeInterceptorList.toMutableList()
        }

        /**
         * Register an [Fetcher.Factory]
         */
        fun addFetcher(fetchFactory: Fetcher.Factory): Builder = apply {
            fetcherFactoryList.add(fetchFactory)
        }

        /**
         * Register an [Decoder.Factory]
         */
        fun addDecoder(decoderFactory: Decoder.Factory): Builder = apply {
            decoderFactoryList.add(decoderFactory)
        }

        /**
         * Append an [RequestInterceptor]
         */
        fun addRequestInterceptor(interceptor: RequestInterceptor): Builder = apply {
            require(if (interceptor is EngineRequestInterceptor) interceptor.sortWeight == 100 else interceptor.sortWeight in 0..99) {
                "sortWeight has a valid range of 0 to 100, and only EngineRequestInterceptor can be 100"
            }
            this.requestInterceptorList.add(interceptor)
        }

        /**
         * Append an [DecodeInterceptor]
         */
        fun addDecodeInterceptor(decodeInterceptor: DecodeInterceptor): Builder =
            apply {
                require(if (decodeInterceptor is EngineDecodeInterceptor) decodeInterceptor.sortWeight == 100 else decodeInterceptor.sortWeight in 0..99) {
                    "sortWeight has a valid range of 0 to 100, and only EngineRequestInterceptor can be 100"
                }
                this.decodeInterceptorList.add(decodeInterceptor)
            }

        fun build(): ComponentRegistry = ComponentRegistry(
            fetcherFactoryList = fetcherFactoryList.toList(),
            decoderFactoryList = decoderFactoryList.toList(),
            requestInterceptorList = requestInterceptorList.sortedBy { it.sortWeight },
            decodeInterceptorList = decodeInterceptorList.sortedBy { it.sortWeight },
        )
    }
}

fun ComponentRegistry.isNotEmpty(): Boolean = !isEmpty()

fun ComponentRegistry?.merged(other: ComponentRegistry?): ComponentRegistry? {
    if (this == null || other == null) {
        return this ?: other
    }
    return this.newBuilder().apply {
        other.fetcherFactoryList.forEach {
            addFetcher(it)
        }
        other.decoderFactoryList.forEach {
            addDecoder(it)
        }
        other.requestInterceptorList.forEach {
            addRequestInterceptor(it)
        }
        other.decodeInterceptorList.forEach {
            addDecodeInterceptor(it)
        }
    }.build()
}

/**
 * Use with [ImageRequest] and the global [ComponentRegistry]
 */
class Components(private val sketch: Sketch, internal val registry: ComponentRegistry) {

    /**
     * Get the [ImageRequest] plus the global [RequestInterceptor] list
     */
    fun getRequestInterceptorList(request: ImageRequest): List<RequestInterceptor> {
        val localRequestInterceptorList =
            request.componentRegistry?.requestInterceptorList?.takeIf { it.isNotEmpty() }
        return (localRequestInterceptorList?.plus(registry.requestInterceptorList))?.sortedBy { it.sortWeight }
            ?: registry.requestInterceptorList
    }

    /**
     * Get the [ImageRequest] plus the global [DecodeInterceptor] list
     */
    fun getDecodeInterceptorList(request: ImageRequest): List<DecodeInterceptor> {
        val localDecodeInterceptorList =
            request.componentRegistry?.decodeInterceptorList?.takeIf { it.isNotEmpty() }
        return (localDecodeInterceptorList?.plus(registry.decodeInterceptorList))?.sortedBy { it.sortWeight }
            ?: registry.decodeInterceptorList
    }

    /**
     * Create a [Fetcher] with [ImageRequest]'s (preferred) and global [Fetcher.Factory]
     */
    fun newFetcherOrThrow(request: ImageRequest): Fetcher =
        request.componentRegistry?.newFetcherOrNull(sketch, request)
            ?: registry.newFetcherOrThrow(sketch, request)

    /**
     * Create a [Decoder] with [ImageRequest]'s (preferred) and global [Decoder.Factory]
     */
    @WorkerThread
    fun newDecoderOrThrow(
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): Decoder =
        requestContext.request.componentRegistry
            ?.newDecoderOrNull(requestContext, fetchResult)
            ?: registry.newDecoderOrThrow(requestContext, fetchResult)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
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