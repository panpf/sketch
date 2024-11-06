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

import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Rect
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.impl.use

/**
 * Use Skia Image to decode statics images
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
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.SkiaDecodeHelperTest
 */
class SkiaDecodeHelper constructor(
    val request: ImageRequest,
    val dataSource: DataSource
) : DecodeHelper {

    private val bytes by lazy {
        dataSource.openSource().buffer().use { it.readByteArray() }
    }
    private val skiaImage by lazy {
        // Image.makeFromEncoded(bytes) will parse exif orientation and does not support closing
        Image.makeFromEncoded(bytes)
    }

    override val imageInfo: ImageInfo by lazy {
        Codec.makeFromData(Data.makeFromBytes(bytes)).use { codec ->
            readImageInfo(codec, skiaImage)
        }
    }
    override val supportRegion: Boolean by lazy {
        // The result returns null, which means unknown, but future versions may support it, so it is still worth trying.
        supportDecodeRegion(imageInfo.mimeType) ?: true
    }

    override fun decode(sampleSize: Int): com.github.panpf.sketch.SketchImage {
        val decodeConfig = DecodeConfig(request, imageInfo.mimeType, skiaImage.isOpaque).apply {
            this.sampleSize = sampleSize
        }
        val skiaBitmap = skiaImage.decode(decodeConfig)
        return skiaBitmap.asImage()
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): com.github.panpf.sketch.SketchImage {
        val decodeConfig = DecodeConfig(request, imageInfo.mimeType, skiaImage.isOpaque).apply {
            this.sampleSize = sampleSize
        }
        val skiaBitmap = skiaImage.decodeRegion(region, decodeConfig)
        return skiaBitmap.asImage()
    }

    override fun close() {
        skiaImage.close()
    }

    override fun toString(): String {
        return "SkiaDecodeHelper(request=$request, dataSource=$dataSource)"
    }
}