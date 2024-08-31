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
import com.github.panpf.sketch.resize.Scale

/**
 * Apply a blur effect to the image
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testBlur
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testBlur
 */
internal expect fun Image.blur(radius: Int, hasAlphaBitmapBgColor: Int?, maskColor: Int?): Image

/**
 * Crop the image into a circle
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testCircleCrop
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testCircleCrop
 */
internal expect fun Image.circleCrop(scale: Scale): Image

/**
 * Apply a mask effect to the image
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testMask
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testMask
 */
internal expect fun Image.mask(maskColor: Int): Image

/**
 * Rotate the image
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testRotate
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testRotate
 */
internal expect fun Image.rotate(degrees: Int): Image

/**
 * Apply rounded corners to the image
 *
 * @param radiusArray Array of 8 values, 4 pairs of [X,Y] radii. The corners are ordered top-left, top-right, bottom-right, bottom-left
 *
 * @see com.github.panpf.sketch.core.android.test.util.ImagesAndroidTest.testRoundedCorners
 * @see com.github.panpf.sketch.core.nonandroid.test.util.ImagesNonAndroidTest.testRoundedCorners
 */
internal expect fun Image.roundedCorners(radiusArray: FloatArray): Image