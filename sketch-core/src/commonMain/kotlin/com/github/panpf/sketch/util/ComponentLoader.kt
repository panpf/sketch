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

package com.github.panpf.sketch.util

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import kotlin.reflect.KClass

/**
 * Component loader. Automatically load and register all components
 *
 * @see com.github.panpf.sketch.componentloadertest.android.test.ComponentLoaderTest
 * @see com.github.panpf.sketch.componentloadertest.desktop.test.ComponentLoaderTest
 * @see com.github.panpf.sketch.componentloadertest.ios.test.ComponentLoaderTest
 * @see com.github.panpf.sketch.componentloadertest.js.test.ComponentLoaderTest
 * @see com.github.panpf.sketch.componentloadertest.wasmjs.test.ComponentLoaderTest
 */
expect object ComponentLoader {

    val fetchers: List<FetcherProvider>

    val decoders: List<DecoderProvider>

    // Only available on non-JVM. Added these declarations to work-around a compiler bug.
    fun register(fetcher: FetcherProvider)

    // Only available on non-JVM. Added these declarations to work-around a compiler bug.
    fun register(decoder: DecoderProvider)
}

/**
 * Register a [FetcherProvider] to [ComponentLoader]
 */
expect interface FetcherProvider {
    fun factory(context: PlatformContext): Fetcher.Factory?
}

/**
 * Register a [DecoderProvider] to [ComponentLoader]
 */
expect interface DecoderProvider {
    fun factory(context: PlatformContext): Decoder.Factory?
}

/**
 * Convert [ComponentLoader] to [ComponentRegistry]
 *
 * @see com.github.panpf.sketch.componentloadertest.android.test.ComponentLoaderTest.testToComponentRegistry
 * @see com.github.panpf.sketch.componentloadertest.desktop.test.ComponentLoaderTest.testToComponentRegistry
 * @see com.github.panpf.sketch.componentloadertest.ios.test.ComponentLoaderTest.testToComponentRegistry
 * @see com.github.panpf.sketch.componentloadertest.js.test.ComponentLoaderTest.testToComponentRegistry
 * @see com.github.panpf.sketch.componentloadertest.wasmjs.test.ComponentLoaderTest.testToComponentRegistry
 */
fun ComponentLoader.toComponentRegistry(
    context: PlatformContext,
    ignoreFetcherProviders: List<KClass<out FetcherProvider>>? = null,
    ignoreDecoderProviders: List<KClass<out DecoderProvider>>? = null,
): ComponentRegistry {
    return ComponentRegistry {
        fetchers.filter { fetcherProvider ->
            ignoreFetcherProviders?.find { ignoreProviderClass ->
                fetcherProvider::class == ignoreProviderClass
            } == null
        }.forEach { fetcherComponent ->
            fetcherComponent.factory(context)?.let { factory -> addFetcher(factory) }
        }
        decoders.filter { decoderProvider ->
            ignoreDecoderProviders?.find { ignoreProviderClass ->
                decoderProvider::class == ignoreProviderClass
            } == null
        }.forEach { decoderComponent ->
            decoderComponent.factory(context)?.let { factory -> addDecoder(factory) }
        }
    }
}