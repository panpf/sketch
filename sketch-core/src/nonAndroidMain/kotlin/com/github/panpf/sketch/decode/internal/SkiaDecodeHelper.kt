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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.SkiaImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Rect
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.impl.use

/**
 * Use Skia Image to decode statics images
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.SkiaDecodeHelperTest
 */
class SkiaDecodeHelper constructor(
    val request: ImageRequest,
    val dataSource: DataSource
) : DecodeHelper {

    override val imageInfo: ImageInfo by lazy { readImageInfo() }
    override val supportRegion: Boolean = true

    private val bytes by lazy {
        dataSource.openSource().buffer().use { it.readByteArray() }
    }
    private val skiaImage by lazy {
        // SkiaImage.makeFromEncoded(bytes) will parse exif orientation and does not support closing
        SkiaImage.makeFromEncoded(bytes)
    }

    override fun decode(sampleSize: Int): com.github.panpf.sketch.Image {
        val skiaBitmap = skiaImage.decode(sampleSize)
        return skiaBitmap.asSketchImage()
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): com.github.panpf.sketch.Image {
        val skiaBitmap = skiaImage.decodeRegion(region, sampleSize)
        return skiaBitmap.asSketchImage()
    }

    private fun readImageInfo(): ImageInfo {
        val encodedImageFormat = Codec.makeFromData(Data.makeFromBytes(bytes)).use {
            it.encodedImageFormat
        }
        val mimeType = "image/${encodedImageFormat.name.lowercase()}"
        return ImageInfo(
            width = skiaImage.width,
            height = skiaImage.height,
            mimeType = mimeType,
        )
    }

    override fun close() {
        skiaImage.close()
    }

    override fun toString(): String {
        return "SkiaDecodeHelper($dataSource)"
    }
}