/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.internal.calculateResizeMapping
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.requiredWorkThread


/* ************************************** sampling ********************************************** */

/**
 * Get the maximum Bitmap size allowed by the runtime platform
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testGetMaxBitmapSize
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testGetMaxBitmapSize
 */
expect fun getMaxBitmapSize(): Size?

/**
 * Get the maximum Bitmap size allowed by the runtime platform
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testGetMaxBitmapSize
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testGetMaxBitmapSize
 */
fun getMaxBitmapSize(targetSize: Size): Size {
    return getMaxBitmapSize() ?: Size(targetSize.width * 2, targetSize.height * 2)
}

/**
 * Calculate the size of the sampled Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testCalculateSampledBitmapSize
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testCalculateSampledBitmapSize
 */
expect fun calculateSampledBitmapSize(
    imageSize: Size,
    sampleSize: Int,
    mimeType: String? = null
): Size

/**
 * Calculate the size of the sampled Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testCalculateSampledBitmapSizeForRegion
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testCalculateSampledBitmapSizeForRegion
 */
expect fun calculateSampledBitmapSizeForRegion(
    regionSize: Size,
    sampleSize: Int,
    mimeType: String? = null,
    imageSize: Size? = null
): Size


/**
 * Calculate the sample size
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testCalculateSampleSize
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testCalculateSampleSize
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
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testCalculateSampleSize2
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testCalculateSampleSize2
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
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testCalculateSampleSizeForRegion
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testCalculateSampleSizeForRegion
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
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testCalculateSampleSizeForRegion2
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testCalculateSampleSizeForRegion2
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

/**
 * Check if the sampled Bitmap size meets the requirements
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.DecodesTest.testCheckSampledBitmapSize
 */
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

/**
 * Decode the full image
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testRealDecode
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testRealDecode
 */
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
    val imageSize = Size(imageInfo.width, imageInfo.height)
    val resize = requestContext.computeResize(imageInfo.size)
    val transformeds = mutableListOf<String>()
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
            transformeds.add(createInSampledTransformed(sampleSize))
        }
        transformeds.add(createSubsamplingTransformed(resizeMapping.srcRect))
        decodeRegion(resizeMapping.srcRect, sampleSize)
    } else {
        val sampleSize = calculateSampleSize(
            imageSize = imageSize,
            targetSize = resize.size,
            smallerSizeMode = resize.precision.isSmallerSizeMode(),
            mimeType = imageInfo.mimeType
        )
        if (sampleSize > 1) {
            transformeds.add(0, createInSampledTransformed(sampleSize))
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
        resize = resize,
        transformeds = transformeds.takeIf { it.isNotEmpty() }?.toList(),
        extras = null,
    )
}

/**
 * Resize image according to [Resize]
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testAppliedResize
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testAppliedResize
 */
@WorkerThread
fun DecodeResult.appliedResize(requestContext: RequestContext): DecodeResult {
    requiredWorkThread()
    val imageTransformer = image.transformer() ?: return this
    val size = requestContext.size!!
    if (size.isEmpty) {
        return this
    }
    val resize: Resize = requestContext.computeResize(imageInfo.size)
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

/**
 * @see com.github.panpf.sketch.core.common.test.decode.internal.DecodesTest.testIsSmallerSizeMode
 */
fun Precision.isSmallerSizeMode(): Boolean {
    return this == Precision.SMALLER_SIZE
}