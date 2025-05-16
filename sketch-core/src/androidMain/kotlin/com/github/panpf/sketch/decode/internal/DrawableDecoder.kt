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

package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.ImageFormat.PNG
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateScaleMultiplierWithOneSide
import com.github.panpf.sketch.util.safeToSoftware
import com.github.panpf.sketch.util.toBitmap
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlin.math.roundToInt

/**
 * Extract the icon of the installed app and convert it to Bitmap
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver
 * * sizeMultiplier
 * * precisionDecider
 * * scaleDecider
 * * colorType
 * * colorSpace
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DrawableDecoderTest
 */
open class DrawableDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DrawableDataSource,
    private val mimeType: String?
) : Decoder {

    private var _imageInfo: ImageInfo? = null
    private val imageInfoLock = SynchronizedObject()

    override val imageInfo: ImageInfo
        get() {
            kotlinx.atomicfu.locks.synchronized(imageInfoLock) {
                return _imageInfo ?: dataSource.drawable.readImageInfo(mimeType)
                    .apply { _imageInfo = this }
            }
        }

    @WorkerThread
    override fun decode(): DecodeResult {
        val request = requestContext.request
        val drawable = dataSource.drawable

        val imageInfo = imageInfo
        val resize = requestContext.computeResize(imageInfo.size)
        var transformeds: List<String>? = null
        val scale: Float = calculateScaleMultiplierWithOneSide(
            sourceSize = imageInfo.size,
            targetSize = resize.size
        )
        if (scale != 1f) {
            transformeds = listOf(createScaledTransformed(scale))
        }
        val dstSize = Size(
            width = (imageInfo.width * scale).roundToInt(),
            height = (imageInfo.height * scale).roundToInt()
        )
        val bitmapSize = Size(width = dstSize.width, height = dstSize.height)
        val decodeConfig = DecodeConfig(request, PNG.mimeType, isOpaque = false)
        val bitmap = drawable.toBitmap(
            colorType = decodeConfig.colorType.safeToSoftware(),
            colorSpace = decodeConfig.colorSpace,
            targetSize = bitmapSize
        )
        val decodeResult = DecodeResult(
            image = bitmap.asImage(),
            imageInfo = imageInfo,
            dataFrom = LOCAL,
            resize = resize,
            transformeds = transformeds,
            extras = null
        )
        val resizeResult = decodeResult.resize(resize)
        return resizeResult
    }

    class Factory : Decoder.Factory {

        override val key: String = "DrawableDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            if (fetchResult.dataSource !is DrawableDataSource) return null
            return DrawableDecoder(requestContext, fetchResult.dataSource, fetchResult.mimeType)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "DrawableDecoder"
    }
}