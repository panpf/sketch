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

import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format

/**
 * Define how to resize the image
 */
data class Resize constructor(
    val size: Size,
    val precision: Precision,
    /**
     * Which part of the original picture should be kept when the original topic needs to be cropped.
     * Works only when precision is [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     */
    val scale: Scale,
) : Key {

    constructor(width: Int, height: Int, precision: Precision, scale: Scale)
            : this(Size(width, height), precision, scale)

    constructor(width: Int, height: Int)
            : this(width, height, Precision.LESS_PIXELS, Scale.CENTER_CROP)

    constructor(width: Int, height: Int, precision: Precision)
            : this(width, height, precision, Scale.CENTER_CROP)

    constructor(width: Int, height: Int, scale: Scale)
            : this(width, height, Precision.LESS_PIXELS, scale)

    override val key: String by lazy {
        "Resize(${size},${precision},${scale})"
    }

    /**
     * Calculate the precision according to the original image, and then decide whether to crop the image according to the precision
     */
    fun shouldClip(imageSize: Size): Boolean {
        if (size.isEmpty || imageSize.isEmpty) {
            return false
        }
        return when (precision) {
            Precision.EXACTLY -> imageSize != size
            Precision.SAME_ASPECT_RATIO -> {
                val imageAspectRatio = imageSize.width.toFloat().div(imageSize.height).format(1)
                val resizeAspectRatio = size.width.toFloat().div(size.height).format(1)
                imageAspectRatio != resizeAspectRatio
            }

            Precision.LESS_PIXELS -> false
            Precision.SMALLER_SIZE -> false
        }
    }

    override fun toString(): String {
        return "Resize(size=${size}, precision=${precision}, scale=${scale})"
    }
}