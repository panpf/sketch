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

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.internal.DecodeHelper
import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.scale
import com.github.panpf.sketch.util.toBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import okio.buffer
import okio.use
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage

/**
 * Adds AVIF image support using Apple's native decoder.
 *
 * @see com.github.panpf.sketch.avif.ios.test.decode.AppleAvifDecoderTest.testSupportAvif
 */
fun ComponentRegistry.Builder.supportAvif(): ComponentRegistry.Builder = apply {
    add(AppleAvifDecoder.Factory())
}

/**
 * Decode AVIF images on iOS with Apple's native decoder, then convert the result to a Skia bitmap.
 *
 * Skia's Image::makeFromEncoded does not support AVIF, but iOS 16+ does.
 *
 * @see com.github.panpf.sketch.avif.ios.test.decode.AppleAvifDecoderTest
 */
class AppleAvifDecoder private constructor(
    requestContext: RequestContext,
    dataSource: DataSource,
    avifBytes: ByteArray,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = {
        AppleAvifDecodeHelper(avifBytes)
    }
) {

    companion object {
        const val MIME_TYPE = "image/avif"
        const val SORT_WEIGHT = 0
    }

    class Factory : Decoder.Factory {

        override val key: String = "AppleAvifDecoder"
        override val sortWeight: Int = SORT_WEIGHT

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            if (!isApplicable(requestContext, fetchResult)) return null
            val avifBytes = fetchResult.dataSource.openSource().buffer().use { it.readByteArray() }
            return AppleAvifDecoder(requestContext, fetchResult.dataSource, avifBytes)
        }

        private fun isApplicable(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Boolean {
            if (fetchResult.mimeType == MIME_TYPE) return true
            return requestContext.request.uri.path.orEmpty().lowercase().endsWith(".avif")
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "AppleAvifDecoder"
    }
}

@OptIn(ExperimentalForeignApi::class)
private class AppleAvifDecodeHelper(
    private val avifBytes: ByteArray
) : DecodeHelper {

    private val uiImage: UIImage by lazy {
        createUIImage(avifBytes)
            ?: throw DecodeException(
                "AppleAvifDecoder could not decode AVIF data. " +
                        "Native AVIF decoding requires iOS 16+ and an AVIF variant supported " +
                        "by Apple's UIImage decoders."
            )
    }

    private val imageInfo: ImageInfo by lazy {
        val cgImage = uiImage.CGImage
            ?: throw DecodeException("UIImage has no CGImage")
        ImageInfo(
            size = Size(
                width = CGImageGetWidth(cgImage).toInt().coerceAtLeast(1),
                height = CGImageGetHeight(cgImage).toInt().coerceAtLeast(1),
            ),
            mimeType = AppleAvifDecoder.MIME_TYPE,
        )
    }

    override suspend fun getImageInfo(): ImageInfo = imageInfo

    override suspend fun isSupportRegion(): Boolean = false

    override suspend fun decode(sampleSize: Int): Image {
        val bitmap = uiImage.toBitmap()
        val sampledBitmap = if (sampleSize > 1) {
            bitmap.scale(scaleFactor = 1 / sampleSize.toFloat())
        } else {
            bitmap
        }
        return sampledBitmap.asImage()
    }

    override suspend fun decodeRegion(region: Rect, sampleSize: Int): Image =
        throw UnsupportedOperationException("Unsupported region decode")

    override fun close() {

    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun createUIImage(avifBytes: ByteArray): UIImage? {
    if (avifBytes.isEmpty()) return null
    val nsData = avifBytes.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = avifBytes.size.toULong())
    }
    return UIImage.imageWithData(nsData)
}
