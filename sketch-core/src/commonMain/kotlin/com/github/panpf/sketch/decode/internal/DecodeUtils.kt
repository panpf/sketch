package com.github.panpf.sketch.decode.internal

import androidx.annotation.Px
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.internal.calculateResizeMapping
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.requiredWorkThread
import kotlin.math.max
import kotlin.math.min

@WorkerThread
fun realDecode(
    requestContext: RequestContext,
    dataFrom: DataFrom,
    imageInfo: ImageInfo,
    decodeFull: (sampleSize: Int) -> Image,
    decodeRegion: ((srcRect: Rect, sampleSize: Int) -> Image)?
): DecodeResult {
    requiredWorkThread()
    val request = requestContext.request
    val size = requestContext.size!!
    val exifOrientationHelper = ExifOrientationHelper(imageInfo.exifOrientation)
    val imageSize = Size(imageInfo.width, imageInfo.height)
    val appliedImageSize = exifOrientationHelper?.applyToSize(imageSize) ?: imageSize
    val resize = Resize(
        width = size.width,
        height = size.height,
        precision = request.precisionDecider.get(
            imageSize = appliedImageSize,
            targetSize = size,
        ),
        scale = request.scaleDecider.get(
            imageSize = appliedImageSize,
            targetSize = size,
        )
    )
    val addedResize = exifOrientationHelper?.addToResize(resize, appliedImageSize) ?: resize
    val transformedList = mutableListOf<String>()
    val bitmap = if (
        addedResize.shouldClip(imageInfo.width, imageInfo.height)
        && addedResize.precision != LESS_PIXELS
        && decodeRegion != null
    ) {
        val resizeMapping = calculateResizeMapping(
            imageWidth = imageInfo.width,
            imageHeight = imageInfo.height,
            resizeWidth = addedResize.width,
            resizeHeight = addedResize.height,
            precision = addedResize.precision,
            scale = addedResize.scale,
        )
        val sampleSize = calculateSampleSizeForRegion(
            regionSize = Size(resizeMapping.srcRect.width(), resizeMapping.srcRect.height()),
            targetSize = Size(resizeMapping.destRect.width(), resizeMapping.destRect.height()),
            smallerSizeMode = addedResize.precision.isSmallerSizeMode(),
            mimeType = imageInfo.mimeType,
            imageSize = imageSize
        )
        if (sampleSize > 1) {
            transformedList.add(createInSampledTransformed(sampleSize))
        }
        transformedList.add(createSubsamplingTransformed(resizeMapping.srcRect))
        decodeRegion(resizeMapping.srcRect, sampleSize)
    } else {
        val sampleSize = run {
            val targetSize = Size(addedResize.width, addedResize.height)
            calculateSampleSize(
                imageSize = imageSize,
                targetSize = targetSize,
                smallerSizeMode = addedResize.precision.isSmallerSizeMode(),
                mimeType = imageInfo.mimeType
            )
        }
        if (sampleSize > 1) {
            transformedList.add(0, createInSampledTransformed(sampleSize))
        }
        decodeFull(sampleSize)
    }
    return DecodeResult(
        image = bitmap,
        imageInfo = imageInfo,
        dataFrom = dataFrom,
        transformedList = transformedList.takeIf { it.isNotEmpty() }?.toList(),
        extras = null,
    )
}

@WorkerThread
fun DecodeResult.appliedExifOrientation(requestContext: RequestContext): DecodeResult {
    requiredWorkThread()
    if (transformedList?.getExifOrientationTransformed() != null
        || imageInfo.exifOrientation == ExifOrientation.UNDEFINED
        || imageInfo.exifOrientation == ExifOrientation.NORMAL
    ) {
        return this
    }
    val exifOrientationHelper = ExifOrientationHelper(imageInfo.exifOrientation) ?: return this
    val newImage = exifOrientationHelper.applyToImage(image) ?: return this
    val newSize = exifOrientationHelper.applyToSize(
        Size(imageInfo.width, imageInfo.height)
    )
    requestContext.sketch.logger.d("appliedExifOrientation") {
        "appliedExifOrientation. successful. ${newImage}. ${imageInfo}. '${requestContext.logKey}'"
    }
    return newResult(
        image = newImage,
        imageInfo = imageInfo.newImageInfo(width = newSize.width, height = newSize.height)
    ) {
        addTransformed(createExifOrientationTransformed(imageInfo.exifOrientation))
    }
}

@WorkerThread
fun DecodeResult.appliedResize(requestContext: RequestContext): DecodeResult {
    requiredWorkThread()
    val imageTransformer = image.transformer() ?: return this
    val request = requestContext.request
    val size = requestContext.size!!
    val resize = Resize(
        width = size.width,
        height = size.height,
        precision = request.precisionDecider.get(
            imageSize = Size(imageInfo.width, imageInfo.height),
            targetSize = size,
        ),
        scale = request.scaleDecider.get(
            imageSize = Size(imageInfo.width, imageInfo.height),
            targetSize = size,
        )
    )
    val newImage = if (resize.precision == LESS_PIXELS) {
        val sampleSize = calculateSampleSize(
            imageSize = Size(image.width, image.height),
            targetSize = Size(resize.width, resize.height),
            smallerSizeMode = resize.precision.isSmallerSizeMode()
        )
        if (sampleSize != 1) {
            imageTransformer.scaled(image = image, scaleFactor = 1 / sampleSize.toFloat())
        } else {
            null
        }
    } else if (resize.shouldClip(image.width, image.height)) {
        val mapping = calculateResizeMapping(
            imageWidth = image.width,
            imageHeight = image.height,
            resizeWidth = resize.width,
            resizeHeight = resize.height,
            precision = resize.precision,
            scale = resize.scale,
        )
        imageTransformer.mapping(image, mapping)
    } else {
        null
    }
    return if (newImage != null) {
        requestContext.sketch.logger.d("appliedResize") {
            "appliedResize. successful. ${newImage}. ${imageInfo}. '${requestContext.logKey}'"
        }
        newResult(image = newImage) {
            addTransformed(createResizeTransformed(resize))
        }
    } else {
        this
    }
}

/**
 * Calculate the sample size, support for BitmapFactory or ImageDecoder
 */
expect fun calculateSampleSize(
    imageSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean,
    mimeType: String? = null
): Int

/**
 * Calculate the sample size, support for BitmapRegionDecoder
 */
expect fun calculateSampleSizeForRegion(
    regionSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean,
    mimeType: String? = null,
    imageSize: Size? = null
): Int

fun computeSizeMultiplier(
    @Px srcWidth: Int,
    @Px srcHeight: Int,
    @Px dstWidth: Int,
    @Px dstHeight: Int,
    fitScale: Boolean
): Double {
    val widthPercent = dstWidth / srcWidth.toDouble()
    val heightPercent = dstHeight / srcHeight.toDouble()
    return if (fitScale) {
        min(widthPercent, heightPercent)
    } else {
        max(widthPercent, heightPercent)
    }
}

fun Precision.isSmallerSizeMode(): Boolean {
    return this == Precision.SMALLER_SIZE
}