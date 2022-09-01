/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import android.graphics.Bitmap
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.appliedExifOrientation
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.decode.internal.realDecode
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercent
import wseemann.media.FFmpegMediaMetadataRetriever
import kotlin.math.roundToInt

/**
 * Decode a frame of a video file and convert it to Bitmap
 *
 * Notes: It is not support MediaMetadataRetriever.BitmapParams
 *
 * Notes：LoadRequest's preferQualityOverSpeed, bitmapConfig, colorSpace attributes will not take effect
 */
class FFmpegVideoFrameBitmapDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val dataSource: DataSource,
    private val mimeType: String,
) : BitmapDecoder {

    companion object {
        const val MODULE = "FFmpegVideoFrameBitmapDecoder"
    }

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        val mediaMetadataRetriever = FFmpegMediaMetadataRetriever().apply {
            if (dataSource is ContentDataSource) {
                setDataSource(dataSource.request.context, dataSource.contentUri)
            } else {
                // Currently running on a limited number of IO contexts, so this warning can be ignored
                @Suppress("BlockingMethodInNonBlockingContext")
                setDataSource(dataSource.file().path)
            }
        }
        try {
            val imageInfo = readImageInfo(mediaMetadataRetriever)
            return realDecode(
                request = request,
                dataFrom = dataSource.dataFrom,
                imageInfo = imageInfo,
                decodeFull = {
                    realDecodeFull(mediaMetadataRetriever, imageInfo, it)
                },
                decodeRegion = null
            ).appliedExifOrientation(sketch, request)
                .appliedResize(sketch, request, request.resize)
        } finally {
            mediaMetadataRetriever.release()
        }
    }

    private fun readImageInfo(mediaMetadataRetriever: FFmpegMediaMetadataRetriever): ImageInfo {
        val srcWidth =
            mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toIntOrNull() ?: 0
        val srcHeight =
            mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toIntOrNull() ?: 0
        if (srcWidth <= 1 || srcHeight <= 1) {
            val message = "Invalid video file. size=${srcWidth}x${srcHeight}"
            throw ImageInvalidException(message)
        }
        val exifOrientation = if (!request.ignoreExifOrientation) {
            readExifOrientation(mediaMetadataRetriever)
        } else {
            ExifInterface.ORIENTATION_UNDEFINED
        }
        return ImageInfo(srcWidth, srcHeight, mimeType, exifOrientation)
    }

    private fun readExifOrientation(mediaMetadataRetriever: FFmpegMediaMetadataRetriever): Int =
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            (mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                ?.toIntOrNull() ?: 0).run {
                when (this) {
                    90 -> ExifInterface.ORIENTATION_ROTATE_90
                    180 -> ExifInterface.ORIENTATION_ROTATE_180
                    270 -> ExifInterface.ORIENTATION_ROTATE_270
                    else -> ExifInterface.ORIENTATION_UNDEFINED
                }
            }
        } else {
            ExifInterface.ORIENTATION_UNDEFINED
        }

    private fun realDecodeFull(
        mediaMetadataRetriever: FFmpegMediaMetadataRetriever,
        imageInfo: ImageInfo,
        decodeConfig: DecodeConfig
    ): Bitmap {
        val option =
            request.videoFrameOption ?: FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC
        val frameMicros = request.videoFrameMicros
            ?: request.videoFramePercent?.let { percentDuration ->
                val duration = mediaMetadataRetriever
                    .extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull() ?: 0L
                (duration * percentDuration * 1000).toLong()
            }
            ?: 0L

        val inSampleSize = decodeConfig.inSampleSize?.toFloat()
        val dstWidth = if (inSampleSize != null) {
            (imageInfo.width / inSampleSize).roundToInt()
        } else {
            imageInfo.width
        }
        val dstHeight = if (inSampleSize != null) {
            (imageInfo.height / inSampleSize).roundToInt()
        } else {
            imageInfo.height
        }
        val bitmap =
            mediaMetadataRetriever.getScaledFrameAtTime(frameMicros, option, dstWidth, dstHeight)
                ?: throw BitmapDecodeException(
                    "Failed to getScaledFrameAtTime. frameMicros=%d, option=%s, dst=%dx%d, image=%dx%d."
                        .format(
                            frameMicros, optionToName(option), dstWidth, dstHeight,
                            imageInfo.width, imageInfo.height
                        )
                )
        sketch.logger.d(MODULE) {
            "realDecodeFull. successful. ${bitmap.logString}. ${imageInfo}. ${request.key}"
        }
        return bitmap
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

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): FFmpegVideoFrameBitmapDecoder? {
            val mimeType = fetchResult.mimeType
            if (mimeType?.startsWith("video/") == true) {
                return FFmpegVideoFrameBitmapDecoder(
                    sketch = sketch,
                    request = request,
                    dataSource = fetchResult.dataSource,
                    mimeType = mimeType
                )
            }
            return null
        }

        override fun toString(): String = "FFmpegVideoFrameBitmapDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}