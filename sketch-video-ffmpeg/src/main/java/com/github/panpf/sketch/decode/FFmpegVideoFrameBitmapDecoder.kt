package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.BitmapDecodeException
import com.github.panpf.sketch.decode.internal.applyExifOrientation
import com.github.panpf.sketch.decode.internal.applyResize
import com.github.panpf.sketch.decode.internal.realDecode
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercentDuration
import kotlinx.coroutines.runBlocking
import wseemann.media.FFmpegMediaMetadataRetriever
import kotlin.math.roundToInt

/**
 * Notes: It is not support MediaMetadataRetriever.BitmapParams
 *
 * Notes：LoadRequest's preferQualityOverSpeed, bitmapConfig, colorSpace attributes will not take effect
 */
class FFmpegVideoFrameBitmapDecoder(
    private val request: ImageRequest,
    private val dataSource: DataSource,
    private val mimeType: String,
) : BitmapDecoder {

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        val mediaMetadataRetriever: FFmpegMediaMetadataRetriever by lazy {
            FFmpegMediaMetadataRetriever().apply {
                if (dataSource is ContentDataSource) {
                    setDataSource(dataSource.request.context, dataSource.contentUri)
                } else {
                    val file = runBlocking {
                        dataSource.file()
                    }
                    setDataSource(file.path)
                }
            }
        }
        try {
            val imageInfo = readImageInfo(mediaMetadataRetriever)
            val exifOrientation = if (!request.ignoreExifOrientation) {
                readExifOrientation(mediaMetadataRetriever)
            } else {
                ExifInterface.ORIENTATION_UNDEFINED
            }
            return realDecode(
                request,
                dataSource.dataFrom,
                imageInfo = imageInfo,
                exifOrientation = exifOrientation,
                decodeFull = {
                    realDecodeFull(mediaMetadataRetriever, imageInfo, it)
                },
                decodeRegion = null
            ).applyExifOrientation(request.sketch.bitmapPool, request.ignoreExifOrientation)
                .applyResize(request.sketch, request.resize)
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
            val message = "Invalid video size. size=${srcWidth}x${srcHeight}"
            throw BitmapDecodeException(request, message)
        }
        return ImageInfo(srcWidth, srcHeight, mimeType)
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
            request.videoFrameOption() ?: FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC
        val frameMicros = request.videoFrameMicros()
            ?: request.videoFramePercentDuration()?.let { percentDuration ->
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
        return mediaMetadataRetriever.getScaledFrameAtTime(frameMicros, option, dstWidth, dstHeight)
            ?: throw BitmapDecodeException(
                request, "Failed to decode frame at $frameMicros microseconds."
            )
    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            request: ImageRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): FFmpegVideoFrameBitmapDecoder? {
            val mimeType = fetchResult.mimeType
            if (mimeType?.startsWith("video/") == true) {
                return FFmpegVideoFrameBitmapDecoder(request, fetchResult.dataSource, mimeType)
            }
            return null
        }

        override fun toString(): String = "FFmpegVideoFrameDecoder"
    }
}