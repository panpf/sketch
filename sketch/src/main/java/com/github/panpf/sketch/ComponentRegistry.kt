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
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DrawableDecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.requiredWorkThread

/**
 * Register components that are required to perform [ImageRequest] and can be extended,
 * such as [Fetcher], [BitmapDecoder], [DrawableDecoder], [RequestInterceptor], [BitmapDecodeInterceptor], [DrawableDecodeInterceptor]
 */
open class ComponentRegistry private constructor(
    /**
     * Registered [Fetcher.Factory]
     */
    val fetcherFactoryList: List<Fetcher.Factory>,
    /**
     * Registered [BitmapDecoder.Factory]
     */
    val bitmapDecoderFactoryList: List<BitmapDecoder.Factory>,
    /**
     * Registered [DrawableDecoder.Factory]
     */
    val drawableDecoderFactoryList: List<DrawableDecoder.Factory>,
    /**
     * All [RequestInterceptor]
     */
    val requestInterceptorList: List<RequestInterceptor>,
    /**
     * All [BitmapDecodeInterceptor]
     */
    val bitmapDecodeInterceptorList: List<BitmapDecodeInterceptor>,
    /**
     * All [DrawableDecodeInterceptor]
     */
    val drawableDecodeInterceptorList: List<DrawableDecodeInterceptor>,
) {

    fun isEmpty(): Boolean {
        return fetcherFactoryList.isEmpty()
                && bitmapDecoderFactoryList.isEmpty()
                && drawableDecoderFactoryList.isEmpty()
                && requestInterceptorList.isEmpty()
                && bitmapDecodeInterceptorList.isEmpty()
                && drawableDecodeInterceptorList.isEmpty()
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
    @WorkerThread
    internal fun newFetcherOrNull(sketch: Sketch, request: ImageRequest): Fetcher? {
        requiredWorkThread()
        return fetcherFactoryList.firstNotNullOfOrNull {
            it.create(sketch, request)
        }
    }

    /**
     * Create a [Fetcher] with the registered [Fetcher.Factory]
     */
    @WorkerThread
    internal fun newFetcher(sketch: Sketch, request: ImageRequest): Fetcher {
        return newFetcherOrNull(sketch, request)
            ?: throw IllegalArgumentException(
                "No Fetcher can handle this uri '${request.uriString}', " +
                        "please pass ComponentRegistry. Builder addFetcher () function to add a new Fetcher to support it"
            )
    }

    /**
     * Create a [BitmapDecoder] with the registered [BitmapDecoder.Factory]
     */
    @WorkerThread
    internal fun newBitmapDecoderOrNull(
        sketch: Sketch,
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): BitmapDecoder? {
        requiredWorkThread()
        return bitmapDecoderFactoryList.firstNotNullOfOrNull {
            it.create(sketch, requestContext, fetchResult)
        }
    }

    /**
     * Create a [BitmapDecoder] with the registered [BitmapDecoder.Factory]
     */
    @WorkerThread
    internal fun newBitmapDecoder(
        sketch: Sketch,
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): BitmapDecoder {
        return newBitmapDecoderOrNull(sketch, requestContext, fetchResult)
            ?: throw IllegalArgumentException(
                "No BitmapDecoder can handle this uri '${requestContext.request.uriString}', " +
                        "please pass ComponentRegistry.Builder.addBitmapDecoder() function to add a new BitmapDecoder to support it"
            )
    }

    /**
     * Create a [DrawableDecoder] with the registered [DrawableDecoder.Factory]
     */
    @WorkerThread
    internal fun newDrawableDecoderOrNull(
        sketch: Sketch,
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): DrawableDecoder? {
        requiredWorkThread()
        return drawableDecoderFactoryList.firstNotNullOfOrNull {
            it.create(sketch, requestContext, fetchResult)
        }
    }

    /**
     * Create a [DrawableDecoder] with the registered [DrawableDecoder.Factory]
     */
    @WorkerThread
    internal fun newDrawableDecoder(
        sketch: Sketch,
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): DrawableDecoder {
        return newDrawableDecoderOrNull(sketch, requestContext, fetchResult)
            ?: throw IllegalArgumentException(
                "No DrawableDecoder can handle this uri '${requestContext.request.uriString}', " +
                        "please pass ComponentRegistry.Builder.addDrawableDecoder() function to add a new DrawableDecoder to support it"
            )
    }

    override fun toString(): String {
        val fetchersString = fetcherFactoryList
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val bitmapDecodersString = bitmapDecoderFactoryList
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val drawableDecodersString = drawableDecoderFactoryList
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val requestInterceptorsString = requestInterceptorList
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val bitmapDecodeInterceptorsString = bitmapDecodeInterceptorList
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val drawableDecodeInterceptorsString = drawableDecodeInterceptorList
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        return "ComponentRegistry(" +
                "fetcherFactoryList=${fetchersString}," +
                "bitmapDecoderFactoryList=${bitmapDecodersString}," +
                "drawableDecoderFactoryList=${drawableDecodersString}," +
                "requestInterceptorList=${requestInterceptorsString}," +
                "bitmapDecodeInterceptorList=${bitmapDecodeInterceptorsString}," +
                "drawableDecodeInterceptorList=${drawableDecodeInterceptorsString}" +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComponentRegistry) return false

        if (fetcherFactoryList != other.fetcherFactoryList) return false
        if (bitmapDecoderFactoryList != other.bitmapDecoderFactoryList) return false
        if (drawableDecoderFactoryList != other.drawableDecoderFactoryList) return false
        if (requestInterceptorList != other.requestInterceptorList) return false
        if (bitmapDecodeInterceptorList != other.bitmapDecodeInterceptorList) return false
        if (drawableDecodeInterceptorList != other.drawableDecodeInterceptorList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fetcherFactoryList.hashCode()
        result = 31 * result + bitmapDecoderFactoryList.hashCode()
        result = 31 * result + drawableDecoderFactoryList.hashCode()
        result = 31 * result + requestInterceptorList.hashCode()
        result = 31 * result + bitmapDecodeInterceptorList.hashCode()
        result = 31 * result + drawableDecodeInterceptorList.hashCode()
        return result
    }

    class Builder {

        private val fetcherFactoryList: MutableList<Fetcher.Factory>
        private val bitmapDecoderFactoryList: MutableList<BitmapDecoder.Factory>
        private val drawableDecoderFactoryList: MutableList<DrawableDecoder.Factory>
        private val requestInterceptorList: MutableList<RequestInterceptor>
        private val bitmapDecodeInterceptorList: MutableList<BitmapDecodeInterceptor>
        private val drawableDecodeInterceptorList: MutableList<DrawableDecodeInterceptor>

        constructor() {
            this.fetcherFactoryList = mutableListOf()
            this.bitmapDecoderFactoryList = mutableListOf()
            this.drawableDecoderFactoryList = mutableListOf()
            this.requestInterceptorList = mutableListOf()
            this.bitmapDecodeInterceptorList = mutableListOf()
            this.drawableDecodeInterceptorList = mutableListOf()
        }

        constructor(componentRegistry: ComponentRegistry) {
            this.fetcherFactoryList = componentRegistry.fetcherFactoryList.toMutableList()
            this.bitmapDecoderFactoryList =
                componentRegistry.bitmapDecoderFactoryList.toMutableList()
            this.drawableDecoderFactoryList =
                componentRegistry.drawableDecoderFactoryList.toMutableList()
            this.requestInterceptorList = componentRegistry.requestInterceptorList.toMutableList()
            this.bitmapDecodeInterceptorList =
                componentRegistry.bitmapDecodeInterceptorList.toMutableList()
            this.drawableDecodeInterceptorList =
                componentRegistry.drawableDecodeInterceptorList.toMutableList()
        }

        /**
         * Register an [Fetcher.Factory]
         */
        fun addFetcher(fetchFactory: Fetcher.Factory): Builder = apply {
            fetcherFactoryList.add(fetchFactory)
        }

        /**
         * Register an [BitmapDecoder.Factory]
         */
        fun addBitmapDecoder(bitmapDecoderFactory: BitmapDecoder.Factory): Builder = apply {
            bitmapDecoderFactoryList.add(bitmapDecoderFactory)
        }

        /**
         * Register an [DrawableDecoder.Factory]
         */
        fun addDrawableDecoder(drawableDecoderFactory: DrawableDecoder.Factory): Builder = apply {
            drawableDecoderFactoryList.add(drawableDecoderFactory)
        }

        /**
         * Append an [RequestInterceptor]
         */
        fun addRequestInterceptor(interceptor: RequestInterceptor): Builder = apply {
            this.requestInterceptorList.add(interceptor)
        }

        /**
         * Append an [BitmapDecodeInterceptor]
         */
        fun addBitmapDecodeInterceptor(bitmapDecodeInterceptor: BitmapDecodeInterceptor): Builder =
            apply {
                this.bitmapDecodeInterceptorList.add(bitmapDecodeInterceptor)
            }

        /**
         * Append an [DrawableDecodeInterceptor]
         */
        fun addDrawableDecodeInterceptor(drawableDecodeInterceptor: DrawableDecodeInterceptor): Builder =
            apply {
                this.drawableDecodeInterceptorList.add(drawableDecodeInterceptor)
            }

        fun build(): ComponentRegistry = ComponentRegistry(
            fetcherFactoryList = fetcherFactoryList.toList(),
            bitmapDecoderFactoryList = bitmapDecoderFactoryList.toList(),
            drawableDecoderFactoryList = drawableDecoderFactoryList.toList(),
            requestInterceptorList = requestInterceptorList.toList(),
            bitmapDecodeInterceptorList = bitmapDecodeInterceptorList.toList(),
            drawableDecodeInterceptorList = drawableDecodeInterceptorList.toList(),
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
        other.bitmapDecoderFactoryList.forEach {
            addBitmapDecoder(it)
        }
        other.drawableDecoderFactoryList.forEach {
            addDrawableDecoder(it)
        }
        other.requestInterceptorList.forEach {
            addRequestInterceptor(it)
        }
        other.bitmapDecodeInterceptorList.forEach {
            addBitmapDecodeInterceptor(it)
        }
        other.drawableDecodeInterceptorList.forEach {
            addDrawableDecodeInterceptor(it)
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
        return localRequestInterceptorList?.plus(registry.requestInterceptorList)
            ?: registry.requestInterceptorList
    }

    /**
     * Get the [ImageRequest] plus the global [BitmapDecodeInterceptor] list
     */
    fun getBitmapDecodeInterceptorList(request: ImageRequest): List<BitmapDecodeInterceptor> {
        val localBitmapDecodeInterceptorList =
            request.componentRegistry?.bitmapDecodeInterceptorList?.takeIf { it.isNotEmpty() }
        return localBitmapDecodeInterceptorList?.plus(registry.bitmapDecodeInterceptorList)
            ?: registry.bitmapDecodeInterceptorList
    }

    /**
     * Get the [ImageRequest] plus the global [DrawableDecodeInterceptor] list
     */
    fun getDrawableDecodeInterceptorList(request: ImageRequest): List<DrawableDecodeInterceptor> {
        val localDrawableDecodeInterceptorList =
            request.componentRegistry?.drawableDecodeInterceptorList?.takeIf { it.isNotEmpty() }
        return localDrawableDecodeInterceptorList?.plus(registry.drawableDecodeInterceptorList)
            ?: registry.drawableDecodeInterceptorList
    }

    /**
     * Create a [Fetcher] with [ImageRequest]'s (preferred) and global [Fetcher.Factory]
     */
    @WorkerThread
    fun newFetcher(request: ImageRequest): Fetcher =
        request.componentRegistry?.newFetcherOrNull(sketch, request)
            ?: registry.newFetcher(sketch, request)

    /**
     * Create a [BitmapDecoder] with [ImageRequest]'s (preferred) and global [BitmapDecoder.Factory]
     */
    @WorkerThread
    fun newBitmapDecoder(
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): BitmapDecoder =
        requestContext.request.componentRegistry
            ?.newBitmapDecoderOrNull(sketch, requestContext, fetchResult)
            ?: registry.newBitmapDecoder(sketch, requestContext, fetchResult)

    /**
     * Create a [DrawableDecoder] with [ImageRequest]'s (preferred) and global [DrawableDecoder.Factory]
     */
    @WorkerThread
    fun newDrawableDecoder(
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): DrawableDecoder =
        requestContext.request.componentRegistry
            ?.newDrawableDecoderOrNull(sketch, requestContext, fetchResult)
            ?: registry.newDrawableDecoder(sketch, requestContext, fetchResult)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Components) return false
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