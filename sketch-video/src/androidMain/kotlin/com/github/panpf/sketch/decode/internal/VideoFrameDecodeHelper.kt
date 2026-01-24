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

import android.annotation.TargetApi
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.BitmapParams
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.VideoFrameOptions
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.getFileOrNull
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.div

/**
 * Use MediaMetadataRetriever to decode video frames
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver: Only sampleSize
 * * sizeMultiplier
 * * precisionDecider: Only LESS_PIXELS and SMALLER_SIZE is supported
 * * colorSpace: Only on Android 30 or later
 * * videoFrameMicros
 * * videoFramePercent
 * * videoFrameOption
 *
 * The following decoding related properties are not supported:
 *
 * * scaleDecider
 * * colorType
 *
 * @see com.github.panpf.sketch.video.test.decode.internal.VideoFrameDecodeHelperTest
 */
@TargetApi(Build.VERSION_CODES.O_MR1)
class VideoFrameDecodeHelper constructor(
    val sketch: Sketch,
    val request: ImageRequest,
    val dataSource: DataSource,
    private val mimeType: String,
) : DecodeHelper {

    override val imageInfo: ImageInfo by lazy { readImageInfo() }
    override val supportRegion: Boolean = false

    private val mediaMetadataRetriever by lazy {
        MediaMetadataRetriever().apply {
            if (dataSource is ContentDataSource) {
                setDataSource(request.context, dataSource.contentUri)
            } else {
                dataSource.getFileOrNull(sketch)?.let { setDataSource(it.toFile().path) }
                    ?: throw Exception("Unsupported DataSource: ${dataSource::class}")
            }
        }
    }
    private val exifOrientation: Int by lazy { readExifOrientation() }
    private val exifOrientationHelper by lazy { ExifOrientationHelper(exifOrientation) }

    override fun decode(sampleSize: Int): Image {
        val frameMicros = request.videoFrameMicros
            ?: request.videoFramePercent?.let { percentDuration ->
                val duration = mediaMetadataRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull() ?: 0L
                (duration * percentDuration * 1000).toLong()
            }
            ?: 0L
        val option = request.videoFrameOption ?: VideoFrameOptions.CLOSEST_SYNC
        val imageSize = imageInfo.size
        val dstSize = imageSize / sampleSize.toFloat()
        val bitmap = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                val config = DecodeConfig(request, imageInfo.mimeType, isOpaque = false)
                val bitmapParams = BitmapParams().apply {
                    config.colorType?.also { preferredConfig = it }
                }
                mediaMetadataRetriever.getScaledFrameAtTime(
                    /* timeUs = */ frameMicros,
                    /* option = */ option,
                    /* dstWidth = */ dstSize.width,
                    /* dstHeight = */ dstSize.height,
                    /* params = */ bitmapParams
                ) ?: throw DecodeException(
                    "Failed to getScaledFrameAtTime. " +
                            "frameMicros=$frameMicros, " +
                            "option=${optionToName(option)}, " +
                            "dstSize=$dstSize, " +
                            "imageSize=$imageSize, " +
                            "preferredConfig=${config.colorType}"
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                mediaMetadataRetriever.getScaledFrameAtTime(
                    /* timeUs = */ frameMicros,
                    /* option = */ option,
                    /* dstWidth = */ dstSize.width,
                    /* dstHeight = */ dstSize.height
                ) ?: throw DecodeException(
                    "Failed to getScaledFrameAtTime. " +
                            "frameMicros=$frameMicros, " +
                            "option=${optionToName(option)}, " +
                            "dstSize=$dstSize, " +
                            "imageSize=$imageSize"
                )
            }

            else -> {
                mediaMetadataRetriever.getFrameAtTime(
                    /* timeUs = */ frameMicros,
                    /* option = */ option
                ) ?: throw DecodeException(
                    "Failed to getFrameAtTime. " +
                            "frameMicros=$frameMicros, " +
                            "option=${optionToName(option)}, " +
                            "imageSize=$imageSize"
                )
            }
        }
        val correctedBitmap = exifOrientationHelper.applyToBitmap(bitmap) ?: bitmap
        return correctedBitmap.asImage()
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): Image {
        throw UnsupportedOperationException("Unsupported region decode")
    }

    private fun readImageInfo(): ImageInfo {
        val srcWidth = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
        val srcHeight = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
        val imageSize = Size(width = srcWidth, height = srcHeight)
        val correctedImageSize = exifOrientationHelper.applyToSize(imageSize)
        return ImageInfo(size = correctedImageSize, mimeType = mimeType)
            .apply { checkImageInfo(this) }
    }

    private fun readExifOrientation(): Int {
        val videoRotation = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            ?.toIntOrNull() ?: 0
        val exifOrientation = when (videoRotation) {
            90 -> ExifInterface.ORIENTATION_ROTATE_90
            180 -> ExifInterface.ORIENTATION_ROTATE_180
            270 -> ExifInterface.ORIENTATION_ROTATE_270
            else -> ExifInterface.ORIENTATION_UNDEFINED
        }
        return exifOrientation
    }

    private fun optionToName(option: Int): String {
        return when (option) {
            VideoFrameOptions.CLOSEST -> "CLOSEST"
            VideoFrameOptions.CLOSEST_SYNC -> "CLOSEST_SYNC"
            VideoFrameOptions.NEXT_SYNC -> "NEXT_SYNC"
            VideoFrameOptions.PREVIOUS_SYNC -> "PREVIOUS_SYNC"
            else -> "Unknown($option)"
        }
    }

    override fun toString(): String {
        return "VideoFrameDecodeHelper(request=$request, dataSource=$dataSource, mimeType=$mimeType)"
    }

    override fun close() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaMetadataRetriever.close()
        } else {
            mediaMetadataRetriever.release()
        }
    }
}
