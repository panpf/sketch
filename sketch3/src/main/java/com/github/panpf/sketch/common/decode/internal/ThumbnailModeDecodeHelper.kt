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
package com.github.panpf.sketch.common.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory.Options
import android.graphics.BitmapRegionDecoder
import android.graphics.Point
import android.os.Build
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.DecodeException
import com.github.panpf.sketch.common.ImageType
import com.github.panpf.sketch.common.LoadableRequest
import com.github.panpf.sketch.common.cache.BitmapPoolHelper
import com.github.panpf.sketch.common.datasource.DataSource
import com.github.panpf.sketch.common.decode.DecodeResult
import com.github.panpf.sketch.common.decode.internal.ResizeCalculator.Mapping
import com.github.panpf.sketch.load.ImageInfo
import com.github.panpf.sketch.load.Resize
import com.github.panpf.sketch.util.supportBitmapRegionDecoder

/**
 * 缩略图模式解码协助器，当开启缩略图模式并且满足使用缩略图模式的条件时会使用此协助器来解码
 *
 * 解码时会根据 resize 的尺寸并使用 [BitmapRegionDecoder] 读取原图中的部分区域来得到更清晰的缩略图
 */
class ThumbnailModeDecodeHelper {

    companion object {
        private const val MODULE = "ThumbnailModeDecodeHelper"
    }

    private val resizeCalculator = ResizeCalculator()
    private val sizeCalculator = ImageSizeCalculator()

    fun canUseThumbnailMode(
        resize: Resize?, imageInfo: ImageInfo, imageType: ImageType?
    ): Boolean {
        if (resize != null && resize.thumbnailMode && imageType?.supportBitmapRegionDecoder() == true) {
            if (resize.width <= imageInfo.width || resize.height <= imageInfo.height) {
                val resizeScale = resize.width.toFloat() / resize.height
                val imageScale = imageInfo.width.toFloat() / imageInfo.height
                return resizeScale.coerceAtLeast(imageScale) > resizeScale.coerceAtMost(imageScale) * 1.5f
            }
        }
        return false
    }

    fun decode(
        sketch: Sketch,
        request: LoadableRequest,
        dataSource: DataSource,
        imageInfo: ImageInfo,
        decodeOptions: Options,
    ): DecodeResult {
        val resize = request.resize!!
        val maxSize = request.maxSize
        val exifOrientation = imageInfo.exifOrientation
        val bitmapPoolHelper = sketch.bitmapPoolHelper

        if (Build.VERSION.SDK_INT <= VERSION_CODES.M && !decodeOptions.inPreferQualityOverSpeed) {
            decodeOptions.inPreferQualityOverSpeed = true
        }

        val imageSize = Point(imageInfo.width, imageInfo.height)

        val imageOrientationCorrector =
            ImageOrientationCorrector.fromExifOrientation(exifOrientation)
        imageOrientationCorrector?.rotateSize(imageSize)

        val resizeMapping = resizeCalculator.calculator(
            imageSize.x,
            imageSize.y,
            resize.width,
            resize.height,
            resize.scaleType,
            false
        )
        val resizeMappingSrcWidth = resizeMapping.srcRect.width()
        val resizeMappingSrcHeight = resizeMapping.srcRect.height()

        val resizeInSampleSize =
            sizeCalculator.calculateInSampleSize(
                resizeMappingSrcWidth, resizeMappingSrcHeight, resize.width, resize.height
            )
        val maxSizeInSampleSize = maxSize?.let {
            sizeCalculator.calculateInSampleSize(
                resizeMappingSrcWidth, resizeMappingSrcHeight, it.width, it.height
            )
        } ?: 1
        decodeOptions.inSampleSize = resizeInSampleSize.coerceAtLeast(maxSizeInSampleSize)

        imageOrientationCorrector?.reverseRotateRect(
            resizeMapping.srcRect,
            imageSize.x,
            imageSize.y,
        )

        if (request.disabledBitmapPool != true) {
            sketch.bitmapPoolHelper.setInBitmapForRegionDecoder(
                decodeOptions,
                resizeMapping.srcRect
            )
        }

        val bitmap = decodeRegionBitmap(
            request, dataSource, resizeMapping, decodeOptions,
            bitmapPoolHelper, imageSize, imageInfo
        )

        val correctedOrientationBitmap =
            imageOrientationCorrector?.rotateBitmap(bitmap, bitmapPoolHelper.bitmapPool)
        return if (correctedOrientationBitmap != null && correctedOrientationBitmap != bitmap) {
            bitmapPoolHelper.freeBitmapToPool(bitmap)
            DecodeResult(correctedOrientationBitmap, imageInfo)
        } else {
            DecodeResult(bitmap, imageInfo)
        }
    }

    private fun decodeRegionBitmap(
        request: LoadableRequest,
        dataSource: DataSource,
        mapping: Mapping,
        decodeOptions: Options,
        bitmapPoolHelper: BitmapPoolHelper,
        imageSize: Point,
        imageInfo: ImageInfo,
    ): Bitmap {
        val bitmap = try {
            dataSource.decodeRegionBitmap(mapping.srcRect, decodeOptions)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when {
                isInBitmapError(throwable, decodeOptions, true) -> {
                    val message = "Bitmap region decode error. Because inBitmap. uri=%s"
                        .format(request.uri)
                    SLog.emt(MODULE, throwable, message)

                    val inBitmap = decodeOptions.inBitmap
                    decodeOptions.inBitmap = null
                    bitmapPoolHelper.freeBitmapToPool(inBitmap)
                    try {
                        dataSource.decodeRegionBitmap(mapping.srcRect, decodeOptions)
                    } catch (throwable2: Throwable) {
                        throwable.printStackTrace()
                        val message2 = "Bitmap region decode error. uri=%s".format(request.uri)
                        SLog.emt(MODULE, throwable2, message2)
                        throw DecodeException(message2, throwable2)
                    }
                }
                isSrcRectError(throwable, imageSize.x, imageSize.y, mapping.srcRect) -> {
                    val message =
                        "Bitmap region decode error. Because srcRect. imageInfo=%s, resize=%s, srcRect=%s, uri=%s"
                            .format(imageInfo, request.resize, mapping.srcRect, request.uri)
                    SLog.emt(MODULE, throwable, message)
                    throw DecodeException(message, throwable)
                }
                else -> {
                    val message = "Bitmap region decode error. uri=%s".format(request.uri)
                    SLog.emt(MODULE, throwable, message)
                    throw DecodeException(message, throwable)
                }
            }
        }
        if (bitmap == null) {
            val message = "Bitmap region decode return null. uri=%s".format(request.uri)
            SLog.em(MODULE, message)
            throw DecodeException(message)
        }

        if (bitmap.width <= 1 || bitmap.height <= 1) {
            bitmap.recycle()
            val message = "Invalid image size. size=%dx%d, uri=%s".format(
                imageInfo.width,
                imageInfo.height,
                request.uri
            )
            SLog.em(BitmapFactoryDecoder.MODULE, message)
            throw DecodeException(message)
        }

        return bitmap
    }
}