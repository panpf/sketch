package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.BitmapParams
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.webkit.MimeTypeMap
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.ImageType
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.AbsBitmapDecoder
import com.github.panpf.sketch.decode.internal.BitmapDecodeException
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.getMimeTypeFromUrl
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

class VideoFrameDecoder(
    sketch: Sketch,
    request: LoadRequest,
    dataSource: DataSource
) : AbsBitmapDecoder(sketch, request, dataSource) {

    companion object {
        const val VIDEO_FRAME_MICROS_KEY = "coil#video_frame_micros"
        const val VIDEO_FRAME_OPTION_KEY = "coil#video_frame_option"
    }

    private val mediaMetadataRetriever: MediaMetadataRetriever by lazy {
        MediaMetadataRetriever().apply {
            val fileDescriptor = dataSource.newFileDescriptor()
            if (fileDescriptor != null) {
                setDataSource(fileDescriptor)
            } else {
                val file = runBlocking {
                    dataSource.file()
                }
                setDataSource(file.path)
            }
        }
    }

    override fun close() {
        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            mediaMetadataRetriever.close()
        } else {
            mediaMetadataRetriever.release()
        }
    }

    override fun readImageInfo(): ImageInfo {
        val srcWidth =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toIntOrNull() ?: 0
        val srcHeight =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toIntOrNull() ?: 0
        if (srcWidth <= 1 || srcHeight <= 1) {
            throw BitmapDecodeException(
                request,
                "Invalid video size. size=${srcWidth}x${srcHeight}, uri=${request.uriString}"
            )
        }
        val exifOrientation =
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
                (mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
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
        // todo mimeType 从 fetchResult 中来
        return ImageInfo("video/mp4", srcWidth, srcHeight, exifOrientation)
    }

    override fun decode(imageInfo: ImageInfo, decodeConfig: DecodeConfig): Bitmap {
        val option = request.videoFrameOption() ?: MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        val frameMicros = request.videoFrameMicros() ?: 0L

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
            VERSION.SDK_INT >= 30 -> {
                val bitmapParams = BitmapParams().apply {
                    val inPreferredConfigFromRequest = decodeConfig.inPreferredConfig
                    if (inPreferredConfigFromRequest != null) {
                        preferredConfig = inPreferredConfigFromRequest
                    }
                }
                mediaMetadataRetriever
                    .getScaledFrameAtTime(frameMicros, option, dstWidth, dstHeight, bitmapParams)
            }
            VERSION.SDK_INT >= 27 -> {
                mediaMetadataRetriever
                    .getScaledFrameAtTime(frameMicros, option, dstWidth, dstHeight)
            }
            else -> {
                mediaMetadataRetriever.getFrameAtTime(frameMicros, option)
            }
        } ?: throw BitmapDecodeException(
            request,
            "Failed to decode frame at $frameMicros microseconds."
        )
    }

    override fun canDecodeRegion(imageInfo: ImageInfo, imageType: ImageType?): Boolean = false

    override fun decodeRegion(
        imageInfo: ImageInfo,
        srcRect: Rect,
        decodeConfig: DecodeConfig
    ): Bitmap = throw UnsupportedOperationException("VideoFrameDecoder not support decode region")

    class Factory : BitmapDecoder.Factory {
        override fun create(
            sketch: Sketch,
            request: LoadRequest,
            dataSource: DataSource
        ): VideoFrameDecoder? {
            // todo mimeType 由 fetchResult 统一提供
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromUrl(request.uriString)
            if (mimeType?.startsWith("video/") != true) return null
            return VideoFrameDecoder(sketch, request, dataSource)
        }
    }
}