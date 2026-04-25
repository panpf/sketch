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

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Interceptor
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

/**
 * Component loader. Automatically load and register all components
 *
 * @see com.github.panpf.sketch.componentloadertest.ios.test.ComponentLoaderTest
 * @see com.github.panpf.sketch.componentloadertest.js.test.ComponentLoaderTest
 * @see com.github.panpf.sketch.componentloadertest.wasmjs.test.ComponentLoaderTest
 */
actual object ComponentLoader {

    private val lock = SynchronizedObject()
    private val _fetchers = mutableListOf<FetcherProvider>()
    private val _decoders = mutableListOf<DecoderProvider>()
    private val _interceptors = mutableListOf<InterceptorProvider>()

    actual val fetchers: List<FetcherProvider>
        get() = synchronized(lock) { _fetchers.toImmutableList() }

    actual val decoders: List<DecoderProvider>
        get() = synchronized(lock) { _decoders.toImmutableList() }

    actual val interceptors: List<InterceptorProvider>
        get() = synchronized(lock) { _interceptors.toImmutableList() }

    actual fun register(fetcher: FetcherProvider) = synchronized(lock) {
        _fetchers += fetcher
    }

    actual fun register(decoder: DecoderProvider) = synchronized(lock) {
        _decoders += decoder
    }

    actual fun register(interceptor: InterceptorProvider) = synchronized(lock) {
        _interceptors += interceptor
    }
}

/**
 * Register a [FetcherProvider] to [ComponentLoader]
 */
actual interface FetcherProvider {
    actual fun factory(context: PlatformContext): Fetcher.Factory?
}

/**
 * Register a [DecoderProvider] to [ComponentLoader]
 */
actual interface DecoderProvider {
    actual fun factory(context: PlatformContext): Decoder.Factory?
}

/**
 * Register a [InterceptorProvider] to [ComponentLoader]
 */
actual interface InterceptorProvider {
    actual fun create(context: PlatformContext): Interceptor?
}