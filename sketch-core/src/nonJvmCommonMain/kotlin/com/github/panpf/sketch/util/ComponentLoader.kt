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
import kotlin.reflect.KClass

/**
 * Component loader. Automatically load and register all components
 *
 * @see com.github.panpf.sketch.componentloadertest.ios.test.ComponentLoaderTest
 * @see com.github.panpf.sketch.componentloadertest.js.test.ComponentLoaderTest
 * @see com.github.panpf.sketch.componentloadertest.wasmjs.test.ComponentLoaderTest
 */
actual object ComponentLoader {

    private val lock = SynchronizedObject()
    private val _componentProviders = mutableListOf<ComponentProvider>()

    actual val componentProviders: List<ComponentProvider>
        get() = synchronized(lock) { _componentProviders.toImmutableList() }

    actual fun register(componentProvider: ComponentProvider) = synchronized(lock) {
        _componentProviders += componentProvider
    }
}

/**
 * Register or disabled [Fetcher], [Decoder] or [Interceptor] to [ComponentLoader]
 */
actual interface ComponentProvider {
    actual fun addFetchers(context: PlatformContext): List<Fetcher.Factory>?
    actual fun addDecoders(context: PlatformContext): List<Decoder.Factory>?
    actual fun addInterceptors(context: PlatformContext): List<Interceptor>?
    actual fun disabledFetchers(context: PlatformContext): List<KClass<out Fetcher.Factory>>?
    actual fun disabledDecoders(context: PlatformContext): List<KClass<out Decoder.Factory>>?
    actual fun disabledInterceptors(context: PlatformContext): List<KClass<out Interceptor>>?
}