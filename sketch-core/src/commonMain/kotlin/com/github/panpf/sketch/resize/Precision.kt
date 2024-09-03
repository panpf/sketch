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

package com.github.panpf.sketch.resize

import com.github.panpf.sketch.Image

/**
 * The precision of the resize operation
 *
 * @see com.github.panpf.sketch.core.common.test.resize.PrecisionTest
 */
enum class Precision {

    /**
     * Try to keep the number of pixels of the returned image smaller than resize. A 10% margin of error is allowed
     *
     * * The aspect ratio of the loaded image is the same as the original image
     * * The number of pixels of the loaded image is less than or slightly greater than resize (10% error)
     * * The width and height of the loaded image are smaller than or slightly larger than resize
     */
    LESS_PIXELS,

    /**
     * The size of the returned image must be smaller than [Resize]
     *
     * * The aspect ratio of the loaded image is the same as the original image
     * * The number of pixels of the loaded image is less than or equal to resize
     * * The width and height of the loaded image are less than or equal to resize
     */
    SMALLER_SIZE,

    /**
     * The size of the new image will not be larger than [Resize], but the aspect ratio will be the same
     *
     * * The aspect ratio of the loaded image is different from the original image, but the same as resize
     * * The number of pixels of the loaded image is less than or equal to resize
     * * The width and height of the loaded image are less than or equal to resize
     */
    SAME_ASPECT_RATIO,

    /**
     * The size of the [Image] returned is exactly the same as [Resize]
     *
     * * The aspect ratio of the loaded image is different from the original image, but the same as resize
     * * The number of image pixels loaded is equal to resize
     * * The width and height of the loaded image are equal to resize
     */
    EXACTLY,
}

/**
 * Whether the precision is [Precision.LESS_PIXELS]
 *
 * @see com.github.panpf.sketch.core.common.test.resize.PrecisionTest.testIsSmallerSizeMode
 */
fun Precision.isSmallerSizeMode(): Boolean {
    return this == Precision.SMALLER_SIZE
}