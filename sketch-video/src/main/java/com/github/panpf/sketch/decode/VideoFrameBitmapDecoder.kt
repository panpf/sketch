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

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.BitmapParams
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.BitmapDecodeException
import com.github.panpf.sketch.decode.internal.appliedExifOrientation
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.realDecode
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercent
import kotlin.math.roundToInt

/**
 * Decode a frame of a video file and convert it to Bitmap
 *
 * Notes: Android O(26/8.0) and before versions do not support scale to read frames,
 * resulting in slow decoding speed and large memory consumption in the case of large videos and causes memory jitter
 *
 * Notes：LoadRequest's preferQualityOverSpeed, colorSpace attributes will not take effect;
 * The bitmapConfig attribute takes effect only on Android 30 or later
 */
@TargetApi(Build.VERSION_CODES.O_MR1)
class VideoFrameBitmapDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val dataSource: DataSource,
    private val mimeType: String,
) : BitmapDecoder {

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        val mediaMetadataRetriever = MediaMetadataRetriever().apply {
            if (dataSource is ContentDataSource) {
                setDataSource(request.context, dataSource.contentUri)
            } else {
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
            ).appliedExifOrientation(sketch).appliedResize(sketch, request.resize)
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mediaMetadataRetriever.close()
            } else {
                mediaMetadataRetriever.release()
            }
        }
    }

    private fun readImageInfo(mediaMetadataRetriever: MediaMetadataRetriever): ImageInfo {
        val srcWidth = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
        val srcHeight = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
        if (srcWidth <= 1 || srcHeight <= 1) {
            val message = "Invalid video size. size=${srcWidth}x${srcHeight}"
            throw BitmapDecodeException(message)
        }
        val exifOrientation = if (!request.ignoreExifOrientation) {
            readExifOrientation(mediaMetadataRetriever)
        } else {
            ExifInterface.ORIENTATION_UNDEFINED
        }
        return ImageInfo(srcWidth, srcHeight, mimeType, exifOrientation)
    }

    private fun readExifOrientation(mediaMetadataRetriever: MediaMetadataRetriever): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val videoRotation = mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                ?.toIntOrNull() ?: 0
            videoRotation.run {
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
        mediaMetadataRetriever: MediaMetadataRetriever,
        imageInfo: ImageInfo,
        decodeConfig: DecodeConfig
    ): Bitmap {
        val option = request.videoFrameOption ?: MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        val frameMicros = request.videoFrameMicros
            ?: request.videoFramePercent?.let { percentDuration ->
                val duration = mediaMetadataRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
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
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                val bitmapParams = BitmapParams().apply {
                    val inPreferredConfigFromRequest = decodeConfig.inPreferredConfig
                    if (inPreferredConfigFromRequest != null) {
                        preferredConfig = inPreferredConfigFromRequest
                    }
                }
                mediaMetadataRetriever
                    .getScaledFrameAtTime(frameMicros, option, dstWidth, dstHeight, bitmapParams)
                    ?: throw BitmapDecodeException(
                        "Failed to getScaledFrameAtTime. frameMicros=%d, option=%s, dst=%dx%d, image=%dx%d, preferredConfig=%s.".format(
                            frameMicros, optionToName(option), dstWidth, dstHeight,
                            imageInfo.width, imageInfo.height, decodeConfig.inPreferredConfig
                        )
                    )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                mediaMetadataRetriever
                    .getScaledFrameAtTime(frameMicros, option, dstWidth, dstHeight)
                    ?: throw BitmapDecodeException(
                        "Failed to getScaledFrameAtTime. frameMicros=%d, option=%s, dst=%dx%d, image=%dx%d.".format(
                            frameMicros, optionToName(option), dstWidth, dstHeight,
                            imageInfo.width, imageInfo.height
                        )
                    )
            }
            else -> {
                mediaMetadataRetriever.getFrameAtTime(frameMicros, option)
                    ?: throw BitmapDecodeException(
                        "Failed to getFrameAtTime. frameMicros=%d, option=%s, image=%dx%d.".format(
                            frameMicros, optionToName(option), imageInfo.width, imageInfo.height
                        )
                    )
            }
        }
    }

    private fun optionToName(option: Int): String {
        return when (option) {
            MediaMetadataRetriever.OPTION_CLOSEST -> "CLOSEST"
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC -> "CLOSEST_SYNC"
            MediaMetadataRetriever.OPTION_NEXT_SYNC -> "NEXT_SYNC"
            MediaMetadataRetriever.OPTION_PREVIOUS_SYNC -> "PREVIOUS_SYNC"
            else -> "Unknown($option)"
        }
    }

    @TargetApi(Build.VERSION_CODES.O_MR1)
    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): VideoFrameBitmapDecoder? {
            val mimeType = fetchResult.mimeType
            if (mimeType?.startsWith("video/") == true) {
                return VideoFrameBitmapDecoder(sketch, request, fetchResult.dataSource, mimeType)
            }
            return null
        }

        override fun toString(): String = "VideoFrameBitmapDecoder"

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