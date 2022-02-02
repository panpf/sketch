package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.newDecodeConfigByQualityParams
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateInSampleSize

abstract class AbsBitmapDecoder(
    protected val sketch: Sketch,
    protected val request: LoadRequest,
    protected val dataSource: DataSource,
) : BitmapDecoder {

    protected val bitmapPool = sketch.bitmapPool
    protected val logger = sketch.logger

    protected abstract fun readImageInfo(): ImageInfo

    protected abstract fun readExifOrientation(imageInfo: ImageInfo): Int

    protected abstract fun canDecodeRegion(imageInfo: ImageInfo): Boolean

    protected abstract fun decodeRegion(
        imageInfo: ImageInfo, srcRect: Rect, decodeConfig: DecodeConfig,
    ): Bitmap

    protected abstract fun decodeFull(imageInfo: ImageInfo, decodeConfig: DecodeConfig): Bitmap

    private fun readExifOrientationWrapper(imageInfo: ImageInfo): Int =
        if (request.ignoreExifOrientation != true) {
            readExifOrientation(imageInfo)
        } else {
            ExifInterface.ORIENTATION_UNDEFINED
        }

    override suspend fun decode(): BitmapDecodeResult {
        val imageInfo = readImageInfo()
        val exifOrientation = readExifOrientationWrapper(imageInfo)
        val exifOrientationHelper = ExifOrientationHelper(exifOrientation)

        val resize = request.resize
        val decodeConfig = request.newDecodeConfigByQualityParams(imageInfo.mimeType)

        val resizeTransformed: ResizeTransformed?
        val bitmap = if (
            resize?.shouldUse(imageInfo.width, imageInfo.height) == true
            && canDecodeRegion(imageInfo)
        ) {
            resizeTransformed = ResizeTransformed(resize)
            decodeRegionWrapper(imageInfo, decodeConfig, exifOrientationHelper, resize)
        } else {
            resizeTransformed = null
            decodeFullWrapper(imageInfo, decodeConfig, exifOrientationHelper)
        }

        return BitmapDecodeResult.Builder(bitmap, imageInfo, exifOrientation, dataSource.from)
            .apply {
                resizeTransformed?.let {
                    addTransformed(it)
                }
                val inSampleSize = decodeConfig.inSampleSize
                if (inSampleSize != null && inSampleSize > 1) {
                    addTransformed(InSampledTransformed(inSampleSize))
                }
            }.build()
    }

    private fun decodeRegionWrapper(
        imageInfo: ImageInfo,
        decodeConfig: DecodeConfig,
        exifOrientationHelper: ExifOrientationHelper,
        resize: Resize,
    ): Bitmap {
        val resizeSize = Size(resize.width, resize.height)
        val rotatedResizeSize = exifOrientationHelper.reverseRotateSize(resizeSize)
        val resizeMapping = ResizeMapping.calculator(
            imageWidth = imageInfo.width,
            imageHeight = imageInfo.height,
            resizeWidth = rotatedResizeSize.width,
            resizeHeight = rotatedResizeSize.height,
            resizeScale = resize.scale,
            exactlySize = resize.precision == Resize.Precision.EXACTLY
        )

        decodeConfig.inSampleSize = calculateInSampleSize(
            resizeMapping.srcRect.width(),
            resizeMapping.srcRect.height(),
            resize.width,
            resize.height
        )

        return decodeRegion(imageInfo, resizeMapping.srcRect, decodeConfig)
    }

    private fun decodeFullWrapper(
        imageInfo: ImageInfo,
        decodeConfig: DecodeConfig,
        exifOrientationHelper: ExifOrientationHelper,
    ): Bitmap {
        val maxSizeInSampleSize = request.maxSize?.let {
            val maxSize = Size(it.width, it.height)
            val rotatedMaxSize = exifOrientationHelper.reverseRotateSize(maxSize)
            calculateInSampleSize(
                imageInfo.width, imageInfo.height, rotatedMaxSize.width, rotatedMaxSize.height
            )
        } ?: 1

        val resizeInSampleSize = request.resize?.let {
            val resizeSize = Size(it.width, it.height)
            val rotatedResizeSize = exifOrientationHelper.reverseRotateSize(resizeSize)
            calculateInSampleSize(
                imageInfo.width, imageInfo.height, rotatedResizeSize.width, rotatedResizeSize.height
            )
        } ?: 1

        decodeConfig.inSampleSize = maxSizeInSampleSize.coerceAtLeast(resizeInSampleSize)
        return decodeFull(imageInfo, decodeConfig)
    }
}