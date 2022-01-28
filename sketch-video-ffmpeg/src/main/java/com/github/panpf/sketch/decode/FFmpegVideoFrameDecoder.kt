package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.AbsBitmapDecoder
import com.github.panpf.sketch.decode.internal.BitmapDecodeException
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.LoadRequest
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
class FFmpegVideoFrameDecoder(
    sketch: Sketch,
    request: LoadRequest,
    dataSource: DataSource,
    val mimeType: String,
) : AbsBitmapDecoder(sketch, request, dataSource) {

    private val mediaMetadataRetriever: FFmpegMediaMetadataRetriever by lazy {
        FFmpegMediaMetadataRetriever().apply {
            if (dataSource is ContentDataSource) {
                setDataSource(dataSource.context, dataSource.contentUri)
            } else {
                val file = runBlocking {
                    dataSource.file()
                }
                setDataSource(file.path)
            }
        }
    }

    override fun close() {
        mediaMetadataRetriever.release()
    }

    override fun readImageInfo(): ImageInfo {
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
        val exifOrientation =
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
                (mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                    ?.toIntOrNull() ?: 0).run {
                    when (this) {
                        0 -> ExifInterface.ORIENTATION_UNDEFINED
                        90 -> ExifInterface.ORIENTATION_ROTATE_90
                        180 -> ExifInterface.ORIENTATION_ROTATE_180
                        270 -> ExifInterface.ORIENTATION_ROTATE_270
                        else -> ExifInterface.ORIENTATION_UNDEFINED
                    }
                }
            } else {
                ExifInterface.ORIENTATION_UNDEFINED
            }
        return ImageInfo(mimeType, srcWidth, srcHeight, exifOrientation)
    }

    override fun decode(imageInfo: ImageInfo, decodeConfig: DecodeConfig): Bitmap {
        // todo 缓存视频帧到磁盘缓存
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

    override fun canDecodeRegion(imageInfo: ImageInfo, imageFormat: ImageFormat?): Boolean = false

    override fun decodeRegion(
        imageInfo: ImageInfo,
        srcRect: Rect,
        decodeConfig: DecodeConfig
    ): Bitmap =
        throw UnsupportedOperationException("FFmpegVideoFrameDecoder not support decode region")

    class Factory : BitmapDecoder.Factory {
        override fun create(
            sketch: Sketch,
            request: LoadRequest,
            fetchResult: FetchResult
        ): FFmpegVideoFrameDecoder? {
            val mimeType = fetchResult.mimeType
            if (mimeType?.startsWith("video/") != true) return null
            return FFmpegVideoFrameDecoder(sketch, request, fetchResult.dataSource, mimeType)
        }
    }
}