/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.format.Formatter
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.cache.BitmapPoolUtils
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.ErrorCause
import com.github.panpf.sketch.request.LoadRequest
import java.util.*

class NormalDecodeHelper : DecodeHelper() {

    override fun match(
        request: LoadRequest, dataSource: DataSource, imageType: ImageType?,
        boundOptions: BitmapFactory.Options
    ): Boolean {
        return true
    }

    @Throws(DecodeException::class)
    override fun decode(
        request: LoadRequest,
        dataSource: DataSource,
        imageType: ImageType?,
        boundOptions: BitmapFactory.Options,
        decodeOptions: BitmapFactory.Options,
        exifOrientation: Int
    ): DecodeResult {
        val orientationCorrector = request.configuration.orientationCorrector
        orientationCorrector.rotateSize(boundOptions, exifOrientation)

        // Calculate inSampleSize according to max size
        val maxSize = request.options.maxSize
        if (maxSize != null) {
            val sizeCalculator = request.configuration.sizeCalculator
            val smallerThumbnail = sizeCalculator.canUseSmallerThumbnails(request, imageType!!)
            decodeOptions.inSampleSize = sizeCalculator.calculateInSampleSize(
                boundOptions.outWidth, boundOptions.outHeight,
                maxSize.width, maxSize.height, smallerThumbnail
            )
        }

        // Set inBitmap from bitmap pool
        if (!request.options.isBitmapPoolDisabled) {
            val bitmapPool = request.configuration.bitmapPool
            BitmapPoolUtils.setInBitmapFromPool(
                decodeOptions,
                boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, bitmapPool
            )
        }
        val bitmap: Bitmap? = try {
            ImageDecodeUtils.decodeBitmap(dataSource, decodeOptions)
        } catch (tr: Throwable) {
            val application = request.configuration.context
            val callback = request.configuration.callback
            val bitmapPool = request.configuration.bitmapPool
            if (ImageDecodeUtils.isInBitmapDecodeError(tr, decodeOptions, false)) {
                ImageDecodeUtils.recycleInBitmapOnDecodeError(
                    callback,
                    bitmapPool,
                    request.uri,
                    boundOptions.outWidth,
                    boundOptions.outHeight,
                    boundOptions.outMimeType,
                    tr,
                    decodeOptions,
                    false
                )
                try {
                    ImageDecodeUtils.decodeBitmap(dataSource, decodeOptions)
                } catch (throwable1: Throwable) {
                    SLog.emf(
                        NAME, "onDecodeNormalImageError. " +
                                "outWidth=%d, outHeight=%d, outMimeType=%s. " +
                                "appMemoryInfo: maxMemory=%s, freeMemory=%s, totalMemory=%s. %s",
                        boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType,
                        Formatter.formatFileSize(application, Runtime.getRuntime().maxMemory()),
                        Formatter.formatFileSize(application, Runtime.getRuntime().freeMemory()),
                        Formatter.formatFileSize(application, Runtime.getRuntime().totalMemory()),
                        request.key
                    )
                    callback.onError(
                        DecodeImageException(
                            throwable1, request, boundOptions.outWidth,
                            boundOptions.outHeight, boundOptions.outMimeType
                        )
                    )
                    throw DecodeException("InBitmap retry", tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION)
                }
            } else {
                SLog.emf(
                    NAME, "onDecodeNormalImageError. " +
                            "outWidth=%d, outHeight=%d, outMimeType=%s. " +
                            "appMemoryInfo: maxMemory=%s, freeMemory=%s, totalMemory=%s. %s",
                    boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType,
                    Formatter.formatFileSize(application, Runtime.getRuntime().maxMemory()),
                    Formatter.formatFileSize(application, Runtime.getRuntime().freeMemory()),
                    Formatter.formatFileSize(application, Runtime.getRuntime().totalMemory()),
                    request.key
                )
                callback.onError(
                    DecodeImageException(
                        tr, request, boundOptions.outWidth,
                        boundOptions.outHeight, boundOptions.outMimeType
                    )
                )
                throw DecodeException(tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION)
            }
        }

        // 过滤掉无效的图片
        if (bitmap == null || bitmap.isRecycled) {
            ImageDecodeUtils.decodeError(request, dataSource, NAME, "Bitmap invalid", null)
            throw DecodeException("Bitmap invalid", ErrorCause.DECODE_RESULT_BITMAP_INVALID)
        }

        // 过滤宽高小于等于1的图片
        if (bitmap.width <= 1 || bitmap.height <= 1) {
            val cause = String.format(
                Locale.US,
                "Bitmap width or height less than or equal to 1px. imageSize: %dx%d. bitmapSize: %dx%d",
                boundOptions.outWidth,
                boundOptions.outHeight,
                bitmap.width,
                bitmap.height
            )
            ImageDecodeUtils.decodeError(request, dataSource, NAME, cause, null)
            bitmap.recycle()
            throw DecodeException(cause, ErrorCause.DECODE_RESULT_BITMAP_SIZE_INVALID)
        }
        val transformCacheManager = request.configuration.transformCacheManager
        val processed = transformCacheManager.canUseByInSampleSize(decodeOptions.inSampleSize)
        val imageAttrs = ImageAttrs(
            boundOptions.outMimeType,
            boundOptions.outWidth,
            boundOptions.outHeight,
            exifOrientation
        )
        val result = BitmapDecodeResult(imageAttrs, bitmap, dataSource.imageFrom)
        result.isProcessed = processed
        try {
            correctOrientation(orientationCorrector, result, exifOrientation, request)
        } catch (e: CorrectOrientationException) {
            throw DecodeException(e, ErrorCause.DECODE_CORRECT_ORIENTATION_FAIL)
        }
        ImageDecodeUtils.decodeSuccess(
            bitmap,
            boundOptions.outWidth,
            boundOptions.outHeight,
            decodeOptions.inSampleSize,
            request,
            NAME
        )
        return result
    }

    companion object {
        private const val NAME = "NormalDecodeHelper"
    }
}