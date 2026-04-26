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
import kotlin.reflect.KClass

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
 * Register or disabled components that are required to perform [ImageRequest] and can be extended,
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
     * Registered [Interceptor]
     */
    val interceptors: List<Interceptor>,
    /**
     * Disabled [Fetcher.Factory]
     */
    val disabledFetchers: List<KClass<out Fetcher.Factory>>,
    /**
     * Disabled [Decoder.Factory]
     */
    val disabledDecoders: List<KClass<out Decoder.Factory>>,
    /**
     * Disabled [Interceptor]
     */
    val disabledInterceptors: List<KClass<out Interceptor>>,
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
     * Registered [Interceptor]
     */
    @Deprecated("Use interceptors instead", ReplaceWith("interceptors"))
    val interceptorLists: List<Interceptor> = interceptors

    /**
     * Check if the [ComponentRegistry] is empty, that is, no components are registered and no components are disabled
     */
    fun isEmpty(): Boolean {
        return fetchers.isEmpty()
                && decoders.isEmpty()
                && interceptors.isEmpty()
                && disabledFetchers.isEmpty()
                && disabledDecoders.isEmpty()
                && disabledInterceptors.isEmpty()
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
        if (disabledFetchers != other.disabledFetchers) return false
        if (disabledDecoders != other.disabledDecoders) return false
        if (disabledInterceptors != other.disabledInterceptors) return false
        return true
    }

    override fun hashCode(): Int {
        var result = fetchers.hashCode()
        result = 31 * result + decoders.hashCode()
        result = 31 * result + interceptors.hashCode()
        result = 31 * result + disabledFetchers.hashCode()
        result = 31 * result + disabledDecoders.hashCode()
        result = 31 * result + disabledInterceptors.hashCode()
        return result
    }

    override fun toString(): String {
        val fetchersString = fetchers
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val decodersString = decoders
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val interceptorsString = interceptors
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val disabledFetchersString = disabledFetchers
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val disabledDecodersString = disabledDecoders
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        val disabledInterceptorsString = disabledInterceptors
            .joinToString(prefix = "[", postfix = "]", separator = ",")
        return "ComponentRegistry(" +
                "fetchers=${fetchersString}," +
                "decoders=${decodersString}," +
                "interceptors=${interceptorsString}," +
                "disabledFetchers=${disabledFetchersString}," +
                "disabledDecoders=${disabledDecodersString}," +
                "disabledInterceptors=${disabledInterceptorsString}" +
                ")"
    }

    class Builder {

        private var fetchers: MutableSet<Fetcher.Factory>? = null
        private var decoders: MutableSet<Decoder.Factory>? = null
        private var interceptors: MutableSet<Interceptor>? = null
        private var disabledFetchers: MutableSet<KClass<out Fetcher.Factory>>? = null
        private var disabledDecoders: MutableSet<KClass<out Decoder.Factory>>? = null
        private var disabledInterceptors: MutableSet<KClass<out Interceptor>>? = null

        constructor()

        constructor(componentRegistry: ComponentRegistry) {
            this.fetchers = componentRegistry.fetchers.takeIf { it.isNotEmpty() }?.toMutableSet()
            this.decoders = componentRegistry.decoders.takeIf { it.isNotEmpty() }?.toMutableSet()
            this.interceptors =
                componentRegistry.interceptors.takeIf { it.isNotEmpty() }?.toMutableSet()
            this.disabledFetchers =
                componentRegistry.disabledFetchers.takeIf { it.isNotEmpty() }?.toMutableSet()
            this.disabledDecoders =
                componentRegistry.disabledDecoders.takeIf { it.isNotEmpty() }?.toMutableSet()
            this.disabledInterceptors =
                componentRegistry.disabledInterceptors.takeIf { it.isNotEmpty() }?.toMutableSet()
        }

        /**
         * Register an [Fetcher.Factory]
         */
        fun add(fetchFactory: Fetcher.Factory): Builder = apply {
            require(fetchFactory.sortWeight in 0..100) {
                "sortWeight has a valid range of 0 to 100: $fetchFactory"
            }
            (this.fetchers
                ?: mutableSetOf<Fetcher.Factory>().apply { this@Builder.fetchers = this }
                    ).add(fetchFactory)
        }

        /**
         * Register an [Fetcher.Factory] List
         */
        fun add(vararg fetchers: Fetcher.Factory): Builder = apply {
            if (fetchers.isNotEmpty()) {
                fetchers.forEach {
                    add(it)
                }
            }
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
            (this.decoders
                ?: mutableSetOf<Decoder.Factory>().apply { this@Builder.decoders = this }
                    ).add(decoderFactory)
        }

        /**
         * Register an [Decoder.Factory] List
         */
        fun add(vararg decoders: Decoder.Factory): Builder = apply {
            if (decoders.isNotEmpty()) {
                decoders.forEach {
                    add(it)
                }
            }
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
            (this.interceptors
                ?: mutableSetOf<Interceptor>().apply { this@Builder.interceptors = this }
                    ).add(interceptor)
        }

        /**
         * Register an [Interceptor] List
         */
        fun add(vararg interceptors: Interceptor): Builder = apply {
            if (interceptors.isNotEmpty()) {
                interceptors.forEach {
                    add(it)
                }
            }
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
         * Disabled a [Fetcher.Factory] by its class
         */
        fun disabledFetcher(vararg fetcherClasses: KClass<out Fetcher.Factory>): Builder = apply {
            (this.disabledFetchers
                ?: mutableSetOf<KClass<out Fetcher.Factory>>().apply {
                    this@Builder.disabledFetchers = this
                }
                    ).addAll(fetcherClasses)
        }

        /**
         * Disabled a [Fetcher.Factory] by its class
         */
        fun disabledDecoder(vararg decoderClasses: KClass<out Decoder.Factory>): Builder = apply {
            (this.disabledDecoders
                ?: mutableSetOf<KClass<out Decoder.Factory>>().apply {
                    this@Builder.disabledDecoders = this
                }
                    ).addAll(decoderClasses)
        }

        /**
         * Disabled a [Fetcher.Factory] by its class
         */
        fun disabledInterceptor(vararg interceptorClasses: KClass<out Interceptor>): Builder =
            apply {
                (this.disabledInterceptors
                    ?: mutableSetOf<KClass<out Interceptor>>().apply {
                        this@Builder.disabledInterceptors = this
                    }
                        ).addAll(interceptorClasses)
            }

        /**
         * Merge the [ComponentRegistry]
         */
        fun addComponents(components: ComponentRegistry): Builder = apply {
            components.interceptors.forEach {
                add(it)
            }
            components.decoders.forEach {
                add(it)
            }
            components.fetchers.forEach {
                add(it)
            }
            components.disabledFetchers.forEach {
                disabledFetcher(it)
            }
            components.disabledDecoders.forEach {
                disabledDecoder(it)
            }
            components.disabledInterceptors.forEach {
                disabledInterceptor(it)
            }
        }

        fun build(): ComponentRegistry {
            val sortedFetchers = fetchers
                ?.asSequence()
                ?.filter { cur -> disabledFetchers?.all { !it.isInstance(cur) } != false }
                ?.sortedBy { it.sortWeight }
                ?.toList()
            val sortedDecoders = decoders
                ?.asSequence()
                ?.filter { cur -> disabledDecoders?.all { !it.isInstance(cur) } != false }
                ?.sortedBy { it.sortWeight }
                ?.toList()
            val sortedInterceptors = interceptors
                ?.asSequence()
                ?.filter { cur -> disabledInterceptors?.all { !it.isInstance(cur) } != false }
                ?.sortedBy { it.sortWeight }
                ?.toList()
            val disabledFetchers = disabledFetchers?.toList()
            val disabledDecoders = disabledDecoders?.toList()
            val disabledInterceptors = disabledInterceptors?.toList()
            return ComponentRegistry(
                fetchers = sortedFetchers ?: emptyList(),
                decoders = sortedDecoders ?: emptyList(),
                interceptors = sortedInterceptors ?: emptyList(),
                disabledFetchers = disabledFetchers ?: emptyList(),
                disabledDecoders = disabledDecoders ?: emptyList(),
                disabledInterceptors = disabledInterceptors ?: emptyList(),
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
    if (this == null || other == null) return this ?: other
    return this.newBuilder().addComponents(other).build()
}