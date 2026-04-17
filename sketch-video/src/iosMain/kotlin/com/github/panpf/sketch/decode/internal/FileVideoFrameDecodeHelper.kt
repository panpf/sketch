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

import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.isNotEmpty
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVAssetTrack
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.naturalSize
import platform.AVFoundation.tracksWithMediaType
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.Foundation.NSURL

/**
 * Help decode video frames from FileDataSource
 *
 * @see com.github.panpf.sketch.video.ios.test.decode.internal.FileVideoFrameDecodeHelperTest
 */
@OptIn(ExperimentalForeignApi::class)
class FileVideoFrameDecodeHelper(
    request: ImageRequest,
    val dataSource: FileDataSource,
    val mimeType: String,
) : BaseAvAssetVideoFrameDecodeHelper(request) {

    override fun readImageInfo(): ImageInfo {
        val size = readTrackSize()
            ?.takeIf { it.isNotEmpty }
            ?: readFrameSize()
        return ImageInfo(size = size, mimeType = mimeType)
    }

    private fun readTrackSize(): Size? {
        val filePath = dataSource.path.toString()
        val url = NSURL.fileURLWithPath(filePath)
        val asset = AVURLAsset.assetWithURL(url)
        val videoTrack = asset.tracksWithMediaType(AVMediaTypeVideo)
            .firstOrNull()?.let { it as AVAssetTrack }
        val size = videoTrack?.naturalSize?.useContents {
            Size(width.toInt(), height.toInt())
        }
        return size
    }

    private fun readFrameSize(): Size {
        val durationMicros = avAsset.durationMicrosOrNull()
        val frameCandidates = frameCandidates(
            requestFrameMicros = 0L,
            durationMicros = durationMicros,
        )
        val firstFrameImage = frameCandidates
            .firstNotNullOfOrNull { frameMicros -> decodeFrame(frameMicros, null) }
            ?: throw DecodeException("Failed to read video info. durationMicros=$durationMicros, frameCandidates=$frameCandidates")
        val size = Size(
            width = CGImageGetWidth(firstFrameImage).toInt(),
            height = CGImageGetHeight(firstFrameImage).toInt(),
        )
        return size
    }

    override fun requestVideoAsset(): AVAsset {
        val filePath = dataSource.path.toString()
        return AVAsset.assetWithURL(NSURL.fileURLWithPath(filePath))
    }
}