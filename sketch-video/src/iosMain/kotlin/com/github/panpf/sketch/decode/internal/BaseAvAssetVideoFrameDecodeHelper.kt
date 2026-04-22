/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright (C) 2026 Kuki93
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
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.preferVideoCover
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.resolveRequestVideoFrameMicros
import com.github.panpf.sketch.util.toBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVAssetImageGenerator
import platform.AVFoundation.AVKeyValueStatusLoaded
import platform.AVFoundation.AVMetadataCommonKeyArtwork
import platform.AVFoundation.AVMetadataItem
import platform.AVFoundation.AVMetadataKeySpaceCommon
import platform.AVFoundation.commonMetadata
import platform.AVFoundation.metadataItemsFromArray
import platform.CoreGraphics.CGImageRef
import platform.CoreGraphics.CGSizeMake
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSData
import platform.UIKit.UIImage
import platform.darwin.ByteVar
import platform.posix.memcpy
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
abstract class BaseAvAssetVideoFrameDecodeHelper(
    val request: ImageRequest,
    val mimeType: String,
) : DecodeHelper {

    val asset: AVAsset by lazy { requestVideoAsset() }

    private val coverHelper by lazy {
        val preferVideoCover = request.preferVideoCover
        val coverBytes = if (preferVideoCover == true)
            getEmbeddedVideoCover(asset) else null
        coverBytes?.let {
            val dataSource = ByteArrayDataSource(it, DataFrom.LOCAL)
            SkiaDecodeHelper(request, dataSource)
        }
    }

    override val imageInfo: ImageInfo by lazy {
        coverHelper?.imageInfo?.copy(mimeType = mimeType) ?: readImageInfo()
    }
    override val supportRegion: Boolean
        get() = coverHelper?.supportRegion ?: false

    override fun decode(sampleSize: Int): Image {
        val coverHelper = coverHelper
        if (coverHelper != null) {
            return coverHelper.decode(sampleSize)
        }

        val durationMicros = asset.durationMicrosOrNull()
        val videoFrameMicros = request.videoFrameMicros
        val videoFramePercent = request.videoFramePercent
        val requestFrameMicros = resolveRequestVideoFrameMicros(
            durationMicros = durationMicros,
            videoFrameMicros = videoFrameMicros,
            videoFramePercent = videoFramePercent,
        )
        val targetSize = calculateSampledBitmapSize(imageInfo.size, sampleSize)
        val frameCandidates = frameCandidates(
            requestFrameMicros = requestFrameMicros,
            durationMicros = durationMicros,
        )
        val cgImage = frameCandidates
            .firstNotNullOfOrNull { frameMicros -> decodeFrame(frameMicros, targetSize) }
            ?: throw DecodeException(
                "Failed to decode video frame. " +
                        "durationMicros=$durationMicros, " +
                        "requestFrameMicros=$requestFrameMicros, " +
                        "frameCandidates=$frameCandidates"
            )
        val uiImage = UIImage.imageWithCGImage(cgImage)
        val bitmap = uiImage.toBitmap()
        return bitmap.asImage()
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): Image {
        return coverHelper?.decodeRegion(region, sampleSize)
            ?: throw UnsupportedOperationException("Unsupported region decode")
    }

    abstract fun readImageInfo(): ImageInfo

    abstract fun requestVideoAsset(): AVAsset

    fun decodeFrame(frameMicros: Long, targetSize: Size?): CGImageRef? {
        val generator = AVAssetImageGenerator(asset = asset).apply {
            appliesPreferredTrackTransform = true
            if (targetSize != null) {
                maximumSize = CGSizeMake(
                    width = targetSize.width.toDouble(),
                    height = targetSize.height.toDouble(),
                )
            }
        }
        val requestTime = CMTimeMakeWithSeconds(
            seconds = frameMicros.toDouble() / 1_000_000.0,
            preferredTimescale = 600,
        )
        return generator.copyCGImageAtTime(requestTime, null, null)
    }

    private fun getEmbeddedVideoCover(asset: AVAsset): ByteArray? {
        val nsData = runBlocking {
            suspendCancellableCoroutine { continuation ->
                asset.loadValuesAsynchronouslyForKeys(listOf("commonMetadata")) {
                    val status = asset.statusOfValueForKey("commonMetadata", null)
                    if (status == AVKeyValueStatusLoaded) {
                        val artworkItems = AVMetadataItem.metadataItemsFromArray(
                            asset.commonMetadata,
                            withKey = AVMetadataCommonKeyArtwork,
                            keySpace = AVMetadataKeySpaceCommon
                        )
                        val artworkItem = artworkItems.firstOrNull() as? AVMetadataItem
                        val data = artworkItem?.value as? NSData
                        continuation.resume(data)
                    } else {
                        continuation.resume(null)
                    }
                }
            }
        }
        if (nsData == null || nsData.length.toLong() == 0L) return null
        val byteArray = ByteArray(nsData.length.toInt())
        val byteVars = nsData.bytes?.reinterpret<ByteVar>()
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), byteVars, nsData.length)
        }
        return byteArray
    }

    override fun close() = Unit
}