@file:Suppress("UnnecessaryVariable", "FoldInitializerAndIfToElvis")

package com.github.panpf.sketch.decode.internal

import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import wseemann.media.FFmpegMediaMetadataRetriever
import kotlin.math.roundToInt

class FFmpegVideoFrameDecodeHelper(
    val request: ImageRequest,
    val dataSource: DataSource,
    private val mimeType: String,
) : DecodeHelper {

    override val imageInfo: ImageInfo by lazy { readInfo() }
    override val supportRegion: Boolean = false

    private val mediaMetadataRetriever by lazy {
        FFmpegMediaMetadataRetriever().apply {
            if (dataSource is ContentDataSource) {
                setDataSource(request.context, dataSource.contentUri)
            } else {
                dataSource.getFileOrNull()?.let { setDataSource(it.toFile().path) }
                    ?: throw Exception("Unsupported DataSource: ${dataSource::class.qualifiedName}")
            }
        }
    }
    private val exifOrientation: Int by lazy { readExifOrientation() }
    private val exifOrientationHelper by lazy { AndroidExifOrientationHelper(exifOrientation) }

    override fun decode(sampleSize: Int): Image {
        val config = request.newDecodeConfigByQualityParams(imageInfo.mimeType).apply {
            inSampleSize = sampleSize
        }
        val option = request.videoFrameOption ?: FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC
        val frameMicros = request.videoFrameMicros
            ?: request.videoFramePercent?.let { percentDuration ->
                val duration = mediaMetadataRetriever
                    .extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull() ?: 0L
                (duration * percentDuration * 1000).toLong()
            }
            ?: 0L

        val inSampleSize = config.inSampleSize?.toFloat()
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
        if (bitmap == null) {
            throw DecodeException(
                "Failed to getScaledFrameAtTime. " +
                        "frameMicros=${frameMicros}, " +
                        "option=${optionToName(option)}, " +
                        "dst=${dstWidth}x${dstHeight}, " +
                        "image=${imageInfo.width}x${imageInfo.height}."
            )
        }
        val image = bitmap.asSketchImage()
        val correctedImage = exifOrientationHelper.applyToImage(image) ?: image
        return correctedImage
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): Image {
        throw UnsupportedOperationException("Unsupported region decode")
    }

    private fun readInfo(): ImageInfo {
        val srcWidth = mediaMetadataRetriever
            .extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()
            ?: 0
        val srcHeight = mediaMetadataRetriever
            .extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()
            ?: 0
        if (srcWidth <= 1 || srcHeight <= 1) {
            val message = "Invalid video file. size=${srcWidth}x${srcHeight}"
            throw ImageInvalidException(message)
        }
        val imageSize = Size(width = srcWidth, height = srcHeight)
        val correctedImageSize = exifOrientationHelper.applyToSize(imageSize)
        return ImageInfo(size = correctedImageSize, mimeType = mimeType)
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
        return "FFmpegVideoFrameDecodeHelper(uri=${request.uriString}, imageInfo=$imageInfo, exifOrientation=$exifOrientation, supportRegion=$supportRegion)"
    }

    override fun close() {
        mediaMetadataRetriever.release()
    }
}