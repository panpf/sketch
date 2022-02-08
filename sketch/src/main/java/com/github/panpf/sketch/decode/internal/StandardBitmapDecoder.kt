package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.resize.Resize
import com.github.panpf.sketch.decode.resize.Precision
import com.github.panpf.sketch.decode.resize.ResizeTransformed
import com.github.panpf.sketch.decode.resize.ResizeMapping
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.newDecodeConfigByQualityParams
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateInSampleSize

abstract class StandardBitmapDecoder(
    sketch: Sketch,
    request: LoadRequest,
    private val dataFrom: DataFrom,
) : AbsBitmapDecoder(sketch, request) {

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

    override suspend fun executeDecode(): BitmapDecodeResult {
        val imageInfo = readImageInfo()
        val exifOrientation = readExifOrientationWrapper(imageInfo)
        val exifOrientationHelper = ExifOrientationHelper(exifOrientation)

        val resize = request.resize
        val applySize = exifOrientationHelper.applyToSize(Size(imageInfo.width, imageInfo.height))
        val addedResize = resize?.let {
            exifOrientationHelper.addToResize(it, applySize)
        }
        val decodeConfig = request.newDecodeConfigByQualityParams(imageInfo.mimeType)
        val resizeTransformed: ResizeTransformed?
        val bitmap = if (
            addedResize?.shouldCrop(imageInfo.width, imageInfo.height) == true
            && canDecodeRegion(imageInfo.mimeType)
        ) {
            resizeTransformed = ResizeTransformed(resize)
            decodeRegionWrapper(imageInfo, decodeConfig, addedResize)
        } else {
            resizeTransformed = null
            decodeFullWrapper(imageInfo, decodeConfig, addedResize)
        }

        return BitmapDecodeResult.Builder(bitmap, imageInfo, exifOrientation, dataFrom)
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
        val precision = addedResize.precision(imageInfo.width, imageInfo.height)
        val resizeMapping = ResizeMapping.calculator(
            imageWidth = imageInfo.width,
            imageHeight = imageInfo.height,
            resizeWidth = addedResize.width,
            resizeHeight = addedResize.height,
            resizeScale = addedResize.scale,
            exactlySize = precision == Precision.EXACTLY
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
        addedResize: Resize?,
    ): Bitmap {
        decodeConfig.inSampleSize = addedResize?.let {
            calculateInSampleSize(imageInfo.width, imageInfo.height, it.width, it.height)
        } ?: 1
        return decodeFull(imageInfo, decodeConfig)
    }
}