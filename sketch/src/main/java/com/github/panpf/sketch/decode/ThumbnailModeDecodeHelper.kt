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
import android.graphics.BitmapRegionDecoder
import android.text.format.Formatter
import com.github.panpf.sketch.SLog.Companion.em
import com.github.panpf.sketch.SLog.Companion.emf
import com.github.panpf.sketch.cache.BitmapPoolUtils.Companion.sdkSupportInBitmapForRegionDecoder
import com.github.panpf.sketch.cache.BitmapPoolUtils.Companion.setInBitmapFromPoolForRegionDecoder
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.ErrorCause
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.SketchUtils.Companion.formatSupportBitmapRegionDecoder
import java.util.*

/**
 * 缩略图模式解码协助器，当开启缩略图模式并且满足使用缩略图模式的条件时会使用此协助器来解码
 *
 * 解码时会根据 resize 的尺寸并使用 [BitmapRegionDecoder] 读取原图中的部分区域来得到更清晰的缩略图
 */
class ThumbnailModeDecodeHelper : DecodeHelper() {

    /**
     * 要想使用缩略图功能需要配置开启缩略图功能、配置resize并且图片格式和系统版本支持BitmapRegionDecoder才行
     */
    override fun match(
        request: LoadRequest,
        dataSource: DataSource,
        imageType: ImageType?,
        boundOptions: BitmapFactory.Options
    ): Boolean {
        val loadOptions = request.options
        if (!loadOptions.isThumbnailMode || !formatSupportBitmapRegionDecoder(imageType)) {
            return false
        }
        val resize = loadOptions.resize
        if (resize == null) {
            em(NAME, "thumbnailMode need resize ")
            return false
        }

        // 只有原始图片的宽高比和resize的宽高比相差3倍的时候才能使用缩略图方式读取图片
        val sizeCalculator = request.configuration.sizeCalculator
        return sizeCalculator.canUseThumbnailMode(
            boundOptions.outWidth, boundOptions.outHeight,
            resize.width, resize.height
        )
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

        // 缩略图模式强制质量优先
        if (!decodeOptions.inPreferQualityOverSpeed) {
            decodeOptions.inPreferQualityOverSpeed = true
        }

        // 计算resize区域在原图中的对应区域
        val loadOptions = request.options
        val resize = loadOptions.resize
        val resizeCalculator = request.configuration.resizeCalculator
        val mapping = resizeCalculator.calculator(
            boundOptions.outWidth, boundOptions.outHeight,
            resize!!.width, resize.height, resize.scaleType, false
        )

        // 根据resize的大小和原图中对应区域的大小计算缩小倍数，这样会得到一个较为清晰的缩略图
        val sizeCalculator = request.configuration.sizeCalculator
        val smallerThumbnail = sizeCalculator.canUseSmallerThumbnails(request, imageType!!)
        decodeOptions.inSampleSize = sizeCalculator.calculateInSampleSize(
            mapping.srcRect.width(), mapping.srcRect.height(),
            resize.width, resize.height, smallerThumbnail
        )
        orientationCorrector.reverseRotate(
            mapping.srcRect,
            boundOptions.outWidth,
            boundOptions.outHeight,
            exifOrientation
        )
        if (sdkSupportInBitmapForRegionDecoder() && !loadOptions.isBitmapPoolDisabled) {
            val bitmapPool = request.configuration.bitmapPool
            setInBitmapFromPoolForRegionDecoder(decodeOptions, mapping.srcRect, bitmapPool)
        }
        val bitmap: Bitmap? = try {
            ImageDecodeUtils.decodeRegionBitmap(dataSource, mapping.srcRect, decodeOptions)
        } catch (tr: Throwable) {
            val application = request.configuration.context
            val callback = request.configuration.callback
            val bitmapPool = request.configuration.bitmapPool
            when {
                ImageDecodeUtils.isInBitmapDecodeError(tr, decodeOptions, true) -> {
                    ImageDecodeUtils.recycleInBitmapOnDecodeError(
                        callback,
                        bitmapPool,
                        request.uri,
                        boundOptions.outWidth,
                        boundOptions.outHeight,
                        boundOptions.outMimeType,
                        tr,
                        decodeOptions,
                        true
                    )
                    try {
                        ImageDecodeUtils.decodeRegionBitmap(dataSource, mapping.srcRect, decodeOptions)
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
                                throwable1, request, boundOptions.outWidth,
                                boundOptions.outHeight, boundOptions.outMimeType
                            )
                        )
                        throw DecodeException("InBitmap retry", tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION)
                    }
                }
                ImageDecodeUtils.isSrcRectDecodeError(
                    tr,
                    boundOptions.outWidth,
                    boundOptions.outHeight,
                    mapping.srcRect
                ) -> {
                    emf(
                        NAME,
                        "onDecodeRegionError. imageUri=%s, imageSize=%dx%d, imageMimeType= %s, srcRect=%s, inSampleSize=%d",
                        request.uri,
                        boundOptions.outWidth,
                        boundOptions.outHeight,
                        boundOptions.outMimeType,
                        mapping.srcRect.toString(),
                        decodeOptions.inSampleSize
                    )
                    callback.onError(
                        DecodeRegionException(
                            tr, request.uri, boundOptions.outWidth, boundOptions.outHeight,
                            boundOptions.outMimeType, mapping.srcRect, decodeOptions.inSampleSize
                        )
                    )
                    throw DecodeException("Because srcRect", tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION)
                }
                else -> {
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
                            tr, request, boundOptions.outWidth,
                            boundOptions.outHeight, boundOptions.outMimeType
                        )
                    )
                    throw DecodeException(tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION)
                }
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
        val imageAttrs = ImageAttrs(
            boundOptions.outMimeType,
            boundOptions.outWidth,
            boundOptions.outHeight,
            exifOrientation
        )
        val result = BitmapDecodeResult(imageAttrs, bitmap, dataSource.imageFrom)
        result.isProcessed = true
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
        private const val NAME = "ThumbnailModeDecodeHelper"
    }
}