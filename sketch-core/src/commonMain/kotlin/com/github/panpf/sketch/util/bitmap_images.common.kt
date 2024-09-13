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
import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.resize.Scale


/**
 * Read an integer pixel array in the format ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testReadIntPixels
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testReadIntPixels
 */
expect fun BitmapImage.readIntPixels(
    x: Int = 0,
    y: Int = 0,
    width: Int = this.width,
    height: Int = this.height
): IntArray

/**
 * Zoom image
 */
expect fun BitmapImage.scale(scaleFactor: Float): BitmapImage

/**
 * Create a new image based on the mapping relationship represented by [ResizeMapping]
 */
expect fun BitmapImage.mapping(mapping: ResizeMapping): BitmapImage

/**
 * Apply a blur effect to the image
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testBlur
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testBlur
 */
expect fun BitmapImage.blur(
    radius: Int,
    hasAlphaBitmapBgColor: Int? = null,
    maskColor: Int? = null,
    firstReuseSelf: Boolean = false
): BitmapImage

/**
 * Crop the image into a circle
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testCircleCrop
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testCircleCrop
 */
expect fun BitmapImage.circleCrop(scale: Scale): BitmapImage

/**
 * Apply a mask effect to the image
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testMask
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testMask
 */
expect fun BitmapImage.mask(maskColor: Int, firstReuseSelf: Boolean = false): BitmapImage

/**
 * Rotate the image
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testRotate
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testRotate
 */
expect fun BitmapImage.rotate(degrees: Int): BitmapImage

/**
 * Apply rounded corners to the image
 *
 * @param radiusArray Array of 8 values, 4 pairs of [X,Y] radii. The corners are ordered top-left, top-right, bottom-right, bottom-left
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testRoundedCorners
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testRoundedCorners
 */
expect fun BitmapImage.roundedCorners(radiusArray: FloatArray): BitmapImage