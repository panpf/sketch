package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.internal.calculateResizeMapping
import com.github.panpf.sketch.size
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.requiredWorkThread


/* ************************************** sampling ********************************************** */

expect fun getMaxBitmapSize(targetSize: Size): Size

/**
 * Calculate the size of the sampled Bitmap, support for BitmapFactory or ImageDecoder
 */
expect fun calculateSampledBitmapSize(
    imageSize: Size,
    sampleSize: Int,
    mimeType: String? = null
): Size

/**
 * Calculate the size of the sampled Bitmap, support for BitmapRegionDecoder
 */
expect fun calculateSampledBitmapSizeForRegion(
    regionSize: Size,
    sampleSize: Int,
    mimeType: String? = null,
    imageSize: Size? = null
): Size


/**
 * Calculate the sample size, support for BitmapFactory or ImageDecoder
 */
fun calculateSampleSize(
    imageSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean,
    mimeType: String? = null,
): Int {
    if (imageSize.isEmpty) {
        return 1
    }
    var sampleSize = 1
    var accepted = false
    val maxBitmapSize = getMaxBitmapSize(targetSize)
    while (!accepted) {
        val sampledBitmapSize = calculateSampledBitmapSize(
            imageSize = imageSize,
            sampleSize = sampleSize,
            mimeType = mimeType
        )
        accepted = checkSampledBitmapSize(
            sampledBitmapSize = sampledBitmapSize,
            targetSize = targetSize,
            smallerSizeMode = smallerSizeMode,
            maxBitmapSize = maxBitmapSize,
        )
        if (!accepted) {
            sampleSize *= 2
        }
    }
    return sampleSize
}

/**
 * Calculate the sample size, support for BitmapFactory or ImageDecoder
 */
fun calculateSampleSize(
    imageSize: Size,
    targetSize: Size,
    mimeType: String? = null
): Int = calculateSampleSize(
    imageSize = imageSize,
    targetSize = targetSize,
    smallerSizeMode = false,
    mimeType = mimeType
)

/**
 * Calculate the sample size, support for BitmapRegionDecoder
 */
fun calculateSampleSizeForRegion(
    regionSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean,
    mimeType: String? = null,
    imageSize: Size? = null,
): Int {
    if (regionSize.isEmpty) {
        return 1
    }
    var sampleSize = 1
    var accepted = false
    val maxBitmapSize = getMaxBitmapSize(targetSize)
    while (!accepted) {
        val sampledBitmapSize = calculateSampledBitmapSizeForRegion(
            regionSize = regionSize,
            sampleSize = sampleSize,
            mimeType = mimeType,
            imageSize = imageSize
        )
        accepted = checkSampledBitmapSize(
            sampledBitmapSize = sampledBitmapSize,
            targetSize = targetSize,
            smallerSizeMode = smallerSizeMode,
            maxBitmapSize = maxBitmapSize,
        )
        if (!accepted) {
            sampleSize *= 2
        }
    }
    return sampleSize
}

/**
 * Calculate the sample size, support for BitmapRegionDecoder
 */
fun calculateSampleSizeForRegion(
    regionSize: Size,
    targetSize: Size,
    mimeType: String? = null,
    imageSize: Size? = null
): Int = calculateSampleSizeForRegion(
    regionSize = regionSize,
    targetSize = targetSize,
    smallerSizeMode = false,
    mimeType = mimeType,
    imageSize = imageSize
)

fun checkSampledBitmapSize(
    sampledBitmapSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean,
    maxBitmapSize: Size? = null
): Boolean {
    var accept = if (targetSize.isEmpty || smallerSizeMode) {
        sampledBitmapSize.checkSideLimit(targetSize)
    } else {
        sampledBitmapSize.checkAreaLimit(targetSize)
    }
    if (accept && maxBitmapSize != null) {
        accept = sampledBitmapSize.checkSideLimit(maxBitmapSize)
    }
    return accept
}

