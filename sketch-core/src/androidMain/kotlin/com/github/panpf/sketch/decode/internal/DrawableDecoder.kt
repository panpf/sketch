/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import android.graphics.drawable.BitmapDrawable
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.DrawableDataSource
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.internal.ImageFormat.PNG
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.internal.DisplaySizeResolver
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toNewBitmap
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Extract the icon of the installed app and convert it to Bitmap
 */
open class DrawableDecoder constructor(
    private val requestContext: RequestContext,
    private val drawableDataSource: DrawableDataSource,
    private val mimeType: String?
) : Decoder {

    companion object {
        const val MODULE = "DrawableDecoder"
    }

    @WorkerThread
    override suspend fun decode(): Result<DecodeResult> = kotlin.runCatching {
        val request = requestContext.request
        val drawable = drawableDataSource.drawable

        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight
        if (imageWidth <= 0 || imageHeight <= 0) {
            throw ImageInvalidException(
                "Invalid drawable resource, intrinsicWidth or intrinsicHeight is less than or equal to 0"
            )
        }
        val size = requestContext.size!!
        var transformedList: List<String>? = null
        val dstSize =
            if (drawable is BitmapDrawable || request.sizeResolver is DisplaySizeResolver) {
                val imageSize = Size(imageWidth, imageHeight)
                val precision = request.precisionDecider.get(
                    imageSize = imageSize,
                    targetSize = size,
                )
                val inSampleSize = calculateSampleSize(
                    imageSize = imageSize,
                    targetSize = size,
                    smallerSizeMode = precision.isSmallerSizeMode(),
                    mimeType = null
                )
                if (inSampleSize > 1) {
                    transformedList = listOf(createInSampledTransformed(inSampleSize))
                }
                calculateSampledBitmapSize(imageSize, inSampleSize, mimeType)
            } else {
                val scale: Float = min(
                    size.width / imageWidth.toFloat(),
                    size.height / imageHeight.toFloat()
                )
                if (scale != 1f) {
                    transformedList = listOf(createScaledTransformed(scale))
                }
                Size(
                    width = (imageWidth * scale).roundToInt(),
                    height = (imageHeight * scale).roundToInt()
                )
            }
        val targetSize = Size(width = dstSize.width, height = dstSize.height)
        val bitmap = drawable.toNewBitmap(
            preferredConfig = request.bitmapConfig?.getConfig(PNG.mimeType),
            targetSize = targetSize
        )
        val imageInfo = ImageInfo(
            width = imageWidth,
            height = imageHeight,
            mimeType = mimeType ?: "image/png",
            exifOrientation = ExifOrientation.UNDEFINED
        )
        requestContext.logger.d(MODULE) {
            "decode. successful. ${bitmap.toLogString()}. ${imageInfo}. '${requestContext.logKey}'"
        }
        DecodeResult(
            image = bitmap.asSketchImage(resources = requestContext.request.context.resources),
            imageInfo = imageInfo,
            dataFrom = LOCAL,
            transformedList = transformedList,
            extras = null
        ).appliedResize(requestContext)
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

        override fun toString(): String = "DrawableDecoder"

        @Suppress("RedundantOverride")
        override fun equals(other: Any?): Boolean {
            // If you add construction parameters to this class, you need to change it here
            return super.equals(other)
        }

        @Suppress("RedundantOverride")
        override fun hashCode(): Int {
            // If you add construction parameters to this class, you need to change it here
            return super.hashCode()
        }
    }
}