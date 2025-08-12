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

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.parseQueryParameters
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.util.BlurHashUtil
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.createBlurHashBitmap
import com.github.panpf.sketch.util.installPixels

class BlurHashDecodeHelper(
    val request: ImageRequest,
    val dataSource: BlurHashDataSource,
    private val fallbackSize: Size = Size(100, 100)
) : DecodeHelper {

    override val imageInfo: ImageInfo by lazy {
        val uriString = request.uri.toString()
        val size = if (uriString.contains('&')) {
            val queryStart = uriString.indexOf('&')
            val queryString = uriString.substring(queryStart + 1)
            parseQueryParameters(queryString) ?: fallbackSize
        } else {
            fallbackSize
        }
        ImageInfo(size, "")
    }
    override val supportRegion: Boolean = false

    override fun decode(sampleSize: Int): Image {
        val pixelData = try {
            BlurHashUtil.decodeByte(dataSource.blurHash, imageInfo.width, imageInfo.height)
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalArgumentException()
        }

        val bitmap = createBlurHashBitmap(imageInfo.width, imageInfo.height)
        bitmap.installPixels(pixelData)
        return bitmap.asImage()
    }

    override fun decodeRegion(
        region: Rect,
        sampleSize: Int
    ): Image {
        throw UnsupportedOperationException("Decoding not implemented yet.")
    }

    override fun close() {

    }
}