package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.resize.Precision
import com.github.panpf.sketch.decode.resize.Resize
import com.github.panpf.sketch.decode.resize.ResizeTransformed
import com.github.panpf.sketch.decode.resize.calculateResizeMapping
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.newDecodeConfigByQualityParams
import com.github.panpf.sketch.util.Size

abstract class StandardBitmapDecoder(
    sketch: Sketch,
    request: LoadRequest,
    private val dataFrom: DataFrom,
) : AbsBitmapDecoder(sketch, request) {

    protected fun realDecode(
        imageInfo: ImageInfo,
        exifOrientation: Int,
        decodeFull: (decodeConfig: DecodeConfig) -> Bitmap,
        decodeRegion: ((srcRect: Rect, decodeConfig: DecodeConfig) -> Bitmap)?
    ): BitmapDecodeResult {
        val exifOrientationHelper = ExifOrientationHelper(
            if (request.ignoreExifOrientation != true) {
                exifOrientation
            } else {
                ExifInterface.ORIENTATION_UNDEFINED
            }
        )

        val resize = request.resize
        val applySize = exifOrientationHelper.applyToSize(Size(imageInfo.width, imageInfo.height))
        val addedResize = resize?.let {
            exifOrientationHelper.addToResize(it, applySize)
        }
        val decodeConfig = request.newDecodeConfigByQualityParams(imageInfo.mimeType)
        val resizeTransformed: ResizeTransformed?
        val bitmap = if (
            addedResize?.shouldClip(imageInfo.width, imageInfo.height) == true
            && decodeRegion != null
        ) {
            resizeTransformed = ResizeTransformed(resize)
            decodeRegionWrapper(imageInfo, decodeConfig, addedResize, decodeRegion)
        } else {
            resizeTransformed = null
            decodeFullWrapper(imageInfo, decodeConfig, addedResize, decodeFull)
        }

        return BitmapDecodeResult.Builder(bitmap, imageInfo, exifOrientation, dataFrom).apply {
            decodeConfig.inSampleSize?.takeIf { it > 1 }?.let {
                addTransformed(InSampledTransformed(it))
            }
            resizeTransformed?.let {
                addTransformed(it)
            }
        }.build()
    }

    private fun decodeRegionWrapper(
        imageInfo: ImageInfo,
        decodeConfig: DecodeConfig,
        addedResize: Resize,
        decodeRegion: (srcRect: Rect, decodeConfig: DecodeConfig) -> Bitmap
    ): Bitmap {
        val precision = addedResize.precision(imageInfo.width, imageInfo.height)
        val resizeMapping = calculateResizeMapping(
            imageWidth = imageInfo.width,
            imageHeight = imageInfo.height,
            resizeWidth = addedResize.width,
            resizeHeight = addedResize.height,
            resizeScale = addedResize.scale,
            exactlySize = precision == Precision.EXACTLY
        )

        decodeConfig.inSampleSize = limitedOpenGLTextureMaxSize(
            resizeMapping.srcRect.width(),
            resizeMapping.srcRect.height(),
            calculateInSampleSize(
                resizeMapping.srcRect.width(),
                resizeMapping.srcRect.height(),
                addedResize.width,
                addedResize.height
            )
        )

        return decodeRegion(resizeMapping.srcRect, decodeConfig)
    }

    private fun decodeFullWrapper(
        imageInfo: ImageInfo,
        decodeConfig: DecodeConfig,
        addedResize: Resize?,
        decodeFull: (decodeConfig: DecodeConfig) -> Bitmap
    ): Bitmap {
        // In cases where clipping is required, the clipping region is used to calculate inSampleSize, this will give you a clearer picture
        if (addedResize?.shouldClip(imageInfo.width, imageInfo.height) == true) {
            val precision = addedResize.precision(imageInfo.width, imageInfo.height)
            val resizeMapping = calculateResizeMapping(
                imageWidth = imageInfo.width,
                imageHeight = imageInfo.height,
                resizeWidth = addedResize.width,
                resizeHeight = addedResize.height,
                resizeScale = addedResize.scale,
                exactlySize = precision == Precision.EXACTLY
            )
            decodeConfig.inSampleSize = limitedOpenGLTextureMaxSize(
                resizeMapping.srcRect.width(),
                resizeMapping.srcRect.height(),
                calculateInSampleSize(
                    resizeMapping.srcRect.width(),
                    resizeMapping.srcRect.height(),
                    addedResize.width,
                    addedResize.height
                )
            )
        } else {
            decodeConfig.inSampleSize = limitedOpenGLTextureMaxSize(
                imageInfo.width,
                imageInfo.height,
                addedResize?.let {
                    calculateInSampleSize(imageInfo.width, imageInfo.height, it.width, it.height)
                } ?: 1
            )
        }
        return decodeFull(decodeConfig)
    }
}