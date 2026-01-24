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

@file:Suppress("UnnecessaryVariable", "RedundantConstructorKeyword")

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.VideoFrameOptions
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.cacheFile
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.div
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image as SkiaImage
import org.jetbrains.skia.Rect as SkiaRect
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVAssetImageGenerator
import platform.AVFoundation.AVAssetTrack
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.naturalSize
import platform.AVFoundation.tracksWithMediaType
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGSizeMake
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.darwin.DISPATCH_TIME_FOREVER
import platform.darwin.dispatch_semaphore_create
import platform.darwin.dispatch_semaphore_signal
import platform.darwin.dispatch_semaphore_wait
import platform.posix.memcpy
import kotlin.math.abs
import kotlin.math.roundToInt

internal actual fun createVideoFrameDecodeHelper(
    sketch: Sketch,
    request: ImageRequest,
    dataSource: DataSource,
    mimeType: String,
): DecodeHelper = VideoFrameDecodeHelper(
    sketch = sketch,
    request = request,
    dataSource = dataSource,
    mimeType = mimeType
)

@OptIn(ExperimentalForeignApi::class)
class VideoFrameDecodeHelper constructor(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val dataSource: DataSource,
    private val mimeType: String,
) : DecodeHelper {

    override val imageInfo: ImageInfo by lazy { readImageInfo() }
    override val supportRegion: Boolean = false

    private val asset: AVAsset by lazy { buildAsset() }
    private val generator: AVAssetImageGenerator by lazy { buildGenerator(asset) }
    private val durationSeconds: Double by lazy {
        CMTimeGetSeconds(asset.duration).takeIf { it.isFinite() && it > 0 } ?: 0.0
    }

    override fun decode(sampleSize: Int): Image {
        val frameMicros = request.videoFrameMicros
            ?: request.videoFramePercent?.takeIf { durationSeconds > 0 }?.let { percentDuration ->
                (durationSeconds * percentDuration * 1_000_000L).toLong()
            }
            ?: 0L
        val frameSeconds = frameMicros / 1_000_000.0
        val option = request.videoFrameOption ?: VideoFrameOptions.CLOSEST_SYNC
        val imageSize = imageInfo.size
        val dstSize = imageSize / sampleSize.toFloat()
        val time = CMTimeMakeWithSeconds(frameSeconds, preferredTimescale = 600)
        val bitmap = decodeFrame(time, option, dstSize, frameMicros)
        return bitmap.asImage()
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): Image {
        throw UnsupportedOperationException("Unsupported region decode")
    }

    private fun decodeFrame(
        time: CValue<CMTime>,
        option: Int,
        targetSize: Size,
        frameMicros: Long,
    ): Bitmap {
        generator.maximumSize = CGSizeMake(targetSize.width.toDouble(), targetSize.height.toDouble())
        applyOption(option)
        val cgImage = generator.copyCGImageAtTime(time, actualTime = null, error = null)
            ?: throw DecodeException(
                "Failed to copyCGImageAtTime. " +
                        "frameMicros=$frameMicros, " +
                        "option=${VideoFrameOptions.nameOf(option)}, " +
                        "targetSize=$targetSize, " +
                        "imageSize=${imageInfo.size}"
            )
        val uiImage = UIImage.imageWithCGImage(cgImage)
        val pngData = UIImagePNGRepresentation(uiImage)
            ?: throw DecodeException(
                "Failed to encode frame image. " +
                        "frameMicros=$frameMicros, " +
                        "option=${VideoFrameOptions.nameOf(option)}"
            )
        val bytes = pngData.toByteArray()
        val skiaImage = SkiaImage.makeFromEncoded(bytes)
        val skiaBitmap = com.github.panpf.sketch.createBitmap(skiaImage.imageInfo)
        Canvas(skiaBitmap).drawImageRect(
            skiaImage,
            SkiaRect.makeWH(skiaImage.width.toFloat(), skiaImage.height.toFloat()),
            SkiaRect.makeWH(skiaBitmap.width.toFloat(), skiaBitmap.height.toFloat())
        )
        skiaImage.close()
        return skiaBitmap
    }

    private fun readImageInfo(): ImageInfo {
        val track = asset.tracksWithMediaType(AVMediaTypeVideo).firstOrNull() as? AVAssetTrack
        val naturalSize = track?.naturalSize ?: CGSizeMake(0.0, 0.0)
        val trackSize = naturalSize.useContents {
            val w = width.roundToInt()
            val h = height.roundToInt()
            Size(width = abs(w), height = abs(h))
        }
        val frameSize = fetchFrameSize()
        val imageSize = when {
            trackSize.width > 0 && trackSize.height > 0 -> trackSize
            frameSize != null -> frameSize
            else -> Size.Empty
        }
        if (imageSize.isEmpty) {
            throw DecodeException("Failed to read video size. trackSize=$trackSize, frameSize=$frameSize")
        }
        return ImageInfo(size = imageSize, mimeType = mimeType)
            .apply { checkImageInfo(this) }
    }

    private fun buildAsset(): AVAsset {
        val file = dataSource.cacheFile(sketch)
        val fileWithExt = ensureVideoExtension(file, sketch.fileSystem)
        val url = NSURL.fileURLWithPath(path = fileWithExt.toString())
        return AVURLAsset(uRL = url, options = null)
    }

    private fun buildGenerator(asset: AVAsset): AVAssetImageGenerator =
        AVAssetImageGenerator(asset).apply {
            appliesPreferredTrackTransform = true
        }

    private fun applyOption(option: Int) {
        // iOS AVAssetImageGenerator does not expose an exact equivalent to Android options;
        // keep defaults.
    }

    private fun fetchFrameSize(): Size? {
        generator.maximumSize = CGSizeMake(4096.0, 4096.0)
        val zeroTime = CMTimeMakeWithSeconds(0.0, preferredTimescale = 600)
        val cgImage = generator.copyCGImageAtTime(zeroTime, actualTime = null, error = null)
            ?: return null
        val width = CGImageGetWidth(cgImage).toInt()
        val height = CGImageGetHeight(cgImage).toInt()
        if (width <= 0 || height <= 0) {
            return null
        }
        return Size(width = width, height = height)
    }

    private fun ensureVideoExtension(file: Path, fileSystem: FileSystem): Path {
        val extension = MimeTypeMap.getExtensionFromMimeType(mimeType) ?: "mp4"
        val currentName = file.name
        if (currentName.endsWith(".$extension", ignoreCase = true)) return file
        val newPath = file.parent?.resolve("$currentName.$extension") ?: "${file}.$extension".toPath()
        runCatching {
            if (!fileSystem.exists(newPath)) {
                fileSystem.createSymlink(source = newPath, target = file)
            }
        }.onFailure {
            return file
        }
        return newPath
    }

    override fun close() {
        generator.cancelAllCGImageGeneration()
    }

    override fun toString(): String {
        return "VideoFrameDecodeHelper(request=$request, dataSource=$dataSource, mimeType=$mimeType)"
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).also { byteArray ->
    val source = bytes ?: return@also
    byteArray.usePinned { pinned ->
        memcpy(pinned.addressOf(0), source, length)
    }
}
