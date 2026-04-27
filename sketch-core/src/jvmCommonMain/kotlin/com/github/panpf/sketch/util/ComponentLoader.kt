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

import androidx.annotation.Keep
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Interceptor
import java.util.ServiceLoader
import kotlin.reflect.KClass

/**
 * Component loader. Automatically load and register all components
 *
 * @see com.github.panpf.sketch.componentloadertest.desktop.test.ComponentLoaderTest
 * @see com.github.panpf.sketch.componentloadertest.android.test.ComponentLoaderTest
 */
actual object ComponentLoader {

    // This code is written intentionally so R8 can optimize it:
    // https://github.com/Kotlin/kotlinx.coroutines/issues/1231
    actual val componentProviders by lazy {
        ServiceLoader.load(
            ComponentProvider::class.java,
            ComponentProvider::class.java.classLoader,
        ).iterator().asSequence().toList().toImmutableList()
    }

    actual fun register(componentProvider: ComponentProvider) {
        throw UnsupportedOperationException()
    }
}

/**
 * Register or disabled [Fetcher], [Decoder] or [Interceptor] to [ComponentLoader]
 */
@Keep
actual interface ComponentProvider {
    actual fun addFetchers(context: PlatformContext): List<Fetcher.Factory>?
    actual fun addDecoders(context: PlatformContext): List<Decoder.Factory>?
    actual fun addInterceptors(context: PlatformContext): List<Interceptor>?
    actual fun disabledFetchers(context: PlatformContext): List<KClass<out Fetcher.Factory>>?
    actual fun disabledDecoders(context: PlatformContext): List<KClass<out Decoder.Factory>>?
    actual fun disabledInterceptors(context: PlatformContext): List<KClass<out Interceptor>>?
}