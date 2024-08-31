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

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.resize.Scale

/**
 * Apply a blur effect to the image
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testBlur
 */
internal actual fun Image.blur(
    radius: Int,
    hasAlphaBitmapBgColor: Int?,
    maskColor: Int?
): Image {
    val image = this
    require(image is SkiaBitmapImage) {
        "Only SkiaBitmapImage is supported: ${image::class}"
    }
    val inputBitmap = image.bitmap
    // Transparent pixels cannot be blurred
    val compatAlphaBitmap = if (hasAlphaBitmapBgColor != null && inputBitmap.hasAlpha()) {
        inputBitmap.backgrounded(hasAlphaBitmapBgColor)
    } else {
        inputBitmap
    }
    val blurImage = compatAlphaBitmap.apply { blur(radius) }
    val maskImage = blurImage.apply { if (maskColor != null) mask(maskColor) }
    return maskImage.asSketchImage()
}

/**
 * Crop the image into a circle
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testCircleCrop
 */
internal actual fun Image.circleCrop(scale: Scale): Image {
    val image = this
    require(image is SkiaBitmapImage) {
        "Only SkiaBitmapImage is supported: ${image::class}"
    }
    val inputBitmap = image.bitmap
    val outBitmap: SkiaBitmap = inputBitmap.circleCropped(scale)
    return outBitmap.asSketchImage()
}

/**
 * Apply a mask effect to the image
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testMask
 */
internal actual fun Image.mask(maskColor: Int): Image {
    val image = this
    require(image is SkiaBitmapImage) {
        "Only SkiaBitmapImage is supported: ${image::class}"
    }
    val inputBitmap = image.bitmap
    val outBitmap: SkiaBitmap = inputBitmap.apply { mask(maskColor) }
    return outBitmap.asSketchImage()
}

/**
 * Rotate the image
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testRotate
 */
internal actual fun Image.rotate(degrees: Int): Image {
    val image = this
    require(image is SkiaBitmapImage) {
        "Only SkiaBitmapImage is supported: ${image::class}"
    }
    val inputBitmap = image.bitmap
    val outBitmap: SkiaBitmap = inputBitmap.rotated(degrees)
    return outBitmap.asSketchImage()
}

/**
 * Apply rounded corners to the image
 *
 * @param radiusArray Array of 8 values, 4 pairs of [X,Y] radii. The corners are ordered top-left, top-right, bottom-right, bottom-left
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testRoundedCorners
 */
internal actual fun Image.roundedCorners(radiusArray: FloatArray): Image {
    val image = this
    require(image is SkiaBitmapImage) {
        "Only SkiaBitmapImage is supported: ${image::class}"
    }
    val inputBitmap = image.bitmap
    val outBitmap: SkiaBitmap = inputBitmap.roundedCornered(radiusArray)
    return outBitmap.asSketchImage()
}