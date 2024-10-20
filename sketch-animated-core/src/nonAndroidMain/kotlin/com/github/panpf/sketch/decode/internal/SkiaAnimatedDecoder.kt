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

import com.github.panpf.sketch.AnimatedImage
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.cacheDecodeTimeoutFrame
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.util.Rect
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.InternalCoroutinesApi
import okio.buffer
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.impl.use

/**
 * Animated image decoder based on Skia
 *
 * The following decoding related properties are supported:
 *
 * * colorType
 * * colorSpace
 * * disallowAnimatedImage
 * * repeatCount
 * * onAnimationStart
 * * onAnimationEnd
 * * cacheDecodeTimeoutFrame
 *
 * The following decoding related properties are not supported:
 *
 * * sizeResolver
 * * sizeMultiplier
 * * precisionDecider
 * * scaleDecider
 * * animatedTransformation
 *
 * @see com.github.panpf.sketch.animated.gif.nonandroid.test.decode.GifSkiaAnimatedDecoderTest
 * @see com.github.panpf.sketch.animated.webp.nonandroid.test.decode.WebpSkiaAnimatedDecoderTest
 */
// TODO rename to AnimatedDecoder
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
                return _imageInfo
                    ?: Codec.makeFromData(data).use { readImageInfoWithIgnoreExifOrientation(it) }
                        .apply { _imageInfo = this }
            }
        }

    override fun decode(): DecodeResult {
        val codec = Codec.makeFromData(data)
        val imageInfo = synchronized(imageInfoLock) {
            _imageInfo ?: readImageInfoWithIgnoreExifOrientation(codec)
                .apply { _imageInfo = this }
        }
        val request = requestContext.request
        val repeatCount = request.repeatCount
        val cacheDecodeTimeoutFrame = request.cacheDecodeTimeoutFrame == true
        val decodeConfig = DecodeConfig(request, imageInfo.mimeType, codec.isOpaque)
        val newColorType = decodeConfig.colorType ?: codec.imageInfo.colorType
        val newColorSpace = decodeConfig.colorSpace ?: codec.imageInfo.colorSpace
        val skiaImageInfo = org.jetbrains.skia.ImageInfo(
            width = codec.imageInfo.width,
            height = codec.imageInfo.height,
            colorType = newColorType,
            alphaType = codec.imageInfo.colorAlphaType,
            colorSpace = newColorSpace
        )
        val animatedImage = AnimatedImage(
            codec = codec,
            imageInfo = skiaImageInfo,
            repeatCount = repeatCount,
            cacheDecodeTimeoutFrame = cacheDecodeTimeoutFrame,
        ).apply {
            animatedTransformation = request.animatedTransformation?.asCompat()
            animationStartCallback = request.animationStartCallback
            animationEndCallback = request.animationEndCallback
        }
        val resize = requestContext.computeResize(imageInfo.size)
        return DecodeResult(
            image = animatedImage,
            imageInfo = imageInfo,
            dataFrom = dataSource.dataFrom,
            resize = resize,
            transformeds = null,
            extras = null,
        )
    }

    private fun AnimatedTransformation.asCompat(): (Any, Rect) -> Unit {
        return { canvas, bounds ->
            this@asCompat.transform(canvas, bounds)
        }
    }
}