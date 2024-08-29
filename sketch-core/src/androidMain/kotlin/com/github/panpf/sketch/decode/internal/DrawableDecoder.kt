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
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.ImageFormat.PNG
import com.github.panpf.sketch.drawable.toNewBitmap
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.computeScaleMultiplierWithOneSide
import kotlin.math.roundToInt

/**
 * Extract the icon of the installed app and convert it to Bitmap
 */
open class DrawableDecoder constructor(
    private val requestContext: RequestContext,
    private val drawableDataSource: DrawableDataSource,
    private val mimeType: String?
) : Decoder {

    @WorkerThread
    override suspend fun decode(): Result<DecodeResult> = kotlin.runCatching {
        val request = requestContext.request
        val drawable = drawableDataSource.drawable

        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight
        if (imageWidth <= 0 || imageHeight <= 0) {
            throw ImageInvalidException("Invalid drawable intrinsicSize, intrinsicSize=${imageWidth}x${imageHeight}")
        }
        val imageSize = Size(imageWidth, imageHeight)
        val targetSize = requestContext.size!!
        var transformeds: List<String>? = null
        val scale: Float = computeScaleMultiplierWithOneSide(
            sourceSize = imageSize,
            targetSize = targetSize
        )
        if (scale != 1f) {
            transformeds = listOf(createScaledTransformed(scale))
        }
        val dstSize = Size(
            width = (imageWidth * scale).roundToInt(),
            height = (imageHeight * scale).roundToInt()
        )
        val bitmapSize = Size(width = dstSize.width, height = dstSize.height)
        val bitmap = drawable.toNewBitmap(
            preferredConfig = request.bitmapConfig?.getConfig(PNG.mimeType),
            targetSize = bitmapSize
        )
        val imageInfo = ImageInfo(
            width = imageWidth,
            height = imageHeight,
            mimeType = mimeType ?: "image/png",
        )
        val resize = requestContext.computeResize(imageInfo.size)
        val decodeResult = DecodeResult(
            image = bitmap.asSketchImage(resources = requestContext.request.context.resources),
            imageInfo = imageInfo,
            dataFrom = LOCAL,
            resize = resize,
            transformeds = transformeds,
            extras = null
        )
        val resizedResult = decodeResult.appliedResize(requestContext)
        resizedResult
    }

    class Factory : Decoder.Factory {

        override val key: String = "DrawableDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            val dataSource = fetchResult.dataSource
            return if (dataSource is DrawableDataSource) {
                DrawableDecoder(requestContext, dataSource, fetchResult.mimeType)
            } else {
                null
            }
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