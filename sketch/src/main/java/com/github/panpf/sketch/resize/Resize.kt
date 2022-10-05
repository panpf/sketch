/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.util.format

/**
 * Define how to resize the image
 */
data class Resize constructor(
    val width: Int,
    val height: Int,
    val precision: Precision,
    /**
     * Which part of the original picture should be kept when the original topic needs to be cropped.
     * Works only when precision is [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     */
    val scale: Scale,
) {

    constructor(width: Int, height: Int)
            : this(width, height, Precision.LESS_PIXELS, Scale.CENTER_CROP)

    constructor(width: Int, height: Int, precision: Precision)
            : this(width, height, precision, Scale.CENTER_CROP)

    constructor(width: Int, height: Int, scale: Scale)
            : this(width, height, Precision.LESS_PIXELS, scale)

    val key: String by lazy {
        "Resize(${width}x$height,${precision},${scale})"
    }

    /**
     * Calculate the precision according to the original image, and then decide whether to crop the image according to the precision
     */
    fun shouldClip(imageWidth: Int, imageHeight: Int): Boolean =
        when (precision) {
            Precision.EXACTLY -> imageWidth != width || imageHeight != height
            Precision.SAME_ASPECT_RATIO -> {
                val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(1)
                val resizeAspectRatio = width.toFloat().div(height).format(1)
                imageAspectRatio != resizeAspectRatio
            }
            Precision.LESS_PIXELS -> false
        }

    override fun toString(): String {
        return "Resize(width=${width}, height=$height, precision=${precision}, scale=${scale})"
    }
}