private fun Size.checkSideLimit(limitSize: Size): Boolean {
    return (limitSize.width <= 0 || this.width <= limitSize.width)
            && (limitSize.height <= 0 || this.height <= limitSize.height)
}

private fun Size.checkAreaLimit(limitSize: Size): Boolean {
    return (this.width * this.height) <= (limitSize.width * limitSize.height)
}


/* **************************************** decode ********************************************* */

// TODO Merge to HelperDecoder
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
    val imageSize = Size(imageInfo.width, imageInfo.height)
    val precision = request.precisionDecider.get(imageSize = imageSize, targetSize = size)
    val scale = request.scaleDecider.get(imageSize = imageSize, targetSize = size)
    val resize = Resize(
        width = size.width,
        height = size.height,
        precision = precision,
        scale = scale
    )
    val transformedList = mutableListOf<String>()
    val resizeMapping = calculateResizeMapping(
        imageSize = imageInfo.size,
        resizeSize = resize.size,
        precision = resize.precision,
        scale = resize.scale,
    )
    val image = if (
        resize.shouldClip(imageInfo.size)
        && resize.precision != LESS_PIXELS
        && decodeRegion != null
        && resizeMapping != null
    ) {
        val sampleSize = calculateSampleSizeForRegion(
            regionSize = Size(resizeMapping.srcRect.width(), resizeMapping.srcRect.height()),
            targetSize = Size(resizeMapping.destRect.width(), resizeMapping.destRect.height()),
            smallerSizeMode = resize.precision.isSmallerSizeMode(),
            mimeType = imageInfo.mimeType,
            imageSize = imageSize
        )
        if (sampleSize > 1) {
            transformedList.add(createInSampledTransformed(sampleSize))
        }
        transformedList.add(createSubsamplingTransformed(resizeMapping.srcRect))
        decodeRegion(resizeMapping.srcRect, sampleSize)
    } else {
        val sampleSize = calculateSampleSize(
            imageSize = imageSize,
            targetSize = resize.size,
            smallerSizeMode = resize.precision.isSmallerSizeMode(),
            mimeType = imageInfo.mimeType
        )
        if (sampleSize > 1) {
            transformedList.add(0, createInSampledTransformed(sampleSize))
        }
        decodeFull(sampleSize)
    }
    if (image.width <= 0 || image.height <= 0) {
        throw ImageInvalidException("Invalid image size. size=${image.width}x${image.height}")
    }
    return DecodeResult(
        image = image,
        imageInfo = imageInfo,
        dataFrom = dataFrom,
        transformedList = transformedList.takeIf { it.isNotEmpty() }?.toList(),
        extras = null,
    )
}

@WorkerThread
fun DecodeResult.appliedResize(requestContext: RequestContext): DecodeResult {
    requiredWorkThread()
    val imageTransformer = image.transformer() ?: return this
    val request = requestContext.request
    val size = requestContext.size!!
    if (size.isEmpty) {
        return this
    }
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
            imageSize = image.size,
            targetSize = resize.size,
            smallerSizeMode = resize.precision.isSmallerSizeMode()
        )
        if (sampleSize != 1) {
            imageTransformer.scale(image = image, scaleFactor = 1 / sampleSize.toFloat())
        } else {
            null
        }
    } else if (resize.shouldClip(image.size)) {
        val mapping = calculateResizeMapping(
            imageSize = image.size,
            resizeSize = resize.size,
            precision = resize.precision,
            scale = resize.scale,
        )
        if (mapping != null) {
            imageTransformer.mapping(image, mapping)
        } else {
            null
        }
    } else {
        null
    }
    return if (newImage != null) {
        newResult(image = newImage) {
            addTransformed(createResizeTransformed(resize))
        }
    } else {
        this
    }
}

fun Precision.isSmallerSizeMode(): Boolean {
    return this == Precision.SMALLER_SIZE
}