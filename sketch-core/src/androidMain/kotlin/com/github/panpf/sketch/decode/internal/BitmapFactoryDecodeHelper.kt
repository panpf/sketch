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

@file:Suppress("UnnecessaryVariable")

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.toAndroidRect

/**
 * Use BitmapFactory to decode statics images
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.BitmapFactoryDecodeHelperTest
 */
class BitmapFactoryDecodeHelper(
    val request: ImageRequest,
    val dataSource: DataSource
) : DecodeHelper {

    private val exifOrientation: Int by lazy { dataSource.readExifOrientation() }
    private val exifOrientationHelper by lazy { ExifOrientationHelper(exifOrientation) }
    override val supportRegion: Boolean by lazy {
        ImageFormat.parseMimeType(imageInfo.mimeType)?.supportBitmapRegionDecoder() == true
    }
    override val imageInfo: ImageInfo by lazy {
        dataSource.readImageInfoWithExifOrientation(exifOrientationHelper)
    }

    override fun decode(sampleSize: Int): Image {
        val config = request.newDecodeConfigByQualityParams(imageInfo.mimeType).apply {
            inSampleSize = sampleSize
        }
        val options = config.toBitmapOptions()
        val bitmap = dataSource.decodeBitmap(options)
            ?: throw ImageInvalidException("Invalid image. decode return null")
        val image = bitmap.asSketchImage()
        val correctedImage = exifOrientationHelper.applyToImage(image) ?: image
        return correctedImage
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): Image {
        val config = request.newDecodeConfigByQualityParams(imageInfo.mimeType).apply {
            inSampleSize = sampleSize
        }
        val options = config.toBitmapOptions()
        val originalRegion =
            exifOrientationHelper.applyToRect(region, imageInfo.size, reverse = true)
        val bitmap = dataSource.decodeRegionBitmap(originalRegion.toAndroidRect(), options)
            ?: throw ImageInvalidException("Invalid image. region decode return null")
        val image = bitmap.asSketchImage()
        val correctedImage = exifOrientationHelper.applyToImage(image) ?: image
        return correctedImage
    }

    override fun close() {

    }

    override fun toString(): String {
        return "BitmapFactoryDecodeHelper($dataSource)"
    }
}