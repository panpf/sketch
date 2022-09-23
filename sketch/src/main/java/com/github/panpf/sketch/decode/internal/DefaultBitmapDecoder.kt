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
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeException
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.Size

/**
 * Decode image files using BitmapFactory
 */
open class DefaultBitmapDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val dataSource: DataSource,
) : BitmapDecoder {

    companion object {
        const val MODULE = "DefaultBitmapDecoder"
    }

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        // Currently running on a limited number of IO contexts, so this warning can be ignored
        @Suppress("BlockingMethodInNonBlockingContext")
        val imageInfo =
            dataSource.readImageInfoWithBitmapFactoryOrThrow(request.ignoreExifOrientation)
        val canDecodeRegion = ImageFormat.parseMimeType(imageInfo.mimeType)
            ?.supportBitmapRegionDecoder() == true
        return realDecode(
            request = request,
            dataFrom = dataSource.dataFrom,
            imageInfo = imageInfo,
            decodeFull = { decodeConfig ->
                realDecodeFull(imageInfo, decodeConfig)
            },
            decodeRegion = if (canDecodeRegion) { srcRect, decodeConfig ->
                realDecodeRegion(imageInfo, srcRect, decodeConfig)
            } else null
        ).appliedExifOrientation(sketch, request)
            .appliedResize(sketch, request, request.resize)
    }

    private fun realDecodeFull(imageInfo: ImageInfo, decodeConfig: DecodeConfig): Bitmap {
        val decodeOptions = decodeConfig.toBitmapOptions()

        // Set inBitmap from bitmap pool
        sketch.bitmapPool.setInBitmap(
            options = decodeOptions,
            imageSize = Size(imageInfo.width, imageInfo.height),
            imageMimeType = imageInfo.mimeType,
            disallowReuseBitmap = request.disallowReuseBitmap,
            caller = "DefaultBitmapDecoder:realDecodeFull"
        )
        sketch.logger.d(MODULE) {
            "realDecodeFull. inBitmap=${decodeOptions.inBitmap?.logString}. ${request.key}"
        }

        val bitmap: Bitmap = try {
            dataSource.decodeBitmap(decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            if (inBitmap != null && isInBitmapError(throwable)) {
                val message = "Bitmap decode error. Because inBitmap. ${request.key}"
                sketch.logger.e(MODULE, throwable, message)

                sketch.bitmapPool.freeBitmap(
                    bitmap = inBitmap,
                    disallowReuseBitmap = request.disallowReuseBitmap,
                    caller = "decode:error"
                )
                sketch.logger.d(MODULE) {
                    "realDecodeFull. freeBitmap. inBitmap error. bitmap=${inBitmap.logString}. ${request.key}"
                }

                decodeOptions.inBitmap = null
                try {
                    dataSource.decodeBitmap(decodeOptions)
                } catch (throwable2: Throwable) {
                    throw BitmapDecodeException("Bitmap decode error2: $throwable", throwable2)
                }
            } else {
                throw BitmapDecodeException("Bitmap decode error: $throwable", throwable)
            }
        } ?: throw ImageInvalidException("Invalid image. decode return null")
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            sketch.logger.e(MODULE) {
                "realDecodeFull. Invalid image. ${bitmap.logString}. ${imageInfo}. ${request.key}"
            }
            bitmap.recycle()
            throw ImageInvalidException("Invalid image. size=${bitmap.width}x${bitmap.height}")
        } else {
            sketch.logger.d(MODULE) {
                "realDecodeFull. successful. ${bitmap.logString}. ${imageInfo}. ${request.key}"
            }
        }
        return bitmap
    }

    private fun realDecodeRegion(
        imageInfo: ImageInfo, srcRect: Rect, decodeConfig: DecodeConfig
    ): Bitmap {
        val decodeOptions = decodeConfig.toBitmapOptions()
        sketch.bitmapPool.setInBitmapForRegion(
            options = decodeOptions,
            regionSize = Size(srcRect.width(), srcRect.height()),
            imageMimeType = imageInfo.mimeType,
            imageSize = Size(imageInfo.width, imageInfo.height),
            disallowReuseBitmap = request.disallowReuseBitmap,
            caller = "DefaultBitmapDecoder:realDecodeRegion"
        )
        sketch.logger.d(MODULE) {
            "realDecodeRegion. inBitmap=${decodeOptions.inBitmap?.logString}. ${request.key}"
        }

        val bitmap = try {
            dataSource.decodeRegionBitmap(srcRect, decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            when {
                inBitmap != null && isInBitmapError(throwable) -> {
                    val message = "Bitmap decode region error. Because inBitmap. ${request.key}"
                    sketch.logger.e(MODULE, throwable, message)

                    sketch.bitmapPool.freeBitmap(
                        bitmap = inBitmap,
                        disallowReuseBitmap = request.disallowReuseBitmap,
                        caller = "decodeRegion:error"
                    )
                    sketch.logger.d(MODULE) {
                        "realDecodeRegion. freeBitmap. inBitmap error. bitmap=${inBitmap.logString}. ${request.key}"
                    }

                    decodeOptions.inBitmap = null
                    try {
                        dataSource.decodeRegionBitmap(srcRect, decodeOptions)
                    } catch (throwable2: Throwable) {
                        val message2 = "Bitmap region decode error"
                        throw BitmapDecodeException(message2, throwable2)
                    }
                }
                isSrcRectError(throwable) -> {
                    val message =
                        "Bitmap region decode error. Because srcRect. imageInfo=${imageInfo}, resize=${request.resize}, srcRect=${srcRect}"
                    throw BitmapDecodeException(message, throwable)
                }
                else -> {
                    throw BitmapDecodeException("Bitmap region decode error", throwable)
                }
            }
        } ?: throw ImageInvalidException("Invalid image. region decode return null")
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            sketch.logger.e(MODULE) {
                "realDecodeRegion. Invalid image. ${bitmap.logString}. ${imageInfo}. ${srcRect}. ${request.key}"
            }
            bitmap.recycle()
            throw ImageInvalidException("Invalid image. size=${bitmap.width}x${bitmap.height}")
        } else {
            sketch.logger.d(MODULE) {
                "realDecodeRegion. successful. ${bitmap.logString}. ${imageInfo}. ${srcRect}. ${request.key}"
            }
        }
        return bitmap
    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): BitmapDecoder = DefaultBitmapDecoder(sketch, request, fetchResult.dataSource)

        override fun toString(): String = "DefaultBitmapDecoder"

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