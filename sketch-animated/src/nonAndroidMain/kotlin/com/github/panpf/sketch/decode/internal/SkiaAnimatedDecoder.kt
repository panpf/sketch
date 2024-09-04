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

@file:OptIn(InternalCoroutinesApi::class)

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.cacheDecodeTimeoutFrame
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.source.DataSource
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import okio.buffer
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data

/**
 * Animated image decoder based on Skia
 *
 * @see com.github.panpf.sketch.animated.nonandroid.test.decode.GifSkiaAnimatedDecoderTest
 * @see com.github.panpf.sketch.animated.nonandroid.test.decode.WebpSkiaAnimatedDecoderTest
 */
open class SkiaAnimatedDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
) : Decoder {

    private var _imageInfo: ImageInfo? = null
    private val imageInfoLock = SynchronizedObject()
    private val data by lazy {
        val bytes = dataSource.openSource().buffer().readByteArray()
        Data.makeFromBytes(bytes)
    }

    override val imageInfo: ImageInfo
        get() {
            synchronized(imageInfoLock) {
                val imageInfo = _imageInfo
                if (imageInfo != null) return imageInfo
                val codec = Codec.makeFromData(data)
                val mimeType = "image/${codec.encodedImageFormat.name.lowercase()}"
                return ImageInfo(
                    width = codec.width,
                    height = codec.height,
                    mimeType = mimeType,
                ).apply {
                    _imageInfo = this
                }
            }
        }

    override suspend fun decode(): Result<DecodeResult> = runCatching {
        val codec = Codec.makeFromData(data)
        val mimeType = "image/${codec.encodedImageFormat.name.lowercase()}"
        val imageInfo = ImageInfo(
            width = codec.width,
            height = codec.height,
            mimeType = mimeType,
        )
        // TODO Support resize
        val request = requestContext.request
        val repeatCount = request.repeatCount
        val cacheDecodeTimeoutFrame = request.cacheDecodeTimeoutFrame == true
        val resize = requestContext.computeResize(imageInfo.size)
        DecodeResult(
            image = SkiaAnimatedImage(
                codec = codec,
                repeatCount = repeatCount,
                cacheDecodeTimeoutFrame = cacheDecodeTimeoutFrame,
                animationStartCallback = request.animationStartCallback,
                animationEndCallback = request.animationEndCallback
            ),
            imageInfo = imageInfo,
            dataFrom = dataSource.dataFrom,
            resize = resize,
            transformeds = null,
            extras = null,
        )
    }
}