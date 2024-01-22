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

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.toAndroidRect

/**
 * Decode image files using BitmapFactory
 */
open class BitmapFactoryDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
) : Decoder {

    companion object {
        const val MODULE = "BitmapFactoryDecoder"
    }

    @WorkerThread
    override suspend fun decode(): Result<DecodeResult> = kotlin.runCatching {
        val request = requestContext.request
        val imageInfo =
            dataSource.readImageInfoWithBitmapFactoryOrThrow(request.ignoreExifOrientation)
        val canDecodeRegion = ImageFormat.parseMimeType(imageInfo.mimeType)
            ?.supportBitmapRegionDecoder() == true
        realDecode(
            requestContext = requestContext,
            dataFrom = dataSource.dataFrom,
            imageInfo = imageInfo,
            decodeFull = { sampleSize ->
                realDecodeFull(imageInfo, sampleSize).asSketchImage()
            },
            decodeRegion = if (canDecodeRegion) { srcRect, sampleSize ->
                realDecodeRegion(imageInfo, srcRect.toAndroidRect(), sampleSize).asSketchImage()
            } else null
        ).appliedExifOrientation(requestContext)
            .appliedResize(requestContext)
    }

    private fun realDecodeFull(imageInfo: ImageInfo, sampleSize: Int): Bitmap {
        val request = requestContext.request
        val decodeConfig = request.newDecodeConfigByQualityParams(imageInfo.mimeType)
        decodeConfig.inSampleSize = sampleSize
        val decodeOptions = decodeConfig.toBitmapOptions()

        // Set inBitmap from bitmap pool
//        sketch.bitmapPool.setInBitmap(
//            options = decodeOptions,
//            imageSize = Size(imageInfo.width, imageInfo.height),
//            imageMimeType = imageInfo.mimeType,
//            disallowReuseBitmap = request.disallowReuseBitmap,
//            caller = "DefaultBitmapDecoder:realDecodeFull"
//        )
//        requestContext.logger.d(MODULE) {
//            "realDecodeFull. inBitmap=${decodeOptions.inBitmap?.logString}. '${requestContext.logKey}'"
//        }

        val bitmap: Bitmap = try {
            dataSource.decodeBitmap(decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            if (inBitmap != null && isInBitmapError(throwable)) {
                val message = "Bitmap decode error. Because inBitmap. '${requestContext.logKey}'"
                requestContext.logger.e(MODULE, throwable, message)

//                sketch.bitmapPool.freeBitmap(
//                    bitmap = inBitmap,
//                    disallowReuseBitmap = request.disallowReuseBitmap,
//                    caller = "decode:error"
//                )
//                requestContext.logger.d(MODULE) {
//                    "realDecodeFull. freeBitmap. inBitmap error. bitmap=${inBitmap.logString}. '${requestContext.logKey}'"
//                }

                decodeOptions.inBitmap = null
                try {
                    dataSource.decodeBitmap(decodeOptions)
                } catch (throwable2: Throwable) {
                    throw DecodeException("Bitmap decode error2: $throwable", throwable2)
                }
            } else {
                throw DecodeException("Bitmap decode error: $throwable", throwable)
            }
        } ?: throw ImageInvalidException("Invalid image. decode return null")
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            requestContext.logger.e(MODULE) {
                "realDecodeFull. Invalid image. ${bitmap.logString}. ${imageInfo}. '${requestContext.logKey}'"
            }
            bitmap.recycle()
            throw ImageInvalidException("Invalid image. size=${bitmap.width}x${bitmap.height}")
        } else {
            requestContext.logger.d(MODULE) {
                "realDecodeFull. successful. ${bitmap.logString}. ${imageInfo}. '${requestContext.logKey}'"
            }
        }
        return bitmap
    }

    private fun realDecodeRegion(imageInfo: ImageInfo, srcRect: Rect, sampleSize: Int): Bitmap {
        val request = requestContext.request
        val decodeConfig = request.newDecodeConfigByQualityParams(imageInfo.mimeType)
        decodeConfig.inSampleSize = sampleSize
        val decodeOptions = decodeConfig.toBitmapOptions()
//        sketch.bitmapPool.setInBitmapForRegion(
//            options = decodeOptions,
//            regionSize = Size(srcRect.width(), srcRect.height()),
//            imageMimeType = imageInfo.mimeType,
//            imageSize = Size(imageInfo.width, imageInfo.height),
//            disallowReuseBitmap = request.disallowReuseBitmap,
//            caller = "DefaultBitmapDecoder:realDecodeRegion"
//        )
//        requestContext.logger.d(MODULE) {
//            "realDecodeRegion. inBitmap=${decodeOptions.inBitmap?.logString}. '${requestContext.logKey}'"
//        }

        val bitmap: Bitmap = try {
            dataSource.decodeRegionBitmap(srcRect, decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            when {
                inBitmap != null && isInBitmapError(throwable) -> {
                    val message =
                        "Bitmap decode region error. Because inBitmap. '${requestContext.logKey}'"
                    requestContext.logger.e(MODULE, throwable, message)

//                    sketch.bitmapPool.freeBitmap(
//                        bitmap = inBitmap,
//                        disallowReuseBitmap = request.disallowReuseBitmap,
//                        caller = "decodeRegion:error"
//                    )
//                    requestContext.logger.d(MODULE) {
//                        "realDecodeRegion. freeBitmap. inBitmap error. bitmap=${inBitmap.logString}. '${requestContext.logKey}'"
//                    }

                    decodeOptions.inBitmap = null
                    try {
                        dataSource.decodeRegionBitmap(srcRect, decodeOptions)
                    } catch (throwable2: Throwable) {
                        val message2 = "Bitmap region decode error"
                        throw DecodeException(message2, throwable2)
                    }
                }

                isSrcRectError(throwable) -> {
                    val message =
                        "Bitmap region decode error. Because srcRect. imageInfo=${imageInfo}, srcRect=${srcRect}"
                    throw DecodeException(message, throwable)
                }

                else -> {
                    throw DecodeException("Bitmap region decode error", throwable)
                }
            }
        } ?: throw ImageInvalidException("Invalid image. region decode return null")
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            requestContext.logger.e(MODULE) {
                "realDecodeRegion. Invalid image. ${bitmap.logString}. ${imageInfo}. ${srcRect}. '${requestContext.logKey}'"
            }
            bitmap.recycle()
            throw ImageInvalidException("Invalid image. size=${bitmap.width}x${bitmap.height}")
        } else {
            requestContext.logger.d(MODULE) {
                "realDecodeRegion. successful. ${bitmap.logString}. ${imageInfo}. ${srcRect}. '${requestContext.logKey}'"
            }
        }
        return bitmap
    }

    class Factory : Decoder.Factory {

        override val key: String = "BitmapFactoryDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder {
            val dataSource = fetchResult.dataSource
            return BitmapFactoryDecoder(requestContext, dataSource)
        }

        override fun toString(): String = "BitmapFactoryDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}