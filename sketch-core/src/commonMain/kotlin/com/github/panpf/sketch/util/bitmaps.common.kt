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

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.height
import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.width


/**
 * Get a mutable copy of the bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testMutableCopy
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testMutableCopy
 */
expect fun Bitmap.mutableCopy(): Bitmap

/**
 * Get a mutable copy of the bitmap, if it is already mutable, return itself
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testMutableCopyOrSelf
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testMutableCopyOrSelf
 */
expect fun Bitmap.mutableCopyOrSelf(): Bitmap


/**
 * Returns true if there are transparent pixels
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testHasAlphaPixels
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testHasAlphaPixels
 */
expect fun Bitmap.hasAlphaPixels(): Boolean

/**
 * Read an integer pixel array in the format ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testReadIntPixels
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testReadIntPixels
 */
expect fun Bitmap.readIntPixels(
    x: Int = 0, y: Int = 0, width: Int = this.width, height: Int = this.height
): IntArray

/**
 * Install integer pixels in the format ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testInstallIntPixels
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testInstallIntPixels
 */
expect fun Bitmap.installIntPixels(intPixels: IntArray)

/**
 * Returns the Color at the specified location. Format ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testReadIntPixel
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testReadIntPixel
 */
expect fun Bitmap.readIntPixel(x: Int, y: Int): Int


/**
 * Add a background color to the current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testBackgrounded
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testBackgrounded2
 */
expect fun Bitmap.background(color: Int): Bitmap

/**
 * Apply a blur effect to the image
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testBlur
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testBlur
 */
expect fun Bitmap.blur(radius: Int, firstReuseSelf: Boolean = false): Bitmap

/**
 * Crop the image into a circle
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testCircleCrop
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testCircleCrop
 */
expect fun Bitmap.circleCrop(scale: Scale): Bitmap

/**
 * Create a new image based on the mapping relationship represented by [ResizeMapping]
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testMapping
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testMapping
 */
expect fun Bitmap.mapping(mapping: ResizeMapping): Bitmap

/**
 * Apply a mask effect to the image
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testMask
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testMask
 */
expect fun Bitmap.mask(maskColor: Int, firstReuseSelf: Boolean = false): Bitmap

/**
 * Rotate the image
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testRotate
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testRotate
 */
expect fun Bitmap.rotate(angle: Int): Bitmap

/**
 * Apply rounded corners to the image
 *
 * @param radiusArray Array of 8 values, 4 pairs of [X,Y] radii. The corners are ordered top-left, top-right, bottom-right, bottom-left
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testRoundedCorners
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testRoundedCorners
 */
expect fun Bitmap.roundedCorners(radiusArray: FloatArray): Bitmap

/**
 * Zoom image
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testScale
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testScale
 */
expect fun Bitmap.scale(scaleFactor: Float): Bitmap