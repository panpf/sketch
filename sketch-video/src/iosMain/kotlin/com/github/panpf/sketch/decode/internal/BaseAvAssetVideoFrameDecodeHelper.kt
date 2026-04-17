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
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.resolveRequestVideoFrameMicros
import com.github.panpf.sketch.util.toBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVAssetImageGenerator
import platform.CoreGraphics.CGImageRef
import platform.CoreGraphics.CGSizeMake
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class)
abstract class BaseAvAssetVideoFrameDecodeHelper(
    private val request: ImageRequest,
) : DecodeHelper {

    val avAsset: AVAsset by lazy { requestVideoAsset() }

    override val imageInfo: ImageInfo by lazy { readImageInfo() }
    override val supportRegion: Boolean = false

    override fun decode(sampleSize: Int): Image {
        val durationMicros = avAsset.durationMicrosOrNull()
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
        throw UnsupportedOperationException("Unsupported region decode")
    }

    abstract fun readImageInfo(): ImageInfo

    abstract fun requestVideoAsset(): AVAsset

    fun decodeFrame(frameMicros: Long, targetSize: Size?): CGImageRef? {
        val generator = AVAssetImageGenerator(asset = avAsset).apply {
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

    override fun close() = Unit
}