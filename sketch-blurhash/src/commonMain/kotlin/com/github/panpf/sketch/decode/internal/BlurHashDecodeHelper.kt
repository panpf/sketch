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
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.readSizeFromBlurHashUri
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.util.BlurHashUtil
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.Uri
import com.github.panpf.sketch.util.createBlurHashBitmap
import com.github.panpf.sketch.util.defaultBlurHashBitmapSize
import com.github.panpf.sketch.util.installPixels
import com.github.panpf.sketch.util.resolveBlurHashBitmapSize

/**
 * A [DecodeHelper] that decodes images from a [BlurHashDataSource].
 *
 * @see com.github.panpf.sketch.blurhash.android.test.decode.internal.BlurHashDecodeHelperAndroidTest
 * @see com.github.panpf.sketch.blurhash.nonandroid.test.decode.internal.BlurHashDecodeHelperNonAndroidTest
 */
class BlurHashDecodeHelper(
    val requestContext: RequestContext,
    val blurHashUri: Uri,
) : DecodeHelper {

    override val imageInfo: ImageInfo by lazy {
        val size: Size = readSizeFromBlurHashUri(blurHashUri) ?: defaultBlurHashBitmapSize
        ImageInfo(size, mimeType = "image/jpeg")
    }

    override val supportRegion: Boolean = false

    override fun decode(sampleSize: Int): Image {
        val resize = requestContext.size
        val bitmapSize = resolveBlurHashBitmapSize(blurHashUri = blurHashUri, size = resize)

        val blurHash = requireNotNull(blurHashUri.authority) {
            "Invalid BlurHash URI: '${blurHashUri}'. The authority part of the URI must contain a valid BlurHash string."
        }
        val pixels = BlurHashUtil.decodeByte(blurHash, bitmapSize.width, bitmapSize.height)

        val decodeConfig =
            DecodeConfig(requestContext.request, imageInfo.mimeType, isOpaque = false)
        val bitmap = createBlurHashBitmap(bitmapSize.width, bitmapSize.height, decodeConfig)
        bitmap.installPixels(pixels)

        return bitmap.asImage()
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): Image {
        throw UnsupportedOperationException("Region decoding is not supported for BlurHash.")
    }

    override fun close() {

    }
}