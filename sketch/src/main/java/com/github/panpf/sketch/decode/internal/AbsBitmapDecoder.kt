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

    protected abstract fun canDecodeRegion(mimeType: String): Boolean

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
        val addedResize = resize?.let {
            exifOrientationHelper.addToResize(it, imageInfo.width, imageInfo.height)
        }
        val decodeConfig = request.newDecodeConfigByQualityParams(imageInfo.mimeType)
        val resizeTransformed: ResizeTransformed?
        val bitmap = if (
            addedResize?.shouldUse(imageInfo.width, imageInfo.height) == true
            && canDecodeRegion(imageInfo.mimeType)
        ) {
            resizeTransformed = ResizeTransformed(resize)
            decodeRegionWrapper(imageInfo, decodeConfig, addedResize)
        } else {
            resizeTransformed = null
            val addedMaxSize = request.maxSize?.let { exifOrientationHelper.addToSize(it) }
            decodeFullWrapper(imageInfo, decodeConfig, addedMaxSize, addedResize)
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
        addedResize: Resize,
    ): Bitmap {
        val resizeMapping = ResizeMapping.calculator(
            imageWidth = imageInfo.width,
            imageHeight = imageInfo.height,
            resizeWidth = addedResize.width,
            resizeHeight = addedResize.height,
            resizeScale = addedResize.scale,
            exactlySize = addedResize.precision == Resize.Precision.EXACTLY
        )

        decodeConfig.inSampleSize = calculateInSampleSize(
            resizeMapping.srcRect.width(),
            resizeMapping.srcRect.height(),
            addedResize.width,
            addedResize.height
        )

        return decodeRegion(imageInfo, resizeMapping.srcRect, decodeConfig)
    }

    private fun decodeFullWrapper(
        imageInfo: ImageInfo,
        decodeConfig: DecodeConfig,
        addedMaxSize: Size?,
        addedResize: Resize?,
    ): Bitmap {
        val maxSizeInSampleSize = addedMaxSize?.let {
            calculateInSampleSize(imageInfo.width, imageInfo.height, it.width, it.height)
        } ?: 1

        val resizeInSampleSize = addedResize?.let {
            calculateInSampleSize(imageInfo.width, imageInfo.height, it.width, it.height)
        } ?: 1

        decodeConfig.inSampleSize = maxSizeInSampleSize.coerceAtLeast(resizeInSampleSize)
        return decodeFull(imageInfo, decodeConfig)
    }
}