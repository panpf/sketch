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

import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format

/**
 * Define how to resize the image
 */
data class Resize constructor(
    val width: Int,
    val height: Int,
    val precisionDecider: PrecisionDecider,
    /**
     * Which part of the original picture should be kept when the original topic needs to be cropped.
     * Works only when precision is [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     */
    val scaleDecider: ScaleDecider,
) {

    constructor(
        width: Int,
        height: Int,
        precision: Precision = Precision.EXACTLY,   // todo default change to LESS_PIXELS
        scale: Scale = Scale.CENTER_CROP
    ) : this(width, height, FixedPrecisionDecider(precision), FixedScaleDecider(scale))

    constructor(
        width: Int,
        height: Int,
        precision: Precision,
    ) : this(width, height, FixedPrecisionDecider(precision), FixedScaleDecider(Scale.CENTER_CROP))

    constructor(
        width: Int,
        height: Int,
        scale: Scale
    ) : this(width, height, FixedPrecisionDecider(Precision.EXACTLY), FixedScaleDecider(scale))

    constructor(
        width: Int,
        height: Int,
        precision: PrecisionDecider,
        scale: Scale = Scale.CENTER_CROP
    ) : this(width, height, precision, FixedScaleDecider(scale))

    constructor(
        width: Int,
        height: Int,
        precision: Precision = Precision.EXACTLY,
        scale: ScaleDecider
    ) : this(width, height, FixedPrecisionDecider(precision), scale)


    constructor(
        size: Size,
        precision: PrecisionDecider,
        scale: ScaleDecider
    ) : this(size.width, size.height, precision, scale)

    constructor(
        size: Size,
        precision: Precision = Precision.EXACTLY,
        scale: Scale = Scale.CENTER_CROP
    ) : this(size.width, size.height, FixedPrecisionDecider(precision), FixedScaleDecider(scale))

    constructor(
        size: Size,
        precision: Precision,
    ) : this(
        size.width,
        size.height,
        FixedPrecisionDecider(precision),
        FixedScaleDecider(Scale.CENTER_CROP)
    )

    constructor(
        size: Size,
        scale: Scale
    ) : this(
        size.width,
        size.height,
        FixedPrecisionDecider(Precision.EXACTLY),
        FixedScaleDecider(scale)
    )

    constructor(
        size: Size,
        precision: PrecisionDecider,
        scale: Scale = Scale.CENTER_CROP
    ) : this(size.width, size.height, precision, FixedScaleDecider(scale))

    constructor(
        size: Size,
        precision: Precision = Precision.EXACTLY,
        scale: ScaleDecider
    ) : this(size.width, size.height, FixedPrecisionDecider(precision), scale)

    val key: String by lazy {
        "Resize(${width}x$height,${precisionDecider.key},${scaleDecider.key})"
    }

    fun getPrecision(imageWidth: Int, imageHeight: Int): Precision =
        precisionDecider.get(imageWidth, imageHeight, width, height)

    fun getScale(imageWidth: Int, imageHeight: Int): Scale =
        scaleDecider.get(imageWidth, imageHeight, width, height)

    /**
     * Calculate the precision according to the original image, and then decide whether to crop the image according to the precision
     */
    fun shouldClip(imageWidth: Int, imageHeight: Int): Boolean =
        when (getPrecision(imageWidth, imageHeight)) {
            Precision.EXACTLY -> imageWidth != width || imageHeight != height
            Precision.SAME_ASPECT_RATIO -> {
                val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(1)
                val resizeAspectRatio = width.toFloat().div(height).format(1)
                imageAspectRatio != resizeAspectRatio
            }
            Precision.LESS_PIXELS -> false
        }

    override fun toString(): String {
        return "Resize(width=${width}, height=$height, precision=${precisionDecider}, scale=${scaleDecider})"
    }
}