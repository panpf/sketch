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

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.isSmallerSizeMode
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.mapping
import com.github.panpf.sketch.util.scale

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
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testGetMaxBitmapSizeOr
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testGetMaxBitmapSizeOr
 */
fun getMaxBitmapSizeOr(targetSize: Size): Size {
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
    val maxBitmapSize = getMaxBitmapSizeOr(targetSize)
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
    val maxBitmapSize = getMaxBitmapSizeOr(targetSize)
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

private fun Size.checkSideLimit(targetSize: Size): Boolean {
    // targetSize.width or targetSize.height less than or equal to 0 means unlimited
    return (targetSize.width <= 0 || this.width <= targetSize.width)
            && (targetSize.height <= 0 || this.height <= targetSize.height)
}

private fun Size.checkAreaLimit(targetSize: Size): Boolean {
    return (this.width * this.height) <= (targetSize.width * targetSize.height)
}

/**
 * Check if the image is valid
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.DecodesTest.testCheckImageSize
 */
fun checkImageSize(imageSize: Size) {
    if (imageSize.isEmpty) {
        throw ImageInvalidException("Invalid image. width or height is 0. $imageSize")
    }
}

/**
 * Check if the image is valid
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.DecodesTest.testCheckImageInfo
 */
fun checkImageInfo(imageInfo: ImageInfo) {
    checkImageSize(imageInfo.size)
}

/**
 * Decode image width, height, MIME type. Ignore the Exif orientation
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testReadImageInfoWithIgnoreExifOrientation
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testReadImageInfoWithIgnoreExifOrientation
 */
expect fun DataSource.readImageInfoWithIgnoreExifOrientation(): ImageInfo

/**
 * Decode image width, height, MIME type. Should be able to parse the exif orientation
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testReadImageInfo
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testReadImageInfo
 */
expect fun DataSource.readImageInfo(): ImageInfo

/**
 * Resize image according to [Resize]
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.DecodesAndroidTest.testResize
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.DecodesNonAndroidTest.testResize
 */
fun DecodeResult.resize(resize: Resize): DecodeResult {
    if (resize.size.isEmpty) return this
    if (image !is BitmapImage) return this
    val inputBitmap = image.bitmap
    val newBitmap = if (resize.precision == LESS_PIXELS) {
        val sampleSize = calculateSampleSize(
            imageSize = image.size,
            targetSize = resize.size,
            smallerSizeMode = resize.precision.isSmallerSizeMode()
        )
        if (sampleSize != 1) {
            inputBitmap.scale(scaleFactor = 1 / sampleSize.toFloat())
        } else {
            null
        }
    } else if (resize.shouldClip(image.size)) {
        val mapping = resize.calculateMapping(imageSize = image.size)
        inputBitmap.mapping(mapping)
    } else {
        null
    }
    return if (newBitmap != null) {
        newResult(image = newBitmap.asImage()) {
            addTransformed(createResizeTransformed(resize))
        }
    } else {
        this
    }
}