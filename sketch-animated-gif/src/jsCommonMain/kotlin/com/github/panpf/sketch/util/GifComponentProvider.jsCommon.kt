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
import com.github.panpf.sketch.decode.SkiaGifDecoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Interceptor
import kotlin.reflect.KClass

/**
 * Cooperate with [ComponentLoader] to achieve automatic registration [SkiaGifDecoder]
 *
 * @see com.github.panpf.sketch.animated.gif.jscommon.test.util.GifComponentProviderJsCommonTest
 */
actual class GifComponentProvider : ComponentProvider {

    actual override fun addFetchers(context: PlatformContext): List<Fetcher.Factory>? {
        return null
    }

    actual override fun addDecoders(context: PlatformContext): List<Decoder.Factory>? {
        return listOf(SkiaGifDecoder.Factory())
    }

    actual override fun addInterceptors(context: PlatformContext): List<Interceptor>? {
        return null
    }

    actual override fun disabledFetchers(context: PlatformContext): List<KClass<out Fetcher.Factory>>? {
        return null
    }

    actual override fun disabledDecoders(context: PlatformContext): List<KClass<out Decoder.Factory>>? {
        return null
    }

    actual override fun disabledInterceptors(context: PlatformContext): List<KClass<out Interceptor>>? {
        return null
    }

    override fun toString(): String = "GifComponentProvider"
}