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

@file:Suppress("UnnecessaryVariable", "FoldInitializerAndIfToElvis")

package com.github.panpf.sketch.decode.internal

import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.preferVideoCover
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.getFileOrNull
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.div
import com.github.panpf.sketch.util.resolveRequestVideoFrameMicros
import com.github.panpf.sketch.util.shapeType
import wseemann.media.FFmpegMediaMetadataRetriever

/**
 * Use FFmpegMediaMetadataRetriever to decode video frames
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver: Only sampleSize
 * * sizeMultiplier
 * * precisionDecider: Only LESS_PIXELS and SMALLER_SIZE is supported
 * * videoFrameMicros
 * * videoFramePercent
 * * videoFrameOption
 *
 * The following decoding related properties are not supported:
 *
 * * scaleDecider
 * * colorType
 * * colorSpace
 *
 * @see com.github.panpf.sketch.video.ffmpeg.test.decode.internal.FFmpegVideoFrameDecodeHelperTest
 */
class FFmpegVideoFrameDecodeHelper(
    val sketch: Sketch,
    val request: ImageRequest,
    val dataSource: DataSource,
    val mimeType: String,
) : DecodeHelper {

    private val mediaMetadataRetriever by lazy {
        FFmpegMediaMetadataRetriever().apply {
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
    private val coverHelper by lazy {
        val preferVideoCover = request.preferVideoCover
        val coverBytes = if (preferVideoCover == true)
            mediaMetadataRetriever.embeddedPicture else null
        coverBytes?.let {
            val dataSource = ByteArrayDataSource(it, DataFrom.LOCAL)
            BitmapFactoryDecodeHelper(request, dataSource)
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

        val durationMicros = mediaMetadataRetriever
            .extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull()?.let { it * 1000 } ?: 0L
        val videoFrameMicros = request.videoFrameMicros
        val videoFramePercent = request.videoFramePercent
        val requestFrameMicros = resolveRequestVideoFrameMicros(
            durationMicros = durationMicros,
            videoFrameMicros = videoFrameMicros,
            videoFramePercent = videoFramePercent,
        )
        val option = request.videoFrameOption ?: FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC
        val imageSize = imageInfo.size
        val dstSize = imageSize / sampleSize.toFloat()
        val bitmap = mediaMetadataRetriever.getScaledFrameAtTime(
            /* timeUs = */ requestFrameMicros,
            /* option = */ option,
            /* width = */ dstSize.width,
            /* height = */ dstSize.height
        ) ?: throw DecodeException(
            "Failed to getScaledFrameAtTime. " +
                    "frameMicros=${requestFrameMicros}, " +
                    "option=${optionToName(option)}, " +
                    "dstSize=${dstSize}, " +
                    "imageSize=$imageSize"
        )
        val needRotate = bitmap.size.shapeType() != imageSize.shapeType()
        val correctedBitmap = if (needRotate) {
            exifOrientationHelper.applyToBitmap(bitmap) ?: bitmap
        } else {
            bitmap
        }
        return correctedBitmap.asImage()
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): Image {
        return coverHelper?.decodeRegion(region, sampleSize)
            ?: throw UnsupportedOperationException("Unsupported region decode")
    }

    private fun readImageInfo(): ImageInfo {
        val srcWidth = mediaMetadataRetriever
            .extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()
            ?: 0
        val srcHeight = mediaMetadataRetriever
            .extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()
            ?: 0
        val imageSize = Size(width = srcWidth, height = srcHeight)
        val correctedImageSize = exifOrientationHelper.applyToSize(imageSize)
        return ImageInfo(size = correctedImageSize, mimeType = mimeType)
            .apply { checkImageInfo(this) }
    }

    private fun readExifOrientation(): Int {
        val videoRotation = mediaMetadataRetriever
            .extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
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
            FFmpegMediaMetadataRetriever.OPTION_CLOSEST -> "CLOSEST"
            FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC -> "CLOSEST_SYNC"
            FFmpegMediaMetadataRetriever.OPTION_NEXT_SYNC -> "NEXT_SYNC"
            FFmpegMediaMetadataRetriever.OPTION_PREVIOUS_SYNC -> "PREVIOUS_SYNC"
            else -> "Unknown($option)"
        }
    }

    override fun toString(): String {
        return "FFmpegVideoFrameDecodeHelper(request=$request, dataSource=$dataSource, mimeType=$mimeType)"
    }

    override fun close() {
        mediaMetadataRetriever.release()
    }
}