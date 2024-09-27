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
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Rect

/**
 * Use BitmapFactory to decode statics images
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver
 * * sizeMultiplier
 * * precisionDecider
 * * scaleDecider
 * * colorType
 * * colorSpace
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
        dataSource.readImageInfo(exifOrientationHelper)
    }

    override fun decode(sampleSize: Int): Image {
        val decodeConfig = DecodeConfig(request, imageInfo.mimeType, isOpaque = false).apply {
            this.sampleSize = sampleSize
        }
        val bitmap = dataSource.decode(
            config = decodeConfig,
            exifOrientationHelper = exifOrientationHelper
        )
        return bitmap.asImage()
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): Image {
        val decodeConfig = DecodeConfig(request, imageInfo.mimeType, isOpaque = false).apply {
            this.sampleSize = sampleSize
        }
        val bitmap = dataSource.decodeRegion(
            srcRect = region,
            config = decodeConfig,
            imageSize = imageInfo.size,
            exifOrientationHelper = exifOrientationHelper
        )
        return bitmap.asImage()
    }

    override fun close() {

    }

    override fun toString(): String {
        return "BitmapFactoryDecodeHelper(request=$request, dataSource=$dataSource)"
    }
}