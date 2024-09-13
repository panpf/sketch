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

package com.github.panpf.sketch.util

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.resize.Scale

/**
 * Read an integer pixel array in the format ARGB_8888
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testReadIntPixels
 */
actual fun BitmapImage.readIntPixels(x: Int, y: Int, width: Int, height: Int): IntArray {
    return bitmap.readIntPixels(x, y, width, height)
}

/**
 * Zoom image
 */
actual fun BitmapImage.scale(scaleFactor: Float): BitmapImage {
    val inputBitmap = bitmap
    val outBitmap = inputBitmap.scaled(scaleFactor)
    return outBitmap.asImage()
}

/**
 * Create a new image based on the mapping relationship represented by [ResizeMapping]
 */
actual fun BitmapImage.mapping(mapping: ResizeMapping): BitmapImage {
    val inputBitmap = bitmap
    val outBitmap = inputBitmap.mapping(mapping)
    return outBitmap.asImage()
}

/**
 * Apply a blur effect to the image
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testBlur
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testBlur2
 */
actual fun BitmapImage.blur(
    radius: Int,
    hasAlphaBitmapBgColor: Int?,
    maskColor: Int?,
    firstReuseSelf: Boolean
): BitmapImage {
    val image = this
    val inputBitmap = image.bitmap
    // Transparent pixels cannot be blurred
    val compatAlphaBitmap = if (hasAlphaBitmapBgColor != null && inputBitmap.hasAlphaPixels()) {
        inputBitmap.backgrounded(hasAlphaBitmapBgColor)
    } else if (!firstReuseSelf) {
        inputBitmap.copied()
    } else {
        inputBitmap.getMutableCopy()
    }
    val blurBitmap = compatAlphaBitmap.apply { blur(radius) }
    val maskBitmap = blurBitmap.apply { if (maskColor != null) mask(maskColor) }
    return maskBitmap.asImage()
}

/**
 * Crop the image into a circle
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testCircleCrop
 */
actual fun BitmapImage.circleCrop(scale: Scale): BitmapImage {
    val image = this
    val inputBitmap = image.bitmap
    val outBitmap: SkiaBitmap = inputBitmap.circleCropped(scale)
    return outBitmap.asImage()
}

/**
 * Apply a mask effect to the image
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testMask
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testMask2
 */
actual fun BitmapImage.mask(maskColor: Int, firstReuseSelf: Boolean): BitmapImage {
    val image = this
    val inputBitmap = if (!firstReuseSelf) {
        image.bitmap.copied()
    } else {
        image.bitmap.getMutableCopy()
    }
    val outBitmap = inputBitmap.apply { mask(maskColor) }
    return outBitmap.asImage()
}

/**
 * Rotate the image
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testRotate
 */
actual fun BitmapImage.rotate(degrees: Int): BitmapImage {
    val image = this
    val inputBitmap = image.bitmap
    val outBitmap: SkiaBitmap = inputBitmap.rotated(degrees)
    return outBitmap.asImage()
}

/**
 * Apply rounded corners to the image
 *
 * @param radiusArray Array of 8 values, 4 pairs of [X,Y] radii. The corners are ordered top-left, top-right, bottom-right, bottom-left
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testRoundedCorners
 */
actual fun BitmapImage.roundedCorners(radiusArray: FloatArray): BitmapImage {
    val image = this
    val inputBitmap = image.bitmap
    val outBitmap: SkiaBitmap = inputBitmap.roundedCornered(radiusArray)
    return outBitmap.asImage()
}