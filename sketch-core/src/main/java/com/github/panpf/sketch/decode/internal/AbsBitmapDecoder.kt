package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory.Options
import android.graphics.Point
import android.graphics.Rect
import com.github.panpf.sketch.ImageType
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.newDecodeOptionsByQualityParams
import com.github.panpf.sketch.util.calculateInSampleSize
import com.github.panpf.sketch.util.format

abstract class AbsBitmapDecoder(
    protected val sketch: Sketch,
    protected val request: LoadRequest,
    protected val source: DataSource,
) : Decoder {

    protected val bitmapPoolHelper = sketch.bitmapPoolHelper
    protected val logger = sketch.logger

    protected abstract fun readImageInfo(): ImageInfo

    protected abstract fun canDecodeRegion(imageInfo: ImageInfo, imageType: ImageType?): Boolean

    protected abstract fun decodeRegion(
        imageInfo: ImageInfo,
        srcRect: Rect,
        decodeOptions: Options,
    ): Bitmap

    protected abstract fun decode(imageInfo: ImageInfo, decodeOptions: Options): Bitmap

    override suspend fun decodeBitmap(): BitmapDecodeResult {
        val imageInfo = readImageInfo()

        val resize = request.resize
        val imageType = ImageType.valueOfMimeType(imageInfo.mimeType)
        val decodeOptions = request.newDecodeOptionsByQualityParams(imageInfo.mimeType)
        val imageOrientationCorrector =
            ExifOrientationCorrector.fromExifOrientation(imageInfo.exifOrientation)

        val bitmap = if (resize != null && shouldUseRegionDecoder(resize, imageInfo, imageType)) {
            decodeRegionWrapper(imageInfo, resize, decodeOptions, imageOrientationCorrector)
        } else {
            decodeWrapper(imageInfo, decodeOptions, imageOrientationCorrector)
        }

        return BitmapDecodeResult(bitmap, imageInfo, source.from)
    }

    private fun shouldUseRegionDecoder(
        resize: Resize, imageInfo: ImageInfo, imageType: ImageType?
    ): Boolean {
        if (canDecodeRegion(imageInfo, imageType)) {
            val resizeAspectRatio = (resize.width.toFloat() / resize.height.toFloat()).format(1)
            val imageAspectRatio =
                (imageInfo.width.toFloat() / imageInfo.height.toFloat()).format(1)
            return if (resize.mode == Resize.Mode.THUMBNAIL_MODE) {
                val maxAspectRatio = resizeAspectRatio.coerceAtLeast(imageAspectRatio)
                val minAspectRatio = resizeAspectRatio.coerceAtMost(imageAspectRatio)
                maxAspectRatio > minAspectRatio * resize.minAspectRatio
            } else {
                resizeAspectRatio != imageAspectRatio
            }
        }
        return false
    }

    private fun decodeRegionWrapper(
        imageInfo: ImageInfo,
        resize: Resize,
        decodeOptions: Options,
        exifOrientationCorrector: ExifOrientationCorrector?
    ): Bitmap {
        val imageSize = Point(imageInfo.width, imageInfo.height)

//        if (Build.VERSION.SDK_INT <= VERSION_CODES.M && !decodeOptions.inPreferQualityOverSpeed) {
//            decodeOptions.inPreferQualityOverSpeed = true
//        }

        exifOrientationCorrector?.rotateSize(imageSize)

        val resizeMapping = ResizeMapping.calculator(
            imageWidth = imageSize.x,
            imageHeight = imageSize.y,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            scaleType = resize.scaleType,
            exactlySame = false
        )
        val resizeMappingSrcWidth = resizeMapping.srcRect.width()
        val resizeMappingSrcHeight = resizeMapping.srcRect.height()

        val resizeInSampleSize = calculateInSampleSize(
            resizeMappingSrcWidth, resizeMappingSrcHeight, resize.width, resize.height
        )
        decodeOptions.inSampleSize = resizeInSampleSize

        exifOrientationCorrector
            ?.reverseRotateRect(resizeMapping.srcRect, imageSize.x, imageSize.y)

        return decodeRegion(imageInfo, resizeMapping.srcRect, decodeOptions)
    }

    private fun decodeWrapper(
        imageInfo: ImageInfo,
        decodeOptions: Options,
        exifOrientationCorrector: ExifOrientationCorrector?
    ): Bitmap {
        val imageSize = Point(imageInfo.width, imageInfo.height)
        exifOrientationCorrector?.rotateSize(imageSize)

        val maxSizeInSampleSize = request.maxSize?.let {
            calculateInSampleSize(imageSize.x, imageSize.y, it.width, it.height)
        } ?: 1
        val resizeInSampleSize = request.resize?.let {
            calculateInSampleSize(imageSize.x, imageSize.y, it.width, it.height)
        } ?: 1
        decodeOptions.inSampleSize = maxSizeInSampleSize.coerceAtLeast(resizeInSampleSize)
        return decode(imageInfo, decodeOptions)
    }
}