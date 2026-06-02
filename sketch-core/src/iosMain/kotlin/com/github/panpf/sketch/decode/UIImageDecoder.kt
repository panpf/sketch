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

package com.github.panpf.sketch.decode

import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.decode.internal.UIImageDecodeHelper
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.isAvifFile
import com.github.panpf.sketch.util.isBmpFile
import com.github.panpf.sketch.util.isGifFile
import com.github.panpf.sketch.util.isHeifFile
import com.github.panpf.sketch.util.isIOSVersionAtLeast
import com.github.panpf.sketch.util.isJpegFile
import com.github.panpf.sketch.util.isPngFile
import com.github.panpf.sketch.util.isWebPFile

/**
 * Decode images using UIImage. It can decode HEIF and AVIF images on iOS 11 and iOS 16 or later versions respectively.
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver
 * * sizeMultiplier
 * * precisionDecider
 * * scaleDecider
 * * exifOrientation
 *
 * The following features are not supported:
 *
 * * colorType
 * * colorSpace
 *
 * @see com.github.panpf.sketch.core.ios.test.decode.UIImageDecoderTest
 */
class UIImageDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
    mimeType: String,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = { UIImageDecodeHelper(dataSource, mimeType) },
) {

    companion object {
        const val SORT_WEIGHT = SkiaDecoder.SORT_WEIGHT - 1
    }

    class SupplementSkiaFactory : Decoder.Factory {

        override val key: String = "SupplementSkiaUIImageDecoder"
        override val sortWeight: Int = SORT_WEIGHT

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): UIImageDecoder? {
            val mimeType = isApplicable(fetchResult) ?: return null
            return UIImageDecoder(
                requestContext = requestContext,
                dataSource = fetchResult.dataSource,
                mimeType = mimeType,
            )
        }

        private fun isApplicable(fetchResult: FetchResult): String? {
            if (isIOSVersionAtLeast(11) && isHeifFile(fetchResult.headerBytes)) {
                return "image/heif"
            }
            if (isIOSVersionAtLeast(16) && isAvifFile(fetchResult.headerBytes)) {
                return "image/avif"
            }
            return null
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int = this::class.hashCode()

        override fun toString(): String = "SupplementSkiaUIImageDecoder"
    }

    class Factory : Decoder.Factory {

        override val key: String = "UIImageDecoder"
        override val sortWeight: Int = SORT_WEIGHT

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): UIImageDecoder? {
            val mimeType = isApplicable(fetchResult) ?: return null
            return UIImageDecoder(
                requestContext = requestContext,
                dataSource = fetchResult.dataSource,
                mimeType = mimeType,
            )
        }

        private fun isApplicable(fetchResult: FetchResult): String? {
            if (isJpegFile(fetchResult.headerBytes)) {
                return "image/jpeg"
            }
            if (isPngFile(fetchResult.headerBytes)) {
                return "image/png"
            }
            if (isWebPFile(fetchResult.headerBytes)) {
                return "image/webp"
            }
            if (isBmpFile(fetchResult.headerBytes)) {
                return "image/bmp"
            }
            if (isGifFile(fetchResult.headerBytes)) {
                return "image/gif"
            }
            if (isIOSVersionAtLeast(11) && isHeifFile(fetchResult.headerBytes)) {
                return "image/heif"
            }
            if (isIOSVersionAtLeast(16) && isAvifFile(fetchResult.headerBytes)) {
                return "image/avif"
            }
            return "image/*"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int = this::class.hashCode()

        override fun toString(): String = "UIImageDecoder"
    }
}
