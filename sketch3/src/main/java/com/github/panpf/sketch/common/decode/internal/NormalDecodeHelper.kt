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
import android.graphics.Point
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.DecodeException
import com.github.panpf.sketch.common.LoadableRequest
import com.github.panpf.sketch.common.cache.BitmapPoolHelper
import com.github.panpf.sketch.common.datasource.DataSource
import com.github.panpf.sketch.common.decode.DecodeResult
import com.github.panpf.sketch.load.ImageInfo

class NormalDecodeHelper {

    companion object {
        private const val MODULE = "NormalDecodeHelper"
    }

    private val sizeCalculator = ImageSizeCalculator()

    fun decode(
        sketch: Sketch,
        request: LoadableRequest,
        dataSource: DataSource,
        imageInfo: ImageInfo,
        decodeOptions: Options,
        imageOrientationCorrector: ImageOrientationCorrector?
    ): Bitmap {
        val resize = request.resize
        val maxSize = request.maxSize
        val bitmapPoolHelper = sketch.bitmapPoolHelper
        val imageSize = Point(imageInfo.width, imageInfo.height)
        imageOrientationCorrector?.rotateSize(imageSize)

        val maxSizeInSampleSize = maxSize?.let {
            sizeCalculator.calculateInSampleSize(
                imageSize.x, imageSize.y, it.width, it.height
            )
        } ?: 1
        val resizeInSampleSize = resize?.let {
            sizeCalculator.calculateInSampleSize(
                imageSize.x, imageSize.y, it.width, it.height
            )
        } ?: 1
        decodeOptions.inSampleSize = maxSizeInSampleSize.coerceAtLeast(resizeInSampleSize)

        // Set inBitmap from bitmap pool
        if (request.disabledBitmapPool != true) {
            bitmapPoolHelper
                .setInBitmap(decodeOptions, imageSize.x, imageSize.y, imageInfo.mimeType)
        }

        return decodeBitmap(request, dataSource, decodeOptions, bitmapPoolHelper)
    }

    private fun decodeBitmap(
        request: LoadableRequest,
        dataSource: DataSource,
        decodeOptions: Options,
        bitmapPoolHelper: BitmapPoolHelper,
    ): Bitmap {
        val bitmap: Bitmap? = try {
            dataSource.decodeBitmap(decodeOptions)
        } catch (throwable: Throwable) {
            val inBitmap = decodeOptions.inBitmap
            if (inBitmap != null && isInBitmapError(throwable, false)) {
                val message = "Bitmap decode error. Because inBitmap. uri=%s"
                    .format(request.uri)
                SLog.emt(MODULE, throwable, message)

                decodeOptions.inBitmap = null
                bitmapPoolHelper.freeBitmapToPool(inBitmap)
                try {
                    dataSource.decodeBitmap(decodeOptions)
                } catch (throwable2: Throwable) {
                    val message2 = "Bitmap decode error. uri=%s".format(request.uri)
                    SLog.emt(MODULE, throwable2, message2)
                    throw DecodeException(message2, throwable2)
                }
            } else {
                val message = "Bitmap decode error. uri=%s".format(request.uri)
                SLog.emt(MODULE, throwable, message)
                throw DecodeException(message, throwable)
            }
        }
        if (bitmap == null) {
            val message = "Bitmap decode return null. uri=%s".format(request.uri)
            SLog.em(MODULE, message)
            throw DecodeException(message)
        }

        if (bitmap.width <= 1 || bitmap.height <= 1) {
            bitmap.recycle()
            val message = "Invalid image size. size=%dx%d, uri=%s"
                .format(bitmap.width, bitmap.height, request.uri)
            SLog.em(MODULE, message)
            throw DecodeException(message)
        }

        return bitmap
    }
}