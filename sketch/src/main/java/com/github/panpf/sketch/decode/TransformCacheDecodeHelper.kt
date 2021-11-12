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
import android.text.TextUtils
import android.text.format.Formatter
import com.github.panpf.sketch.SLog.Companion.emf
import com.github.panpf.sketch.cache.BitmapPoolUtils.Companion.setInBitmapFromPool
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.request.ErrorCause
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.uri.GetDataSourceException
import com.github.panpf.sketch.util.ExifInterface
import java.util.*

/**
 * 解码经过处理的缓存图片时只需原封不动读取，然后读取原图的类型、宽高信息即可
 */
class TransformCacheDecodeHelper : DecodeHelper() {
    override fun match(
        request: LoadRequest,
        dataSource: DataSource,
        imageType: ImageType?,
        boundOptions: BitmapFactory.Options
    ): Boolean {
        return dataSource is DiskCacheDataSource && dataSource.isFromProcessedCache
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
        decodeOptions.inSampleSize = 1

        // Set inBitmap from bitmap pool
        if (!request.options.isBitmapPoolDisabled) {
            val bitmapPool = request.configuration.bitmapPool
            setInBitmapFromPool(
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
                    emf(
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
                            throwable1,
                            request,
                            boundOptions.outWidth,
                            boundOptions.outHeight,
                            boundOptions.outMimeType
                        )
                    )
                    throw DecodeException("InBitmap retry", tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION)
                }
            } else {
                emf(
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
                        tr,
                        request,
                        boundOptions.outWidth,
                        boundOptions.outHeight,
                        boundOptions.outMimeType
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

        // 由于是读取的经过处理的缓存图片，因此要重新读取原图的类型、宽高信息
        val originFileDataSource: DataSource = try {
            request.getDataSource(true)
        } catch (e: GetDataSourceException) {
            ImageDecodeUtils.decodeError(request, null, NAME, "Unable create DataSource", e)
            throw DecodeException(e, ErrorCause.DECODE_UNABLE_CREATE_DATA_SOURCE)
        }
        val originImageOptions = BitmapFactory.Options()
        originImageOptions.inJustDecodeBounds = true
        try {
            ImageDecodeUtils.decodeBitmap(originFileDataSource, originImageOptions)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        val orientationCorrector = request.configuration.orientationCorrector
        val imageAttrs: ImageAttrs
        if (!TextUtils.isEmpty(originImageOptions.outMimeType)) {
            // Read image orientation
            var realExifOrientation = ExifInterface.ORIENTATION_UNDEFINED
            if (!request.options.isCorrectImageOrientationDisabled) {
                realExifOrientation = orientationCorrector.readExifOrientation(
                    originImageOptions.outMimeType,
                    originFileDataSource
                )
            }
            imageAttrs = ImageAttrs(
                originImageOptions.outMimeType,
                originImageOptions.outWidth,
                originImageOptions.outHeight,
                realExifOrientation
            )
        } else {
            imageAttrs = ImageAttrs(
                boundOptions.outMimeType,
                boundOptions.outWidth,
                boundOptions.outHeight,
                exifOrientation
            )
        }
        orientationCorrector.rotateSize(imageAttrs, imageAttrs.exifOrientation)
        ImageDecodeUtils.decodeSuccess(
            bitmap,
            boundOptions.outWidth,
            boundOptions.outHeight,
            decodeOptions.inSampleSize,
            request,
            NAME
        )
        val result = BitmapDecodeResult(imageAttrs, bitmap, dataSource.imageFrom)
        result.isBanProcess = true
        return result
    }

    companion object {
        private const val NAME = "ProcessedCacheDecodeHelper"
    }
}