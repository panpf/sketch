/*
 * Copyright (C) 2026 panpf <panpfpanpf@outlook.com>
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
import com.github.panpf.sketch.decode.FileVideoFrameDecoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Interceptor
import kotlin.reflect.KClass

/**
 * Register the AVFoundation file-video decoder on macOS.
 *
 * @see com.github.panpf.sketch.video.macos.test.util.VideoComponentProviderTest
 */
class VideoComponentProvider : ComponentProvider {

    override fun addFetchers(context: PlatformContext): List<Fetcher.Factory>? = null

    override fun addDecoders(context: PlatformContext): List<Decoder.Factory> =
        listOf(FileVideoFrameDecoder.Factory())

    override fun addInterceptors(context: PlatformContext): List<Interceptor>? = null

    override fun disabledFetchers(
        context: PlatformContext
    ): List<KClass<out Fetcher.Factory>>? = null

    override fun disabledDecoders(
        context: PlatformContext
    ): List<KClass<out Decoder.Factory>>? = null

    override fun disabledInterceptors(
        context: PlatformContext
    ): List<KClass<out Interceptor>>? = null

    override fun toString(): String = "VideoComponentProvider"
}

@Suppress("DEPRECATION")
@OptIn(ExperimentalStdlibApi::class)
@EagerInitialization
@Deprecated("", level = DeprecationLevel.HIDDEN)
val videoComponentProviderInitHook: Any =
    ComponentLoader.register(VideoComponentProvider())
