package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.newDecodeConfigByQualityParams
import com.github.panpf.sketch.util.calculateInSampleSize

abstract class AbsBitmapDecoder(
    protected val sketch: Sketch,
    protected val request: LoadRequest,
    protected val dataSource: DataSource,
) : BitmapDecoder {

    protected val bitmapPool = sketch.bitmapPool
    protected val logger = sketch.logger

    protected abstract fun readImageInfo(): ImageInfo

    protected abstract fun canDecodeRegion(imageInfo: ImageInfo): Boolean

    protected abstract fun decodeRegion(
        imageInfo: ImageInfo, srcRect: Rect, decodeConfig: DecodeConfig,
    ): Bitmap

    protected abstract fun decodeFull(imageInfo: ImageInfo, decodeConfig: DecodeConfig): Bitmap

    override suspend fun decode(): BitmapDecodeResult {
        val imageInfo = readImageInfo()

        val resize = request.resize
        val decodeConfig = request.newDecodeConfigByQualityParams(imageInfo.mimeType)

        val resizeTransformed: ResizeTransformed?
        val bitmap = if (
            resize?.shouldUse(imageInfo.width, imageInfo.height) == true
            && canDecodeRegion(imageInfo)
        ) {
            resizeTransformed = ResizeTransformed(resize)
            decodeRegionWrapper(imageInfo, decodeConfig, resize)
        } else {
            resizeTransformed = null
            decodeFullWrapper(imageInfo, decodeConfig)
        }

        return BitmapDecodeResult.Builder(bitmap, imageInfo, dataSource.from).apply {
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
        resize: Resize,
    ): Bitmap {
        // todo support LoadRequest.disabledCorrectExifOrientation
        val exifOrientationCorrector =
            newExifOrientationCorrectorWithExifOrientation(imageInfo.exifOrientation)
        val imageSize = Point(imageInfo.width, imageInfo.height)

//        if (Build.VERSION.SDK_INT <= VERSION_CODES.M && !decodeOptions.inPreferQualityOverSpeed) {
//            decodeConfig.inPreferQualityOverSpeed = true
//        }

        exifOrientationCorrector?.rotateSize(imageSize)

        val resizeMapping = ResizeMapping.calculator(
            imageWidth = imageSize.x,
            imageHeight = imageSize.y,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            resizeScale = resize.scale,
            exactlySize = resize.precision == Resize.Precision.EXACTLY
        )
        val resizeMappingSrcWidth = resizeMapping.srcRect.width()
        val resizeMappingSrcHeight = resizeMapping.srcRect.height()

        val resizeInSampleSize = calculateInSampleSize(
            resizeMappingSrcWidth, resizeMappingSrcHeight, resize.width, resize.height
        )
        decodeConfig.inSampleSize = resizeInSampleSize

        exifOrientationCorrector
            ?.reverseRotateRect(resizeMapping.srcRect, imageSize.x, imageSize.y)

        return decodeRegion(imageInfo, resizeMapping.srcRect, decodeConfig)

//        val resizeSize = Size(resize.width, resize.height)
//        val rotatedResizeSize = exifOrientationCorrector?.reverseRotateSize(resizeSize) ?: resizeSize
//        val resizeMapping = ResizeMapping.calculator(
//            imageWidth = imageInfo.width,
//            imageHeight = imageInfo.height,
//            resizeWidth = rotatedResizeSize.width,
//            resizeHeight = rotatedResizeSize.height,
//            resizeScale = resize.scale,
//            exactlySize = resize.precision == Resize.Precision.EXACTLY
//        )
//
//        decodeConfig.inSampleSize = calculateInSampleSize(
//            resizeMapping.srcRect.width(),
//            resizeMapping.srcRect.height(),
//            resize.width,
//            resize.height
//        )
//
//        return decodeRegion(imageInfo, resizeMapping.srcRect, decodeConfig)
    }

    private fun decodeFullWrapper(
        imageInfo: ImageInfo,
        decodeConfig: DecodeConfig,
    ): Bitmap {
        // todo support LoadRequest.disabledCorrectExifOrientation
        val exifOrientationCorrector =
            newExifOrientationCorrectorWithExifOrientation(imageInfo.exifOrientation)
        val imageSize = Point(imageInfo.width, imageInfo.height)
        exifOrientationCorrector?.rotateSize(imageSize)

        val maxSizeInSampleSize = request.maxSize?.let {
            calculateInSampleSize(imageSize.x, imageSize.y, it.width, it.height)
        } ?: 1
        val resizeInSampleSize = request.resize?.let {
            calculateInSampleSize(imageSize.x, imageSize.y, it.width, it.height)
        } ?: 1
        decodeConfig.inSampleSize = maxSizeInSampleSize.coerceAtLeast(resizeInSampleSize)
        return decodeFull(imageInfo, decodeConfig)
    }
}